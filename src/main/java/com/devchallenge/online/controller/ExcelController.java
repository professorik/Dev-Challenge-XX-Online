package com.devchallenge.online.controller;

import com.devchallenge.online.dto.*;
import com.devchallenge.online.model.Cell;
import com.devchallenge.online.repository.CellRepository;
import com.devchallenge.online.service.ExcelService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
public class ExcelController {

    @Autowired
    private ExcelService service;

    @Autowired
    private ModelMapper modelMapper;


    @PostMapping("/{sheet_id}/{cell_id}/subscribe")
    public ResponseEntity<?> setCell(@PathVariable("sheet_id") String sheetId, @PathVariable("cell_id") String cellId, @RequestBody WebHookDto url){
        try {
             service.setWebHook(sheetId, cellId, url.getWebhook_url());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }

    @PostMapping("/{sheet_id}/{cell_id}")
    public ResponseEntity<?> setCell(@PathVariable("sheet_id") String sheetId, @PathVariable("cell_id") String cellId, @RequestBody CellValueDto cellValue){
        CellDto result;
        try {
            result = modelMapper.map(service.setCell(sheetId, cellId, cellValue.getValue()), CellDto.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new CellDto(cellValue.getValue(), "ERROR"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{sheet_id}/{cell_id}")
    public ResponseEntity<CellDto> getCell(@PathVariable("sheet_id") String sheetId, @PathVariable("cell_id") String cellId) {
        var cell = service.getCell(sheetId, cellId);
        if (cell.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(modelMapper.map(cell, CellDto.class));
    }

    @GetMapping("/{sheet_id}")
    public ResponseEntity<Map<String, CellDto>> getSheet(@PathVariable("sheet_id") String sheetId) {
        var cells = service.getSheet(sheetId);
        if (cells.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Map<String, CellDto> map = new HashMap<>();
        for (Cell cell: cells) {
            map.put(cell.getId().getCellId(), modelMapper.map(cell, CellDto.class));
        }
        return ResponseEntity.ok(map);
    }
}
