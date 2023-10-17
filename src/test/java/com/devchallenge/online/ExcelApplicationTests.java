package com.devchallenge.online;

import com.devchallenge.online.util.Graph;
import com.devchallenge.online.util.Parser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ExcelApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void parserTest() {
		Map<String, Double> values = new HashMap<>();
		values.put("a", 5.0);
		values.put("b", 5.0);
		values.put("c", 5.0);
		values.put("d", 5.0);
		values.put("e", 5.0);
		values.put("f", 5.0);
		values.put("var1", 5.0);
		values.put("var2", 5.0);
		values.put("var3", 5.0);
		values.put("var4", 5.0);
		values.put("var5", 5.0);
		assert(Parser.evaluate("var1+var2+12/6+var3*(var4-var5)", values).equals("12.0"));
		assert(Parser.evaluate("a+b-c*d/e", values).equals("5.0"));
		assert(Parser.evaluate("a+b*f-c*d/e", values).equals("25.0"));
		assert(Parser.evaluate("(a+b)*(c+d)", values).equals("100.0"));
		assert(Parser.evaluate("a+b*c", values).equals("30.0"));
		assert(Parser.evaluate("a*b+c", values).equals("30.0"));
		assert(Parser.evaluate("-a-4+b+(-c)", new HashMap<>()).equals("-4.0"));
	}

	@Test
	void graphTest() {
		Graph g = new Graph();
		g.addEdge("var1", "var2");
		g.addEdge("var1", "var3");
		g.addEdge("var2", "var3");
		g.addEdge("var2", "var4");
		g.addEdge("var3", "var4");
		g.addEdge("var4", "var5");
		g.addEdge("var5", "var6");
		assert(g.topologicalSort().equals(List.of("var1", "var2", "var3", "var4", "var5", "var6")));

		Graph g2 = new Graph();
		g2.addEdge("var1", "var2");
		g2.addEdge("var1", "var3");
		g2.addEdge("var2", "var4");
		g2.addEdge("var4", "var3");
		g2.addEdge("var4", "var5");
		g2.addEdge("var5", "var6");
		g2.addEdge("var3", "var6");
		assert(g2.topologicalSort().equals(List.of("var1", "var2", "var4", "var5", "var3", "var6")));
	}
}
