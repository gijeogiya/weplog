package com.ssafy.ploggingservice.global.common.error.exception;

public class NotExistException extends RuntimeException{

    public NotExistException(String message) {
        super(message);
    }
}
