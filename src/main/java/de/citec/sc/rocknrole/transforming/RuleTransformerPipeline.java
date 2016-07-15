package de.citec.sc.rocknrole.transforming;

import de.citec.sc.rocknrole.graph.Edge;
import de.citec.sc.rocknrole.graph.Graph;

/**
 *
 * @author cunger
 */
public class RuleTransformerPipeline extends RuleTransformer {

    // language-specific rules
    RuleTransformer_en      t_en      = new RuleTransformer_en();
    RuleTransformer_ko      t_ko      = new RuleTransformer_ko();
    
    // general, language-independent rules
    RuleTransformer_general t_general = new RuleTransformer_general();
    
    // final pruning rules
    RuleTransformer_prune   t_prune   = new RuleTransformer_prune(); 
    
    
    @Override
    public Graph transform(Graph graph) {
           
        graph.initRoots();
        
        // Step 1 (language-specific rules)
        
        Graph language_independent_graph = null;
        
        switch (language) {
            
            case "en": language_independent_graph = t_en.transform(graph); break;
            case "ko": language_independent_graph = t_ko.transform(graph); break;
        }
        
        if (verbose) { 
            System.out.println("====== Step 1 (" + language + ") =======");
            System.out.println(language_independent_graph.toString(true));
        }
        
        // Step 2 (general, language-independent rules)
        
        Graph transformed_graph = t_general.transform(language_independent_graph);
        
        if (verbose) { 
            System.out.println("====== Step 2 (general) =======");
            System.out.println(transformed_graph.toString(true));
        }
        
        // Step 3 (pruning)
        
        Graph pruned_graph = t_prune.transform(transformed_graph);
        
        if (verbose) { 
            System.out.println("====== Step 3 (pruning) =======");
            System.out.println(transformed_graph.toString(true));
        }
        
        // Finally, keep only semantic edges
        
        Graph srl = new Graph();
        
        for (Edge e : pruned_graph.getEdges()) {
             if (e.getColor() == Edge.Color.SRL) {
                 srl.addEdge(e);
                 srl.addNode(pruned_graph.getNode(e.getHead()));
                 srl.addNode(pruned_graph.getNode(e.getDependent()));
             }
        }
        
        if (verbose) { 
            System.out.println("====== Final graph =======");
            System.out.println(srl.toString(true));
        }
        
        // Done
        
        return srl;
        
    }
    
}
