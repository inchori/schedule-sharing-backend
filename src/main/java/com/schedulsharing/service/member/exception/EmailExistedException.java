package com.schedulsharing.service.member.exception;

import com.schedulsharing.excpetion.InvalidValueException;

public class EmailExistedException extends InvalidValueException {
    public EmailExistedException() {
        super("중복된 이메일입니다.");
    }
}
