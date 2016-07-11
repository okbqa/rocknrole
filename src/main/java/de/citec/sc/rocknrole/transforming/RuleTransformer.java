package de.citec.sc.rocknrole.transforming;

import de.citec.sc.rocknrole.graph.interpreter.GraphReader;

/**
 *
 * @author cunger
 */
public abstract class RuleTransformer implements Transformer {
    
    String language = "en";
    boolean verbose = false;
    
    GraphReader reader = new GraphReader();

    
    @Override
    public void setLanguage(String l) { 
        language = l;
    }
    
    @Override
    public void setVerbose(boolean b) { 
        verbose = b;
    }

}
