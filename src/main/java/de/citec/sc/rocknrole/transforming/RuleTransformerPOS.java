package de.citec.sc.rocknrole.transforming;

import de.citec.sc.rocknrole.graph.*;
import de.citec.sc.rocknrole.graph.interpreter.GraphReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 *
 * @author cunger
 */
public class RuleTransformerPOS implements Transformer {
    
    boolean verbose = false;
    
    GraphReader reader = new GraphReader();
        
    
    @Override
    public Graph transform(Graph sentenceGraph) {

        if (verbose) System.out.println("\nSentence graph:\n" + sentenceGraph.toString());
        
        Graph graph = sentenceGraph.copy();

        Graph con_graph = new Graph();        
        Graph sem_graph = new Graph();

                
        // 1. Condense graph 
        
        
        // Normalize POS tags 
        
        for (Node n : graph.getNodes()) {
            if (n.getPOS() != null) {
                if (n.getPOS().equals("PRP$"))   n.setPOS("POSS"); 
                if (n.getPOS().equals("PRP"))    n.setPOS("N"); // nominal
                if (n.getPOS().startsWith("NN")) n.setPOS("N"); // nominal                
                if (n.getPOS().startsWith("JJ")) n.setPOS("A"); // adjectival
                if (n.getPOS().startsWith("V"))  n.setPOS("V"); // verbal
                if (n.getPOS().startsWith("IN")) n.setPOS("P"); // preposition
            }
        }
        
        // Repair systematic POS tagging errors
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"tt(*/DT-1,*/V-2)")) {
                        
            Map<Integer,Integer> m = subgraph.getRight();            
            graph.getNode(m.get(2)).setPOS("N");            
        }        
        
        // Collapse adjacent nodes with the same POS tag
        
        String[] pos = {"N","A","V","P"};
        for (String s : pos) {
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"tt(*/"+s+"-1,*/"+s+"-2)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.DEP,m.get(1),"compound",m.get(2)));
        }}
                
        List<Edge> toCollapse = graph.getEdges("compound");
        for (Edge e : toCollapse) {
            graph.deleteEdge(e);
            Node head = graph.getNode(e.getHead());
            Node dpnd = graph.getNode(e.getDependent());
            head.setForm(head.getForm() + " " + dpnd.getForm());
            graph.deleteNode(dpnd);
            graph.renameNode(dpnd.getId(),head.getId());
        }
        
        // Keep only semantically relevant nodes 
        
        List<String> keep = new ArrayList<>(); 
        keep.add("N");
        keep.add("A");
        keep.add("V");
        keep.add("P");
        
        List<Integer> keepNodes = new ArrayList<>();

        for (Node n : graph.getNodes()) {
             if (keep.contains(n.getPOS())) {
                 keepNodes.add(n.getId());
             } 
        }
        Collections.sort(keepNodes);
                
        for (int i = 0; i < keepNodes.size(); i++) {
            int id = keepNodes.get(i);
            con_graph.addNode(graph.getNode(id));
            if (i < keepNodes.size()-1) {
                con_graph.addEdge(new Edge(Edge.Color.LIN,id,"tt",keepNodes.get(i+1)));
            }
        }
       
        if (verbose) System.out.println("\nCondensed graph:\n" + con_graph.toString());       
        
        
        // 2. Pattern-based constructions of semantic graph 
                
        // 2.1 Modifier layer 
        
        // Adjective + Noun
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(con_graph,"tt(*/A-1,*/N-2)")) {
            
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            sem_graph.addNode(con_graph.getNode(m.get(1)));
            sem_graph.addNode(con_graph.getNode(m.get(2)));
            sem_graph.addEdge(new Edge(Edge.Color.SEM,m.get(1),"MOD",m.get(2)));
            
            con_graph.deleteNode(con_graph.getNode(m.get(1))); 
            con_graph.renameNode(m.get(1),m.get(2));
        }
                
        // 2.2 N-grams with particular semantics
        
        // how many // TODO con_graph will not contain tt(how-1,many-2) \n tt(many-2,*/N-3)
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(con_graph,"tt(how-1,many-2) \n tt(many-2,*/N-3)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            sem_graph.addNode(new Node(m.get(2),"COUNT"));
            sem_graph.addEdge(new Edge(Edge.Color.SEM,m.get(3),"AMOUNT",m.get(2)));
        }
        
        // a lot of, lots of, many
        String[] patterns = {"tt(lot-1,of-2) \n tt(of-2,*/N-3)",
                             "tt(lots-1,of-2) \n tt(of-2,*/N-3)",
                             "tt(many-1,*/N-3)"};
        for (String pattern : patterns) {
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(con_graph,pattern)) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            sem_graph.addNode(new Node(m.get(1),"MANY"));
            sem_graph.addEdge(new Edge(Edge.Color.SEM,m.get(3),"AMOUNT",m.get(1)));
            
            con_graph.delete(g);
        }}
        
        // or, and
        
        // 2.3 Relational layer
        
        // Noun + Preposition + Noun
        String[] connector = {"P","V"};
        for (String s : connector) {
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(con_graph,"tt(*/N-1,*/"+s+"-2) \n tt(*/"+s+"-2,*/N-3)")) {
            
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            sem_graph.addNode(con_graph.getNode(m.get(1)));
            sem_graph.addNode(con_graph.getNode(m.get(3)));
            sem_graph.addEdge(new Edge(Edge.Color.SEM,m.get(1),con_graph.getNode(m.get(2)).getForm(),m.get(3)));            
        }}
        
        // Noun + Verb + Preposition + Noun | Adjective
        String[] tags = {"N","A"};
        for (String s : tags) {
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(con_graph,"tt(*/N-1,*/V-2) \n tt(*/V-2,*/P-3) \n tt(*/P-3,*/"+s+"-4)")) {
            
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            sem_graph.addNode(con_graph.getNode(m.get(1)));
            sem_graph.addNode(con_graph.getNode(m.get(4)));
            sem_graph.addEdge(new Edge(Edge.Color.SEM,m.get(1),con_graph.getNode(m.get(2)).getForm()+" "+con_graph.getNode(m.get(3)).getForm(),m.get(4)));
        }}
        
        // Noun + Verb + to + Verb + Noun
        
        // also keep non-connected nominal nodes
        for (Node n : con_graph.getNodes()) {
            if (n.getPOS() != null && (n.getPOS().equals("N"))) {
                if (!sem_graph.getNodes().contains(n)) 
                     sem_graph.addNode(n);
            }
        }

        if (verbose) System.out.println("\nSemGraph:\n" + sem_graph.toString());
        
        return sem_graph;
    }    
    
    private List<Pair<Graph,Map<Integer,Integer>>> getSubgraphs(Graph graph, String regex) {
               
        Graph subgraph = reader.interpret(regex);
                
        return subgraph.subGraphMatches(graph);
    }
    
}
