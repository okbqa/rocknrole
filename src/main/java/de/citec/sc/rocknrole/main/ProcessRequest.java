package de.citec.sc.rocknrole.main;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.citec.sc.rocknrole.pipeline.TemplatorPipeline;
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
          
        JsonArray output = new JsonArray();
        
        try {
            JsonParser json  = new JsonParser();
            JsonObject input = (JsonObject) json.parse(entity.getText());
                    
            String str  = (String) input.get("string").getAsString();
            String lang = (String) input.get("language").getAsString();
                             
            switch (lang) {
                case "en": output = pipeline_en.run(str); break;
                case "ko": output = pipeline_ko.run(str); break;
                default: throw new IllegalArgumentException("Unknown language: " + lang + " (currently supported: en, ko)");
            }
                  
            if (output == null) {
                throw new Exception("Oops, something went wrong...");
            }
                  
        } catch (Exception ex) {
        }
    
        return output.getAsString();
    }
    
}
