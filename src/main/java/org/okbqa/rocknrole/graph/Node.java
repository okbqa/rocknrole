package org.okbqa.rocknrole.graph;

import org.okbqa.rocknrole.template.SlotType;
import java.util.Objects;

/**
 *
 * @author cunger
 */
public class Node {

    int id;
    
    String form;
    String pos;
        
    
    public Node(int i, String f) {
        this(i, f, null);
    }
    
    public Node(int i, String f, String p) {
        id    = i;
        form  = f;
        pos   = p;
    }
    
    
    // Getters
    
    public int getId() {
        return id;
    }

    public String getForm() {
        return form.replaceAll("\\?","");
    }

    public String getPOS() {
        return pos;   
    }
     
    // Setter 
    
    public void setId(int i) {
        id = i;
    }
    
    public void setForm(String f) {
        form = f;
    }
    
    public void setPOS(String p) {
        pos = p;
    }
    
    // Checks 
    
    public boolean hasProperForm() {
        for (SlotType t : SlotType.values()) {
            if (t.name().equals(form)) {
                return false;
            }
        }
        return true;
    }
    
    // Matching 
    
    public boolean matches(Node n) {
                                    
        if (getPOS() != null && n.getPOS() != null && !getPOS().equals(n.getPOS())) {
            return false;
        }
        if (getForm().equals("*") || n.getForm().equals("*")) {
            return true;
        }
        return (getForm().toLowerCase().equals(n.getForm().toLowerCase()));
    }

    // Equality (automatically generated)
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.id;
        hash = 37 * hash + Objects.hashCode(this.form);
        hash = 37 * hash + Objects.hashCode(this.pos);
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
        final Node other = (Node) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.form, other.form)) {
            return false;
        }
        if (!Objects.equals(this.pos, other.pos)) {
            return false;
        }
        return true;
    }
    
    
    // Copy 
    
    public Node copy() {
        
        return new Node(id,form,pos);
    }
    
    // Show 
    
    @Override
    public String toString() {
        
        String out = id + "-" + form;
        
        if (pos != null) {
            out += ":" + pos;
        }
        
        return out;
    }
    
}
