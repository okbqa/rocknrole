package de.citec.sc.rocknrole.template;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;

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
    
    public Slot clone() {       
        return new Slot(var,form,type);
    }

}
