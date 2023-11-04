package com.devchallenge.online;

import com.devchallenge.online.util.Parser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author professorik
 * @created 04/11/2023 - 12:08
 * @project online
 */
public class Test {
    public static Map<String, Double> values = new HashMap<>();

    public static void main(String[] args) {
        values.put("pi", 3.1415);
        eval("sin(max(1, 3, 2)/3 * pi)");
        eval("sin(pi*max(2, 3)/3)");
        eval("sin(pi*max(-2, 3)/3)");
        eval("sin(pi*max(2, -3, -4)/3)");
        //eval("sin(pi*max(3, 3-3)/3)");
    }

    public static void eval(String expression) {
        var processedExpression = expression.replaceAll("\\s", "");
        var res = Parser.evaluate(processedExpression, values);
        System.out.println(res);
    }
}
//2 3 max 3 ÷ π × sin
