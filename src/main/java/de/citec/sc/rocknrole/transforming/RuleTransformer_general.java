package de.citec.sc.rocknrole.transforming;

import de.citec.sc.rocknrole.graph.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cunger
 */
public class RuleTransformer_general extends RuleTransformer {

        
    @Override
    public Graph transform(Graph g_in) {
        
        Graph graph = g_in.copy();
        
        if (verbose) System.out.println("\n-------RuleTransformer_general.java--------");
        if (verbose) System.out.println("\nInput graph:\n" + graph.toString());
        
        
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
        
        // Renaming edges
        
        for (Edge e : graph.getEdges()) {
            
            switch (e.getLabel()) {
                
                case "nsubj":     e.setLabel("ARG0"); e.setColor(Edge.Color.SRL); break;
                case "dobj":      e.setLabel("ARG1"); e.setColor(Edge.Color.SRL); break;
                case "iobj":      e.setLabel("ARG2"); e.setColor(Edge.Color.SRL); break;
                case "nmod":      e.setLabel("ARG0"); e.setColor(Edge.Color.SRL); break;
                case "rcmod":     e.setLabel("ARG0"); e.setColor(Edge.Color.SRL); break;
                case "nsubjpass": e.setLabel("ARG1"); e.setColor(Edge.Color.SRL); break;
                case "det":       e.setLabel("SPEC"); e.setColor(Edge.Color.DEP); break;
                case "amod":      e.setLabel("MOD");  e.setColor(Edge.Color.SRL); break;
                case "poss":      e.setLabel("REL");  e.setColor(Edge.Color.SRL); break;                
            }
        }
        
        // Transforming edges
        
        // Prepositional arguments
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"prep(*-1,*-2) \n pobj(*-2,*-3)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SRL,m.get(1),"ARGP",m.get(3)));
            graph.delete(g);
        }
        
        String[] prepArgs = { "ARG0(*-1,*-2) \n ARGP(*-1,*-3)", 
                              "cop(*-2,*-1) \n ARGP(*-1,*-3)",
                              "ARG1(*-1,*-2) \n ARGP(*-1,*-3)"
                            };
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,prepArgs)) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SRL,m.get(2),graph.getNode(m.get(1)).getForm(),m.get(3)));
            graph.delete(g);
        }
        
        // Copulative constructions
        
        String[] copulatives = { "cop(*-1,*-2) \n ARG0(*-2,*-3)", 
                                 "cop(*-1,*-2) \n ARG0(*-1,*-3)",
                                 "ARG0(BE-2,*-1) \n ARG1(BE-2,*-3)"
                               };
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,copulatives)) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SRL,m.get(1),"EQUALS",m.get(3)));
            graph.delete(g);
        }
        
        // Argument structure
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"ARG0(*-1,*-2) \n ARG1(*-1,*-3)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SRL,m.get(2),graph.getNode(m.get(1)).getForm(),m.get(3)));
            graph.delete(g);
        }
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"ARG0(*-1,*-2) \n ARGP(*-1,*-3)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SRL,m.get(2),graph.getNode(m.get(1)).getForm(),m.get(3)));
            graph.delete(g);
        }
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"ARGP(*-1,*-2)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SRL,m.get(1),"REL",m.get(2)));
            graph.delete(g);
        }
                
        // Relative clauses 
        
        // TODO
        
        // HAVE 
        // Note: It's important that this is applied after the argument structure rules.
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"HAVE(*-1,*-2)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SRL,m.get(1),graph.getNode(m.get(2)).getForm(),m.get(2)));
            graph.getNode(m.get(2)).setForm("RESOURCEorLITERAL");
            graph.delete(g);
        }
        
        // MOD (interpret modifier as restriction class)
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"MOD(*-1,*-2)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SRL,m.get(1),"REL",m.get(2)));
            graph.getNode(m.get(2)).setPOS("NNP");
            graph.delete(g);
        }
        
        // Coordination
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"cc(*-1,*-2) \n conj(*-1,*-3)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();

            graph.addEdge(new Edge(Edge.Color.SRL,m.get(2),"conjunct",m.get(1)));            
            graph.addEdge(new Edge(Edge.Color.SRL,m.get(2),"conjunct",m.get(3)));
        }

        if (verbose) System.out.println("\nOutput graph:\n" + graph.toString());

        return graph;
    }
    
    private List<Pair<Graph,Map<Integer,Integer>>> getSubgraphs(Graph graph, String regex) {
               
        Graph subgraph = reader.interpret(regex);
                
        return subgraph.subGraphMatches(graph);
    }
    
    private List<Pair<Graph,Map<Integer,Integer>>> getSubgraphs(Graph graph, String[] regexes) {
        
        List<Pair<Graph,Map<Integer,Integer>>> subgraphs = new ArrayList<>();
        
        for (String regex : regexes) {
            subgraphs.addAll(getSubgraphs(graph,regex));
        }
        
        return subgraphs;
    }
}
