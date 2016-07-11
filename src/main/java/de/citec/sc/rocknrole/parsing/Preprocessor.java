package de.citec.sc.rocknrole.parsing;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cunger
 */
public class Preprocessor {
    
    String language = "en"; // default
    
    public Preprocessor(String l) {
        language = l;
    }
    
    public String preprocess(String input) {
  
        String output = input;
        
        List<String> question_prefixes = new ArrayList<>();
        String replacement = "";
  
        switch (language) {
            
            case "en": {
                
                question_prefixes.add("give me all");
                question_prefixes.add("give me the");
                question_prefixes.add("give me");
                question_prefixes.add("list all");
                question_prefixes.add("list the");
                question_prefixes.add("list");
                question_prefixes.add("show me all");
                question_prefixes.add("show me the");
                question_prefixes.add("show me");
                
                replacement = "show";
            }
                
            case "ko": {
                
                question_prefixes.add(""); // TODO
                
                replacement = ""; // TODO
            }

        } 
        
        for (String p : question_prefixes) {
             if (output.startsWith(p)) {
                 output = output.replace(p,replacement);
             }
        }
        
//        VISUAL QA
//        List<String> redundants = new ArrayList<>();
//        redundants.add(" visible");
//        redundants.add( "any evidence of");
//        redundants.add(" evidence of");
//        redundants.add(" in this picture");
//        redundants.add(" in the picture");
//        redundants.add(" in the image");
//        redundants.add(" in this image");
//        redundants.add(" in this photo");
//        redundants.add(" in the photo");
//        redundants.add(" in this photograph");
//        redundants.add(" of this picture");
//        redundants.add(" in the scene");
//        redundants.add(" in this scene");
//        redundants.add(" shown");
//        // 
//        redundants.add(" in the foreground");
//        redundants.add(" in the background");
//        redundants.add(" in the distance");
        
        return output;
    }
}
