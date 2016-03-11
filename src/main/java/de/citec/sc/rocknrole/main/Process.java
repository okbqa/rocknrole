package de.citec.sc.rocknrole.main;

import de.citec.sc.rocknrole.graph.Graph;
import de.citec.sc.rocknrole.parsing.ParseResult;
import de.citec.sc.rocknrole.parsing.Parser;
import de.citec.sc.rocknrole.parsing.Stanford;
import de.citec.sc.rocknrole.transforming.RuleTransformerSem;
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
import de.citec.sc.rocknrole.parsing.Preprocessor;
import de.citec.sc.rocknrole.transforming.RuleTransformerSRL;
import de.citec.sc.rocknrole.transforming.RuleTransformerPOS;

/**
 *
 * @author cunger
 */
public class Process {

    
    public static void main(String[] args) {
        
        String file_in  = "src/main/resources/visualqa/mscoco_2014_train_ISTHERE.json";
        String file_out = "src/main/resources/visualqa/target/mscoco_2014_train_ISTHERE.json";
        
        Preprocessor pre = new Preprocessor();
        Parser stanford  = new Stanford();
        JsonParser json  = new JsonParser();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        Transformer transformer = new RuleTransformerPOS();
                                   
        try {
            
            String input = FileUtils.readFileToString(new File(file_in));
            JsonObject doc = json.parse(input).getAsJsonObject();
                       
            JsonArray questions = doc.getAsJsonArray("questions");
            for (int n = 0; n < questions.size(); n++) {
                 JsonObject question = questions.get(n).getAsJsonObject();
                                
                 String q = question.getAsJsonPrimitive("question").getAsString();
                 q = pre.preprocess(q.replace("-","_"));
                
                 ParseResult parse = stanford.parse(q);
                 
                 try {
                    String postagged = parse.toString_withPOS();
                    Graph semGraph = transformer.transform(parse.toGraph());
                 
                    question.addProperty("parse",postagged);
                    question.addProperty("graph",semGraph.toString(false));
                 } 
                 catch (Exception e) {
                     System.out.println("[ERROR] " + q);
                     e.printStackTrace();
                 }
            }
                        
            FileUtils.writeStringToFile(new File(file_out),gson.toJson(doc).replace("\\/","/").replace("\\n"," \\n "));
            
        } catch (Exception ex) {
            Logger.getLogger(Process.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
