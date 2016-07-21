package org.okbqa.rocknrole.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cunger
 */
public class Graph {
   
    List<Node> nodes;
    List<Edge> edges;
        
    public Graph() {
        nodes   = new ArrayList<>();
        edges   = new ArrayList<>();
    }
    

    // Getter and setter

    public List<Edge> getEdges() {
        return edges;
    }
    public List<Node> getNodes() {
        return nodes;
    }
      
    public Node getNode(int i) {
        
        for (Node n : nodes) {
            if (n.getId() == i) {
                return n;
            }
        }
        return null;
    }
    
    public List<Integer> findNode(String s) {
        
        List<Integer> ids = new ArrayList<>();
        
        for (Node n : nodes) {
             if (n.form.equals(s)) {
                 ids.add(n.id);
             }
        }
        
        return ids;
    }
    
    public void renameNode(int n_old, int n_new) {
        
        for (Node n : nodes) {
             if (n.getId() == n_old) {
                 n.setId(n_new);
             }
        }
        for (Edge e : edges) {
             if (e.getHead() == n_old) {
                 e.setHead(n_new);
             }
             if (e.getDependent() == n_old) {
                 e.setDependent(n_new);
             }
        }
    }
    
    public boolean isConnected(int i) {
        for (Edge e : edges) {
            if (e.head == i || e.dependent == i) return true;
        }
        return false;
    }
    
    public List<Edge> getEdges(String label) {
        
        List<Edge> results = new ArrayList<>();
        
        for (Edge e : edges) {
             if (e.getLabel().equals(label)) {
                 results.add(e);
             }
        }
        
        return results;
    }
    
    public int getMaxId() {
        
        int max = 0;
        
        for (Node n : nodes) {
            if (n.getId() > max) {
                max = n.getId();
            }
        }
        
        return max;
    }
    
    // Build graph
    
    public void addNode(Node n) {
        addNode(n,false);
    }
    public void addNode(Node n, boolean overwrite) {
                
        if (overwrite) deleteNode(getNode(n.getId()));
        if (!nodes.contains(n)) nodes.add(n);
    }
    public void addEdge(Edge e) {
        edges.add(e);
    }
        
    // Manipulate graph 
    
    public void deleteNode(Node n) {
        nodes.remove(n);
    }
    public void deleteEdge(Edge e) {
        edges.remove(e);
    }
    
    public void delete(Graph g) {
        
        for (Edge e : g.getEdges()) {
            deleteEdge(e);
        }
        
        List<Integer> ids = new ArrayList<>();
        for (Edge e : this.getEdges()) {
             ids.add(e.head);
             ids.add(e.dependent);
        }
        
        for (Node n : g.getNodes()) {
            if (!ids.contains(n.id)) {
                deleteNode(n);
            }
        }        
    }
    
    // Other manipulations 
    
    public void collapseEdges(List<Edge> edges) {
                
        for (Edge e : new HashSet<>(edges)) {
             Node head = getNode(e.getHead());
             Node dpnd = getNode(e.getDependent());
             head.setForm(dpnd.getForm() + " " + head.getForm());
             // TODO The order of forms should be determined correctly!!
             deleteNode(dpnd);
             deleteEdge(e);
             renameNode(dpnd.getId(),head.getId());
        } 
    }
    
    // Merging with another graph 
    
    public void merge(Graph other) {
        
        Map<Integer,Integer> renaming = new HashMap<>();
        
        int max = this.getMaxId() + 1;

        for (Node n : other.getNodes()) {
             int i = n.getId();
             renaming.put(i,max);
             n.setId(max);
             this.addNode(n);
             max++;
        }
        for (Edge e : other.getEdges()) {
             e.setHead(renaming.get(e.getHead()));
             e.setDependent(renaming.get(e.getDependent()));
             this.addEdge(e);
        }
        
    }
    
    // Matching
    
    public boolean containsEdge(Edge edge) {
        
        for (Edge e : edges) {
            if (e.equals(edge)) {
                return true;
            }
        }
        return false;
    }
    
    public List<Edge> findEdgesBetween(Integer i1, Integer i2) {
        
        List<Edge> edges = new ArrayList<>();
        
        for (Edge e : this.getEdges()) {
             if ((e.getHead() == i1) && e.getDependent() == i2) {
                 edges.add(e);
             }
        }
        
        return edges;
    }
    
    public boolean containsEdgeBetween(Integer i1, Integer i2) {
        
        for (Edge e : edges) {
            if (e.getHead() == i1 && e.getDependent() == i2) {
                return true;
            }
        }
        return false;
    }    
    
    public Node getMatchingNode(Node node) {
                            
        for (Node n : nodes) {
             if (n.matches(node)) {
                 return n;
             }
        }
        return null;
    }
    
    public boolean contains(Graph g) {
        
        for (Edge e_g : g.getEdges()) {
             boolean found = false;
             for (Edge e_this : edges) {
                  if (e_this.getLabel().equals(e_g.getLabel())
                   && e_this.getHead() == e_g.getHead() 
                   && e_this.getDependent() == e_g.getDependent()) {
                      found = true;
                      break;
                  }
             }
             if (!found) {
                 return false;
             }
        }
        return true;
    }
    
    public List<Pair<Graph,Map<Integer,Integer>>> subGraphMatches(Graph g) {

        List<Pair<Graph,Map<Integer,Integer>>> matches = new ArrayList<>();
        List<Edge> alreadyMatched = new ArrayList<>();

        boolean keepGoing = true;
        while  (keepGoing) { // find a matching subgraph
        
            Graph match = new Graph();
            Map<Integer,Integer> indexmap = new HashMap<>();

            for (Edge e_sub : edges) {

                int head_sub = e_sub.getHead();
                int depd_sub = e_sub.getDependent();
                Node head_sub_node = getNode(head_sub);
                Node depd_sub_node = getNode(depd_sub);

                // find corresponding edge in g
                boolean found = false;

                for (Edge e_super : g.edges) {
                    
                    if (alreadyMatched.contains(e_super)) {
                        continue;
                    }

                    int head_super = e_super.getHead();
                    int depd_super = e_super.getDependent();
                    Node head_super_node = g.getNode(head_super);
                    Node depd_super_node = g.getNode(depd_super);

                    boolean match_head = head_sub_node.matches(head_super_node);
                    if (indexmap.containsKey(head_sub) 
                    && (indexmap.get(head_sub) != head_super)) {
                            match_head = false;
                    } 
                    boolean match_depd = depd_sub_node.matches(depd_super_node);
                    if (indexmap.containsKey(depd_sub) 
                    && (indexmap.get(depd_sub) != depd_super)) {
                            match_depd = false;
                    } 
                    boolean match_label = e_sub.getLabel().equals(e_super.getLabel());

                    if (match_head && match_depd && match_label) {

                        found = true;

                        match.addEdge(e_super);
                        match.addNode(head_super_node);
                        match.addNode(depd_super_node);

                        indexmap.put(head_sub,head_super);
                        indexmap.put(depd_sub,depd_super);
                        
                        alreadyMatched.addAll(match.getEdges());

                        break;
                    }
                }
                
                if (!found) {
                    keepGoing = false;
                }
            }
            
            if (keepGoing) { // i.e. if a match was found
                matches.add(new Pair<>(match,indexmap));
            }
        }
               
        return matches;
    }
    
    
    // Copy
    
    public Graph copy() {
        
        Graph copy = new Graph();
        
        for (Node n : this.nodes) {
             copy.addNode(n.copy());
        }
        for (Edge e : this.edges) {
             copy.addEdge(e.copy());
        }
        
        return copy;
    }
    
    
    // Show 
    
    @Override   
    public String toString() {
        return toString(false);
    }

    public String toString(boolean full) {
                
        String out = "";
        
        for (Edge e : edges) {
            String head = "";
            for (Node n : nodes) { 
                 if (n.id == e.getHead()) {
                     head = n.form;
                     if (n.getPOS() != null) head += "/" + n.getPOS(); 
                     head += "-" + n.id;
                     break;
                 }
            }
            String dpnd = "";
            for (Node n : nodes) { 
                 if (n.id == e.getDependent()) {
                     dpnd = n.form;
                     if (n.getPOS() != null) dpnd += "/" + n.getPOS(); 
                     dpnd += "-" + n.id;
                     break;
                 }
            }     
            if (full) out+= e.color + ": ";
            out += e.label + "(" + head + "," + dpnd + ")";
            out += "\n"; 
        }
        
        // unconnected nodes
        for (Node n : nodes) {
            int i = n.getId();
            if (!this.isConnected(i)) {
                out += this.getNode(i).getForm() + "\n";
            }
        }
        
        return out.trim();
    }
    
}
