package de.citec.sc.rocknrole.transforming;

import de.citec.sc.rocknrole.graph.*;
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
        
        if (verbose) System.out.println("\n-------RuleTransformer_en.java--------");
        if (verbose) System.out.println("\nInput graph:\n" + graph.toString());
        
        
        final String who   = "who";
        final String when  = "when";
        final String where = "where";
        final String why   = "why";
        
        List<String> COP = new ArrayList<>();
        COP.add("be");
        COP.add("is");
        COP.add("was");
        COP.add("are");
        COP.add("were");
        
        List<String> DTs = new ArrayList<>();
        DTs.add("the");
        DTs.add("this");
        DTs.add("these");
        
        List<String> WDTs = new ArrayList<>();
        WDTs.add("what");
        WDTs.add("which");
 
        Map<String,String> normalize = new HashMap<>();
        for (String s : COP)  normalize.put(s,"BE");
        for (String s : DTs)  normalize.put(s,"THIS");
        for (String s : WDTs) normalize.put(s,"WH");
        
        // Renaming nodes
        
        for (Node n : graph.getNodes()) {
            if (normalize.containsKey(n.getForm().toLowerCase())) {
                n.setForm(normalize.get(n.getForm().toLowerCase()));
            }
            
            switch (n.getForm().toLowerCase()) {
                
                case who:   n.setForm("AGENT"); break;
                case when:  n.setForm("DATETIME"); break;
                case where: n.setForm("LOCATION"); break;
                case why:   n.setForm("REASON"); break;
                case "wh":  n.setForm("THING");
            }
        }
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"dobj(show-1,*-2)")) {
            // Preprocessing did: (show|list|give) (me)? -> show
            // TODO maybe this should be part of a more general pruning method?
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addRoot(m.get(2));
            graph.delete(g);
        }
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"advmod(many-1,how-2) \n amod(*-3,many-1)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addRoot(m.get(3)); 
            // TODO mark this root as COUNT
            graph.delete(g);
        }
                
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"advmod(*-1,how-2) \n dep(BE-3,*-1) \n nsubj(BE-3,*-4)")) {
            // TODO this is probably not the only dependency structure for "how ADJ"
            
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SRL,m.get(4),graph.getNode(m.get(1)).getForm().toLowerCase(),m.get(2)));
            graph.addNode(new Node(m.get(2),"LITERAL"),true);
            graph.addRoot(m.get(2)); 
            graph.delete(g);
        }

        if (verbose) System.out.println("\nOutput graph:\n" + graph.toString());

        return graph;
    }
    
    private List<Pair<Graph,Map<Integer,Integer>>> getSubgraphs(Graph graph, String regex) {
               
        Graph subgraph = reader.interpret(regex);
                
        return subgraph.subGraphMatches(graph);
    }
}
