package de.citec.sc.rocknrole.transforming;

import de.citec.sc.rocknrole.graph.Graph;

/**
 *
 * @author cunger
 */
public interface Transformer {
 
    public Graph transform(Graph graph);
    
    public void setLanguage(String l);
    public void setVerbose(boolean b);
}
