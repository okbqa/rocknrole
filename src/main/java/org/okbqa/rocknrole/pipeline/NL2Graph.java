package org.okbqa.rocknrole.pipeline;

import com.google.gson.JsonParser;
import org.okbqa.rocknrole.graph.Graph;
import org.okbqa.rocknrole.parsing.ETRI;
import org.okbqa.rocknrole.parsing.ParseResult;
import org.okbqa.rocknrole.parsing.Parser;
import org.okbqa.rocknrole.parsing.Stanford;
import org.okbqa.rocknrole.transforming.RuleTransformerPipeline;
import org.okbqa.rocknrole.transforming.Transformer;

/**
 *
 * @author cunger
 */
public class NL2Graph {
    
    
    String language;
    
    Parser parser;
    Transformer transformer;
    
    JsonParser json;
    
    boolean verbose = false;
    

    public NL2Graph(String l) {
        
        language = l;
                
        switch (language) {
            case "en": parser = new Stanford(); break;
            case "ko": parser = new ETRI(); break;
        }
        
        transformer = new RuleTransformerPipeline();
        transformer.setLanguage(language);
        transformer.setVerbose(verbose);
        
        json  = new JsonParser();
        
    }
    
    public void debugMode() {
        verbose = true;
        transformer.setVerbose(true);
    }
    
    public Graph constructGraph(String input) {
        
        if (verbose) {
            System.out.println("\n------ INPUT ------\n");
            System.out.println(input);
        }
        
        ParseResult parse = parser.parse(input);
                 
        try {
            String postagged = parse.toString_withPOS();
            Graph  synGraph  = parse.toDependencyGraph();
            Graph  semGraph  = transformer.transform(synGraph);
            
            if (verbose) {
                System.out.println("\n------ POS ------\n");
                System.out.println(postagged);
                System.out.println("\n------ SYN ------\n");
                System.out.println(synGraph.toString(true));
                System.out.println("\n------ SEM ------\n");
                System.out.println(semGraph.toString(true));
            }
            
            return semGraph;
            
        } catch (Exception e) {
            
            return null;
        }
    }
    
}
