package com.schedulsharing.domain.schedule.repository.clubSchedule;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.schedulsharing.domain.schedule.ClubSchedule;

import javax.persistence.EntityManager;
import java.time.YearMonth;
import java.util.List;

import static com.schedulsharing.domain.schedule.QClubSchedule.clubSchedule;

public class ClubScheduleRepositoryImpl implements ClubScheduleCustomRepository {
    private JPAQueryFactory queryFactory;

    public ClubScheduleRepositoryImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ClubSchedule> findAllByClubId(Long clubId, YearMonth yearMonth) {
        return queryFactory.selectFrom(clubSchedule)
                .where(clubSchedule.startMeetingDate.year().eq(yearMonth.getYear())
                        .and(clubSchedule.startMeetingDate.month().eq(yearMonth.getMonthValue()))
                        .or(clubSchedule.endMeetingDate.year().eq(yearMonth.getYear()))
                        .and(clubSchedule.endMeetingDate.month().eq(yearMonth.getMonthValue()))
                        .and(clubSchedule.club.id.eq(clubId)))
                .fetch();
    }
}
