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
public class RuleTransformerSRL implements Transformer {

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
        DTs.add("the");
        DTs.add("this");
        DTs.add("these");
        
        List<String> WDTs = new ArrayList<>();
        WDTs.add("what");
        WDTs.add("which");
 
        
        // Renaming nodes

        Map<String,String> normalize = new HashMap<>();
        for (String s : COP)  normalize.put(s,"BE");
        for (String s : DTs)  normalize.put(s,"THIS");
        for (String s : WDTs) normalize.put(s,"WH");
        
        for (Node n : graph.getNodes()) {
            if (normalize.containsKey(n.getForm().toLowerCase())) {
                n.setForm(normalize.get(n.getForm().toLowerCase()));
            }
            
            switch (n.getForm()) {
                
                case who:   n.setForm("RETURN:agent"); break;
                case when:  n.setForm("RETURN:datetime"); break;
                case where: n.setForm("RETURN:location"); break;
                case why:   n.setForm("RETURN:reason"); break;
                case "WH":  n.setForm("RETURN");
            }
        }

        
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
        
        
        // Transforming edges
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"prep(*-1,*-2) \n pobj(*-2,*-3)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SRL,m.get(1),"ARGP:"+graph.getNode(m.get(2)).getForm(),m.get(3)));
        }
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"cop(*-1,*-2) \n nsubj(*-2,*-3)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SRL,m.get(1),"cop",m.get(3)));
        }
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"nsubj(BE-1,*-2) \n dobj(BE-1,*-3)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SRL,m.get(1),"cop",m.get(3)));
        }
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"cc(*-1,*-2) \n conj(*-1,*-3)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();

            graph.addEdge(new Edge(Edge.Color.SRL,m.get(2),"conjunct",m.get(1)));            
            graph.addEdge(new Edge(Edge.Color.SRL,m.get(2),"conjunct",m.get(3)));
        }
        
        
        // Renaming edges
        
        for (Edge e : graph.getEdges()) {
            
            switch (e.getLabel()) {
                
                case "nsubj":     e.setLabel("ARG0"); e.setColor(Edge.Color.SRL); break;
                case "dobj":      e.setLabel("ARG1"); e.setColor(Edge.Color.SRL); break;
                case "iobj":      e.setLabel("ARG2"); e.setColor(Edge.Color.SRL); break;
                case "nmod":      e.setLabel("ARG0"); e.setColor(Edge.Color.SRL); break;
                case "nsubjpass": e.setLabel("ARG1"); e.setColor(Edge.Color.SRL); break;
                case "det":       e.setLabel("SPEC"); e.setColor(Edge.Color.SRL); break;
                case "amod":      e.setLabel("MOD");  e.setColor(Edge.Color.SRL); break;                
            }
        }

        
        // Finally, keep only semantic edges
        
        Graph srl_graph = new Graph();
        
        for (Edge e : graph.getEdges()) {
             if (e.getColor() == Edge.Color.SRL) {
                 srl_graph.addEdge(e);
                 srl_graph.addNode(graph.getNode(e.getHead()));
                 srl_graph.addNode(graph.getNode(e.getDependent()));
             }
        }
        
        
        if (verbose) System.out.println("\nSRL graph:\n" + srl_graph.toString());

        
        return srl_graph;
    }
    
    private List<Pair<Graph,Map<Integer,Integer>>> getSubgraphs(Graph graph, String regex) {
               
        Graph subgraph = reader.interpret(regex);
                
        return subgraph.subGraphMatches(graph);
    }
}
