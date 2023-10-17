package com.devchallenge.online.dto.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidFormulaException extends RuntimeException {
    public InvalidFormulaException() {
        super("invalid formula");
    }
}
