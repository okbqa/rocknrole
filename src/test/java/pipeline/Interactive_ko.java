package pipeline;


import org.okbqa.rocknrole.pipeline.TemplatorPipeline;
import java.io.IOException;
import java.util.Scanner;


/**
 *
 * @author cunger
 */
public class Interactive_ko {
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
                                
        test();
    }
    
    public static void test() throws IOException {
        
        TemplatorPipeline pipeline = new TemplatorPipeline("ko");
        pipeline.debugMode();
        Scanner scanner = new Scanner(System.in,"UTF-8");
        
        String input;
        
        while (true) {
            
            System.out.print("\n> ");
            input = scanner.nextLine();
                        
            if (input.isEmpty()) {
                System.exit(0);
            }
            
            pipeline.run(input);
        }
    }
    
}
