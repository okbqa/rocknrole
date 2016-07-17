package transformer;

import java.io.IOException;
import java.net.URISyntaxException;
import org.okbqa.rocknrole.graph.Graph;
import org.okbqa.rocknrole.graph.interpreter.GraphReader;
import org.okbqa.rocknrole.transforming.RuleApplication;

/**
 *
 * @author cunger
 */
public class RuleApplicationTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws URISyntaxException, IOException {

        GraphReader antlr = new GraphReader();
        Graph       graph = antlr.interpret("NP_SUBJ(누구-1,THING-2) \n ARG2(나열-3,THING-2) \n ARG3(blah-4,THING-2) \n prep(capital-5,of-6) \n pobj(of-6,Korea-7) \n AP(hello-8,얼마나-9) \n ARG0(hello-8,world-10)");
        
        System.out.println("Input graph:\n\n" + graph.toString(true));
        
        RuleApplication app = new RuleApplication("ko");
        app.readFile("example.rules");
        app.debugMode();
        
        app.execute(graph);
        
        System.out.println("Output graph:\n\n" + graph.toString(true));
    }
    
}
