package de.citec.sc.rocknrole.template;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author cunger
 */
public class Slot {
    
    String var;
    String form;
    SlotType type;
    
    public Slot(String var,String f,SlotType t) {
        this.var   = var;
        this.form  = f;
        this.type  = t;
    }
    
    // Boolean 
    
    public boolean isSortal() {
        return type.equals(SlotType.SORTAL);
    }
    
    // Getter 
    
    public String getVar() {
        return var;
    }
    public String getForm() {
        return form;
    }
    public SlotType getType() {
        return type;
    }
    
    // Setter 
    
    public void setType(SlotType t) {
        type = t;
    }
    public void setForm(String f) {
        form = f;
    }
    
    // JSON
      
    public List<JsonObject> toListofJSONObjects() {
        
        List<JsonObject> triples = new ArrayList<>();
        
        // type
        JsonObject t = new JsonObject();
        t.add("s",new JsonPrimitive(var));
        t.add("p",new JsonPrimitive("is"));
        t.add("o",new JsonPrimitive(type.toString()));
        triples.add(t);
        // form
        if (!form.isEmpty()) {
            JsonObject f = new JsonObject();
            f.add("s",new JsonPrimitive(var));
            f.add("p",new JsonPrimitive("verbalization"));
            f.add("o",new JsonPrimitive(form)); 
            triples.add(f);
        }
        
        return triples;
    }
    
    // Show 
    
    @Override
    public String toString() {
        String out = var + " ";
        if (form.isEmpty()) {
            out += "-";
        } else {
            out += form;
        }
        out += " (" + type + ")";
        return out;
    }
    
    // Clone 
    
    public Slot clone() {       
        return new Slot(var,form,type);
    }

    // Equals 

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.var);
        hash = 83 * hash + Objects.hashCode(this.form);
        hash = 83 * hash + Objects.hashCode(this.type);
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
        final Slot other = (Slot) obj;
        if (!Objects.equals(this.var, other.var)) {
            return false;
        }
        if (!Objects.equals(this.form, other.form)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }
    
}
