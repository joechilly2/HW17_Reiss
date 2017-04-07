
import java.util.Scanner;

/*
 *  Ryan Reiss
 *  HW #17
 *  4/5/17
 */

//Im sorry for the messy code with little to no comments. I will try to get a version with comments / final bug fixes up soon.
//But if you need something to grade, this is it.

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
