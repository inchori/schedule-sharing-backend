package com.schedulsharing.service.club.exception;

import com.schedulsharing.excpetion.EntityNotFoundException;

public class ClubNotFoundException extends EntityNotFoundException {

    public ClubNotFoundException() {
        super("존재하지 않는 Club 입니다.");
    }
}
