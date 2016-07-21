package org.okbqa.rocknrole.transforming;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.okbqa.rocknrole.graph.Graph;

import java.nio.file.Paths;
import org.okbqa.rocknrole.transforming.Rule.Mode;

/**
 *
 * @author cunger
 */
public class RuleApplication {
    
    
    String language;
    boolean verbose;
    
    List<Rule> rules;
    
    Mode mode;
    Rule rule;
   
    // stages: 0 = init and renaming rules, 1 = reading matches, 2 = reading actions
    int stage_last;
    int stage_current;
    
    
    public RuleApplication(String l) {
        
        language = l;
        verbose = false;
        
        rules = new ArrayList<>();
        
        stage_last = 0;
        stage_current = 0;
    }
    
    public void debugMode() {
        verbose = true;
    }
    
    
    public void init() throws URISyntaxException, UnsupportedEncodingException, IOException {
        
        String path = "rules/";
        
        URL url = this.getClass().getClassLoader().getResource(path+language+".config");
        File file = Paths.get(url.toURI()).toFile();
        
        FileInputStream stream = new FileInputStream(file);
 	BufferedReader  reader = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
 
	String line;
	while((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            System.out.print("\nLoading " + line + "...");
            readFile(path+line);
        }
        
        reader.close();
    }

    public void readFile(String path) throws FileNotFoundException, URISyntaxException, UnsupportedEncodingException, IOException {
                
        int numberOfRules = rules.size();
        
        URL url = this.getClass().getClassLoader().getResource(path);
        File file = Paths.get(url.toURI()).toFile();
        
        FileInputStream stream = new FileInputStream(file);
 	BufferedReader  reader = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
 
	String line;
	while((line = reader.readLine()) != null) {
		
           line = line.trim();
           
           if (line.isEmpty()) continue;
                                  
           if (line.startsWith("##")) {
               if (line.contains("RENAME NODE")) {
                   mode = Mode.RENAME_NODE;
                   continue;
               }
               if (line.contains("RENAME EDGE")) {
                   mode = Mode.RENAME_EDGE;
                   continue;
               }
               if (line.contains("TRANSFORM")) {
                   mode = Mode.TRANSFORM;
                   continue;
               }
           } 
           
           if (mode == null) continue;

           if (mode == Mode.RENAME_NODE || mode == Mode.RENAME_EDGE) {

               if (rule != null && !rules.contains(rule)) rules.add(rule);

               stage_current = 0;
               
               rule = new Rule();
               rule.setMode(mode);
               rule.resetDeleteMatch();
                              
               String left  = line.split("<-")[0].trim();
               String right = line.split("<-")[1].trim();
               
               rule.addAction(left);
               
               if (right.contains(",")) {
                   String[] matches = right.split(",");
                   for (String m : matches) {
                       rule.addMatch(m.trim());
                   }
               } 
               else {
                   rule.addMatch(right);
               }
               
               rules.add(rule);
               continue;
           }
           
           if (mode == Mode.TRANSFORM) {
               
               if (line.startsWith("#")) {
                   
                   stage_current = 1;
                   
                   if (stage_last != stage_current) {
                       rules.add(rule); 
                       rule = new Rule();
                       rule.setMode(mode);
                       rule.resetDeleteMatch();
                   }
                   
                   rule.addMatch(line.replace("#","").replace("\\n","\n").trim());
                   
                   stage_last = stage_current;
               }
               
               else {
                   
                   stage_current = 2;
                   
                   rule.addAction(line);
                   
                   stage_last = stage_current;
               }
           }
	}
        
        if (!rules.contains(rule)) rules.add(rule);
 
	reader.close();
        
        System.out.print(rules.size()-numberOfRules + " rules.");
    }
    
    
    public void execute(Graph graph) {
        
        if (verbose) System.out.println("\n========== RULES ===========");
        
        for (Rule r : rules) {

            r.apply(graph);
            
            if (verbose && r.wasMatched) {
                System.out.println("-------------");
                System.out.println("Matches:\n" + r.matches.toString() 
                               + "\nActions:\n" + r.actions.toString());
                System.out.println("\nResult:\n" + graph.toString(true));
            }
        }
        
        if (verbose) System.out.println("============================");
    }
    
    
}
