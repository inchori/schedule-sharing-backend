package com.schedulsharing.excpetion;

public class PermissionException extends RuntimeException{
    public PermissionException() {
        super("권한이 없습니다.");
    }
}
