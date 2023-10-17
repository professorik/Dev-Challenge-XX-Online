package com.devchallenge.online.dto.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class CyclicDependencyException extends RuntimeException {
    public CyclicDependencyException() {
        super("cyclic dependency");
    }
}
