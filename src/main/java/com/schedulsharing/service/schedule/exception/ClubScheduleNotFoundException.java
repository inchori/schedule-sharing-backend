package com.schedulsharing.service.schedule.exception;

import com.schedulsharing.excpetion.EntityNotFoundException;

public class ClubScheduleNotFoundException extends EntityNotFoundException {

    public ClubScheduleNotFoundException() {
        super("존재하지 않는 ClubSchedule 입니다.");
    }
}
