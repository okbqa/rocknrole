package org.okbqa.rocknrole.transforming;

import org.okbqa.rocknrole.graph.Graph;

/**
 *
 * @author cunger
 */
public class Transformer {
    
    
    String language;
    
    Preprocessing preprocessing;
    Pruning pruning;
    
    RuleApplication app;
        
    
    public Transformer(String l) {
        
        language = l;
        
        preprocessing = new Preprocessing();
        pruning = new Pruning();
        
        app = new RuleApplication(language);
        try {
            app.init();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void debugMode() {
        app.debugMode();
    }
    
    
    public Graph transform(Graph graph) {
        
        Graph g = graph.copy();
        
        // Step 1: Preprocessing
        preprocessing.apply(g);
        
        // Step 2: Execute rules
        app.execute(g);
        
        // Step 3: Postprocessing (pruning)
        pruning.apply(g);
        
        return g;
    }
    
}
