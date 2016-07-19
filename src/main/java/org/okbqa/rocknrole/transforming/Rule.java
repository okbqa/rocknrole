package org.okbqa.rocknrole.transforming;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.okbqa.rocknrole.graph.*;
import org.okbqa.rocknrole.graph.Edge.Color;
import org.okbqa.rocknrole.graph.interpreter.GraphReader;

/**
 *
 * @author cunger
 */
public class Rule {
    
    public enum Mode { RENAME_NODE, RENAME_EDGE, TRANSFORM };
    
    Mode mode; 
            
    List<String> matches;
    List<String> actions;
    
    Map<String,Integer> assignments;
    
    boolean deleteMatch;
    boolean wasMatched;
    
    GraphReader reader;
    
    // regexes 
    String label = "[a-zA-Z0-9\\u0080-\\uFFFE'.?!*#&$%_^:]+";
    
    
    public Rule() {
        
        matches = new ArrayList<>();
        actions = new ArrayList<>();
        
        assignments = new HashMap<>();
        
        reader = new GraphReader();
    }
    
    
    public void setMode(Mode m) {
        
        mode = m;
        
        resetDeleteMatch();
    }
    
    public void resetDeleteMatch() {
        
        switch (mode) {
            case TRANSFORM: deleteMatch = true; break;
            default:        deleteMatch = false;
        }
    }
    
    public void addMatch(String s) { 
        matches.add(s);
    }
    
    public void addAction(String s) {
        actions.add(s);
    } 
    
    public void apply(Graph graph) {
        
        wasMatched = false;
        
        switch (mode) {
            
            case RENAME_NODE: {
            
                if (actions.isEmpty()) break;
                
                String new_form = actions.get(0);
                for (Node n : graph.getNodes()) {
                for (String m : matches) {
                    if (n.getForm().toLowerCase().equals(m)) {
                        n.setForm(new_form);
                        wasMatched = true;
                    }
                }}
                break;
            }
            
            case RENAME_EDGE: {
                
                if (actions.isEmpty()) break;
                
                String new_label = actions.get(0);
                for (Edge e : graph.getEdges()) {
                for (String m : matches) {
                    if (e.getLabel().equals(m)) {
                        e.setLabel(new_label);
                        wasMatched = true;
                    }
                }}
                break;
            }
            
            case TRANSFORM: {
                
                for (String match : matches) {
                
                // If it is a node...
                if (match.matches("("+label+")\\s*(/"+label+")?\\s*\\-\\s*("+label+")")) {
                
                    String  regex   = "("+label+")\\s*(/"+label+")?\\s*\\-\\s*("+label+")";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(match);

                    while (matcher.find()) {

                        String f = matcher.group(1);
                        String p = matcher.group(2);
                        String i = matcher.group(3);
                        int i_id = getId(graph,i);

                        Node node = new Node(i_id,f);
                        if (p != null) node.setPOS(p);

                        for (Node n : graph.getNodes()) {
                            if (n.matches(node)) {
                                Map<Integer,Integer> m = new HashMap<>();
                                m.put(i_id,n.getId());
                                wasMatched = true;
                                for (String action : actions) {
                                      applyAction(graph,m,action);
                                }
                            }
                        }
                    }
                }
                
                // If it is an edge...
                else {
                    for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,match)) {

                        Graph g = subgraph.getLeft();
                        Map<Integer,Integer> m = subgraph.getRight();

                        wasMatched = true;

                        for (String action : actions) {
                             applyAction(graph,m,action);
                        }

                        if (deleteMatch) graph.delete(g);
                    }
                }
                }
            }
        }
        
    }
    
    
    private void applyAction(Graph graph, Map<Integer,Integer> m, String action) {
                
        
        String  regex; 
        Pattern pattern;
        Matcher matcher;
        
        // Declaring a variable
        // v = 1
        // ?x2 = new
        
        regex   = "("+label+")\\s*=\\s*("+label+")";
        pattern = Pattern.compile(regex); 
        matcher = pattern.matcher(action);
        
        while (matcher.find()) {
            
            String v = matcher.group(1);
            String i = matcher.group(2);
            
            int i_id = getId(graph,i);
            
            if (i_id != 0) {
                assignments.put(v,i_id);
            }
        }
        
        // Adding and removing an edge
        // + LABEL(1,2)
        // - LABEL(1,2)
        
        regex   = "(\\+|-)\\s*("+label+")\\s*\\(("+label+")\\s*,\\s*("+label+")\\s*\\)";
        pattern = Pattern.compile(regex); 
        matcher = pattern.matcher(action);
        
        while (matcher.find()) {
            
            String op = matcher.group(1);
            String l  = matcher.group(2);
            String h  = matcher.group(3);
            String d  = matcher.group(4);
            
            if (isId(l)) l = graph.getNode(m.get(Integer.valueOf(l))).getForm();
            
            int h_id = getId(graph,h);
            int d_id = getId(graph,d);
            
            if (h_id != 0 && d_id != 0) {
                
                if (m.containsKey(h_id)) h_id = m.get(h_id);
                if (m.containsKey(d_id)) d_id = m.get(d_id);
                Edge edge = new Edge(Color.SEM,h_id,l,d_id);
                            
                if (op.equals("+")) graph.addEdge(edge);
                if (op.equals("-")) graph.deleteEdge(edge);
            }
        }
        
        // Adding and removing a node
        // + FORM-1
        // + FORM/POS-1
        // - FORM-1
        // - FORM/POS-1
        
        regex   = "(\\+|-)\\s*("+label+")\\s*(/"+label+")?\\s*\\-\\s*("+label+")";
        pattern = Pattern.compile(regex); 
        matcher = pattern.matcher(action);
        
        while (matcher.find()) {
            
            String op = matcher.group(1);
            String f  = matcher.group(2);
            String p  = matcher.group(3);
            String i  = matcher.group(4);
            
            int i_id = getId(graph,i);
            
            if (i_id != 0) {
                
                if (m.containsKey(i_id)) i_id = m.get(i_id);
                Node node = new Node(i_id,f);
                if (p != null) node.setPOS(p);

                if (op.equals("+")) graph.addNode(node,true);
                if (op.equals("-")) graph.deleteNode(node);
            }
        }
        
        // NOT deleting matches 
        
        if (action.trim().startsWith("NOT")) {
            deleteMatch = false;
        }
                
    }
    
    
    private boolean isId(String s) {
        return s.matches("\\d+");
    }
    
    private int getId(Graph graph, String s) {
        
        int i = 0;
        
        if (s.equals("new")) {
            i = graph.getMaxId()+1;
        } 
        else if (isId(s)) {
            i = Integer.valueOf(s);
        } 
        else if (assignments.containsKey(s)) {
            i = assignments.get(s);
        }
        
        return i;
    }
    
    
    private List<Pair<Graph,Map<Integer,Integer>>> getSubgraphs(Graph graph, String regex) {
               
        Graph subgraph = reader.interpret(regex);
                
        return subgraph.subGraphMatches(graph);
    }

}
