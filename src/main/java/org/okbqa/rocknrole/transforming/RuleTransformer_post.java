package org.okbqa.rocknrole.transforming;

import org.okbqa.rocknrole.graph.Edge;
import org.okbqa.rocknrole.graph.Node;
import org.okbqa.rocknrole.graph.Pair;
import org.okbqa.rocknrole.graph.Graph;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cunger
 */
public class RuleTransformer_post extends RuleTransformer {

        
    @Override
    public Graph transform(Graph g_in) {
        
        Graph graph = g_in.copy();
        
        // Copulative construction
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"ARG0(BE-2,*-1) \n ARG1(BE-2,*-3)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SEM,m.get(1),"EQUALS",m.get(3)));
            graph.delete(g);
        }
        
        // Argument structure
        
        // Case where there are two arguments (ARG0+ARG1 or ARG0+ARGP)
        // *-1 = verb 
        // *-2 = subject 
        // *-3 = object
        String[] args = { "ARG0(*-1,*-2) \n ARG1(*-1,*-3)",
                          "ARG0(*-3,*-1) \n ARG1(*-1,*-2)",
                          "ARG0(*-1,*-2) \n ARG1(*-3,*-1)",
                          "ARG0(*-1,*-2) \n ARG0(*-1,*-3)",
                          "ARG1(*-1,*-2) \n ARG1(*-1,*-3)",
                          "ARG0(*-1,*-2) \n ARGP(*-1,*-3)", 
                          "ARG0(*-2,*-1) \n ARGP(*-1,*-3)" 
                        };
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,args)) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SEM,m.get(2),graph.getNode(m.get(1)).getForm(),m.get(3)));
            graph.delete(g);
        }
        
        // Case where there is only an ARG0, but no ARG1 or ARGP
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"ARG0(*-1,*-2)")) {
            
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            int obj = graph.getMaxId()+1;
            graph.addNode(new Node(obj,"RESOURCE"));
            graph.addEdge(new Edge(Edge.Color.SEM,m.get(2),graph.getNode(m.get(1)).getForm(),obj));
            graph.delete(g);
        }
        
        // Case where there is only an ARG1, but no ARG0 
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"ARG1(*-1,*-2)")) {
            
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            int subj = graph.getMaxId()+1;
            graph.addNode(new Node(subj,"RESOURCE"));
            graph.addEdge(new Edge(Edge.Color.SEM,subj,graph.getNode(m.get(1)).getForm(),m.get(2)));
            graph.delete(g);
        }
        
        // case of additional prepositional arguments
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"ARGP(*-1,*-2)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            graph.addEdge(new Edge(Edge.Color.SEM,m.get(1),"REL",m.get(2)));
            graph.delete(g);
        }

                
        // Relative clauses 
        
        // TODO
                
        // HAVE 
        // Note: It's important that this is applied after the argument structure rules.
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"HAVE(*-1,*-2)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();
            
            if (graph.getNode(m.get(1)).hasProperForm() && graph.getNode(m.get(2)).hasProperForm()) {
                graph.addEdge(new Edge(Edge.Color.SEM,m.get(1),"REL",m.get(2)));
            }
            else if (graph.getNode(m.get(1)).hasProperForm()) {
                graph.addEdge(new Edge(Edge.Color.SEM,m.get(2),graph.getNode(m.get(1)).getForm(),m.get(1)));
                graph.getNode(m.get(1)).setForm("RESOURCEorLITERAL");
            }
            else if (graph.getNode(m.get(2)).hasProperForm()) {
                graph.addEdge(new Edge(Edge.Color.SEM,m.get(1),graph.getNode(m.get(2)).getForm(),m.get(2)));
                graph.getNode(m.get(2)).setForm("RESOURCEorLITERAL");
            }
            
            graph.delete(g);
        }
        
        // Coordination
        
        for (Pair<Graph,Map<Integer,Integer>> subgraph : getSubgraphs(graph,"cc(*-1,*-2) \n conj(*-1,*-3)")) {
                        
            Graph g = subgraph.getLeft();
            Map<Integer,Integer> m = subgraph.getRight();

            graph.addEdge(new Edge(Edge.Color.SEM,m.get(2),"conjunct",m.get(1)));            
            graph.addEdge(new Edge(Edge.Color.SEM,m.get(2),"conjunct",m.get(3)));
        }

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
