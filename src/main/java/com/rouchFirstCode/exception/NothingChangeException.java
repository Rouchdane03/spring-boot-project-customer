package com.rouchFirstCode.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NothingChangeException extends RuntimeException{
    public NothingChangeException(String message) {
        super(message);
    }
}
