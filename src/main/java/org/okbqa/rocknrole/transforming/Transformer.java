package org.okbqa.rocknrole.transforming;

import org.okbqa.rocknrole.graph.Graph;

/**
 *
 * @author cunger
 */
public interface Transformer {
 
    public Graph transform(Graph graph);
    
    public void setLanguage(String l);
    public void setVerbose(boolean b);
}
