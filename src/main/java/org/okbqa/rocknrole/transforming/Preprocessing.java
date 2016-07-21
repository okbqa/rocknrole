package org.okbqa.rocknrole.transforming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.okbqa.rocknrole.graph.*;

/**
 *
 * @author cunger
 */
public class Preprocessing {
    
    
    public void apply(Graph graph) {
        
        // Normalizing POS tags 
        String[] nouns = { "NN", "NNS", "NP", "NNG" }; // --> NN
        String[] verbs = { "VBZ", "VBN", "VBP" };      // --> VV
        String[] whs   = { "WP" };                     // --> WH
        
        for (Node n : graph.getNodes()) {
             String pos = n.getPOS();
             if (pos != null && Arrays.asList(nouns).contains(pos)) {
                 n.setPOS("NN");
                 continue;
             }
             if (pos != null && Arrays.asList(verbs).contains(pos)) {
                 n.setPOS("VV");
                 continue;
             }
             if (pos != null && Arrays.asList(whs).contains(pos)) {
                 n.setPOS("WH");
             }
        }
        
        // Collapsing nodes: compounds
        
        String[] compoundLabels = {"nn","number","mwe","compound"};
        for (String label : compoundLabels) {
             graph.collapseEdges(graph.getEdges(label));
        }
        
        // Collapsing nodes: Named Entities 
        
        List<Edge> toCollapse = new ArrayList<>();
        
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
