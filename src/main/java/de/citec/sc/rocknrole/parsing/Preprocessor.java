package de.citec.sc.rocknrole.parsing;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cunger
 */
public class Preprocessor {
    
    
    public Preprocessor() {
    }
    
    // VisualQA- and English-specific 
    public String preprocess(String input) {
        
        List<String> redundants = new ArrayList<>();
        redundants.add(" visible");
        redundants.add( "any evidence of");
        redundants.add(" evidence of");
        redundants.add(" in this picture");
        redundants.add(" in the picture");
        redundants.add(" in the image");
        redundants.add(" in this image");
        redundants.add(" in this photo");
        redundants.add(" in the photo");
        redundants.add(" in this photograph");
        redundants.add(" of this picture");
        redundants.add(" in the scene");
        redundants.add(" in this scene");
        redundants.add(" shown");
        // 
        redundants.add(" in the foreground");
        redundants.add(" in the background");
        redundants.add(" in the distance");
        
        String output = input;
        
        for (String r : redundants) {
             output = output.replace(r,"");
        }    
        
        return output;
    }
}
