package com.schedulsharing.domain.schedule.repository.suggestion;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.schedulsharing.domain.schedule.ScheduleSuggestion;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static com.schedulsharing.domain.schedule.QScheduleSuggestion.scheduleSuggestion;

public class ScheduleSuggestionRepositoryImpl implements ScheduleSuggestionCustomRepository {
    private JPAQueryFactory queryFactory;

    public ScheduleSuggestionRepositoryImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ScheduleSuggestion> findAllByClubIdConfirm(Long clubId, YearMonth yearMonth) {
        return queryFactory.selectFrom(scheduleSuggestion)
                .where(scheduleSuggestion.scheduleStartDate.year().eq(yearMonth.getYear())
                        .and(scheduleSuggestion.scheduleStartDate.month().eq(yearMonth.getMonthValue()))
                        .or(scheduleSuggestion.scheduleEndDate.year().eq(yearMonth.getYear())
                                .and(scheduleSuggestion.scheduleEndDate.month().eq(yearMonth.getMonthValue())))
                        .and(scheduleSuggestion.club.id.eq(clubId))
                        .and(scheduleSuggestion.isConfirm.eq(true)))
                .fetch();
    }

    @Override
    public List<ScheduleSuggestion> findAllByClubId(Long clubId, LocalDate now) {
        return queryFactory.selectFrom(scheduleSuggestion)
                .where(scheduleSuggestion.voteEndDate.year().eq(now.getYear())
                        .and(scheduleSuggestion.voteEndDate.month().eq(now.getMonthValue()))
                        .and(scheduleSuggestion.voteEndDate.dayOfMonth().goe(now.getDayOfMonth()))
                        .and(scheduleSuggestion.club.id.eq(clubId)))
                .fetch();
    }
}
