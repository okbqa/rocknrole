package de.citec.sc.rocknrole.transforming;

import de.citec.sc.rocknrole.graph.*;
import de.citec.sc.rocknrole.graph.interpreter.GraphReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cunger
 */
public class RuleTransformerVisual implements Transformer {

    boolean verbose = false;
    
    GraphReader reader = new GraphReader();
    
    
    @Override
    public Graph transform(Graph dep) {
        
        Graph graph = dep.copy();
                
        if (verbose) System.out.println("\nDependency graph:\n" + graph.toString());
        
        
        final String who   = "who";
        final String when  = "when";
        final String where = "where";
        final String why   = "why";
        
        List<String> COP = new ArrayList<>();
        COP.add("be");
        COP.add("is");
        COP.add("was");
        COP.add("are");
        COP.add("were");
        
        List<String> DTs = new ArrayList<>();
        DTs.add("this");
        DTs.add("these");
        DTs.add("those");
        
        List<String> WDTs = new ArrayList<>();
        WDTs.add("what");
        WDTs.add("which");
 
        
        // Normalizing edges 
        
        List<Edge> new_edges = new ArrayList<>();
        for (Edge e : graph.getEdges()) {
             if (e.getLabel().equals("poss")) {
                 new_edges.add(new Edge(Edge.Color.SEM,e.getHead(),"RELATED",e.getDependent()));
             }
        }
        for (Edge e : new_edges) graph.addEdge(e);
        
        // Normalizing nodes
        
        graph.initRoots();

        Map<String,String> normalize = new HashMap<>();
        for (String s : COP)  normalize.put(s,"BE");
        for (String s : DTs)  normalize.put(s,"THIS");
        for (String s : WDTs) normalize.put(s,"WH");
        
        for (Node n : graph.getNodes()) {
            if (normalize.containsKey(n.getForm().toLowerCase())) {
                n.setForm(normalize.get(n.getForm().toLowerCase()));
            }
            if (n.getForm().toLowerCase().equals("how")) {
                n.setForm("HOW");
            }
        }
        
        
        if (verbose) System.out.println("\nNormalized dependency graph:\n"+graph.toString());
        
        
        // Collapsing nodes
        
        String[] compoundLabels = {"nn","number","mwe","compound"};
        for (String label : compoundLabels) {
             List<Edge> toCollapse = graph.getEdges(label);
             for (Edge e : toCollapse) {
                  Node head = graph.getNode(e.getHead());
                  Node dpnd = graph.getNode(e.getDependent());
                  head.setForm(dpnd.getForm() + " " + head.getForm());
                  graph.deleteNode(dpnd);
                  graph.deleteEdge(e);
                  graph.renameNode(dpnd.getId(),head.getId());
             }             
        }
        
        
        // Determiners 
            
        // what kind of        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"det(*-2,WH-1) \n prep(*-2,of-3) \n pobj(of-3,*-4)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            Node old_4 = graph.getNode(m.get(4));
            Node old_2 = graph.getNode(m.get(2));
            String f = old_2.getForm(); 
            if (f.endsWith("s")) f = f.substring(0,f.length()-2);
            if (f.equals("kind") || f.equals("sort") || f.equals("type")) {
            
                graph.delete(g);
                                               
                Node new_2 = new Node(m.get(4),"RETURN:KIND");
                Node new_4 = new Node(m.get(2),old_4.getForm(),old_4.getPOS());

                graph.addNode(new_2);
                graph.addNode(new_4);
                graph.addEdge(new Edge(Edge.Color.SEM,m.get(2),"SPEC",m.get(4)));
            }
        }
        
        // what, which
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"det(*-2,WH-1)")) {
                    
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            Node n = graph.getNode(m.get(2));
            String f = n.getForm(); 
            if (f.endsWith("s")) f = f.substring(0,f.length()-2);
            // specific attributes
            if (f.equals("color")) {
                n.setForm("COLOR");
            } 
            if (f.equals("shape")) {
                n.setForm("SHAPE");
            } 
            graph.addEdge(new Edge(Edge.Color.SEM,m.get(2),"SPEC",m.get(1)));
        }
        
        // how many 
        List<Pair<Graph,Map<Integer,Integer>>> howmany_subgraphs = new ArrayList<>();
        howmany_subgraphs.addAll(getSubgraphs(graph,"advmod(many-2,HOW-3) \n amod(*-1,many-2)"));
        howmany_subgraphs.addAll(getSubgraphs(graph,"dep(many-2,HOW-3) \n amod(*-1,many-2)"));
        howmany_subgraphs.addAll(getSubgraphs(graph,"advmod(many-2,HOW-3) \n dep(*-1,many-2)"));
                
        for (Pair<Graph,Map<Integer,Integer>> subgraph : howmany_subgraphs) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            Node n = graph.getNode(m.get(1));
            n.setForm("COUNT:" + n.getForm());
            graph.addRoot(n.getId());
            
            graph.delete(g);
        }
        
        // TODO advmod(*/JJ-1,how-2)
        
        // is there / there is
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"expl(BE-1,there-2) \n nsubj(BE-1,*-3)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addRoot(m.get(3));            
            graph.delete(g);
        }
        
        for (Node n : graph.getNodes()) {
             
            switch (n.getForm()) {
                
                case who:   n.setForm("RETURN:AGENT"); break;
                case when:  n.setForm("RETURN:DATETIME"); break;
                case where: n.setForm("RETURN:LOCATION"); break;
                case why:   n.setForm("RETURN:REASON"); break;
                case "WH":  n.setForm("RETURN"); break;
            }
        }
        
                
        // Copulatives
        
        String[] subs = { "cop(BE-2,*-1) \n nsubj(*-1,*-3)", 
                          "cop(*-1,BE-2) \n nsubj(*-1,*-3)", 
                          "dep(BE-2,*-1) \n nsubj(BE-2,*-3)"
                      //  "aux(*-1,BE-2) \n nsubj(*-1,*-3)"
                        };
        for (String sub : subs) {
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,sub)) {
            
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SEM,m.get(3),"OVERLAP",m.get(1)));
        }}
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"nsubj(BE-1,*-4) \n prep(BE-1,*-2) \n pobj(*-2,*-3)")) {
            
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SEM,m.get(4),"RELATED",m.get(3)));
        }
        
        
        // Argument structure
                       
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"prep(*-1,*-2) \n pobj(*-2,*-3)")) {
            
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SEM,m.get(1),graph.getNode(m.get(2)).getForm(),m.get(3)));
        }
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"nsubj(*-1,*-2) \n dobj(*-1,*-3)")) {
                       
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SEM,m.get(2),"RELATED",m.get(3)));
        }
       
        // Passives 
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"auxpass(*-1,BE-2) \n nsubjpass(*-1,*-3) \n dobj(*-1,*-4)")) {
            
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SEM,m.get(3),"RELATED",m.get(4)));
        }
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"auxpass(*-1,BE-2) \n nsubjpass(*-1,*-3)")) {
            
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SEM,m.get(3),"OVERLAP",m.get(1)));
        }
        
        // Gerunds 
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"nsubj(*-1,*-2) \n rcmod(*-3,*-1)")) {
                       
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SEM,m.get(2),"RELATED",m.get(3)));
        }
        
        
        // Modifiers
        
        String[] mods = {"amod","vmod"}; // ,"advmod"
        for (String mod : mods) {
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,mod+"(*-1,*-2)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SEM,m.get(1),"EQUAL",m.get(2)));
        }} 
        
        // Numericals 
        // TODO also requires some normalization 
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"num(*-1,*-2)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SEM,m.get(1),"NUM",m.get(2)));
        }
        
        
        // Coordination
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"cc(*-1,or-2) \n conj(*-1,*-3)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            int i = graph.getMaxId()+1;
            graph.addNode(new Node(i,"CHOICE"));
            graph.addEdge(new Edge(Edge.Color.SEM,m.get(1),"EQUAL",i));
            graph.addEdge(new Edge(Edge.Color.SEM,i,"EQUAL",m.get(3)));
        }
        
        
        // Finally, keep only semantic edges
        
        Graph sem_graph = new Graph();
        
        for (Edge e : graph.getEdges()) {
             if (e.getColor() == Edge.Color.SEM) {
                 sem_graph.addEdge(e);
                 sem_graph.addNode(graph.getNode(e.getHead()));
                 sem_graph.addNode(graph.getNode(e.getDependent()));
             }
        }
        
        for (int i : graph.getRoots()) { 
            sem_graph.addRoot(i);
            sem_graph.addNode(graph.getNode(i));
        }
        
        // and clean up errors 
        // TODO
        
        for (Edge e : graph.getEdges()) {
             if (graph.getNode(e.getHead()).getForm().equals("BE") || 
                (graph.getNode(e.getHead()).getForm().equals("THE") && graph.getNode(e.getDependent()).getForm().equals("RETURN"))) {
                 sem_graph.deleteEdge(e);
             }
        }
        
        
        if (verbose) System.out.println("\nSemGraph:\n" + sem_graph.toString());

        
        return sem_graph;
    }
    
    
    private List<Pair<Graph,Map<Integer,Integer>>> getSubgraphs(Graph graph, String regex) {
               
        Graph subgraph = reader.interpret(regex);
                
        return subgraph.subGraphMatches(graph);
    }
    
}
