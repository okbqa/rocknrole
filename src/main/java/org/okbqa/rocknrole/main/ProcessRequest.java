package org.okbqa.rocknrole.main;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.HashSet;
import java.util.Set;
import org.okbqa.rocknrole.graph.Pair;
import org.okbqa.rocknrole.pipeline.TemplatorPipeline;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

/**
 *
 * @author cunger
 */
public class ProcessRequest extends ServerResource {
    
    boolean oldInterface = true;
    
    public TemplatorPipeline pipeline_en;
    public TemplatorPipeline pipeline_ko;
            
    public ProcessRequest() {
        
        pipeline_en = new TemplatorPipeline("en");
        pipeline_ko = new TemplatorPipeline("ko");
    }

    @Post
    public String process(Representation entity) throws Exception {
          
        JsonParser json  = new JsonParser();
        JsonArray output = new JsonArray();
        
        try {
            JsonObject input = json.parse(entity.getText()).getAsJsonObject();
                    
            String str  = input.get("string").getAsString();
            String lang = input.get("language").getAsString();
            
            Set<Pair<Integer,Integer>> entities = new HashSet<>();
            if (!input.has("entities")) {
                oldInterface = true;
            }
            else {
                oldInterface = false;
                JsonArray nes = input.getAsJsonArray("entities");
                for (JsonElement ne : nes) {
                     int start = ne.getAsJsonObject().get("offset_start").getAsInt();
                     int end   = ne.getAsJsonObject().get("offset_end").getAsInt();
                     entities.add(new Pair(start,end));
                }
            }

            if (!oldInterface) entities = null;
            switch (lang) {
                case "en": output = pipeline_en.run(str,entities); break;
                case "ko": output = pipeline_ko.run(str,entities); break;
                default: throw new IllegalArgumentException("Unknown language: " + lang + " (currently supported: en, ko)");
            }
            
            if (output == null) {
                // TODO send a warning 
                output = new JsonArray();
            }
            
//            input.add("templates",output);
//            
//            if (oldInterface) return output.toString();
//            else return input.toString();
            
            return output.toString();
                  
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    
        if (oldInterface) return "[]"; 
        else return entity.getText();
    }
    
}
