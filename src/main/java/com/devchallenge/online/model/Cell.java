package com.devchallenge.online.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@Data
@Document(collection = "cells")
public class Cell {

    @Id
    private CellId id;
    private String value;
    private String result;
    private List<String> dependsOn;
}
