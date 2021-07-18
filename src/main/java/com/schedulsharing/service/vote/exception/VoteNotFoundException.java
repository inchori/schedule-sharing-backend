package com.schedulsharing.service.vote.exception;

import com.schedulsharing.excpetion.EntityNotFoundException;

public class VoteNotFoundException extends EntityNotFoundException {

    public VoteNotFoundException() {
        super("존재하지 않는 Vote 입니다.");
    }
}
