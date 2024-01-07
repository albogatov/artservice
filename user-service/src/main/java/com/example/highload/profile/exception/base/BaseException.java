package com.example.highload.exception.base;

import com.example.highload.exception.ExceptionType;
import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {
    private final ExceptionType type;
    private final HttpStatus httpStatus;

    public BaseException(ExceptionType type, HttpStatus httpStatus) {
        this.type = type;
        this.httpStatus = httpStatus;
    }

    public ExceptionType getType() {
        return type;
    }

    public abstract String getDescription();

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
