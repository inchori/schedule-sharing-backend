package com.schedulsharing.domain.schedule.repository.myschedule;

import com.schedulsharing.domain.schedule.MySchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyScheduleRepository extends JpaRepository<MySchedule, Long>, MyScheduleCustomRespository {
}
