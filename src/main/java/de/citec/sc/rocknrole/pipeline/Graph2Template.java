package de.citec.sc.rocknrole.pipeline;

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
    
    int i = 0; // for fresh variable supply
    
    public Template constructTemplate(Graph graph) {
        
        i = 0;
        
        Template template = new Template();
        
        // projection variables 
        for (int i : graph.getRoots()) {
             template.addProjVar(varString(i));
        }
        
        // counts 
        
        // triples
        
        List<Integer> coveredNodes = new ArrayList<>();
        
        for (Edge e : graph.getEdges()) {
            
            Node s = graph.getNode(e.getHead());
            Node o = graph.getNode(e.getDependent());
            
            coveredNodes.add(s.getId());
            coveredNodes.add(o.getId());
            
            String vs = varString(e.getHead());
            String vp = varString(fresh());
            String vo = varString(e.getDependent());
            
            template.addTriple(new Triple(Var.alloc(vs),Var.alloc(vp),Var.alloc(vo)));
            
            // subject
            addTypeTriple(template,vs,s.getForm());
            
            // object
            if (!o.getForm().equals("LITERAL")) {
                addTypeTriple(template,vo,o.getForm()); 
            } 
            
            // poperty 
            if (o.getForm().equals("LITERAL")) {
                template.addSlot(new Slot(vp,e.getLabel(),SlotType.DATAPROPERTY));
            } else {
                template.addSlot(new Slot(vp,e.getLabel(),SlotType.PROPERTY));
            }
            
            // unconnected nodes
            for (Node n : graph.getNodes()) {
                if (!coveredNodes.contains(n.getId())) {
                    addTypeTriple(template,varString(n.getId()),n.getForm());
                }
            }
            
        }
        
        template.assemble();
        
        return template;
    }
    
    
    private void addTypeTriple(Template template, String s, String c) {
        
        String vc = varString(fresh());
        String vp = varString(fresh());
        template.addTriple(new Triple(Var.alloc(s),Var.alloc(vp),Var.alloc(vc)));
        template.addSlot(new Slot(vc,c,SlotType.CLASS));
        template.addSlot(new Slot(vp,"",SlotType.SORTAL));
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
