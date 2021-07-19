package com.schedulsharing.domain.schedule.repository.suggestion;

import com.schedulsharing.domain.schedule.ScheduleSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleSuggestionRepository extends JpaRepository<ScheduleSuggestion, Long> ,ScheduleSuggestionCustomRepository{

}
