package com.schedulsharing.domain.schedule.repository.clubSchedule;

import com.schedulsharing.domain.schedule.ClubSchedule;

import java.time.YearMonth;
import java.util.List;

public interface ClubScheduleCustomRepository {
    List<ClubSchedule> findAllByClubId(Long clubId, YearMonth yearMonth);
}
