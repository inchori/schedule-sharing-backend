package com.schedulsharing.service;


import com.schedulsharing.domain.member.Member;
import com.schedulsharing.domain.member.repository.MemberRepository;
import com.schedulsharing.domain.schedule.repository.myschedule.MyScheduleRepository;
import com.schedulsharing.service.schedule.MyScheduleService;
import com.schedulsharing.web.schedule.my.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityModel;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MyScheduleServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MyScheduleRepository myScheduleRepository;
    @Autowired
    private MyScheduleService myScheduleService;

    @BeforeEach
    public void setup() {
        memberRepository.deleteAll();
        myScheduleRepository.deleteAll();
    }

    @DisplayName("내 스케줄 생성 성공")
    @Test
    public void 내_스케줄_생성_성공() {
        //given
        Member member = createMember();
        String myScheduleName = "내 스케줄 생성 테스트";
        String myScheduleContents = "스터디 모임";
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();

        MyScheduleCreateRequest createRequest = MyScheduleCreateRequest.builder()
                .name(myScheduleName)
                .contents(myScheduleContents)
                .scheduleStartDate(startDate)
                .scheduleEndDate(endDate)
                .build();
        //when
        MyScheduleCreateResponse result = myScheduleService.create(createRequest, member.getEmail()).getContent();
        //then
        assertEquals(result.getName(), myScheduleName);
        assertEquals(result.getContents(), myScheduleContents);
        assertEquals(result.getScheduleStartDate(), startDate);
        assertEquals(result.getScheduleEndDate(), endDate);
    }

    @DisplayName("내 스케줄 단건 조회")
    @Test
    public void 내_스케줄_단건_조회() throws Exception {
        //given
        Member member = createMember();
        String name = "나 스케줄 생성 테스트";
        String contents = "스터디 모임";
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();

        MyScheduleCreateRequest createRequest = MyScheduleCreateRequest.builder()
                .name(name)
                .contents(contents)
                .scheduleStartDate(startDate)
                .scheduleEndDate(endDate)
                .build();

        MyScheduleCreateResponse createResponse = myScheduleService.create(createRequest, member.getEmail()).getContent();

        Long myScheduleId = createResponse.getMyScheduleId();
        MyScheduleResponse result = myScheduleService.getMySchedule(myScheduleId, "test@example.com").getContent();
        assertEquals(result.getName(), name);
        assertEquals(result.getContents(), contents);
    }

    @DisplayName("내 스케줄 리스트 조회")
    @Test
    public void 내_스케줄_리스트_조회() throws Exception {
        Member member = createMember();
        for (int i = 0; i < 10; i++) {
            MyScheduleCreateRequest createRequest = MyScheduleCreateRequest.builder()
                    .name("2021-2 내 스케줄 이름 테스트" + i)
                    .contents("2021-2 내 스케줄 내용 테스트" + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 2, 15, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 3, 1, 0, 0).plusDays(i))
                    .build();
            myScheduleService.create(createRequest, member.getEmail()).getContent();
        }

        for (int i = 0; i < 20; i++) {
            MyScheduleCreateRequest createRequest = MyScheduleCreateRequest.builder()
                    .name("2021-3 내 스케줄 이름 테스트" + i)
                    .contents("2021-3 내 스케줄 내용 테스트" + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 3, 1, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 3, 1, 0, 0).plusDays(i))
                    .build();
            myScheduleService.create(createRequest, member.getEmail()).getContent();
        }

        for (int i = 0; i < 10; i++) {
            MyScheduleCreateRequest createRequest = MyScheduleCreateRequest.builder()
                    .name("2021-4 클럽 이름 테스트" + i)
                    .contents("2021-4 클럽 내용 테스트" + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 4, 1, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 4, 1, 0, 0).plusDays(i))
                    .build();
            myScheduleService.create(createRequest, member.getEmail()).getContent();
        }

        YearMonth yearMonth = YearMonth.of(2021,3);
        Collection<EntityModel<MyScheduleResponse>> myScheduleList = myScheduleService.getMyScheduleList(yearMonth, member.getEmail()).getContent();
        //3월시작 3월끝 = 30개
        assertEquals(myScheduleList.size(), 30);
    }

    @DisplayName("내 스케줄 수정 성공")
    @Test
    public void 내_스케줄_수정_성공() throws Exception {
        Member member = createMember();
        String name = "나 스케줄 생성 테스트";
        String contents = "스터디 모임";
        LocalDateTime starDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();
        MyScheduleCreateRequest createRequest = MyScheduleCreateRequest.builder()
                .name(name)
                .contents(contents)
                .scheduleStartDate(starDate)
                .scheduleEndDate(endDate)
                .build();

        MyScheduleCreateResponse createResponse = myScheduleService.create(createRequest, member.getEmail()).getContent();
        String updateName = "수정된 나의 스케줄 이름";
        String updateContents = "수정된 나의 스케줄 내용";
        LocalDateTime updateStartDate = LocalDateTime.now().plusDays(1);
        LocalDateTime updateEndDate = LocalDateTime.now().plusDays(1);

        MyScheduleUpdateRequest updateRequest = MyScheduleUpdateRequest.builder()
                .name(updateName)
                .contents(updateContents)
                .scheduleStartDate(updateStartDate)
                .scheduleEndDate(updateEndDate)
                .build();

        MyScheduleUpdateResponse updateResponse = myScheduleService.update(createResponse.getMyScheduleId(), updateRequest, member.getEmail()).getContent();

        assertEquals(updateResponse.getName(), updateName);
        assertEquals(updateResponse.getContents(), updateContents);
        assertEquals(updateResponse.getScheduleStartDate(), updateStartDate);
        assertEquals(updateResponse.getScheduleEndDate(), updateEndDate);
    }

    @DisplayName("내 스케줄 삭제 성공")
    @Test
    public void 내_스케줄_삭제_성공() throws Exception {
        Member member = createMember();
        String name = "나의 스케줄 생성 테스트";
        String contents = "스터디 모임";
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();

        MyScheduleCreateRequest createRequest = MyScheduleCreateRequest.builder()
                .name(name)
                .contents(contents)
                .scheduleStartDate(startDate)
                .scheduleEndDate(endDate)
                .build();

        MyScheduleCreateResponse createResponse = myScheduleService.create(createRequest, member.getEmail()).getContent();

        myScheduleService.delete(createResponse.getMyScheduleId(), member.getEmail());

        assertEquals(myScheduleRepository.findById(createResponse.getMyScheduleId()).isEmpty(), true);
    }

    private Member createMember() {
        Member member = Member.builder()
                .email("test@example.com")
                .name("tester")
                .password("1234")
                .imagePath("imagePath")
                .build();
        return memberRepository.save(member);
    }

}
