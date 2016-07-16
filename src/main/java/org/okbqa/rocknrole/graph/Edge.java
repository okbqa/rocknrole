package org.okbqa.rocknrole.graph;

import java.util.Objects;

/**
 *
 * @author cunger
 */
public class Edge {    
    
    
    public enum Color { SYN, SEM }
    
    
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

    // Equals
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.color);
        hash = 89 * hash + this.head;
        hash = 89 * hash + this.dependent;
        hash = 89 * hash + Objects.hashCode(this.label);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Edge other = (Edge) obj;
        if (this.color != other.color) {
            return false;
        }
        if (this.head != other.head) {
            return false;
        }
        if (this.dependent != other.dependent) {
            return false;
        }
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        return true;
    }
    
}
