package com.devchallenge.online.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "cells")
public class Cell {

    @Id
    private CellId id;
    private String value;
    private String result;
    private List<String> dependsOn;
    private String webhookUrl;

    public Cell(CellId id, String value, String result, List<String> dependsOn) {
        this.id = id;
        this.value = value;
        this.result = result;
        this.dependsOn = dependsOn;
        this.webhookUrl = "";
    }
}
