/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.customtype.values.other;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.number.DecimalType;
import ru.mousecray.mousecore.api.customtype.values.number.IntegralType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseNumbers;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class ExpressionType extends OtherType<String> {
    public static final ExpressionType ZERO = ExpressionType.create("0");

    private static final Map<String, Integer> FUNCTION_ARITIES = Map.ofEntries(
            Map.entry("floor", 1),
            Map.entry("ceil", 1),
            Map.entry("round", 1),
            Map.entry("abs", 1),
            Map.entry("sqrt", 1),
            Map.entry("min", 2),
            Map.entry("max", 2),
            Map.entry("pow", 2),
            Map.entry("clamp", 3),
            Map.entry("if", 3)
    );

    static { storage.put(ExpressionType.class, ExpressionType::parse); }

    private final transient List<Token> rpn;
    private final           Set<String> variables;

    private ExpressionType(String rawExpression, List<Token> rpn, Set<String> variables) {
        super(rawExpression);
        this.rpn = rpn;
        this.variables = Collections.unmodifiableSet(variables);
    }

    public static ExpressionType create(String expression) throws ValueFormatException {
        String clean = MouseStrings.trimWith(Objects.requireNonNull(expression, "Expression cannot be null"), true, '\t');
        if (clean == null || clean.isEmpty()) throw new ValueFormatException("Expression cannot be empty");

        Set<String> discoveredVars = new LinkedHashSet<>();
        List<Token> tokens         = tokenize(clean, discoveredVars);
        List<Token> rpn            = compileToRpn(tokens, clean);
        dryRunValidate(rpn, discoveredVars, clean);

        return new ExpressionType(clean, rpn, discoveredVars);
    }

    public static ExpressionType parse(@Nullable Object val) throws UnsupportedValException, ValueFormatException {
        if (val instanceof ExpressionType expr) return expr;
        else if (val instanceof String str) return parse(str);
        throw new UnsupportedValException("Cannot parse ExpressionType from " + val);
    }

    public static ExpressionType parse(@Nullable String raw) throws ValueFormatException {
        String clean = MouseStrings.trimWith(raw, true, '\t');
        if (clean == null || clean.isEmpty()) throw new ValueFormatException("Cannot parse empty expression string");
        return create(clean);
    }

    public static ExpressionType fromString(@Nullable String raw) throws ValueFormatException { return parse(raw); }

    public String asString()                                                                  { return value; }
    public Set<String> asVariables()                                                          { return variables; }
    public boolean hasVariables()                                                             { return !variables.isEmpty(); }

    public void validateVariables(Collection<String> allowedVariables) throws ValueFormatException {
        Objects.requireNonNull(allowedVariables, "Allowed variables collection cannot be null");
        for (String var : variables) {
            if (!allowedVariables.contains(var)) {
                throw new ValueFormatException("Unknown variable '{" + var + "}' in expression '" + value
                        + "'. Allowed variables: " + allowedVariables);
            }
        }
    }

    public double evaluateDouble(@Nullable Map<String, Double> varValues) {
        double[] stack = new double[rpn.size()];
        int      sp    = 0;

        for (Token token : rpn) {
            switch (token.type) {
                case NUMBER -> stack[sp++] = token.numberValue;
                case VARIABLE -> {
                    Double val = varValues != null ? varValues.get(token.text) : null;
                    if (val == null) throw new IllegalArgumentException("Missing variable '{" + token.text + "}' for expression: " + value);
                    stack[sp++] = val;
                }
                case UNARY_OPERATOR -> {
                    double a = stack[--sp];
                    stack[sp++] = switch (token.text) {
                        case "u-" -> -a;
                        case "u+" -> a;
                        default -> throw new IllegalStateException("Unknown unary operator: " + token.text);
                    };
                }
                case OPERATOR -> {
                    double b = stack[--sp];
                    double a = stack[--sp];
                    stack[sp++] = switch (token.text) {
                        case "+" -> a + b;
                        case "-" -> a - b;
                        case "*" -> a * b;
                        case "/" -> a / b;
                        case "%" -> a % b;
                        case "^" -> Math.pow(a, b);
                        case "<" -> a < b ? 1.0D : 0.0D;
                        case "<=" -> a <= b ? 1.0D : 0.0D;
                        case ">" -> a > b ? 1.0D : 0.0D;
                        case ">=" -> a >= b ? 1.0D : 0.0D;
                        case "==" -> Double.compare(a, b) == 0 ? 1.0D : 0.0D;
                        case "!=" -> Double.compare(a, b) != 0 ? 1.0D : 0.0D;
                        default -> throw new IllegalStateException("Unknown operator: " + token.text);
                    };
                }
                case FUNCTION -> {
                    stack[sp - token.argCount] = evaluateFunction(token.text, stack, sp - token.argCount);
                    sp = sp - token.argCount + 1;
                }
                default -> throw new IllegalStateException("Unexpected RPN token: " + token);
            }
        }
        return stack[0];
    }

    public double evaluateDouble() {
        return evaluateDouble(Collections.emptyMap());
    }

    public DecimalType evaluate(@Nullable Map<String, ? extends NumberType<?>> varValues) {
        if (varValues == null || varValues.isEmpty()) return DecimalType.create(evaluateDouble());
        Map<String, Double> doubles = new HashMap<>(varValues.size());
        varValues.forEach((k, v) -> doubles.put(k, v.asDouble()));
        return DecimalType.create(evaluateDouble(doubles));
    }

    public DecimalType evaluate() {
        return DecimalType.create(evaluateDouble());
    }

    public IntegralType evaluateIntegral(@Nullable Map<String, ? extends NumberType<?>> varValues) {
        return IntegralType.create(MouseNumbers.round(evaluate(varValues).asDouble()));
    }

    private static double evaluateFunction(String name, double[] stack, int baseIdx) {
        return switch (name) {
            case "floor" -> MouseNumbers.floor(stack[baseIdx]);
            case "ceil" -> MouseNumbers.ceil(stack[baseIdx]);
            case "round" -> MouseNumbers.round(stack[baseIdx]);
            case "abs" -> Math.abs(stack[baseIdx]);
            case "sqrt" -> Math.sqrt(stack[baseIdx]);
            case "min" -> Math.min(stack[baseIdx], stack[baseIdx + 1]);
            case "max" -> Math.max(stack[baseIdx], stack[baseIdx + 1]);
            case "pow" -> Math.pow(stack[baseIdx], stack[baseIdx + 1]);
            case "clamp" -> MouseNumbers.clamp(stack[baseIdx], stack[baseIdx + 1], stack[baseIdx + 2]);
            case "if" -> {
                double cond = stack[baseIdx];
                yield (cond != 0.0D && !Double.isNaN(cond)) ? stack[baseIdx + 1] : stack[baseIdx + 2];
            }
            default -> throw new IllegalStateException("Unknown function: " + name);
        };
    }

    private static List<Token> tokenize(String expr, Set<String> discoveredVars) {
        List<Token> tokens = new ArrayList<>();
        int         len    = expr.length();
        int         i      = 0;

        while (i < len) {
            char c = expr.charAt(i);
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (Character.isDigit(c) || (c == '.' && i + 1 < len && Character.isDigit(expr.charAt(i + 1)))) {
                int     start  = i;
                boolean hasDot = false;
                while (i < len) {
                    char ch = expr.charAt(i);
                    if (Character.isDigit(ch)) i++;
                    else if (ch == '.' && !hasDot) {
                        hasDot = true;
                        i++;
                    } else break;
                }
                String numStr = expr.substring(start, i);
                Double parsed = MouseNumbers.tryParseDouble(numStr);
                if (parsed == null) throw new ValueFormatException("Invalid number literal '" + numStr + "' in expression: " + expr);
                tokens.add(new Token(TokenType.NUMBER, numStr, parsed, 0));
                continue;
            }

            if (c == '{') {
                int closeIdx = expr.indexOf('}', i + 1);
                if (closeIdx == -1) throw new ValueFormatException("Unclosed variable brace '{' in expression: " + expr);
                String varName = MouseStrings.trimWith(expr.substring(i + 1, closeIdx), true, '\t');
                if (varName == null || varName.isEmpty()) throw new ValueFormatException("Empty variable name '{}' in expression: " + expr);
                if (varName.contains(" ") || varName.contains("\t") || varName.contains("{")) {
                    throw new ValueFormatException("Invalid variable name '{" + varName + "}' in expression: " + expr);
                }
                discoveredVars.add(varName);
                tokens.add(new Token(TokenType.VARIABLE, varName, 0.0D, 0));
                i = closeIdx + 1;
                continue;
            }

            if (Character.isLetter(c) || c == '_') {
                int start = i;
                while (i < len && (Character.isLetterOrDigit(expr.charAt(i)) || expr.charAt(i) == '_')) i++;
                String ident = expr.substring(start, i).toLowerCase(Locale.ROOT);
                if (ident.equals("pi")) tokens.add(new Token(TokenType.NUMBER, ident, Math.PI, 0));
                else if (ident.equals("e")) tokens.add(new Token(TokenType.NUMBER, ident, Math.E, 0));
                else if (FUNCTION_ARITIES.containsKey(ident)) tokens.add(new Token(TokenType.FUNCTION, ident, 0.0D, FUNCTION_ARITIES.get(ident)));
                else throw new ValueFormatException("Unknown function or constant '" + ident + "' in expression: " + expr);
                continue;
            }

            if (i + 1 < len) {
                String two = expr.substring(i, i + 2);
                if (two.equals("<=") || two.equals(">=") || two.equals("==") || two.equals("!=")) {
                    tokens.add(new Token(TokenType.OPERATOR, two, 0.0D, 2));
                    i += 2;
                    continue;
                }
            }

            switch (c) {
                case '(' -> tokens.add(new Token(TokenType.LEFT_PAREN, "(", 0.0D, 0));
                case ')' -> tokens.add(new Token(TokenType.RIGHT_PAREN, ")", 0.0D, 0));
                case ',' -> tokens.add(new Token(TokenType.COMMA, ",", 0.0D, 0));
                case '+', '-' -> {
                    boolean isUnary = tokens.isEmpty() || isPrefixContext(tokens.get(tokens.size() - 1).type);
                    if (isUnary) tokens.add(new Token(TokenType.UNARY_OPERATOR, "u" + c, 0.0D, 1));
                    else tokens.add(new Token(TokenType.OPERATOR, String.valueOf(c), 0.0D, 2));
                }
                case '*', '/', '%', '^', '<', '>' -> tokens.add(new Token(TokenType.OPERATOR, String.valueOf(c), 0.0D, 2));
                default -> throw new ValueFormatException("Unexpected character '" + c + "' at position " + i + " in expression: " + expr);
            }
            i++;
        }

        if (tokens.isEmpty()) throw new ValueFormatException("Expression contains no valid tokens: " + expr);
        return tokens;
    }

    private static boolean isPrefixContext(TokenType prevType) {
        return prevType == TokenType.OPERATOR
                || prevType == TokenType.UNARY_OPERATOR
                || prevType == TokenType.LEFT_PAREN
                || prevType == TokenType.COMMA;
    }

    private static List<Token> compileToRpn(List<Token> tokens, String rawExpr) {
        List<Token>    output        = new ArrayList<>(tokens.size());
        Deque<Token>   opStack       = new ArrayDeque<>();
        Deque<Integer> argCountStack = new ArrayDeque<>();

        Token prev = null;
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            switch (token.type) {
                case NUMBER, VARIABLE -> output.add(token);

                case FUNCTION -> {
                    if (i + 1 >= tokens.size() || tokens.get(i + 1).type != TokenType.LEFT_PAREN) {
                        throw new ValueFormatException("Expected '(' after function '" + token.text + "' in expression: " + rawExpr);
                    }
                    opStack.push(token);
                }

                case COMMA -> {
                    if (prev == null || prev.type == TokenType.LEFT_PAREN || prev.type == TokenType.COMMA) {
                        throw new ValueFormatException("Empty function argument before ',' in expression: " + rawExpr);
                    }
                    while (!opStack.isEmpty() && opStack.peek().type != TokenType.LEFT_PAREN) output.add(opStack.pop());
                    if (opStack.isEmpty() || argCountStack.isEmpty())
                        throw new ValueFormatException("Misplaced ',' outside function call in expression: " + rawExpr);
                    argCountStack.push(argCountStack.pop() + 1);
                }

                case OPERATOR, UNARY_OPERATOR -> {
                    int     prec      = getPrecedence(token);
                    boolean leftAssoc = isLeftAssociative(token);
                    while (!opStack.isEmpty()) {
                        Token top = opStack.peek();
                        if (top.type != TokenType.OPERATOR && top.type != TokenType.UNARY_OPERATOR) break;
                        int topPrec = getPrecedence(top);
                        if ((leftAssoc && topPrec >= prec) || (!leftAssoc && topPrec > prec)) output.add(opStack.pop());
                        else break;
                    }
                    opStack.push(token);
                }

                case LEFT_PAREN -> {
                    if (!opStack.isEmpty() && opStack.peek().type == TokenType.FUNCTION) argCountStack.push(1);
                    opStack.push(token);
                }

                case RIGHT_PAREN -> {
                    if (prev != null && prev.type == TokenType.COMMA) throw new ValueFormatException("Trailing ',' before ')' in expression: " + rawExpr);
                    while (!opStack.isEmpty() && opStack.peek().type != TokenType.LEFT_PAREN) output.add(opStack.pop());
                    if (opStack.isEmpty()) throw new ValueFormatException("Mismatched parentheses: unexpected ')' in expression: " + rawExpr);
                    opStack.pop();

                    if (!opStack.isEmpty() && opStack.peek().type == TokenType.FUNCTION) {
                        Token func       = opStack.pop();
                        int   actualArgs = (prev != null && prev.type == TokenType.LEFT_PAREN) ? 0 : argCountStack.pop();
                        if (prev != null && prev.type == TokenType.LEFT_PAREN) argCountStack.pop();

                        if (actualArgs != func.argCount) {
                            throw new ValueFormatException("Function '" + func.text + "' expects " + func.argCount
                                    + " arguments, but got " + actualArgs + " in expression: " + rawExpr);
                        }
                        output.add(func);
                    } else if (prev != null && prev.type == TokenType.LEFT_PAREN) {
                        throw new ValueFormatException("Empty parentheses '()' in expression: " + rawExpr);
                    }
                }
            }
            prev = token;
        }

        while (!opStack.isEmpty()) {
            Token top = opStack.pop();
            if (top.type == TokenType.LEFT_PAREN || top.type == TokenType.RIGHT_PAREN) {
                throw new ValueFormatException("Mismatched parentheses: unclosed '(' in expression: " + rawExpr);
            }
            output.add(top);
        }

        return Collections.unmodifiableList(output);
    }

    private static void dryRunValidate(List<Token> rpn, Set<String> discoveredVars, String rawExpr) {
        int stackDepth = 0;
        for (Token token : rpn) {
            switch (token.type) {
                case NUMBER, VARIABLE -> stackDepth++;
                case UNARY_OPERATOR -> {
                    if (stackDepth < 1) throw new ValueFormatException("Missing operand for unary operator '" + token.text + "' in expression: " + rawExpr);
                }
                case OPERATOR -> {
                    if (stackDepth < 2) throw new ValueFormatException("Missing operand for operator '" + token.text + "' in expression: " + rawExpr);
                    stackDepth--;
                }
                case FUNCTION -> {
                    if (stackDepth < token.argCount) {
                        throw new ValueFormatException("Not enough arguments for function '" + token.text + "' in expression: " + rawExpr);
                    }
                    stackDepth = stackDepth - token.argCount + 1;
                }
                default -> throw new ValueFormatException("Unexpected token in compiled RPN: " + token);
            }
        }

        if (stackDepth != 1) {
            throw new ValueFormatException("Invalid expression structure (missing operator or extra operands) in: " + rawExpr);
        }

        Map<String, Double> testVars = new HashMap<>(discoveredVars.size());
        double              seed     = 1.17D;
        for (String var : discoveredVars) {
            testVars.put(var, seed);
            seed += 1.31D;
        }
        ExpressionType probe = new ExpressionType(rawExpr, rpn, discoveredVars);
        probe.evaluateDouble(testVars);
    }

    private static int getPrecedence(Token token) {
        if (token.type == TokenType.UNARY_OPERATOR) return 5;
        return switch (token.text) {
            case "==", "!=", "<", "<=", ">", ">=" -> 1;
            case "+", "-" -> 2;
            case "*", "/", "%" -> 3;
            case "^" -> 4;
            default -> 0;
        };
    }

    private static boolean isLeftAssociative(Token token) {
        if (token.type == TokenType.UNARY_OPERATOR) return false;
        return !token.text.equals("^");
    }

    @Override public IValType getValType()                                                { return ValType.EXPRESSION; }

    @Override @SuppressWarnings("unchecked") public ExpressionType createType(Object val) { return parse(val); }

    private enum TokenType {
        NUMBER, VARIABLE, OPERATOR, UNARY_OPERATOR, FUNCTION, LEFT_PAREN, RIGHT_PAREN, COMMA
    }

    private record Token(TokenType type, String text, double numberValue, int argCount) { }
}