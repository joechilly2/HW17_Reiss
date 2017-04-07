import java.io.Console;

public class ExpressionScanner {

	public static enum State {
		START() {
			@Override
			public State next(char c) {
				switch (c) {
					case '(': return OPEN_PARENTHESIS;
					case ')': return CLOSE_PARENTHESIS;
					case '+': return PLUS;
					case '-': return MINUS;
					case '*': return STAR;
					case '/': return SLASH;
					case '%': return PERCENT;
					case '=': return EQUAL;
					default:
						if (Character.isWhitespace(c)) return START;
						if (Character.isLetter(c)) return IDENTIFIER;
						if (Character.isDigit(c)) return NUMBER;
						return ERROR;
				}
			}
		},

		IDENTIFIER(Token.Kind.IDENTIFIER) {
			@Override
			public State next(char c) {
				if (Character.isLetter(c)) return IDENTIFIER;
				if (Character.isDigit(c)) return IDENTIFIER;
				if (c == '_') return UNDERSCORE;
				return ERROR;
			}
		},

		UNDERSCORE() {
			@Override
			public State next(char c) {
				if (Character.isLetter(c)) return IDENTIFIER;
				if (Character.isDigit(c)) return IDENTIFIER;
				return ERROR;
			}
		},

		NUMBER(Token.Kind.NUMBER) {
			@Override
			public State next(char c) {
				if (Character.isDigit(c)) return NUMBER;
				return ERROR;
			}
		},

		PLUS(Token.Plus) {
			@Override
			public State next(char c) {
				switch (c) {
					case '+': return PLUS_PLUS;
					case '=': return PLUS_EQUAL;
					default:  return ERROR;
				}
			}
		},

		MINUS(Token.Minus) {
			@Override
			public State next(char c) {
				switch (c) {
					case '-': return MINUS_MINUS;
					case '=': return MINUS_EQUAL;
					default:  return ERROR;
				}
			}
		},

		STAR(Token.Times) {
			@Override
			public State next(char c) {
				if (c == '=') return STAR_EQUAL;
				return ERROR;
			}
		},

		SLASH(Token.Divide) {
			@Override
			public State next(char c) {
				if (c == '=') return SLASH_EQUAL;
				return ERROR;
			}
		},

		PERCENT(Token.Modulo) {
			@Override
			public State next(char c) {
				if (c == '=') return PERCENT_EQUAL;
				return ERROR;
			}
		},

		EQUAL(Token.Assign),
		PLUS_PLUS(Token.PlusPlus),
		MINUS_MINUS(Token.MinusMinus),
		PLUS_EQUAL(Token.PlusAssign),
		MINUS_EQUAL(Token.MinusAssign),
		STAR_EQUAL(Token.TimesAssign),
		SLASH_EQUAL(Token.DivideAssign),
		PERCENT_EQUAL(Token.ModuloAssign),
		OPEN_PARENTHESIS(Token.OpenParenthesis),
		CLOSE_PARENTHESIS(Token.CloseParenthesis),
		ERROR();

		private Token token;
		private Token.Kind kind;

		private State(Token token) {
			this.kind = token.kind();
			this.token = token;
		}

		private State(Token.Kind kind) {
			this.kind = kind;
			this.token = null;
		}

		private State() {
			this(Token.Kind.ERROR);
		}

		public boolean accepting() {
			return this.kind() != Token.Kind.ERROR;
		}

		public Token token() {
			return this.token;
		}

		public Token.Kind kind() {
			return this.kind;
		}

		public State next(char c) {
			return ERROR;
		}
	}


	private String line;
	private State state;
	private State lastAcceptingState;
	private int startingIndex;
	private int endingIndex;
	private int position;


	public ExpressionScanner(String line) {
		State state = State.START;
		this.line = line;
		this.startingIndex = 0;
		this.endingIndex = -1;
		this.position = 0;
		this.lastAcceptingState = State.ERROR;
	}

	public int position() {
		return this.position;
	}

	public boolean hasNext() {
		return startingIndex < line.length();
	}


	public Token next() {
		State state = State.START;
		lastAcceptingState = State.START;
		Token token;

		endingIndex = startingIndex;
		for (int i = startingIndex; i < line.length(); i++) {
			state = state.next(line.charAt(i));
			if (state == State.ERROR) {
				break;
			} else if (state == State.START) {
				startingIndex = i+1;
			} else if (state.accepting()) {
				lastAcceptingState = state;
				endingIndex = i;
			}
		}

		switch(lastAcceptingState.kind()) {
			case IDENTIFIER:
				String name = line.substring(startingIndex, endingIndex+1);
				token = new Token.IdentifierToken(name);
				break;

			case NUMBER:
				String value = line.substring(startingIndex, endingIndex+1);
				token = new Token.NumberToken(Integer.parseInt(value));
				break;

			case ERROR:
				String text = line.substring(startingIndex);
				token = new Token.ErrorToken(text);
				endingIndex = line.length();
				break;

			default:
				token = lastAcceptingState.token();
				break;
		}
		
		position = startingIndex;
		startingIndex = endingIndex + 1;
		return token;
	}


    public static void main(String[] args){
        Console console = System.console();
		String prompt = "Expression: ";

	    String line = console.readLine(prompt);
	    while (line.length() > 0) {
		    ExpressionScanner scanner = new ExpressionScanner(line);
			while (scanner.hasNext()) {
				System.out.println(scanner.next());
			}
			line = console.readLine(prompt);
        }
    }
}
