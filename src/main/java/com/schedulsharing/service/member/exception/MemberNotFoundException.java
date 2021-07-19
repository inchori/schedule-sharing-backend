package com.schedulsharing.service.member.exception;

import com.schedulsharing.excpetion.EntityNotFoundException;

public class MemberNotFoundException extends EntityNotFoundException {

    public MemberNotFoundException() {
        super("존재하지 않는 유저입니다.");
    }
}
