package com.schedulsharing.service.suggestion.exception;

import com.schedulsharing.excpetion.InvalidValueException;

public class DuplicateVoteCheckException extends InvalidValueException {

    public DuplicateVoteCheckException() {
        super("중복 투표는 불가능 합니다.");
    }
}
