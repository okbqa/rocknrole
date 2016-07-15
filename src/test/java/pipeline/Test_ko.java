package pipeline;


import com.google.gson.JsonArray;
import de.citec.sc.rocknrole.pipeline.TemplatorPipeline;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


/**
 *
 * @author cunger
 */
public class Test_ko {
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
                                
        test();
    }
    
    public static void test() throws IOException {
        
        List<String> test = Arrays.asList(
             
            "한국의 수도는 무엇인가?", // what is the capital of korea
            "카이스트는 얼마나 많은 학생이 있는가?", // how many students does kaist have? (얼마나 많은 = how many)
            "어떤 강이 군산을 흐르는가?", // which rivers flow through gunsan
            "군산에 흐르는 강은 무엇인가?", // which rivers flow through gunsan          
            "구글의 설립자는 누구인가?", // who is the founder of google 
            "대한민국에 있는 도시를 나열하라", // list cities in korea 
            "대한민국의 도시를 나열하라", // list cities in korea
            "한라산은 얼마나 높은가?" // how high is hallasan?
        ); 
                
        TemplatorPipeline pipeline = new TemplatorPipeline("ko");
        pipeline.debugMode();
        Scanner scanner = new Scanner(System.in);
        
        for (String question : test) {
            
            JsonArray output = pipeline.run(question);
                        
            System.out.println("\n\nContinue? y/n");
            String response = scanner.nextLine();
            if (response.equals("n")) {
                System.exit(0);
            }
        }
    }
    
}
