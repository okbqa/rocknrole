package org.okbqa.rocknrole.pipeline;

import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Var;
import org.okbqa.rocknrole.graph.Edge;
import org.okbqa.rocknrole.graph.Graph;
import org.okbqa.rocknrole.graph.Node;
import org.okbqa.rocknrole.template.Slot;
import org.okbqa.rocknrole.template.SlotType;
import org.okbqa.rocknrole.template.Template;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cunger
 */
public class Graph2Template {
    
    int i; // for fresh variable supply
    
    public Template constructTemplate(Graph graph) {
        
        if (graph == null) return new Template();
        
        i = graph.getMaxId()+1;
        
        Template template = new Template();
        
        // triples
        
        List<Integer> coveredNodes = new ArrayList<>();
        
        for (Edge e : graph.getEdges()) {
            
            Node s = graph.getNode(e.getHead());
            Node o = graph.getNode(e.getDependent());
            
            if (e.getLabel().equals("SELECT")) {
                Graph remainder = graph.copy();
                remainder.deleteEdge(e);
                if (remainder.isConnected(s.getId())) {
                    template.addProjVar(varString(s.getId()));
                    template.addToBlackList(varString(s.getId()));
                }
                continue;
            } 
            
            if (e.getLabel().equals("SELECT_COUNT")) {
                Graph remainder = graph.copy();
                remainder.deleteEdge(e);
                if (remainder.isConnected(s.getId())) {
                    template.addCountVar(varString(s.getId()));
                    template.addToBlackList(varString(s.getId()));
                }
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
            add(template,o); 
            
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
        
        String   form;
        SlotType type;
        
        if (!n.hasProperForm()) {
            form = "";
            type = SlotType.valueOf(n.getForm());
        } else {
            form = n.getForm();
            type = SlotType.CLASSorRESOURCE; 
        }
    
        if (n.getPOS() != null && n.getPOS().startsWith("NNP")) { 
            type = SlotType.RESOURCE;
        }

        if (n.getPOS() != null && (n.getPOS().equals("NN") || n.getPOS().equals("NNS"))) {
            String vs = varString(n.getId());
            String vc = varString(fresh());
            String vp = varString(fresh());
            template.addTriple(new Triple(Var.alloc(vs),Var.alloc(vp),Var.alloc(vc)));
            template.addSlot(new Slot(vc,form,SlotType.CLASS));
            template.addSlot(new Slot(vp,"",SlotType.SORTAL));
        }
        else {
            template.addSlot(new Slot(varString(n.getId()),form,type));
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
