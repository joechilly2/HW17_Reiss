import java.io.Console;

public abstract class Token {

	// A class to represent the tokens in the Java-like simple expression language.
	// The token Kind's represent the different kinds of tokens that are representable
	// while the Token class itself represents both the kind of the toke and its text
	// (image) in the expression.  Various categories of tokens are represented as
	// subclasses of Token (operator tokens, identifier tokens, etc).

	public static enum Kind {
		NUMBER,
		IDENTIFIER,
		OPEN_PARENTHESIS,
		CLOSE_PARENTHESIS,
		PLUS,
		MINUS,
		TIMES,
		DIVIDE,
		MODULO,
		PLUS_PLUS,
		MINUS_MINUS,
		ASSIGN,
		PLUS_ASSIGN,
		MINUS_ASSIGN,
		TIMES_ASSIGN,
		DIVIDE_ASSIGN,
		MODULO_ASSIGN,
		ERROR,
		END
	}

	private Kind kind;

	public Token(Kind kind) {
		this.kind = kind;
	}

	public Kind kind() {
		return this.kind;
	}


	public static class SimpleToken extends Token {
		
		private String image;

		protected SimpleToken(Kind kind, String image) {
			super(kind);
			this.image = image;
		}

		@Override
		public String toString() {
			return this.image;
		}
	}


	public static class OperatorToken extends SimpleToken {
		public OperatorToken(Kind kind, String image) {
			super(kind, image);
		}
	}


	public static class ErrorToken extends SimpleToken {
		public ErrorToken(String image) {
			super(Kind.ERROR, image);
		}
	}


	public static class NumberToken extends Token {

		private int value;

		public NumberToken(int value) {
			super(Kind.NUMBER);
			this.value = value;
		}

		public int value() {
			return this.value;
		}

		@Override
		public String toString() {
			return "" + value;
		}

	}


	public static class IdentifierToken extends Token {

		private String name;

		public IdentifierToken(String name) {
			super(Kind.IDENTIFIER);
			this.name = name;
		}

		public String name() {
			return this.name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	// Pre-defined tokens for the various operators.

	public static final OperatorToken Plus           = new OperatorToken(Kind.PLUS, "+");
	public static final OperatorToken Minus          = new OperatorToken(Kind.MINUS, "-");
	public static final OperatorToken Times          = new OperatorToken(Kind.TIMES, "*");
	public static final OperatorToken Divide         = new OperatorToken(Kind.DIVIDE, "/");
	public static final OperatorToken Modulo         = new OperatorToken(Kind.MODULO, "%");
	public static final OperatorToken Assign         = new OperatorToken(Kind.ASSIGN, "=");
	public static final OperatorToken PlusAssign     = new OperatorToken(Kind.PLUS_ASSIGN, "+=");
	public static final OperatorToken MinusAssign    = new OperatorToken(Kind.MINUS_ASSIGN, "-=");
	public static final OperatorToken TimesAssign    = new OperatorToken(Kind.TIMES_ASSIGN, "*=");
	public static final OperatorToken DivideAssign   = new OperatorToken(Kind.DIVIDE_ASSIGN, "/=");
	public static final OperatorToken ModuloAssign   = new OperatorToken(Kind.MODULO_ASSIGN, "%=");
	public static final OperatorToken PlusPlus       = new OperatorToken(Kind.PLUS_PLUS, "++");
	public static final OperatorToken MinusMinus     = new OperatorToken(Kind.MINUS_MINUS, "--");
	public static final SimpleToken OpenParenthesis  = new SimpleToken(Kind.OPEN_PARENTHESIS, "(");
	public static final SimpleToken CloseParenthesis = new SimpleToken(Kind.CLOSE_PARENTHESIS, ")");
	public static final SimpleToken End              = new SimpleToken(Kind.END, "");

}
