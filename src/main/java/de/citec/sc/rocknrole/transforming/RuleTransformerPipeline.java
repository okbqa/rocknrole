package de.citec.sc.rocknrole.transforming;

import de.citec.sc.rocknrole.graph.Edge;
import de.citec.sc.rocknrole.graph.Graph;

/**
 *
 * @author cunger
 */
public class RuleTransformerPipeline extends RuleTransformer {

    RuleTransformer_prune t_qa = new RuleTransformer_prune(); 
    RuleTransformer_en t_en = new RuleTransformer_en();
    RuleTransformer_general t_general = new RuleTransformer_general();
    
    @Override
    public Graph transform(Graph graph) {
        
        t_qa.setVerbose(verbose);
        t_en.setVerbose(verbose);
        t_general.setVerbose(verbose);
        
        // TODO switch language en/ko
        
        if (verbose) System.out.println("===================================");
        
        graph.initRoots();
        Graph transformed = t_qa.transform(t_general.transform(t_en.transform(graph)));
        
        // Finally, keep only semantic edges
        
        Graph srl = new Graph();
        
        for (Edge e : transformed.getEdges()) {
             if (e.getColor() == Edge.Color.SRL) {
                 srl.addEdge(e);
                 srl.addNode(transformed.getNode(e.getHead()));
                 srl.addNode(transformed.getNode(e.getDependent()));
             }
        }
        
        if (verbose) System.out.println("===================================");
        if (verbose) System.out.println("\nFinal graph:\n" + srl.toString());
        
        return srl;
        
    }
    
}
