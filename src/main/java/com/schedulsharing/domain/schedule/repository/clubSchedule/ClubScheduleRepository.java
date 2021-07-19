package com.schedulsharing.domain.schedule.repository.clubSchedule;

import com.schedulsharing.domain.schedule.ClubSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubScheduleRepository extends JpaRepository<ClubSchedule, Long> ,ClubScheduleCustomRepository{

}
