package com.devchallenge.online.util;

import com.devchallenge.online.dto.exceptions.InvalidFormulaException;

import java.util.*;

public class Parser {

    private static final String regex = "(?<=[-+*/(),])|(?=[-+*/(),])";
    private static final String varNameRegex = "^[a-zA-Z_$][\\w$]*$";

    public static Set<String> getVariables(String expression) {
        Set<String> result = new HashSet<>();
        String[] tokens = expression.substring(1).split(regex);
        for (String token : tokens) {
            if (token.matches(varNameRegex)) {
                result.add(token);
            }
        }
        return result;
    }

    public static String evaluate(String expression, Map<String, Double> values) throws ArithmeticException, InvalidFormulaException {
        var expr = expression.charAt(0) == '=' ? expression.substring(1) : expression;
        List<String> output = shuntingYard(expr.split(regex));
        Stack<Double> result = new Stack<>();
        for (String token : output) {
            System.out.println(result + " " + token);
            if (isFunction(token)) {
                result.push(calculate(token, result));
                continue;
            }else if (isOperator(token)) {
                if (isUnary(token)) {
                    result.push(calculate(result.pop(), token));
                } else {
                    result.push(calculate(result.pop(), token, result.pop()));
                }
                continue;
            }
            double value;
            try {
                value = Double.parseDouble(token);
            } catch (NumberFormatException e) {
                value = values.getOrDefault(token, 0.0);
            }
            result.push(value);
        }
        return String.valueOf(result.peek());
    }

    private static boolean isFunction(String token) {
        return token.equalsIgnoreCase("sum") ||
                token.equalsIgnoreCase("avg") ||
                token.equalsIgnoreCase("min") ||
                token.equalsIgnoreCase("max") ||
                token.equalsIgnoreCase("sin");
    }

    private static List<String> shuntingYard(String[] tokens) throws InvalidFormulaException {
        System.out.println(Arrays.toString(tokens));

        Stack<String> stack = new Stack<>();
        Stack<Boolean> prev = new Stack<>();
        Stack<Integer> argAmount = new Stack<>();
        boolean unary = true;
        List<String> output = new ArrayList<>();

        for (String token : tokens) {
            if (isFunction(token)) {
                stack.push(token);
                argAmount.push(0);
                if (!prev.isEmpty()) {
                    prev.pop();
                    prev.push(true);
                }
                prev.push(false);
            } else if (token.equals(",")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                if (!prev.empty() && prev.pop()) {
                    argAmount.push(argAmount.pop() + 1);
                    prev.push(false);
                }
                unary = true;
            } else if (isOperator(token)) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    var tokenPriority = getPriority(token);
                    var topPriority = getPriority(stack.peek());

                    if (tokenPriority > topPriority) break;
                    else if (tokenPriority == topPriority && !hasLeftAssociativity(token)) break;

                    output.add(stack.pop());
                }
                if (unary){
                    stack.push(token + "#");
                }else {
                    stack.push(token);
                }
            } else if (token.equals("(")) {
                stack.push(token);
                unary = true;
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                stack.pop();
                if (!stack.isEmpty() && isFunction(stack.peek())) {
                    var f = stack.pop();
                    var a = argAmount.pop();
                    if (prev.pop()) {
                        ++a;
                    }
                    output.add(String.valueOf(a));
                    output.add(f);
                    System.out.println(a + " " + f + " " + output + " " + stack);
                }
                unary = false;
            } else {
                output.add(token);
                if (!prev.isEmpty()) {
                    prev.pop();
                    prev.push(true);
                }
                unary = false;
            }
        }
        while (!stack.isEmpty()) {
            if (stack.peek().equals("(")) {
                throw new InvalidFormulaException();
            }
            output.add(stack.pop());
        }
        System.out.println(output);
        return output;
    }

    private static int getPriority(String operator) {
        return switch (operator.toLowerCase()) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "+#", "-#" -> 3;
            case "sum", "min", "max", "avg" -> 4;
            default -> -1;
        };
    }

    private static boolean isUnary(String token) {
        return token.equals("+#") || token.equals("-#");
    }

    private static boolean hasLeftAssociativity(String operator) {
        return operator.equals("+") || operator.equals("-") || operator.equals("/") || operator.equals("*");
    }

    private static double calculate(double first, String operator) {
        return switch (operator) {
            case "+#" -> first;
            case "-#" -> -first;
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

    private static double calculate(String function, Stack<Double> stack) {
        int length = stack.pop().intValue();
        Stack<Double> args = new Stack<>();
        for (int i = 0; i < length; i++) {
            args.push(stack.pop());
        }
        double result = 0;
        switch (function.toLowerCase()) {
            case "sum" -> {
                while (!args.isEmpty()) {
                    result += args.pop();
                }
            }
            case "avg" -> {
                int n = args.size();
                while (!args.isEmpty()) {
                    result += args.pop() / n;
                }
            }
            case "min" -> {
                result = args.pop();
                while (!args.isEmpty()) {
                    result = Math.min(result, args.pop());
                }
            }
            case "max" -> {
                result = args.pop();
                while (!args.isEmpty()) {
                    result = Math.max(result, args.pop());
                }
            }
            case "sin" -> result = Math.sin(args.pop());
        }
        return result;
    }

    private static boolean isOperator(String token) {
        return getPriority(token) != -1;
    }
}
