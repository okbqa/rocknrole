package pipeline;

import org.okbqa.rocknrole.graph.Graph;
import org.okbqa.rocknrole.parsing.ParseResult;
import org.okbqa.rocknrole.parsing.Parser;
import org.okbqa.rocknrole.parsing.Stanford;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.HashSet;
import org.okbqa.rocknrole.pipeline.Graph2Template;
import org.okbqa.rocknrole.template.Template;
import org.okbqa.rocknrole.transforming.Transformer;

/**
 *
 * @author cunger
 */
public class QALD6_en {

    
    public static void main(String[] args) {
        
        String language = "en";
        
        String file_in  = "src/main/resources/benchmarks/qald/qald-6-train-multilingual-raw.json";
        String file_out = "src/main/resources/benchmarks/qald/target/qald-6-train-multilingual-raw.json";
        
        Parser stanford  = new Stanford();
        JsonParser json  = new JsonParser();
        Gson       gson  = new GsonBuilder().setPrettyPrinting().create();
        
        Transformer transformer  = new Transformer(language);        
        Graph2Template templator = new Graph2Template();
                                   
        try {
            
            String input = FileUtils.readFileToString(new File(file_in));
            JsonObject doc = json.parse(input).getAsJsonObject();
                       
            JsonArray questions = doc.getAsJsonArray("questions");
            for (int n = 0; n < questions.size(); n++) {
                 JsonObject question = questions.get(n).getAsJsonObject();
                          
                 String q = "";
                 
                 JsonArray questionstrings = question.getAsJsonArray("question");
                 for (int m = 0; m < questionstrings.size(); m++) {
                     JsonObject o = questionstrings.get(m).getAsJsonObject();
                     if (o.getAsJsonPrimitive("language").getAsString().equals(language)) {
                         q = o.getAsJsonPrimitive("string").getAsString();
                         // Preprocessing: remove all characters that are special characters for the GraphReader
                         q = q.replace("/","").replace("-","_").replace("(","").replace(")","").replace(",",";").trim();
                         break;
                     }
                 }
                 
                 ParseResult parse = stanford.parse(q,new HashSet<>());
                 
                 try {
                    String   postagged = parse.toString_withPOS();
                    Graph    synGraph  = parse.toDependencyGraph();
                    Graph    semGraph  = transformer.transform(synGraph);
                    Template template  = templator.constructTemplate(semGraph);
                 
                    JsonArray annotations = new JsonArray();
                    
                    JsonObject pos_annotation = new JsonObject();
                    pos_annotation.add("type",new JsonPrimitive("POS"));
                    pos_annotation.add("value",new JsonPrimitive(postagged));
                    pos_annotation.add("creator",new JsonPrimitive("Stanford Core NLP"));
                    
                    JsonObject syn_annotation = new JsonObject();
                    syn_annotation.add("type",new JsonPrimitive("DEP"));
                    syn_annotation.add("value",new JsonPrimitive(synGraph.toString(false)));
                    syn_annotation.add("creator",new JsonPrimitive("Stanford Core NLP"));
                    
                    JsonObject sem_annotation = new JsonObject();
                    sem_annotation.add("type",new JsonPrimitive("SRL"));
                    sem_annotation.add("value",new JsonPrimitive(semGraph.toString(false)));
                    sem_annotation.add("creator",new JsonPrimitive("rocknrole"));
                    
                    JsonObject temp_annotation = new JsonObject();
                    temp_annotation.add("type",new JsonPrimitive("Template"));
                    temp_annotation.add("value",new JsonPrimitive(template.toString()));
                    temp_annotation.add("creator",new JsonPrimitive("rocknrole"));
                    
                    annotations.add(pos_annotation);
                    annotations.add(syn_annotation);
                    annotations.add(sem_annotation);
                    annotations.add(temp_annotation);
                    
                    question.add("annotations",annotations);
                 } 
                 catch (Exception e) {
                     System.out.println("[ERROR] " + q);
                     e.printStackTrace();
                 }
            }
                        
            FileUtils.writeStringToFile(new File(file_out),gson.toJson(doc).replace("\\/","/").replace("\\n"," \\n "));
            
        } catch (Exception ex) {
            Logger.getLogger(QALD6_en.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
