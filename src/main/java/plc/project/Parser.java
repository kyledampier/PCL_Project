package plc.project;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * The parser takes the sequence of tokens emitted by the lexer and turns that
 * into a structured representation of the program, called the Abstract Syntax
 * Tree (AST).
 *
 * The parser has a similar architecture to the lexer, just with {@link Token}s
 * instead of characters. As before, {@link #peek(Object...)} and {@link
 * #match(Object...)} are helpers to make the implementation easier.
 *
 * This type of parser is called <em>recursive descent</em>. Each rule in our
 * grammar will have it's own function, and reference to other rules correspond
 * to calling that functions.
 */
public final class Parser {

    private final TokenStream tokens;

    public Parser(List<Token> tokens) {
        this.tokens = new TokenStream(tokens);
    }

    /**
     * Parses the {@code source} rule.
     */
    public Ast.Source parseSource() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Parses the {@code f     * next tokens start a field, aka {@code LET}.ield} rule. This method should only be called if the
     */
    public Ast.Field parseField() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Parses the {@code method} rule. This method should only be called if the
     * next tokens start a method, aka {@code DEF}.
     */
    public Ast.Method parseMethod() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Parses the {@code statement} rule and delegates to the necessary method.
     * If the next tokens do not start a declaration, if, while, or return
     * statement, then it is an expression/assignment statement.
     */
    public Ast.Stmt parseStatement() throws ParseException {
        try {
            return new Ast.Stmt.Expression(parseExpression());
        } catch (ParseException p) {
            throw new ParseException(p.getMessage(), p.getIndex());
        }
    }

    /**
     * Parses a declaration statement from the {@code statement} rule. This
     * method should only be called if the next tokens start a declaration
     * statement, aka {@code LET}.
     */
    public Ast.Stmt.Declaration parseDeclarationStatement() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Parses an if statement from the {@code statement} rule. This method
     * should only be called if the next tokens start an if statement, aka
     * {@code IF}.
     */
    public Ast.Stmt.If parseIfStatement() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Parses a for statement from the {@code statement} rule. This method
     * should only be called if the next tokens start a for statement, aka
     * {@code FOR}.
     */
    public Ast.Stmt.For parseForStatement() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Parses a while statement from the {@code statement} rule. This method
     * should only be called if the next tokens start a while statement, aka
     * {@code WHILE}.
     */
    public Ast.Stmt.While parseWhileStatement() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Parses a return statement from the {@code statement} rule. This method
     * should only be called if the next tokens start a return statement, aka
     * {@code RETURN}.
     */
    public Ast.Stmt.Return parseReturnStatement() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Parses the {@code expression} rule.
     */
    public Ast.Expr parseExpression() throws ParseException {
        try {
            return parseLogicalExpression();
        } catch (ParseException p) {
            throw new ParseException(p.getMessage(), p.getIndex());
        }
    }

    /**
     * Parses the {@code logical-expression} rule.
     */
    public Ast.Expr parseLogicalExpression() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Parses the {@code equality-expression} rule.
     */
    public Ast.Expr parseEqualityExpression() throws ParseException {
        try {
            Ast.Expr leftExpr = parseAdditiveExpression();
            Ast.Expr.Binary output = null;

            while (peek("!=")
                    || peek("==")
                    || peek(">=")
                    || peek(">")
                    || peek("<=")
                    || peek("<")) { // right
                Ast.Expr rightExpr;
                String operation;

                // find which operation
                if(match("!=")) {
                    operation = tokens.get(-1).getLiteral();
                } else if(match("==")) {
                    operation = tokens.get(-1).getLiteral();
                } else if(match(">=")) {
                    operation = tokens.get(-1).getLiteral();
                } else if(match(">")) {
                    operation = tokens.get(-1).getLiteral();
                } else if(match("<=")) {
                    operation = tokens.get(-1).getLiteral();
                } else {
                    match("<");
                    operation = tokens.get(-1).getLiteral();
                }
                rightExpr = parseEqualityExpression();

                // check for initial output
                if (Objects.isNull(output)) {
                    output = new Ast.Expr.Binary(operation, leftExpr, rightExpr);
                } else {
                    output = new Ast.Expr.Binary(operation, output, rightExpr);
                }
            }
            return output;
        } catch(ParseException p) {
            throw new ParseException(p.getMessage(), p.getIndex());
        }
    }

    /**
     * Parses the {@code additive-expression} rule.
     */
    public Ast.Expr parseAdditiveExpression() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Parses the {@code multiplicative-expression} rule.
     */
    public Ast.Expr parseMultiplicativeExpression() throws ParseException {
        try {
            Ast.Expr secondaryExpr = parseSecondaryExpression();
            Ast.Expr.Binary output = null;

            while (peek("/") || peek("*")) { // right
                Ast.Expr rightExpr;
                String operation;

                // find which operation
                if(match("/")) {
                    operation = tokens.get(-1).getLiteral();
                }  else {
                    match("*");
                    operation = tokens.get(-1).getLiteral();
                }
                rightExpr = parseSecondaryExpression();

                // check for initial output
                if (Objects.isNull(output)) {
                    output = new Ast.Expr.Binary(operation, secondaryExpr, rightExpr);
                } else {
                    output = new Ast.Expr.Binary(operation, output, rightExpr);
                }
            }
            return output;
        } catch(ParseException p) {
            throw new ParseException(p.getMessage(), p.getIndex());
        }
    }

    /**
     * Parses the {@code secondary-expression} rule.
     */
    public Ast.Expr parseSecondaryExpression() throws ParseException {
        try {
          Ast.Expr initalExpr = parsePrimaryExpression();
          List<Ast.Expr> args = new ArrayList<Ast.Expr>();
          String name = null;

          while (match(".")) {
              if (!match(Token.Type.IDENTIFIER)) {
                  throw new ParseException("Invalid Identifier", tokens.get(0).getIndex());
              }
              name = tokens.get(-1).getLiteral();

              // Identifier found
              if (!peek("(")) {
                  // No expression after
                  return new Ast.Expr.Access(Optional.empty(), name);

              } else {
                  // Found '('
                  // Found expression after
                  Ast.Expr initArgsExpr = parseExpression();
                  args.add(initArgsExpr);

                  while (match(",")) {
                      args.add(parseExpression());
                  }

                  // Check for closing parentheses
                  if(!match(")")) {
                      throw new ParseException("Invalid function closing parentheses not found", tokens.get(0).getIndex());
                  }
              }
          }

          if (args.size() > 0 && Objects.nonNull(name)) {
              return new Ast.Expr.Function(Optional.empty(), name, args);
          }
          return initalExpr;

        } catch (ParseException p) {
            throw new ParseException(p.getMessage(), p.getIndex());
        }
    }

    /**
     * Parses the {@code primary-expression} rule. This is the top-level rule
     * for expressions and includes literal values, grouping, variables, and
     * functions. It may be helpful to break these up into other methods but is
     * not strictly necessary.
     */
    public Ast.Expr parsePrimaryExpression() throws ParseException {
//        throw new UnsupportedOperationException(); //TODO
        if (match("NIL")) {
            return new Ast.Expr.Literal(null);
        } else if (match("TRUE")) {
            return new Ast.Expr.Literal(true);
        } else if (match("FALSE")) {
            return new Ast.Expr.Literal(false);
        } else if (match(Token.Type.INTEGER)) {
            // INTEGER LITERAL FOUND
            return new Ast.Expr.Literal(new BigInteger(tokens.get(-1).getLiteral()));
        } else if (match(Token.Type.DECIMAL)) {
            // DECIMAL LITERAL FOUND
            return new Ast.Expr.Literal(new BigDecimal(tokens.get(-1).getLiteral()));
        } else if (match(Token.Type.CHARACTER)) {
            // CHARACTER LITERAL FOUND
            // 'a'
            char selectedChar = tokens.get(-1).getLiteral().charAt(1);
            return new Ast.Expr.Literal(selectedChar);
        } else if (match(Token.Type.STRING)) {
            // STRING LITERAL FOUND
            // "example string"
            String str = tokens.get(-1).getLiteral();
            str = str.substring(1, str.length() - 1);
            return new Ast.Expr.Literal(str);
        } else if (match(Token.Type.IDENTIFIER)) {
            // IDENTIFIER FOUND
            String name = tokens.get(-1).getLiteral();

            if (!match("(")) {
                // No expression after
                // TODO: Fill out Optional
                return new Ast.Expr.Access(Optional.empty(), name);

            } else {

                // Found expression after
                if (!peek(")")) {
                    Ast.Expr initalExpr = parseExpression();
                    List<Ast.Expr> args = new ArrayList<Ast.Expr>();
                    args.add(initalExpr);

                    while (match(",")) {
                        args.add(parseExpression());
                    }

                    // Check for closing parentheses
                    if (match(")")) {
                        // TODO: Fill out Optional
                        return new Ast.Expr.Function(Optional.empty(), name, args);
                    } else {
                        throw new ParseException("Invalid function closing parentheses not found", tokens.get(0).getIndex());
                    }
                } else {
                    if (!match(")")) {
                        throw new ParseException("Invalid function closing parentheses not found", tokens.get(0).getIndex());
                    } else {
                        return new Ast.Expr.Function(Optional.empty(), name, Arrays.asList());
                    }
                }


            }
        } else if (match("(")) {
            Ast.Expr expr = parseExpression();
            if (!match(')')) {
                throw new ParseException("Expected closing parenthesis", -1);
            }
            return new Ast.Stmt.Expr.Group(expr);
        } else {
            throw new ParseException("Invalid Primary Expression", tokens.get(0).getIndex());
            // TODO: handle storing the actual character index instead of I
        }
    }

    /**
     * As in the lexer, returns {@code true} if the current sequence of tokens
     * matches the given patterns. Unlike the lexer, the pattern is not a regex;
     * instead it is either a {@link Token.Type}, which matches if the token's
     * type is the same, or a {@link String}, which matches if the token's
     * literal is the same.
     *
     * In other words, {@code Token(IDENTIFIER, "literal")} is matched by both
     * {@code peek(Token.Type.IDENTIFIER)} and {@code peek("literal")}.
     */
    private boolean peek(Object... patterns) {
        for (int i = 0; i < patterns.length; i++) {
           if (!tokens.has(i)) {
               return false;
           } else if (patterns[i] instanceof Token.Type) {
               if (patterns[i] != tokens.get(i).getType()) {
                   return false;
               }
           } else if (patterns[i] instanceof String) {
               if (!patterns[i].equals(tokens.get(i).getLiteral())) {
                   return false;
               }
           } else {
               throw new AssertionError("Invalid pattern object: "
                                            + patterns[i].getClass());
           }
        }
        return true;
    }

    /**
     * As in the lexer, returns {@code true} if {@link #peek(Object...)} is true
     * and advances the token stream.
     */
    private boolean match(Object... patterns) {
        boolean peek = peek(patterns);
        if (peek) {
            for (int i = 0; i < patterns.length; i++)
                tokens.advance();
        }
        return peek;
    }

    private static final class TokenStream {

        private final List<Token> tokens;
        private int index = 0;

        private TokenStream(List<Token> tokens) {
            this.tokens = tokens;
        }

        /**
         * Returns true if there is a token at index + offset.
         */
        public boolean has(int offset) {
            return index + offset < tokens.size();
        }

        /**
         * Gets the token at index + offset.
         */
        public Token get(int offset) {
            return tokens.get(index + offset);
        }

        /**
         * Advances to the next token, incrementing the index.
         */
        public void advance() {
            index++;
        }

    }

}
