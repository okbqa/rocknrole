package de.citec.sc.rocknrole.pipeline;

import com.google.gson.JsonArray;
import de.citec.sc.rocknrole.graph.Graph;
import de.citec.sc.rocknrole.template.Template;
import java.util.Set;


/**
 *
 * @author cunger
 */
public class TemplatorPipeline {
        
    NL2Graph       nl2graph; 
    Graph2Template graph2template;
    // TemplateRewriting rewriter;
    
    boolean verbose = false;
    
    public TemplatorPipeline(String language) {
                
        nl2graph       = new NL2Graph(language);
        graph2template = new Graph2Template();
        //rewriter     = new TemplateRewriting();
    }
    
    public void debugMode() {
        verbose = true;
        nl2graph.debugMode();
    }
    
 
    public JsonArray run(String input) {
        
        JsonArray output = new JsonArray();
        
        // 1. Graph construction :: String -> Graph
        
        Graph g = nl2graph.constructGraph(input);
        
        // 2. Mapping :: Graph -> Template 

        Template t = graph2template.constructTemplate(g);
        output.add(t.toJSON());
        
        // 3. Template rewriting
        
        // Set<Template> variations = rewriter.rewrite(t);
    
        
        // Done.
        
        if (verbose) {
            System.out.println("\n----------Final JSON Output-----------");
            System.out.println(output.getAsString());
        }
        
        return output;
    }
    
}
