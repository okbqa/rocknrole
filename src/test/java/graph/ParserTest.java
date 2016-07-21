package graph;

import java.util.HashSet;
import org.okbqa.rocknrole.graph.Graph;
import org.okbqa.rocknrole.graph.interpreter.GraphReader;
import org.okbqa.rocknrole.parsing.ParseResult;
import org.okbqa.rocknrole.parsing.Parser;
import org.okbqa.rocknrole.parsing.Stanford;

/**
 *
 * @author cunger
 */
public class ParserTest {

        
    public static void main(String[] args) {
    
        Parser stanford = new Stanford();
        GraphReader antlr = new GraphReader();

        String test = "What is the color of the bus?";
        
        ParseResult parse = stanford.parse(test,new HashSet<>());
        
        System.out.println("\nStanford:\n" + parse.getParses().values().toString());
        
        Graph  g1 = parse.toGraph();        
        String gs = g1.toString();
        
        System.out.println("\nInterpreted Stanford:\n");
        System.out.println(gs);
        
        Graph g2 = antlr.interpret(gs);
        
        System.out.println("\nInterpreted interpreted Stanford:\n");
        System.out.println(g2.toString());
    }
    
}
