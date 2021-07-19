package com.schedulsharing.domain.schedule.repository.myschedule;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.schedulsharing.domain.schedule.MySchedule;

import javax.persistence.EntityManager;
import java.time.YearMonth;
import java.util.List;

import static com.schedulsharing.domain.schedule.QMySchedule.mySchedule;

public class MyScheduleRepositoryImpl implements MyScheduleCustomRespository {

    private JPAQueryFactory queryFactory;

    public MyScheduleRepositoryImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<MySchedule> findAllByEmail(String email, YearMonth yearMonth) {
        return queryFactory.selectFrom(mySchedule)
                .where(mySchedule.scheduleStartDate.year().eq(yearMonth.getYear())
                        .and(mySchedule.scheduleStartDate.month().eq(yearMonth.getMonthValue()))
                        .or(mySchedule.scheduleEndDate.year().eq(yearMonth.getYear()))
                        .and(mySchedule.scheduleEndDate.month().eq(yearMonth.getMonthValue()))
                        .and(mySchedule.member.email.eq(email)))
                .fetch();
    }
}
