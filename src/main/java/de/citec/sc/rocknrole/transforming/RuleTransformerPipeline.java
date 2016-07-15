package de.citec.sc.rocknrole.transforming;

import de.citec.sc.rocknrole.graph.Edge;
import de.citec.sc.rocknrole.graph.Graph;

/**
 *
 * @author cunger
 */
public class RuleTransformerPipeline extends RuleTransformer {

    // language-specific rules
    RuleTransformer_en    t_en    = new RuleTransformer_en();
    RuleTransformer_ko    t_ko    = new RuleTransformer_ko();
    
    // general, language-independent rules
    RuleTransformer_pre   t_pre   = new RuleTransformer_pre();
    RuleTransformer_post  t_post  = new RuleTransformer_post();
    
    // final pruning rules
    RuleTransformer_prune t_prune = new RuleTransformer_prune(); 
    
    
    @Override
    public Graph transform(Graph graph) {
           
        // Step 1 
        
        Graph g1 = t_pre.transform(graph);
        
        if (verbose) { 
            System.out.println("====== Step 1 (RuleTransformer_pre) =======");
            System.out.println(g1.toString(true));
        }
        
        // Step 2 (language-specific rules)
        
        Graph g2 = null;
        
        switch (language) {
            
            case "en": g2 = t_en.transform(g1); break;
            case "ko": g2 = t_ko.transform(g1); break;
        }
        
        if (verbose) { 
            System.out.println("====== Step 2 (RuleTransformer_" + language + ") =======");
            System.out.println(g2.toString(true));
        }
        
        // Step 3 
        
        Graph g3 = t_post.transform(g2);
        
        if (verbose) { 
            System.out.println("====== Step 3 (RuleTransformer_post) =======");
            System.out.println(g3.toString(true));
        }
        
        // Step 4 (pruning)
        
        Graph g4 = t_prune.transform(g3);
        
        if (verbose) { 
            System.out.println("====== Step 3 (pruning) =======");
            System.out.println(g4.toString(true));
        }
        
        // Done
        
        return g4;
        
    }
    
}
