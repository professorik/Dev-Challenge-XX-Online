package com.devchallenge.online.dto.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class NaNException extends RuntimeException {
    public NaNException(String variable) {
        super(String.format("%s is not a number", variable));
    }
}
