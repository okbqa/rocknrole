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
    public Graph transform(Graph g_in) {
        
        Graph graph = g_in.copy();
                
        if (verbose) System.out.println("\n-------RuleTransformer_qa.java--------");
        if (verbose) System.out.println("\nInput graph:\n" + graph.toString());

        
        // Transforming edges
            
        // TODO language independent?
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"ARG2(give-1,me-2) \n ARG1(give-1,*-3)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.delete(g);
            graph.addRoot(m.get(3));
        }
        
        // TODO list of
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"SPEC(*-1,RETURN-2)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addRoot(m.get(1));
        }
        
        if (verbose) System.out.println("\nOutput graph:\n" + graph.toString());

        return graph;
    }
    
    private List<Pair<Graph,Map<Integer,Integer>>> getSubgraphs(Graph graph, String regex) {
               
        Graph subgraph = reader.interpret(regex);
                
        return subgraph.subGraphMatches(graph);
    }
}
