package org.okbqa.rocknrole.pipeline;

import com.google.gson.JsonArray;
import org.okbqa.rocknrole.graph.Graph;
import org.okbqa.rocknrole.template.Template;


/**
 *
 * @author cunger
 */
public class TemplatorPipeline {
        
    NL2Graph       nl2graph; 
    Graph2Template graph2template;
        
    boolean verbose = false;
    
    public TemplatorPipeline(String language) {
                
        nl2graph       = new NL2Graph(language);
        graph2template = new Graph2Template();
    }
    
    public void debugMode() {
        verbose = true;
        nl2graph.debugMode();
    }
    
 
    public JsonArray run(String input) {
        
        // Preprocessing: remove all characters that are special characters for the GraphReader
        input = input.replace("/","").replace("-","_").replace("(","").replace(")","").replace(",",";").trim();
                
        JsonArray output = new JsonArray();
        
        // 1. Graph construction :: String -> Graph
        
        Graph g = nl2graph.constructGraph(input);
        
        // 2. Mapping :: Graph -> Template 

        Template t = graph2template.constructTemplate(g);
        output.add(t.toJSON());
        
        if (verbose) {
            System.out.println("\n----------Template-----------\n");
            System.out.println(t.toString());
        }
        
        // Done.
        
        if (verbose) {
            System.out.println("\n----------Final JSON Output-----------\n");
            System.out.println(output.toString());
        }
        
        return output;
    }
    
}
