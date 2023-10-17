package com.devchallenge.online.service;

import com.devchallenge.online.dto.CellDto;
import com.devchallenge.online.dto.exceptions.CyclicDependencyException;
import com.devchallenge.online.dto.exceptions.NaNException;
import com.devchallenge.online.model.*;
import com.devchallenge.online.repository.CellRepository;
import com.devchallenge.online.util.*;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExcelService {

    @Autowired
    private CellRepository cellRepository;

    public List<Cell> getSheet(String sheetId) {
        return cellRepository.findAllById_SheetId(sheetId);
    }

    public Optional<Cell> getCell(String sheetId, String cellId) {
        return cellRepository.findById(new CellId(sheetId, cellId));
    }

    public Cell setCell(String sheetId, String cellId, String expression) throws Exception {
        if (expression.charAt(0) != '=') {
            var startCell = new Cell(new CellId(sheetId, cellId), expression, expression, List.of());
            cellRepository.saveAll(getUpdatedCells(startCell));
            return startCell;
        }
        var processedExpression = expression.replaceAll("\\s", "");

        var variables = Parser.getVariables(processedExpression);
        if (variables.contains(cellId)) {
            throw new CyclicDependencyException();
        }
        List<CellId> ids = new ArrayList<>();
        for (String variable : variables) {
            ids.add(new CellId(sheetId, variable));
        }
        List<Cell> cells = cellRepository.findAllById(ids);
        if (hasCyclicDependency(cellId, cells, sheetId)) {
            throw new CyclicDependencyException();
        }
        Map<String, Double> values = convert(cells);
        var res = Parser.evaluate(processedExpression, values);

        var startCell = new Cell(new CellId(sheetId, cellId), expression, res, new ArrayList<>(variables));
        cellRepository.saveAll(getUpdatedCells(startCell));
        return startCell;
    }

    private boolean hasCyclicDependency(String cellId, List<Cell> cells, String sheetId) {
        Stack<Cell> stack = new Stack<>();
        stack.addAll(cells);
        while (!stack.isEmpty()) {
            var current = stack.pop();
            if (current.getId().getCellId().equals(cellId)) {
                return true;
            }
            var variables = current.getDependsOn();
            List<CellId> ids = new ArrayList<>();
            for (String variable : variables) {
                ids.add(new CellId(sheetId, variable));
            }
            stack.addAll(cellRepository.findAllById(ids));
        }
        return false;
    }

    @NonNull
    private List<Cell> getUpdatedCells(Cell start) throws Exception {
        Map<String, Cell> cellsToUpdate = new HashMap<>();
        cellsToUpdate.put(start.getId().getCellId(), start);

        Graph graph = new Graph();
        Stack<String> toAdd = new Stack<>();
        toAdd.push(start.getId().getCellId());
        var sheetId = start.getId().getSheetId();
        while (!toAdd.isEmpty()) {
            var current = toAdd.pop();
            var dependent = cellRepository.findAllById_SheetIdAndDependsOnContains(sheetId, List.of(current));
            for (Cell i : dependent) {
                cellsToUpdate.put(i.getId().getCellId(), i);
                graph.addEdge(current, i.getId().getCellId());
                toAdd.push(i.getId().getCellId());
            }
        }
        var order = graph.topologicalSort();
        if (order.size() < 2) {
            return new ArrayList<>(cellsToUpdate.values());
        }

        Map<String, Double> pool = new HashMap<>();
        try {
            pool.put(start.getId().getCellId(), Double.parseDouble(start.getResult()));
        } catch (NumberFormatException e) {
            throw new NaNException(start.getId().getCellId());
        }
        for (String i : order) {
            var cell = cellsToUpdate.get(i);
            var expr = cell.getValue().replaceAll("\\s", "");
            var variables = Parser.getVariables(expr);
            List<CellId> ids = new ArrayList<>();
            for (String variable : variables) {
                if (pool.containsKey(variable)) {
                    continue;
                }
                ids.add(new CellId(sheetId, variable));
            }
            List<Cell> cells = cellRepository.findAllById(ids);
            pool.putAll(convert(cells));

            var res = Parser.evaluate(expr, pool);
            cell.setResult(res);
            cellsToUpdate.put(i, cell);
            pool.put(i, Double.parseDouble(res));
        }
        return new ArrayList<>(cellsToUpdate.values());
    }

    private Map<String, Double> convert(List<Cell> cells) throws NaNException {
        Map<String, Double> values = new HashMap<>();
        for (Cell cell : cells) {
            try {
                values.put(cell.getId().getCellId(), Double.parseDouble(cell.getResult()));
            } catch (NumberFormatException e) {
                throw new NaNException(cell.getId().getCellId());
            }
        }
        return values;
    }
}
