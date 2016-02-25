package graph;

import de.citec.sc.rocknrole.graph.Graph;
import de.citec.sc.rocknrole.graph.interpreter.GraphReader;

/**
 *
 * @author cunger
 */
public class SubgraphMatchingTest {


    public static void main(String[] args) {

        
        GraphReader reader = new GraphReader();
        
        Graph g1 = reader.interpret("det(color/NN-2,what/WDT-3) \n amod(color/NN-2,nice/JJ-1)");
        Graph g2 = reader.interpret("det(*-2,what-1)");

        System.out.println(g2.subGraphMatches(g1).size());
    }
    
}
