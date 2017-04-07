import java.io.Console;
import java.util.Scanner;

public class ExpressionTree {

	public static LinearProbingHashMap<String, Integer> symbols =
		new LinearProbingHashMap();

	public static class UndefinedVariable extends Exception {
		public UndefinedVariable(String name) {
			super("Undefined variable: " + name);
		}
	}

	public static class NotAVariable extends Exception {
		public NotAVariable(Node node) {
			super ("Variable expected, found: " + node.format());
		}
	}


	public static abstract class Node {

		// An abstract class for the nodes in an expression tree.

		public abstract int evaluate()
			throws UndefinedVariable;     // Evaluate the sub-tree rooted at this node

		public abstract String format();  // Format (RPN) the sub-tree rooted at this node
	}


	public static class Number extends Node {

		// A (sub)class to hold literals (integers) in an expression tree.

		private int value;

		public Number (int value) {
			this.value = value;
		}

		@Override
		public int evaluate() throws UndefinedVariable {
			return this.value;
		}

		@Override
		public String format() {
			return "" + value;
		}
	}


	public static class Variable extends Node {

		// A (sub)class to hold variables in an expression tree.
		// Variable names are strings following the Java syntax
		// for variable names.

		// For now, assume that the value of all variables is zero.
		// We will add a symbol table later to keep track of the
		// current value of each variable.

		private String name;

		public Variable(String name) {
			this.name = name;
		}

		@Override
		public int evaluate() throws UndefinedVariable {
			Integer value = symbols.find(this.name);
			if (value == null) {
				throw new UndefinedVariable(this.name);
			} else {
				return value;
			}
		}

		public void update(int value) {
			symbols.add(this.name, value);
		}

		@Override
		public String format() {
			return name;
		}
	}


	public abstract static class UnaryOperator extends Node {

		// An abstract subclass for unary operator nodes.

		private Node operand;

		public UnaryOperator(Node operand) {
			this.operand = operand;
		}

		public Node operand() {
			return this.operand;
		}

		public String format() {
			return operand.format() + " " + this.op();
		}

		public abstract String op();
		// The symbol (string) used for this operator
	}


	public static class Negate extends UnaryOperator {

		// A subclass for expression tree nodes for a particular
		// unary operator: negate.

		public Negate(Node operand) {
			super(operand);
		}

		@Override
		public int evaluate() throws UndefinedVariable {
			return -operand().evaluate();
		}

		@Override
		public String op() {
			return "~"; // For now, use '~' to disambiguate it from the binary subtract op.
		}
	}


	public static abstract class UnaryAssignmentOperator extends UnaryOperator {

		public UnaryAssignmentOperator(Node operand) throws NotAVariable {
			super(operand);
			if (!(operand instanceof Variable)) {
				throw new NotAVariable(operand);
			}
		}

		public Variable variable() {
			Node operand = this.operand();
			return (Variable) operand;
		}
	}


	public static class PreIncrement extends UnaryAssignmentOperator {

		public PreIncrement(Node operand) throws NotAVariable {
			super(operand);
		}

		@Override
		public int evaluate() throws UndefinedVariable {
			Variable variable = this.variable();
			int value = variable.evaluate() + 1;
			variable.update(value);
			return value;
		}

		@Override
		public String op() {
			return "++";
		}
	}


	public static class PreDecrement extends UnaryAssignmentOperator {

		public PreDecrement(Node operand) throws NotAVariable {
			super(operand);
		}

		@Override
		public int evaluate() throws UndefinedVariable {
			Variable variable = this.variable();
			int value = variable.evaluate() - 1;
			variable.update(value);
			return value;
		}

		@Override
		public String op() {
			return "--";
		}
	}


	public static class PostIncrement extends UnaryAssignmentOperator {

		public PostIncrement(Node operand) throws NotAVariable {
			super(operand);
		}

		@Override
		public int evaluate() throws UndefinedVariable {
			Variable variable = this.variable();
			int value = variable.evaluate();
			variable.update(value + 1);
			return value;
		}

		@Override
		public String op() {
			return "+++"; // For now, to disambiguate it from the pre-increment op
		}
	}


	public static class PostDecrement extends UnaryAssignmentOperator {

		public PostDecrement(Node operand) throws NotAVariable {
			super(operand);
		}

		@Override
		public int evaluate() throws UndefinedVariable {
			Variable variable = this.variable();
			int value = variable.evaluate();
			variable.update(value - 1);
			return value;
		}

		@Override
		public String op() {
			return "---"; // For now, to disambiguate it from the pre-increment op
		}
	}


	public abstract static class BinaryOperator extends Node {

		private Node left;
		private Node right;

		private BinaryOperator(Node left, Node right) {
			this.left = left;
			this.right = right;
		}

		public Node left() {
			return this.left;
		}

		public Node right() {
			return this.right;
		}

		public String format() {
			return left.format() + " " + right.format() + " " + this.op();
		}

		public abstract String op();
		// The symbol (string) used for this operator

	}

	public static class Add extends BinaryOperator { 
	
		public Add(Node left, Node right) {
			super(left, right);
		}

		@Override
		public int evaluate() throws UndefinedVariable {
			return left().evaluate() + right().evaluate();
		}

		@Override
		public String op() {
			return "+";
		}
	}


	public static class Subtract extends BinaryOperator {
	
		public Subtract(Node left, Node right) {
			super(left, right);
		}

		@Override
		public int evaluate() throws UndefinedVariable {
			return left().evaluate() - right().evaluate();
		}

		@Override
		public String op() {
			return "-";
		}
	}


	public static class Multiply extends BinaryOperator {
	
		public Multiply(Node left, Node right) {
			super(left, right);
		}

		@Override
		public int evaluate() throws UndefinedVariable {
			return left().evaluate() * right().evaluate();
		}

		@Override
		public String op() {
			return "*";
		}
	}


	public static class Divide extends BinaryOperator {
	
		public Divide(Node left, Node right) {
			super(left, right);
		}

		@Override
		public int evaluate() throws UndefinedVariable {
			return left().evaluate() / right().evaluate();
		}

		@Override
		public String op() {
			return "/";
		}
	}


	public static class Mod extends BinaryOperator {
	
		public Mod(Node left, Node right) {
			super(left, right);
		}

		@Override
		public int evaluate() throws UndefinedVariable {
			return left().evaluate() % right().evaluate();
		}

		@Override
		public String op() {
			return "%";
		}
	}


	public static abstract class AssignmentOperator extends BinaryOperator {
	
		public AssignmentOperator(Node left, Node right) throws NotAVariable {
			super(left, right);
			if (!(left instanceof Variable)) {
				throw new NotAVariable(left);
			}
		}

		public Variable variable() {
			return (Variable) (left());
		}
	}


	public static class Assign extends AssignmentOperator {

		public Assign(Node left, Node right) throws NotAVariable {
			super(left, right);
		}

		public int evaluate() throws UndefinedVariable {
			Variable variable = this.variable();
			int value = this.right().evaluate();
			variable.update(value);
			return value;
		}

		@Override
		public String op() {
			return "=";
		}
	}

	public static class AddTo extends AssignmentOperator {

		public AddTo(Node left, Node right) throws NotAVariable {
			super(left, right);
		}

		public int evaluate() throws UndefinedVariable {
			Variable variable = this.variable();
			int value = variable.evaluate();
			value += right().evaluate();
			variable.update(value);
			return value;
		}

		@Override
		public String op() {
			return "+=";
		}
	}

	public static class SubtractFrom extends AssignmentOperator {

		public SubtractFrom(Node left, Node right) throws NotAVariable {
			super(left, right);
		}

		public int evaluate() throws UndefinedVariable {
			Variable variable = this.variable();
			int value = variable.evaluate();
			value -= right().evaluate();
			variable.update(value);
			return value;
		}

		@Override
		public String op() {
			return "-=";
		}
	}

	public static class MultiplyBy extends AssignmentOperator {

		public MultiplyBy(Node left, Node right) throws NotAVariable {
			super(left, right);
		}

		public int evaluate() throws UndefinedVariable {
			Variable variable = this.variable();
			int value = variable.evaluate();
			value *= right().evaluate();
			variable.update(value);
			return value;
		}

		@Override
		public String op() {
			return "*=";
		}
	}

	public static class DivideBy extends AssignmentOperator {

		public DivideBy(Node left, Node right) throws NotAVariable {
			super(left, right);
		}

		public int evaluate() throws UndefinedVariable {
			Variable variable = this.variable();
			int value = variable.evaluate();
			value /= right().evaluate();
			variable.update(value);
			return value;
		}

		@Override
		public String op() {
			return "/=";
		}
	}

	public static class ModBy extends AssignmentOperator {

		public ModBy(Node left, Node right) throws NotAVariable {
			super(left, right);
		}

		public int evaluate() throws UndefinedVariable {
			Variable variable = this.variable();
			int value = variable.evaluate();
			value %= right().evaluate();
			variable.update(value);
			return value;
		}

		@Override
		public String op() {
			return "%=";
		}
	}


	// The main program to test your class hierachy implementation.

	private static Node[] stack;
	private static int top = 0;

	private static Node pop() {
		return stack[--top];
	}

	private static void push(Node node) {
		stack[top++] = node;
	}

	public static int evaluate(String expression) throws NotAVariable, UndefinedVariable {
		String[] args = expression.split(" ");
		stack = new Node[args.length];
		Node left, right;

		for (String arg : args) {
			switch(arg) {
				case "~": // For disambiguation
					push(new Negate(pop()));
					break;

				case "++":
					push(new PreIncrement(pop()));
					break;

				case "--":
					push(new PreDecrement(pop()));
					break;

				case "+++": // For disambiguation
					push(new PostIncrement(pop()));
					break;

				case "---": // For disambiguation
					push(new PostDecrement(pop()));
					break;

				case "+":
					right = pop();
					left = pop();
					push(new Add(left, right));
					break;

				case "-":
					right = pop();
					left = pop();
					push(new Subtract(left, right));
					break;

				case "*":
					right = pop();
					left = pop();
					push(new Multiply(left, right));
					break;

				case "/":
					right = pop();
					left = pop();
					push(new Divide(left, right));
					break;

				case "%":
					right = pop();
					left = pop();
					push(new Mod(left, right));
					break;

				case "=":
					right = pop();
					left = pop();
					push(new Assign(left, right));
					break;

				case "+=":
					right = pop();
					left = pop();
					push(new AddTo(left, right));
					break;

				case "-=":
					right = pop();
					left = pop();
					push(new SubtractFrom(left, right));
					break;

				case "*=":
					right = pop();
					left = pop();
					push(new MultiplyBy(left, right));
					break;

				case "/=":
					right = pop();
					left = pop();
					push(new DivideBy(left, right));
					break;

				case "%=":
					right = pop();
					left = pop();
					push(new ModBy(left, right));
					break;

				default:
					try {
						int value = Integer.parseInt(arg);
						push(new Number(value));
					} catch (NumberFormatException e) {
						push(new Variable(arg));
					}
			}
		}

		Node node = pop();
		return node.evaluate();
	}

	public static void main(String[] args) {
		Scanner console = new Scanner(System.in);
		System.out.println("Expression:");
                
                String line = console.nextLine();
		while (line.length() > 0) {
			try {
				int value = evaluate(line);
				System.out.println(value);
			} catch (NotAVariable | UndefinedVariable e) {
				System.out.println(e);
			}
			System.out.println("Expression");
                        line = console.nextLine();
		}
	}
}
