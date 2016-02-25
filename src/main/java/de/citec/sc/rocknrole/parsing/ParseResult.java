package de.citec.sc.rocknrole.parsing;

import de.citec.sc.rocknrole.graph.Graph;
import de.citec.sc.rocknrole.graph.Node;
import de.citec.sc.rocknrole.graph.interpreter.GraphReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cunger
 */
public class ParseResult {

    Map<Integer,String> sentences;
    Map<Integer,String> parses;
    Map<Integer,Map<Integer,String>> pos;
    
    GraphReader interpreter;

    
    public ParseResult() {
        parses      = new HashMap<>();
        sentences   = new HashMap<>();
        pos         = new HashMap<>();
        interpreter = new GraphReader();
    }
    
    public Map<Integer,String> getParses() {
        return parses;
    }

    public void addSentence(int i, String s) {
        sentences.put(i,s);
    }
    public void addParse(int i, String s) {
        parses.put(i,s);
    }
    public void addPOS(int i, int j, String p) {
        if (!pos.containsKey(i)) pos.put(i,new HashMap<Integer,String>());
        pos.get(i).put(j,p);
    }
    
    public Graph toGraph() {
        
        Graph graph = new Graph();

        // Merge sentence parses  
   
        for (int i : sentences.keySet()) {
             
            Graph g = interpreter.interpret(parses.get(i));
            for (Node n : g.getNodes()) n.setPOS(pos.get(i).get(n.getId()));
            graph.merge(g);
        }
        
        return graph;
    }
    
}
