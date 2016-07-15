package de.citec.sc.rocknrole.transforming;

import de.citec.sc.rocknrole.graph.*;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cunger
 */
public class RuleTransformer_prune extends RuleTransformer {

        
    @Override
    public Graph transform(Graph graph) {
        
        Graph g = new Graph();
                
        // Keep only semantic edges
                
        for (Edge e : graph.getEdges()) {
             if (e.getColor() == Edge.Color.SRL) {
                 g.addEdge(e);
                 g.addNode(graph.getNode(e.getHead()));
                 g.addNode(graph.getNode(e.getDependent()));
             }
        }
        
        return g;
    }
    
    private List<Pair<Graph,Map<Integer,Integer>>> getSubgraphs(Graph graph, String regex) {
               
        Graph subgraph = reader.interpret(regex);
                
        return subgraph.subGraphMatches(graph);
    }
}
