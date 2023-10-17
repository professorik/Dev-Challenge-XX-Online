package com.devchallenge.online.util;

import com.devchallenge.online.dto.exceptions.CyclicDependencyException;

import java.util.*;

public class Graph {

    Map<String, Set<String>> adj = new HashMap<>();

    public void addEdge(String u, String v) {
        if (!adj.containsKey(u)) {
            adj.put(u, new HashSet<>());
        }
        if (!adj.containsKey(v)) {
            adj.put(v, new HashSet<>());
        }
        adj.get(u).add(v);
    }

    public List<String> topologicalSort() throws CyclicDependencyException {
        Map<String, Integer> outdegrees = new HashMap<>();
        for (String node : adj.keySet()) {
            outdegrees.put(node, 0);
        }
        for (String node : adj.keySet()) {
            for (String neighbor : adj.get(node)) {
                outdegrees.put(neighbor, outdegrees.get(neighbor) + 1);
            }
        }

        Queue<String> queue = new LinkedList<>();
        for (String key : outdegrees.keySet()) {
            if (outdegrees.get(key) == 0) {
                queue.add(key);
            }
        }

        int visited = 0;
        List<String> topOrder = new ArrayList<>();
        while (!queue.isEmpty()) {
            String u = queue.poll();
            topOrder.add(u);

            for (String node : adj.get(u)) {
                var outdegree = outdegrees.get(node);
                outdegrees.put(node, --outdegree);
                if (outdegree == 0) {
                    queue.add(node);
                }
            }
            visited++;
        }

        if (visited != adj.size()) {
            throw new CyclicDependencyException();
        }
        return topOrder;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("\n");
        for (String key : adj.keySet()) {
            sb.append(key).append(" <- ");
            sb.append(adj.get(key)).append("\n");
        }
        return sb.toString();
    }
}
