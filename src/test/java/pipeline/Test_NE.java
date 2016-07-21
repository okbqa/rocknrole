package pipeline;


import org.okbqa.rocknrole.pipeline.TemplatorPipeline;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.okbqa.rocknrole.graph.Pair;


/**
 *
 * @author cunger
 */
public class Test_NE {
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
                                
        test();
    }
    
    public static void test() throws IOException {
        
        String question = "who was the 16th President of south korea?";
        
        Set<Pair<Integer,Integer>> entities = new HashSet<>();
        entities.add(new Pair(30,41));

        TemplatorPipeline pipeline = new TemplatorPipeline("en");
        pipeline.debugMode();
        
        pipeline.run(question,entities); // NEs = from input : [south korea]

        pipeline.run(question,null); // NEs = from Stanford : [16th]
    }
    
}
