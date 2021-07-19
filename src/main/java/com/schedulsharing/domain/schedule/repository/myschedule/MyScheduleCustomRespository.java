package com.schedulsharing.domain.schedule.repository.myschedule;

import com.schedulsharing.domain.schedule.MySchedule;

import java.time.YearMonth;
import java.util.List;

public interface MyScheduleCustomRespository {
    List<MySchedule> findAllByEmail(String email, YearMonth yearMonth);
}
