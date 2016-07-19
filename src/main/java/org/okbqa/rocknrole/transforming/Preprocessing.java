package org.okbqa.rocknrole.transforming;

import java.util.ArrayList;
import java.util.List;
import org.okbqa.rocknrole.graph.*;

/**
 *
 * @author cunger
 */
public class Preprocessing {
    
    
    public void apply(Graph graph) {
        
        // Collapsing nodes: compounds
        
        String[] compoundLabels = {"nn","number","mwe","compound"};
        for (String label : compoundLabels) {
             graph.collapseEdges(graph.getEdges(label));
        }
        
        // Collapsing nodes: Named Entities 
        
        List<Edge> toCollapse = new ArrayList<Edge>();
        
        for (Node n : graph.getNodes()) {
        
            if (n.getPOS() != null && n.getPOS().equals("NE")) {
                 
                 int i = n.getId();
                 int j;
                 
                 for (Edge e : graph.getEdges()) {
                      if (e.getHead() == i) {
                          j = e.getDependent();
                          if (graph.getNode(j).getPOS() != null && graph.getNode(j).getPOS().equals("NE")) {
                              toCollapse.add(e);
                          }
                      } 
                      else if (e.getDependent() == i) {
                          j = e.getHead();
                          if (graph.getNode(j).getPOS() != null && graph.getNode(j).getPOS().equals("NE")) {
                              toCollapse.add(e);
                          }
                      }
                 }
             }
        }
        graph.collapseEdges(toCollapse);
    }
    
}
