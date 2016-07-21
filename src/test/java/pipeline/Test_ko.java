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
             
            "서울에 있는 산은 무엇인가?", // Which mountains are in Seoul?
            "세계에서 가장 높은 산은 무엇인가?", // What is the highest mountain in the world?
            "한국의 수도는 무엇인가?", // What is the capital of Korea?
            "미국의 수도는 무엇인가?", // What is the capital of USA?
            "와인 생산국으로 유명한 나라는 어디인가?", // Which countries are known for wine production?
            "한국의 제 16대 대통령은 누구인가?", // Who was the 16th president of South Korea?
            "1904년에 노벨생리의학상을 받은 러시아의 생리학자는 누구인가?", // Who was the Russian physiologist that won the Nobel Prize for Physiology or Medicine in 1904?
            "대한민국의 국화는 무엇인가?", // What is the national flower of South Korea?
            "예수의 부모는 누구인가?", // Who are the parents of Jesus?
            
            "카이스트는 얼마나 많은 학생이 있는가?", // How many students does KAIST have? 
            "어떤 강이 군산을 흐르는가?", // Which rivers flow through Gunsan?
            "군산에 흐르는 강은 무엇인가?", // Which rivers flow through Gunsan?          
            "대한민국에 있는 도시를 나열하라", // List cities in Korea.
            "대한민국의 도시를 나열하라", // List cities in Korea.
            "한라산은 얼마나 높은가?" // How high is Hallasan?
        ); 
        
        TemplatorPipeline pipeline = new TemplatorPipeline("ko");
        pipeline.debugMode();
        Scanner scanner = new Scanner(System.in);
        
        for (String question : test) {
            
            JsonArray output = pipeline.run(question,null);
                        
            System.out.println("\n\nContinue? y/n");
            String response = scanner.nextLine();
            if (response.equals("n")) {
                System.exit(0);
            }
        }
    }
    
}
