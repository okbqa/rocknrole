package org.okbqa.rocknrole.transforming;

import org.okbqa.rocknrole.graph.Edge;
import org.okbqa.rocknrole.graph.Node;
import org.okbqa.rocknrole.graph.Pair;
import org.okbqa.rocknrole.graph.Graph;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cunger
 */
public class RuleTransformer_pre extends RuleTransformer {

        
    @Override
    public Graph transform(Graph g_in) {
        
        Graph graph = g_in.copy();

        // Collapsing nodes (compounds)
        
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
        
        // Collapsing edges (multiple dependencies between same nodes)
        
        List<Edge> del = new ArrayList<>();
        for (Edge e1 : graph.getEdges()) {
             if (del.contains(e1)) continue;
             for (Edge e2 : graph.findEdgesBetween(e1.getHead(),e1.getDependent())) {
                 if (e1 != e2 && e1.getColor() == e2.getColor()) {
                     del.add(e2);
                 }
             }
        }
        for (Edge e : del) graph.deleteEdge(e);
        
        // Renaming edges
        
        for (Edge e : graph.getEdges()) {
            
            switch (e.getLabel()) {
                
                // Stanford dependencies
                
                case "nsubj":     e.setLabel("ARG0"); e.setColor(Edge.Color.SRL); break;
                case "dobj":      e.setLabel("ARG1"); e.setColor(Edge.Color.SRL); break;
                case "iobj":      e.setLabel("ARG2"); e.setColor(Edge.Color.SRL); break;
                case "nmod":      e.setLabel("ARG0"); e.setColor(Edge.Color.SRL); break;
                case "rcmod":     e.setLabel("ARG0"); e.setColor(Edge.Color.SRL); break;
                case "nsubjpass": e.setLabel("ARG1"); e.setColor(Edge.Color.SRL); break;
                case "amod":      e.setLabel("MOD");  e.setColor(Edge.Color.SRL); break;
                case "poss":      e.setLabel("REL");  e.setColor(Edge.Color.SRL); break;
                  
                // ETRI dependencies
                    
                case "AJT":       e.setLabel("MOD");  e.setColor(Edge.Color.SRL); break;
                case "NP_SBJ":    e.setLabel("ARG0"); e.setColor(Edge.Color.SRL); break;
                case "NP_OBJ":    e.setLabel("ARG1"); e.setColor(Edge.Color.SRL); break;
                case "NP_MOD":    e.setLabel("ARG1"); e.setColor(Edge.Color.SRL); break;
                case "ARG0":      e.setColor(Edge.Color.SRL); break;
                case "ARG1":      e.setColor(Edge.Color.SRL); break;
            }
        }
                
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"prep(*-1,*-2) \n pobj(*-2,*-3)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SRL,m.get(1),"ARGP",m.get(3)));
            graph.delete(g);
        }
        
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
