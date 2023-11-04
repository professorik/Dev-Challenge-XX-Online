package com.devchallenge.online.repository;

import com.devchallenge.online.model.Cell;
import com.devchallenge.online.model.CellId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CellRepository extends MongoRepository<Cell, CellId> {

    List<Cell> findAllById_SheetId(final String sheetId);

    List<Cell> findAllById_SheetIdAndDependsOnContains(String sheetId, List<String> dependsOn);
}
