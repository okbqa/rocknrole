package de.citec.sc.rocknrole.main;

import de.citec.sc.rocknrole.graph.Graph;
import de.citec.sc.rocknrole.parsing.ParseResult;
import de.citec.sc.rocknrole.parsing.Parser;
import de.citec.sc.rocknrole.parsing.Stanford;
import de.citec.sc.rocknrole.transforming.RuleTransformer;
import de.citec.sc.rocknrole.transforming.Transformer;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 *
 * @author cunger
 */
public class Process {

    
    public static void main(String[] args) {
        
        String file_in  = "src/main/resources/visualqa/OpenEnded_mscoco_train2014_questions.json";
        String file_out = "src/main/resources/visualqa/OpenEnded_mscoco_train2014_questions_parsed.json";
        
        Parser stanford = new Stanford();
        JsonParser json = new JsonParser();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        Transformer transformer = new RuleTransformer();
                            
        int i = 0;
        
        try {
            
            String input = FileUtils.readFileToString(new File(file_in));
            JsonObject doc = json.parse(input).getAsJsonObject();
                       
            JsonArray questions = doc.getAsJsonArray("questions");
            for (int n = 0; n < questions.size(); n++) {
                JsonObject question = questions.get(n).getAsJsonObject();
                
                if (i > 100) break;
                i++;
                                
                 String q = question.getAsJsonPrimitive("question").getAsString();
                 q = q.replace("-","_");
                
                 ParseResult parse = stanford.parse(q);
                 
                 try {
                    Graph depGraph = parse.toGraph();
                    Graph semGraph = transformer.transform(depGraph);
                 
                    question.addProperty("parse",depGraph.toString());
                    question.addProperty("graph",semGraph.toString());
                 } 
                 catch (Exception e) {
                 }
            }
                        
            FileUtils.writeStringToFile(new File(file_out),gson.toJson(doc).replace("\\/","/").replace("\\n"," \\n "));
            
        } catch (Exception ex) {
            Logger.getLogger(Process.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
