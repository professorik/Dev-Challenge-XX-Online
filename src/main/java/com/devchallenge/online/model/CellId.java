package com.devchallenge.online.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Data
@Document
public class CellId {
    private String sheetId;
    private String cellId;
}
