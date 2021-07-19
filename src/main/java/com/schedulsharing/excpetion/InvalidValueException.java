package com.schedulsharing.excpetion;

public class InvalidValueException extends BusinessException{
    public InvalidValueException(String msg) {
        super(msg);
    }
}
