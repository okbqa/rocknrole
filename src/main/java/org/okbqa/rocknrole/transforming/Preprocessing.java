package org.okbqa.rocknrole.transforming;

import java.util.List;
import org.okbqa.rocknrole.graph.*;

/**
 *
 * @author cunger
 */
public class Preprocessing {
    
    
    public void apply(Graph graph) {
        
        // Collapsing nodes (compounds)
        // TODO This should be replace by a first step of NER!
        
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
    }
    
}
