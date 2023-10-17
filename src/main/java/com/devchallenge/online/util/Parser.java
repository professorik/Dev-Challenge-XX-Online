package com.devchallenge.online.util;

import com.devchallenge.online.dto.exceptions.InvalidFormulaException;

import java.util.*;

public class Parser {

    private static final String regex = "(?<=[-+*/()])|(?=[-+*/()])";
    private static final String varNameRegex = "^[a-zA-Z_$][\\w$]*$";

    public static Set<String> getVariables(String expression) {
        Set<String> result = new HashSet<>();
        String[] tokens = expression.substring(1).split(regex);
        for (String token: tokens) {
            if (token.matches(varNameRegex)) {
                result.add(token);
            }
        }
        return result;
    }

    public static String evaluate(String expression, Map<String, Double> values) throws ArithmeticException, InvalidFormulaException{
        var expr = expression.charAt(0) == '='? expression.substring(1): expression;
        List<String> output = shuntingYard(expr.split(regex));
        Stack<Double> result = new Stack<>();
        for (String token: output) {
            if (isOperator(token)) {
                if (result.size() > 1) {
                    result.push(calculate(result.pop(), token, result.pop()));
                } else {
                    result.push(calculate(result.pop(), token));
                }
                continue;
            }
            double value;
            try {
                value = Double.parseDouble(token);
            }catch (NumberFormatException e) {
                value = values.getOrDefault(token, 0.0);
            }
            result.push(value);
        }
        return String.valueOf(result.peek());
    }

    private static List<String> shuntingYard(String[] tokens) throws InvalidFormulaException {
        Stack<String> operators = new Stack<>();
        List<String> output = new ArrayList<>();
        for (String token: tokens) {
            if (token.equals("(")) {
                operators.push(token);
            }else if (isOperator(token)) {
                while (!operators.isEmpty()
                        && getPriority(token) <= getPriority(operators.peek())
                        && hasLeftAssociativity(token)) {

                    output.add(operators.pop());
                }
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.add(operators.pop());
                }
                operators.pop();
                if (!operators.isEmpty() && isOperator(operators.peek())) {
                    output.add(operators.pop());
                }
            } else {
                output.add(token);
            }
        }
        while (!operators.isEmpty()) {
            if (operators.peek().equals("(")) {
                throw new InvalidFormulaException();
            }
            output.add(operators.pop());
        }
        return output;
    }

    private static int getPriority(String operator) {
        return switch (operator){
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            default -> -1;
        };
    }

    private static boolean hasLeftAssociativity(String operator) {
        return operator.equals("+") || operator.equals("-") ||operator.equals("/") ||operator.equals("*");
    }

    private static double calculate(double first, String operator) {
        return switch (operator) {
            case "+" -> first;
            case "-" -> -first;
            default -> 0;
        };
    }

    private static double calculate(double first, String operator, double second) throws ArithmeticException {
        if (first == 0 && operator.equals("/")) {
            throw new ArithmeticException("division by zero");
        }
        return switch (operator) {
            case "+" -> first + second;
            case "-" -> second - first;
            case "*" -> first * second;
            case "/" -> second / first;
            default -> 0;
        };
    }

    private static boolean isOperator(String token) {
        return getPriority(token) != -1;
    }
}
