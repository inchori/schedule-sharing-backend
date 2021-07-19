package com.schedulsharing.domain.schedule.repository.suggestion;

import com.schedulsharing.domain.schedule.ScheduleSuggestion;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface ScheduleSuggestionCustomRepository {

    List<ScheduleSuggestion> findAllByClubIdConfirm(Long clubId, YearMonth yearMonth);

    List<ScheduleSuggestion> findAllByClubId(Long clubId, LocalDate now);
}
