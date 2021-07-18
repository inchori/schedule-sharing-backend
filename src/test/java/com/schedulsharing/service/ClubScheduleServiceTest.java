package com.schedulsharing.service;

import com.schedulsharing.service.club.ClubService;
import com.schedulsharing.service.schedule.ClubScheduleService;
import com.schedulsharing.web.club.dto.ClubCreateRequest;
import com.schedulsharing.web.club.dto.ClubCreateResponse;
import com.schedulsharing.domain.member.Member;
import com.schedulsharing.domain.club.repository.ClubRepository;
import com.schedulsharing.domain.member.repository.MemberRepository;
import com.schedulsharing.domain.schedule.repository.clubSchedule.ClubScheduleRepository;
import com.schedulsharing.web.schedule.club.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityModel;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ClubScheduleServiceTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private ClubScheduleRepository clubScheduleRepository;
    @Autowired
    private ClubScheduleService clubScheduleService;
    @Autowired
    private ClubService clubService;

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAll();
        clubRepository.deleteAll();
        clubScheduleRepository.deleteAll();
    }

    @DisplayName("클럽스케줄 생성 성공")
    @Test
    public void 클럽스케줄_생성_성공() {
        Member member = createMember();
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");
        String scheduleName = "클럽 스케줄 생성 테스트";
        String scheduleContents = "스터디 모임";
        LocalDateTime startMeetingDate = LocalDateTime.of(2020, 03, 03, 17, 00);
        LocalDateTime endMeetingDate = LocalDateTime.of(2020, 03, 04, 17, 00);
        ClubScheduleCreateRequest createRequest = ClubScheduleCreateRequest.builder()
                .name(scheduleName)
                .contents(scheduleContents)
                .startMeetingDate(startMeetingDate)
                .endMeetingDate(endMeetingDate)
                .clubId(clubCreateResponse.getClubId())
                .build();
        ClubScheduleCreateResponse result = clubScheduleService.create(createRequest, member.getEmail()).getContent();
        assertEquals(result.getName(), scheduleName);
        assertEquals(result.getContents(), scheduleContents);
        assertEquals(result.getStartMeetingDate(), startMeetingDate);
        assertEquals(result.getEndMeetingDate(), endMeetingDate);
    }

    @DisplayName("클럽스케줄 단건 조회 성공")
    @Test
    public void 클럽스케줄_단건_조회() {
        Member member = createMember();
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");
        String scheduleName = "클럽 스케줄 생성 테스트";
        String scheduleContents = "스터디 모임";
        LocalDateTime startMeetingDate = LocalDateTime.now();
        LocalDateTime endMeetingDate = LocalDateTime.now();
        ClubScheduleCreateRequest createRequest = ClubScheduleCreateRequest.builder()
                .name(scheduleName)
                .contents(scheduleContents)
                .startMeetingDate(startMeetingDate)
                .endMeetingDate(endMeetingDate)
                .clubId(clubCreateResponse.getClubId())
                .build();
        ClubScheduleCreateResponse clubSchedule = clubScheduleService.create(createRequest, member.getEmail()).getContent();

        Long clubScheduleId = clubSchedule.getId();
        ClubScheduleResponse result = clubScheduleService.getClubSchedule(clubScheduleId, "test@example.com").getContent();
        assertEquals(result.getName(), scheduleName);
        assertEquals(result.getContents(), scheduleContents);
        assertEquals(result.getMemberName(), "tester");
    }

    @Test
    @DisplayName("클럽스케줄 수정 성공")
    public void 클럽스케줄_수정_성공() {
        Member member = createMember();
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");
        String scheduleName = "클럽 스케줄 생성 테스트";
        String scheduleContents = "스터디 모임";
        LocalDateTime startMeetingDate = LocalDateTime.now();
        LocalDateTime endMeetingDate = LocalDateTime.now();
        ClubScheduleCreateRequest createRequest = ClubScheduleCreateRequest.builder()
                .name(scheduleName)
                .contents(scheduleContents)
                .startMeetingDate(startMeetingDate)
                .endMeetingDate(endMeetingDate)
                .clubId(clubCreateResponse.getClubId())
                .build();
        ClubScheduleCreateResponse clubSchedule = clubScheduleService.create(createRequest, member.getEmail()).getContent();
        String updateName = "수정된 클럽 스케줄 이름";
        String updateContents = "수정된 클럽 스케줄 내용";
        LocalDateTime updateStartTime = LocalDateTime.now().plusDays(1);
        LocalDateTime updateEndTime = LocalDateTime.now().plusDays(1);
        ClubScheduleUpdateRequest clubScheduleUpdateRequest = ClubScheduleUpdateRequest.builder()
                .name(updateName)
                .contents(updateContents)
                .startMeetingDate(updateStartTime)
                .endMeetingDate(updateEndTime)
                .build();

        ClubScheduleUpdateResponse updateResponse = clubScheduleService.update(clubSchedule.getId(), clubScheduleUpdateRequest, "test@example.com").getContent();
        assertEquals(updateResponse.getName(), updateName);
        assertEquals(updateResponse.getContents(), updateContents);
        assertEquals(updateResponse.getStartMeetingDate(), updateStartTime);
        assertEquals(updateResponse.getEndMeetingDate(), updateEndTime);
    }

    @DisplayName("클럽스케줄 삭제 성공")
    @Test
    public void 클럽_스케줄_삭제() {
        Member member = createMember();
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");
        String scheduleName = "클럽 스케줄 생성 테스트";
        String scheduleContents = "스터디 모임";
        LocalDateTime startMeetingDate = LocalDateTime.now();
        LocalDateTime endMeetingDate = LocalDateTime.now();
        ClubScheduleCreateRequest createRequest = ClubScheduleCreateRequest.builder()
                .name(scheduleName)
                .contents(scheduleContents)
                .startMeetingDate(startMeetingDate)
                .endMeetingDate(endMeetingDate)
                .clubId(clubCreateResponse.getClubId())
                .build();
        ClubScheduleCreateResponse clubSchedule = clubScheduleService.create(createRequest, member.getEmail()).getContent();

        clubScheduleService.delete(clubSchedule.getId(), "test@example.com");

        assertEquals(clubScheduleRepository.findById(clubSchedule.getId()).isEmpty(), true);
    }

    @DisplayName("특정 연,월에 해당하는 클럽 스케줄 리스트 가져오기")
    @Test
    public void 연_월_클럽스케줄리스트조회() {
        Member member = createMember();
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");
        for (int i = 0; i < 10; i++) {
            ClubScheduleCreateRequest createRequest = ClubScheduleCreateRequest.builder()
                    .name("2021-2 클럽 이름 테스트" + i)
                    .contents("2021-2 클럽 내용 테스트" + i)
                    .startMeetingDate(LocalDateTime.of(2021, 2, 15, 0, 0).plusDays(i))
                    .endMeetingDate(LocalDateTime.of(2021, 3, 1, 0, 0).plusDays(i))
                    .clubId(clubCreateResponse.getClubId())
                    .build();
            clubScheduleService.create(createRequest, member.getEmail()).getContent();
        }
        for (int i = 0; i < 20; i++) {
            ClubScheduleCreateRequest createRequest = ClubScheduleCreateRequest.builder()
                    .name("2021-3 클럽 이름 테스트" + i)
                    .contents("2021-3 클럽 내용 테스트" + i)
                    .startMeetingDate(LocalDateTime.of(2021, 3, 1, 0, 0).plusDays(i))
                    .endMeetingDate(LocalDateTime.of(2021, 3, 1, 0, 0).plusDays(i))
                    .clubId(clubCreateResponse.getClubId())
                    .build();
            clubScheduleService.create(createRequest, member.getEmail()).getContent();
        }

        for (int i = 0; i < 10; i++) {
            ClubScheduleCreateRequest createRequest = ClubScheduleCreateRequest.builder()
                    .name("2021-4 클럽 이름 테스트" + i)
                    .contents("2021-4 클럽 내용 테스트" + i)
                    .startMeetingDate(LocalDateTime.of(2021, 4, 1, 0, 0).plusDays(i))
                    .endMeetingDate(LocalDateTime.of(2021, 4, 1, 0, 0).plusDays(i))
                    .clubId(clubCreateResponse.getClubId())
                    .build();
            clubScheduleService.create(createRequest, member.getEmail()).getContent();
        }

        YearMonth yearMonth = YearMonth.of(2021,3);

        Collection<EntityModel<ClubScheduleResponse>> clubScheduleList = clubScheduleService.getClubScheduleList(clubCreateResponse.getClubId(), yearMonth, member.getEmail()).getContent();
        //3월시작 3월끝 = 30개
        assertEquals(clubScheduleList.size(), 30);
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

    private ClubCreateResponse createClub(Member savedMember, String clubName, String categories) {
        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName(clubName)
                .categories(categories)
                .build();
        return clubService.createClub(clubCreateRequest, savedMember.getEmail()).getContent();
    }
}