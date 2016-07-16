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
public class RuleTransformer_en extends RuleTransformer {

        
    @Override
    public Graph transform(Graph g_in) {
        
        Graph graph = g_in.copy();

        final String who   = "who";
        final String when  = "when";
        final String where = "where";
        final String why   = "why";
        
        String[] COP  = { "be", "is", "was", "are", "were" };
        String[] DTs  = { "the", "this", "these", "those", "that" };
        String[] WDTs = { "what", "which" };
        String[] HAVE = { "have", "has", "had" };
 
        Map<String,String> normalize = new HashMap<>();
        for (String s : COP)  normalize.put(s,"BE");
        for (String s : DTs)  normalize.put(s,"THIS");
        for (String s : WDTs) normalize.put(s,"WH");
        for (String s : HAVE) normalize.put(s,"HAVE");
        
        // Renaming nodes
        
        for (Node n : graph.getNodes()) {
            if (normalize.containsKey(n.getForm().toLowerCase())) {
                n.setForm(normalize.get(n.getForm().toLowerCase()));
            }
            
            switch (n.getForm().toLowerCase()) {
                
                case who:   n.setForm("AGENT");    graph.addEdge(new Edge(Color.SRL,n.getId(),"SELECT",n.getId())); break;
                case when:  n.setForm("DATETIME"); graph.addEdge(new Edge(Color.SRL,n.getId(),"SELECT",n.getId())); break;
                case where: n.setForm("LOCATION"); graph.addEdge(new Edge(Color.SRL,n.getId(),"SELECT",n.getId())); break;
                case why:   n.setForm("REASON");   graph.addEdge(new Edge(Color.SRL,n.getId(),"SELECT",n.getId())); break;
                case "wh":  n.setForm("THING");    graph.addEdge(new Edge(Color.SRL,n.getId(),"SELECT",n.getId())); break;
            }
        }
        
        // which NN, what NN
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"det(*-1,THING-2)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Color.SRL,m.get(1),"SELECT",m.get(1)));
            graph.deleteEdge(new Edge(Color.SRL,m.get(2),"SELECT",m.get(2)));
            graph.delete(g);
        }
        
        // give me NN
        
        String[] questionPrefixes = { "iobj(give-1,me-2) \n dobj(give-1,*-3)",
                                      "iobj(show-1,me-2) \n dobj(show-1,*-3)",
                                      "iobj(give-1,me-2) \n det(list-4,a-5) \n dobj(give-1,list-4) \n prep(list-4,of-6) \n pobj(of-6,*-3)",
                                      "dobj(list-1,*-3)"
                                    };
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,questionPrefixes)) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Color.SRL,m.get(3),"SELECT",m.get(3)));
            graph.delete(g);
        }
        
        // how many NN 
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"advmod(many-1,how-2) \n MOD(*-3,many-1)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();

            graph.addEdge(new Edge(Color.SRL,m.get(3),"SELECT_COUNT",m.get(3)));
            graph.delete(g);
        }
        
        // how JJ
                        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"advmod(*-1,how-2) \n dep(BE-3,*-1) \n ARG0(BE-3,*-4)")) {
            // TODO this is probably not the only dependency structure for "how ADJ"
            
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Color.SRL,m.get(4),graph.getNode(m.get(1)).getForm().toLowerCase(),m.get(2)));
            graph.addEdge(new Edge(Color.SRL,m.get(2),"SELECT",m.get(2)));
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
