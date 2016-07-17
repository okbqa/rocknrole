package org.okbqa.rocknrole.transforming;

import java.util.ArrayList;
import java.util.List;
import org.okbqa.rocknrole.graph.*;

/**
 *
 * @author cunger
 */
public class Pruning {
    
    
    public void apply(Graph graph) {
        
        List<Edge> del_edges = new ArrayList<>();
        List<Node> del_nodes = new ArrayList<>();
        
        // Get rid of all non-semantic edges
        for (Edge e : graph.getEdges()) {
             if (e.getColor() != Edge.Color.SEM) {
                 del_edges.add(e);
             }
        }

        // Get rid of edges that don't make sense 
        // TODO It would be better if this doesn't happen in the first place!
        for (Edge e : graph.getEdges()) {
            if (e.getLabel().equals("THING")) {
                del_edges.add(e);
            }
        }
        
        // Do the actual removing
        for (Edge e : del_edges) graph.deleteEdge(e);

        // Remove unconnected nodes
        for (Node n : graph.getNodes()) {
             if (!graph.isConnected(n.getId())) {
                 del_nodes.add(n);
             }
        }
        for (Node n : del_nodes) graph.deleteNode(n);
        
    }
    
}
