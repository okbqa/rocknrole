package org.okbqa.rocknrole.parsing;

import java.util.ArrayList;
import org.okbqa.rocknrole.graph.*;
import org.okbqa.rocknrole.graph.interpreter.GraphReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author cunger
 */
public class ParseResult {

    Map<Integer,String> sentences;
    Map<Integer,Map<Integer,String>> tokens;
    Map<Integer,String> parses;
    Map<Integer,Map<Integer,String>> pos;
    Map<Integer,List<NamedEntity>> namedentities;
    
    GraphReader interpreter;

    
    public ParseResult() {
        parses      = new HashMap<>();
        sentences   = new HashMap<>();
        tokens      = new HashMap<>();
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
    
    public void addToken(int i, int j, String l) {
        if (!tokens.containsKey(i)) tokens.put(i,new TreeMap<>());
        tokens.get(i).put(j,l);
    }
    
    public void addPOS(int i, int j, String p) {
        if (!pos.containsKey(i)) pos.put(i,new TreeMap<>());
        pos.get(i).put(j,p);
    }
    
    public void addNE(int i, String text, int begin, int end) {
        if (!namedentities.containsKey(i)) namedentities.put(i,new ArrayList<>());
        namedentities.get(i).add(new NamedEntity(text,begin,end));
    }
    
    // Show 
    
    public String toString_withPOS() {
        
        String tagged = "";
        
        for (int i : sentences.keySet()) {
            if (tokens.containsKey(i)) {
                for (int j : tokens.get(i).keySet()) {
                    tagged += tokens.get(i).get(j); 
                    if (pos.get(i).containsKey(j)) tagged += "/" + pos.get(i).get(j); 
                    tagged += " ";
                }
            } else {
                tagged += sentences.get(i);
                tagged += " ";
            }
        }
        
        return tagged;
    }
    
    // Graph
    
    public Graph toGraph() {
        
        Graph graph = new Graph();
        
        for (int i : sentences.keySet()) {
             
            Graph g = new Graph(); 
            Map<Integer,String> ts = tokens.get(i);
            Map<Integer,String> ps = pos.get(i);
            for (int j : ts.keySet()) {
                 g.addNode(new Node(j,ts.get(j),ps.get(j)));
                 // if (ts.containsKey(j-1)) g.addEdge(new Edge(Edge.Color.LIN,j-1,"tt",j));
            }
            
            graph.merge(g);
        }
        
        return graph;
    }
    
    public Graph toDependencyGraph() {
        
        Graph graph = new Graph();

        // Merge sentence parses  
   
        for (int i : sentences.keySet()) {
             
            Graph g = interpreter.interpret(parses.get(i));
            if (pos.containsKey(i)) {
                for (Node n : g.getNodes()) { 
                    n.setPOS(pos.get(i).get(n.getId()));
                }
            }
            graph.merge(g);
        }
        
        return graph;
    }
    
}
