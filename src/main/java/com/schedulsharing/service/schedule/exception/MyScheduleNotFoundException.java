package com.schedulsharing.service.schedule.exception;

import com.schedulsharing.excpetion.EntityNotFoundException;

public class MyScheduleNotFoundException extends EntityNotFoundException {
    public MyScheduleNotFoundException() {
        super("존재하지 않는 MySchedule 입니다.");
    }
}
