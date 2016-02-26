package de.citec.sc.rocknrole.graph;

/**
 *
 * @author cunger
 */
public class Edge {    
    
    
    public enum Color { DEP, SEM, SRL }
    
    
    Color  color;
    int    head;
    int    dependent;
    String label;    

    
    public Edge(Color c, int t1, String l, int t2) {
        color     = c;
        head      = t1;
        dependent = t2;
        label     = l;
    }

    public Edge() {
    }
    
    public void setHead(int t) {
        head = t;
    }
    public void setDependent(int t) {
        dependent = t;
    }
    public void setLabel(String s) {
        label = s;
    }
    public void setColor(Color c) {
        color = c;
    }
    
    public int getHead() {
        return head;
    }
    public int getDependent() {
        return dependent;
    }
    public String getLabel() {
        return label;
    }
    public Color getColor() {
        return color;
    }
    
    // Matching 
    
    public boolean matches(Edge e) {
        
        return (head == e.getHead() 
             && dependent == e.getDependent()
             && label.equals(e.getLabel()));
    }
    
    // Copy 
    
    public Edge copy() {
        
        return new Edge(color,head,label,dependent);
    }
    
    // Show 
    
    @Override
    public String toString() {
           return " " + head + " --"+label+"--> " + dependent;
    }
    
}
