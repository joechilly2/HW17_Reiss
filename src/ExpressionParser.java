
import java.io.Console;
import java.util.Scanner;

public class ExpressionParser {

    // A program to parser simple Java-like arithmetic expressions
    // according to the following grammar:
    //
    //
    // Expression -> SimpleExpression
    // Expression -> Expression AssignOp SimpleExpression
    //
    // SimpleExpression -> Term
    // SimpleExpression -> SimpleExpression AddOp Term
    //
    // Term -> Factor
    // Term -> Term MulOp Factor
    //
    // Factor -> Atom
    // Factor -> PreOp Factor
    // Factor -> Factor PostOp
    //
    // Atom -> number
    // Atom -> identifier
    // Atom -> '(' Expression ')'
    //
    // AssignOp -> '='
    // AssignOp -> '+='
    // AssignOp -> '-='
    // AssignOp -> '*='
    // AssignOp -> '/='
    // AssignOp -> '%='
    //
    // AddOp -> '+'
    // AddOp -> '-'
    //
    // MulOp -> '*'
    // MulOp -> '/'
    // MulOp -> '%'
    //
    // PreOp -> '+'
    // PreOp -> '-'
    // PreOp -> '++'
    // PreOp -> '--'
    //
    // PostOp -> '++'
    // PostOp -> '--'
    private ExpressionScanner scanner;		// The tokenizer for the terminal symbols
    private Token currentToken;		// The current input token being examined

    public static class SyntaxError extends Exception {

        private int position;

        public SyntaxError(String message, int position) {
            super(message + " at " + position);
            this.position = position;
        }

        public SyntaxError(int position) {
            this("Syntax error", position);
        }

        public int position() {
            return this.position;
        }
    }

    // Utility method to advance to the next token.
    private Token getNextToken() {
        if (scanner.hasNext()) {
            currentToken = scanner.next();
        } else {
            currentToken = Token.End;
        }
        return currentToken;
    }

    // Utility method to check to see if the current token is
    // a specified token (and advance to the next token if so)
    // and throw a SyntaxError exception if no.
    private void checkToken(Token.Kind kind) throws SyntaxError {
        if (currentToken.kind() == kind) {
            getNextToken();
        } else {
            throw new SyntaxError(scanner.position());
        }
    }

    private boolean isAssignOp(Token token) {
        switch (token.kind()) {
            case ASSIGN:
                return true;
            case PLUS_ASSIGN:
                return true;
            case MINUS_ASSIGN:
                return true;
            case TIMES_ASSIGN:
                return true;
            case DIVIDE_ASSIGN:
                return true;
            case MODULO_ASSIGN:
                return true;
            default:
                return false;
        }
    }

    private boolean isAddOp(Token token) {
        switch (token.kind()) {
            case PLUS:
                return true;
            case MINUS:
                return true;
            default:
                return false;
        }
    }

    private boolean isMulOp(Token token) {
        switch (token.kind()) {
            case TIMES:
                return true;
            case DIVIDE:
                return true;
            case MODULO:
                return true;
            default:
                return false;
        }
    }

    private boolean isPreOp(Token token) {
        switch (token.kind()) {
            case PLUS:
                return true;
            case MINUS:
                return true;
            case PLUS_PLUS:
                return true;
            case MINUS_MINUS:
                return true;
            default:
                return false;
        }
    }

    private boolean isPostOp(Token token) {
        switch (token.kind()) {
            case PLUS_PLUS:
                return true;
            case MINUS_MINUS:
                return true;
            default:
                return false;
        }
    }

    // The recursive-descent parsing methods for the various
    // syntactic categories in the grammar given above.
    private ExpressionTree.Node parseAtom() throws SyntaxError, ExpressionTree.NotAVariable {
        ExpressionTree.Node ret = null;
        switch (currentToken.kind()) {
            case NUMBER:
                Token.NumberToken num = (Token.NumberToken) currentToken;
                ret = new ExpressionTree.Number(num.value());
                getNextToken();
                break;
                
            case IDENTIFIER:
                Token.IdentifierToken iden = (Token.IdentifierToken) currentToken;
                String ident = iden.name();
//                if(ExpressionTree.symbols.contains(ident)){
//                    ret = new ExpressionTree.Number(ExpressionTree.symbols.find(ident));
//                }
//                else{
//                    ret = new ExpressionTree.Variable(ident);
//                }
                ret = new ExpressionTree.Variable(ident);
                getNextToken();
                break;
                
            case OPEN_PARENTHESIS:
                getNextToken();
                ret = parseExpression();
                checkToken(Token.Kind.CLOSE_PARENTHESIS);
                break;

            default:
                throw new SyntaxError(scanner.position());
        }
        return ret;
    }

    private ExpressionTree.Node parseFactor() throws SyntaxError, ExpressionTree.NotAVariable {
        Token.Kind k = null;
        ExpressionTree.Node ret = null;
        while (isPreOp(currentToken)) {
            k = currentToken.kind();
            getNextToken();
        }
        Token temp = currentToken;
        ExpressionTree.Node left = parseAtom();
        if (k != null) {
            switch (k) {
                case PLUS:
                    break;
                case MINUS:
                    ret = new ExpressionTree.Negate(left);
                    break;
                case PLUS_PLUS:
                    ret = new ExpressionTree.PreIncrement(left);
                    break;
                case MINUS_MINUS:
                    ret = new ExpressionTree.PreDecrement(left);
                    break;
                default:
                    break;
            }
        } else {
            ret = left;
        }
        while (isPostOp(temp)) {
            if(temp.kind().equals(Token.Kind.PLUS_PLUS)){
                ret = new ExpressionTree.PostIncrement(ret);
            }
            else if(temp.kind().equals(Token.Kind.MINUS_MINUS)){
                ret = new ExpressionTree.PostDecrement(ret);
            }
            getNextToken();
        }
        return ret;
    }

    private ExpressionTree.Node parseTerm() throws SyntaxError, ExpressionTree.NotAVariable {
        ExpressionTree.Node ret = parseFactor();
        while (isMulOp(currentToken)) {
            Token temp = currentToken;
            getNextToken();
            ExpressionTree.Node left = ret;
            ExpressionTree.Node right = parseFactor();
            switch (temp.kind()) {
                case TIMES:
                    ret = new ExpressionTree.Multiply(left, right);
                    break;
                case DIVIDE:
                    ret = new ExpressionTree.Divide(left, right);
                    break;
                case MODULO:
                    ret = new ExpressionTree.Mod(left, right);
                default:
                    break;//shouldnt get here
            }
        }
        return ret;
    }

    private ExpressionTree.Node parseSimpleExpression() throws SyntaxError, ExpressionTree.NotAVariable {
        ExpressionTree.Node ret = parseTerm();
        while (isAddOp(currentToken)) {
            Token temp = currentToken;
            getNextToken();
            ExpressionTree.Node left = ret;
            ExpressionTree.Node right = parseTerm();
            if (temp.kind().equals(Token.Kind.PLUS)) {
                ret = new ExpressionTree.Add(left, right);
            } else if (temp.kind().equals(Token.Kind.MINUS)) {
                ret = new ExpressionTree.Subtract(left, right);
            }
        }
        return ret;
    }

    private ExpressionTree.Node parseExpression() throws SyntaxError, ExpressionTree.NotAVariable {
        ExpressionTree.Node ret = parseSimpleExpression();
        while (isAssignOp(currentToken)) {
            Token temp = currentToken;
            getNextToken();
            ExpressionTree.Node left = ret;
            ExpressionTree.Node right = parseSimpleExpression();
            switch (temp.kind()) {
                case ASSIGN:
                    ret = new ExpressionTree.Assign(left, right);
                    break;
                case PLUS_ASSIGN:
                    ret = new ExpressionTree.AddTo(left, right);
                    break;
                case MINUS_ASSIGN:
                    ret = new ExpressionTree.SubtractFrom(left, right);
                    break;
                case TIMES_ASSIGN:
                    ret = new ExpressionTree.MultiplyBy(left, right);
                    break;
                case DIVIDE_ASSIGN:
                    ret = new ExpressionTree.DivideBy(left, right);
                    break;
                case MODULO_ASSIGN:
                    ret = new ExpressionTree.ModBy(left, right);
                    break;
                default:
                    break;//shouldnt get here
            }
        }
        return ret;
    }

    //Current problems:
    //Parse only returns 1 node, needs to return
    
    public ExpressionTree.Node parse(String s) throws SyntaxError, ExpressionTree.NotAVariable, ExpressionTree.UndefinedVariable {
        scanner = new ExpressionScanner(s);
        getNextToken();
        ExpressionTree.Node parseExpression = parseExpression();
        if (currentToken.kind() != Token.Kind.END) {
            throw new SyntaxError(scanner.position());
        }
        return parseExpression;
    }

    // A main program to test your parser:
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
                parser.parse(line);
                System.out.println("OK");
            } catch (SyntaxError e) {
                System.out.println("Error at position " + e.position());
            }
//			line = console.readLine(prompt);
            line = console.nextLine();
            System.out.println(prompt);
        }
    }
}
