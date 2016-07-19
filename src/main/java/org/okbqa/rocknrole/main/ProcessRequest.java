package org.okbqa.rocknrole.main;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.okbqa.rocknrole.pipeline.TemplatorPipeline;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

/**
 *
 * @author cunger
 */
public class ProcessRequest extends ServerResource {
    
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
            JsonObject input = (JsonObject) json.parse(entity.getText());
                    
            String str  = (String) input.get("string").getAsString();
            String lang = (String) input.get("language").getAsString();
                             
            switch (lang) {
                case "en": output = pipeline_en.run(str); break;
                case "ko": output = pipeline_ko.run(str); break;
                default: throw new IllegalArgumentException("Unknown language: " + lang + " (currently supported: en, ko)");
            }
                  
            if (output == null) {
                // TODO send a warning 
                output = new JsonArray();
            }
            
            input.add("templates",output);
            return input.toString();
                  
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    
        return entity.getText();
    }
    
}
