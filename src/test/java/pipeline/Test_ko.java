package pipeline;


import com.google.gson.JsonArray;
import org.okbqa.rocknrole.pipeline.TemplatorPipeline;
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
             
            "한국의 수도는 무엇인가?", // What is the capital of Korea?
            "카이스트는 얼마나 많은 학생이 있는가?", // How many students does KAIST have? 
            "어떤 강이 군산을 흐르는가?", // Which rivers flow through Gunsan?
            "군산에 흐르는 강은 무엇인가?", // Which rivers flow through Gunsan?          
            "구글의 설립자는 누구인가?", // Who is the founder of Google?
            "대한민국에 있는 도시를 나열하라", // List cities in Korea.
            "대한민국의 도시를 나열하라", // List cities in Korea.
            "한라산은 얼마나 높은가?" // How high is Hallasan?
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
