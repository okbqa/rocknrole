package org.okbqa.rocknrole.template;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.aggregate.AggCountVar;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author cunger
 */
public class Template {
       
    Set<String>  projvars;
    Set<String>  countvars;

    Set<Triple>  triples;
    Set<Slot>    slots;
    
    ElementGroup body;
    
    Query        query; // constructed by assemble()
    
    double       score;
        
    public Template() {
        projvars   = new HashSet<>();
        countvars  = new HashSet<>();
        triples    = new HashSet<>();
        slots      = new HashSet<>();
        body       = new ElementGroup();        
    }
    
    public Template(Query q, Set<Slot> s) {
        query = q;
        slots = s;
    }
    
    
    // Getter 
    
    public Set<String> getProjvars() {
        return projvars;
    }
    public Set<String> getCountvars() {
        return countvars;
    }
    public Set<Triple> getTriples() {
        return triples;
    }
    public ElementGroup getBody() {
        return body;
    }
    public Query getQuery() {
        return query;
    }
    public Set<Slot> getSlots() {
        return slots;
    }
    public double getScore() {
        return score;
    }
    
    // Setter 
    
    public void setScore(double s) {
        score = s;
    }
    
    public void addSlot(Slot s) {
        if (!containsSlotFor(s.getVar())) {
            slots.add(s);
        }
    }
    
    public void addTriple(Triple triple) {
        triples.add(triple);
    }
    
    public void addProjVar(String var) {
        projvars.add(var);
    }
    public void addCountVar(String var) {
        countvars.add(var);
    }
    
    
    // Tests 
    
    public boolean containsSlotFor(String var) {
        for (Slot slot : slots) {
            if (slot.getVar().equals(var)) {
                return true;
            }
        }
        return false;
    }
    
    // Removing
    
    public void removeTriple(Triple t) {
        triples.remove(t);
    }
    
    public void removeSlot(String varname) {
        for (Slot s : slots) {
            if (s.getVar().equals(varname)) {
                slots.remove(s);
                break;
            }
        }
    }
    
    // Assembly
    
    public void assemble() {  
        
        query = QueryFactory.make();
        
        Set<String> vars = new HashSet<>();
        
        // query body
        ElementGroup queryBody = new ElementGroup();
       
        for (Triple t : triples) {
            
            queryBody.addTriplePattern(t);
            
            if (t.getSubject().isVariable())   vars.add(t.getSubject().toString());
            if (t.getPredicate().isVariable()) vars.add(t.getPredicate().toString());
            if (t.getObject().isVariable())    vars.add(t.getObject().toString());
        }
        for (Element e : body.getElements()) {
            queryBody.addElement(e);
        }        
        query.setQueryPattern(queryBody);
        
        // projection variables
        for (String v : projvars) {
            query.getProject().add(Var.alloc(v));
            vars.add(v);
        }
        for (String v : countvars) {
            query.getProject().add(Var.alloc(v+"_count"),query.allocAggregate(new AggCountVar(new ExprVar(Var.alloc(v)))));
            vars.add(v);
        }

        // query type
        if (query.getProjectVars().isEmpty()) {
            query.setQueryAskType();
        }
        else {
            query.setQuerySelectType();
        }
                
        // delete slots that are not used in the query
        List<Slot> blacklisted = new ArrayList<>();
        for (Slot s : slots) {
            if (!vars.contains("?"+s.getVar())) {
                blacklisted.add(s);
            }
        }
        slots.removeAll(blacklisted);
        
        score();
    }
    
    
    // Scoring 
    
    public double score() {
        
        Set<String> nodes = new HashSet<>();
        for (Triple t : triples) {
            if (t.getSubject().isVariable())   nodes.add(t.getSubject().getName());
            if (t.getPredicate().isVariable()) nodes.add(t.getPredicate().getName());
            if (t.getObject().isVariable())    nodes.add(t.getObject().getName());
        }
        
        double numberOfKnownNodes = 0.0;
        for (String n : nodes) {
            if (projvars.contains(n)
             || countvars.contains(n)
             || containsSlotFor(n)) {
                numberOfKnownNodes++;
            }
        }
        for (Slot s : slots) {
            if (s.getForm().isEmpty() && !s.isSortal()) {
                numberOfKnownNodes--;
            }
        }
        
        double numberOfNodes = nodes.size();
  
        // compute score

        if (nodes.isEmpty()) {
            score = 0.0;
        } else {
            score = numberOfKnownNodes / numberOfNodes;
        }
        
        return score;
    }
    
    // JSON
    
    public JsonObject toJSON() {
                
        JsonObject template = new JsonObject();
        
        if (query == null) return template;
        
        template.add("query",new JsonPrimitive(sanityCheck(query.toString())));
        
        JsonArray slotlist = new JsonArray();
        for (Slot slot : slots) {
            for (JsonObject j : slot.toListofJSONObjects()) {
                 slotlist.add(j);
            }
        }
        template.add("slots",slotlist);
        template.add("score",new JsonPrimitive(Double.toString(score))); 
        
        return template;
    }
    
    private String sanityCheck(String querystring) {
        return querystring.replaceAll("\\n"," ").replaceAll("\\s+"," ");
    }
    
    // Show 
    
    @Override
    public String toString() {
        
        String out = "";
        
        if (query == null) return out;
        
        out += query.toString();
        for (Slot slot : slots) {
            out += "\n " + slot.toString();
        }
        
        return out + "\n\nScore: " + score;
    }
    
    // Clone 
    
    @Override
    public Template clone() {
                
        Set<String> new_projvars  = new HashSet<>();
        Set<String> new_countvars = new HashSet<>();
        new_projvars.addAll(projvars);
        new_countvars.addAll(countvars);

        Set<Triple> new_triples = new HashSet<>();
        for (Triple t : triples) {
            new_triples.add(new Triple(t.getSubject(),t.getPredicate(),t.getObject()));
        }
        
        Set<Slot> new_slots = new HashSet<>();
        for (Slot s : slots) {
            new_slots.add(s.clone());
        }
        
        Template clone = new Template();
        
        clone.projvars = new_projvars;
        clone.countvars = new_countvars;
        clone.triples = new_triples;
        clone.slots = new_slots;
        clone.body = body;
        clone.query = null;
        clone.score = score;
        
        return clone;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.projvars);
        hash = 29 * hash + Objects.hashCode(this.countvars);
        hash = 29 * hash + Objects.hashCode(this.triples);
        hash = 29 * hash + Objects.hashCode(this.slots);
        hash = 29 * hash + Objects.hashCode(this.body);
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
        final Template other = (Template) obj;
        if (!Objects.equals(this.projvars, other.projvars)) {
            return false;
        }
        if (!Objects.equals(this.countvars, other.countvars)) {
            return false;
        }
        if (!Objects.equals(this.triples, other.triples)) {
            return false;
        }
        if (!Objects.equals(this.slots, other.slots)) {
            return false;
        }
        if (!Objects.equals(this.body, other.body)) {
            return false;
        }
        return true;
    }


}
