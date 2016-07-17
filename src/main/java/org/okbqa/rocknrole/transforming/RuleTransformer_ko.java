package org.okbqa.rocknrole.transforming;

import org.okbqa.rocknrole.graph.Edge;
import org.okbqa.rocknrole.graph.Node;
import org.okbqa.rocknrole.graph.Pair;
import org.okbqa.rocknrole.graph.Graph;
import org.okbqa.rocknrole.graph.Edge.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cunger
 */
public class RuleTransformer_ko extends RuleTransformer {

        
    @Override
    public Graph transform(Graph g_in) {
        
        Graph graph = g_in.copy();

        final String who  = "누구";
        final String when = "언제";
        final String why   = "왜";
        final String where = "어디";
        
        String[] WHO   = { "누구", "누가", "누굴" };
        String[] WHERE = { "어디", "어디에", "어디에서", "어디서" };
        String[] DTs   = { "이", "그", "저" }; // TODO '저' can also be 'I', in which case it should not be normalized to DT
        String[] WDTs  = { "뭐", "뭘", "무얼", "어느", "무엇", "무슨", "어떤" };
        String[] HAVE  = { "있" }; // ETRI parser already separates stem and endings
 
        Map<String,String> normalize = new HashMap<>();
        for (String s : WHO)   normalize.put(s,"누구");
        for (String s : WHERE) normalize.put(s,"어디");
        for (String s : DTs)   normalize.put(s,"THIS");
        for (String s : WDTs)  normalize.put(s,"WH");
        for (String s : HAVE)  normalize.put(s,"HAVE");
        
        // Renaming nodes
        
        for (Node n : graph.getNodes()) {
            if (normalize.containsKey(n.getForm().toLowerCase())) {
                n.setForm(normalize.get(n.getForm().toLowerCase()));
            }
            
            switch (n.getForm().toLowerCase()) {
                
                case who:   n.setForm("AGENT");    graph.addEdge(new Edge(Color.SEM,n.getId(),"SELECT",n.getId())); break;
                case when:  n.setForm("DATETIME"); graph.addEdge(new Edge(Color.SEM,n.getId(),"SELECT",n.getId())); break;
                case where: n.setForm("LOCATION"); graph.addEdge(new Edge(Color.SEM,n.getId(),"SELECT",n.getId())); break;
                case why:   n.setForm("REASON");   graph.addEdge(new Edge(Color.SEM,n.getId(),"SELECT",n.getId())); break;
                case "wh":  n.setForm("THING");    graph.addEdge(new Edge(Color.SEM,n.getId(),"SELECT",n.getId())); break;
            }
        }
                        
        // which NN, what NN
        
        // String[] whichNN = { "DP(*-1,THING-2)", "ARG0(*-1,THING-2)", "ARG0(THING-2,*-1)" };
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"DP(*-1,THING-2)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Color.SEM,m.get(1),"SELECT",m.get(1)));
            graph.deleteEdge(new Edge(Color.SEM,m.get(2),"SELECT",m.get(2)));
            graph.delete(g);
        }
        
        // give me, show me, list all NN
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"ARG1(나열-1,*-2)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Color.SEM,m.get(2),"SELECT",m.get(2)));
            graph.delete(g);
        }
        
        // how many NN 
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"AP(많-1,얼마나-2) \n ARG1(*-3,많-1)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();

            graph.addEdge(new Edge(Color.SEM,m.get(3),"SELECT_COUNT",m.get(3)));
            graph.delete(g);
        }
              
        // how JJ
                
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"AP(*-1,얼마나-2) \n ARG0(*-1,*-3)")) {
            
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Color.SEM,m.get(3),graph.getNode(m.get(1)).getForm().toLowerCase(),m.get(2)));
            graph.addEdge(new Edge(Color.SEM,m.get(2),"SELECT",m.get(2)));
            graph.addNode(new Node(m.get(2),"LITERAL"),true);
            graph.delete(g);
        }
        
        // passive 
        
        // TODO
                
        // done
        
        return graph;
    }
    
    private List<Pair<Graph,Map<Integer,Integer>>> getSubgraphs(Graph graph, String regex) {
               
        Graph subgraph = reader.interpret(regex);
                
        return subgraph.subGraphMatches(graph);
    }
    
    private List<Pair<Graph,Map<Integer,Integer>>> getSubgraphs(Graph graph, String[] regexes) {
        
        List<Pair<Graph,Map<Integer,Integer>>> subgraphs = new ArrayList<>();
        
        for (String regex : regexes) {
            subgraphs.addAll(getSubgraphs(graph,regex));
        }
        
        return subgraphs;
    }
}
