package de.citec.sc.rocknrole.pipeline;

import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Var;
import de.citec.sc.rocknrole.graph.Edge;
import de.citec.sc.rocknrole.graph.Graph;
import de.citec.sc.rocknrole.graph.Node;
import de.citec.sc.rocknrole.template.Slot;
import de.citec.sc.rocknrole.template.SlotType;
import de.citec.sc.rocknrole.template.Template;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cunger
 */
public class Graph2Template {
    
    int i; // for fresh variable supply
    
    public Template constructTemplate(Graph graph) {
        
        i = graph.getMaxId()+1;
        
        Template template = new Template();
        
        // triples
        
        List<Integer> coveredNodes = new ArrayList<>();
        
        for (Edge e : graph.getEdges()) {
            
            Node s = graph.getNode(e.getHead());
            Node o = graph.getNode(e.getDependent());
            
            if (e.getLabel().equals("SELECT")) {
                template.addProjVar(varString(s.getId()));
                continue;
            } 
            
            if (e.getLabel().equals("SELECT_COUNT")) {
                template.addCountVar(varString(s.getId()));
                continue;
            } 
            
            coveredNodes.add(s.getId());
            coveredNodes.add(o.getId());
            
            String vs = varString(e.getHead());
            String vp = varString(fresh());
            String vo = varString(e.getDependent());
            
            if (e.getLabel().equals("EQUALS")) {
                template.addTriple(new Triple(Var.alloc(vs),NodeFactory.createURI("http://www.w3.org/2002/07/owl#sameAs"),Var.alloc(vo)));                
                continue;
            } 
            
            template.addTriple(new Triple(Var.alloc(vs),Var.alloc(vp),Var.alloc(vo)));
            
            // subject
            add(template,s);
            
            // object
            if (!o.getForm().equals("RESOURCE") && 
                !o.getForm().equals("LITERAL")  && 
                !o.getForm().equals("RESOURCEorLITERAL")) {
                add(template,o); 
            } 
            if (o.getForm().equals("RESOURCE")) {
                template.addSlot(new Slot(vo,"",SlotType.RESOURCE)); 
            }
            
            // poperty 
            String label = e.getLabel();
            if (label.equals("REL")) label = "";
            
            if (o.getForm().equals("LITERAL")) {
                template.addSlot(new Slot(vp,label,SlotType.DATAPROPERTY));
            } else {
                template.addSlot(new Slot(vp,label,SlotType.PROPERTY));
            }          
        }
        
        // unconnected nodes
        
        for (Node n : graph.getNodes()) {
            if (!coveredNodes.contains(n.getId())) {
                add(template,n);
            }
        }
        
        template.assemble();
        
        return template;
    }
    
    
    private void add(Template template, Node n) {
        
        if (n.getPOS() != null && n.getPOS().startsWith("NNP")) {
            template.addSlot(new Slot(varString(n.getId()),n.getForm(),SlotType.RESOURCE));
        } 
        else {        
            String vs = varString(n.getId());
            String vc = varString(fresh());
            String vp = varString(fresh());
            template.addTriple(new Triple(Var.alloc(vs),Var.alloc(vp),Var.alloc(vc)));
            template.addSlot(new Slot(vc,n.getForm(),SlotType.CLASS));
            template.addSlot(new Slot(vp,"",SlotType.SORTAL));
        }
    }
    
    
    private String varString(int i) {
        return "v" + i;
    }
    
    // Fresh variable supply

    private int fresh() {
        i++;
        return i;
    }

}
