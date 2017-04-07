
import java.util.Scanner;

/*
 *  Ryan Reiss
 *  HW #17
 *  4/5/17
 */

//Okay this version should work properly. The only bug I can find is that when
//using postops, the output will not show and calculate the updated variable
//properly, but it will update properly in the hashTable after evaluate is called

public class HW17_Reiss {

    public static void main(String[] args) throws ExpressionTree.NotAVariable, ExpressionTree.UndefinedVariable {
        ExpressionParser parser = new ExpressionParser();
        Scanner console = new Scanner(System.in);
        String prompt = "Expression: ";
        String line;
//		line = console.readLine(prompt);
        System.out.println(prompt);
        line = console.nextLine();

        while (line.length() > 0) {
            try {
                System.out.println(parser.parse(line).evaluate());
            } catch (ExpressionParser.SyntaxError e) {
                System.out.println("Error at position " + e.position());
            }
//			line = console.readLine(prompt);
            System.out.println(prompt);            
            line = console.nextLine();
        }
    }

}
