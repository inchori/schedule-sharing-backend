package com.schedulsharing.service;

import com.schedulsharing.domain.club.repository.ClubRepository;
import com.schedulsharing.domain.member.Member;
import com.schedulsharing.domain.member.repository.MemberRepository;
import com.schedulsharing.domain.schedule.ScheduleSuggestion;
import com.schedulsharing.domain.schedule.repository.suggestion.ScheduleSuggestionRepository;
import com.schedulsharing.excpetion.PermissionException;
import com.schedulsharing.service.suggestion.exception.DuplicateVoteCheckException;
import com.schedulsharing.service.club.ClubService;
import com.schedulsharing.service.member.MemberService;
import com.schedulsharing.service.suggestion.ScheduleSuggestionService;
import com.schedulsharing.web.club.dto.ClubCreateRequest;
import com.schedulsharing.web.club.dto.ClubCreateResponse;
import com.schedulsharing.web.club.dto.ClubInviteRequest;
import com.schedulsharing.web.member.dto.SignUpRequestDto;
import com.schedulsharing.web.member.dto.SignUpResponseDto;
import com.schedulsharing.web.schedule.club.dto.suggestion.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ScheduleSuggestionServiceTest {
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ScheduleSuggestionRepository scheduleSuggestionRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ClubService clubService;
    @Autowired
    private ScheduleSuggestionService scheduleSuggestionService;

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAll();
        clubRepository.deleteAll();
        scheduleSuggestionRepository.deleteAll();
        String email = "test@example.com";
        String password = "1234";
        String imagePath = "imagePath";
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email(email)
                .password(password)
                .name("테스터")
                .imagePath(imagePath)
                .build();

        memberService.signup(signUpRequestDto);

        String email2 = "test2@example.com";
        String password2 = "1234";
        String imagePath2 = "imagePath2";
        SignUpRequestDto signUpRequestDto2 = SignUpRequestDto.builder()
                .email(email2)
                .password(password2)
                .name("테스터2")
                .imagePath(imagePath2)
                .build();

        memberService.signup(signUpRequestDto2);
    }

    @DisplayName("스케줄제안 생성 성공")
    @Test
    public void 스케줄제안_생성_성공() {
        String email = "test@example.com";
        Member member = memberRepository.findByEmail(email).get(); //setUp에서 생성한 멤버
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");
        String title = "테스트 제안 제목";
        String contents = "테스트 제안 내용";
        String location = "테스트 제안 위치";
        int minMember = 2;
        SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                .title(title)
                .contents(contents)
                .location(location)
                .minMember(minMember)
                .scheduleStartDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .scheduleEndDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .voteStartDate(LocalDateTime.of(2021, 3, 5, 0, 0))
                .voteEndDate(LocalDateTime.of(2021, 3, 8, 0, 0))
                .clubId(clubCreateResponse.getClubId())
                .build();
        SuggestionCreateResponse result = scheduleSuggestionService.create(suggestionCreateRequest, email).getContent();
        assertEquals(result.getContents(), contents);
        assertEquals(result.getLocation(), location);
        assertEquals(result.getMinMember(), minMember);
        assertEquals(result.getTitle(), title);
    }

    @DisplayName("스케줄제안 단건조회 성공")
    @Test
    public void 스케줄제안_단건조회() {
        String email = "test@example.com";
        Member member = memberRepository.findByEmail(email).get(); //setUp에서 생성한 멤버
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");
        String title = "테스트 제안 제목";
        String contents = "테스트 제안 내용";
        String location = "테스트 제안 위치";
        int minMember = 2;
        SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                .title(title)
                .contents(contents)
                .location(location)
                .minMember(minMember)
                .scheduleStartDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .scheduleEndDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .voteStartDate(LocalDateTime.of(2021, 3, 5, 0, 0))
                .voteEndDate(LocalDateTime.of(2021, 3, 8, 0, 0))
                .clubId(clubCreateResponse.getClubId())
                .build();
        SuggestionCreateResponse content = scheduleSuggestionService.create(suggestionCreateRequest, email).getContent();

        SuggestionVoteCheckResponse result = scheduleSuggestionService.getSuggestion(content.getId(), email).getContent();
        assertEquals(result.getTitle(), title);
        assertEquals(result.getContents(), contents);
        assertEquals(result.getLocation(), location);
        assertEquals(result.getMinMember(), minMember);
        assertEquals(result.getMemberName(), member.getName());
        assertEquals(result.getMemberEmail(), member.getEmail());
    }

    @DisplayName("스케줄제안 confirm 리스트 조회")
    @Test
    public void 스케줄제안_confirm_true_리스트() {
        Member member = memberRepository.findByEmail("test@example.com").get();
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");
        for (int i = 0; i < 3; i++) {
            //2021-2월 시작 2021-3월 끝 3개
            SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                    .title("2021-2~2021-3 테스트 제안 제목" + i)
                    .contents("2021-2~2021-3 테스트 제안 내용" + i)
                    .location("2021-2~2021-3 테스트 제안 위치" + i)
                    .minMember(3 + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 2, 28, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 3, 2, 0, 0).plusDays(i))
                    .voteStartDate(LocalDateTime.of(2021, 2, 5, 0, 0).plusDays(i))
                    .voteEndDate(LocalDateTime.of(2021, 2, 8, 0, 0).plusDays(i))
                    .clubId(clubCreateResponse.getClubId())
                    .build();

            scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();
        }
        for (int i = 0; i < 5; i++) {
            //2021-3월 시작 2021-3월 끝 5개 이 5개만 confirm
            SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                    .title("2021-3~2021-3 테스트 제안 제목" + i)
                    .contents("2021-3~2021-3 테스트 제안 내용" + i)
                    .location("2021-3~2021-3 테스트 제안 위치" + i)
                    .minMember(3 + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 3, 27, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 3, 28, 0, 0).plusDays(i))
                    .voteStartDate(LocalDateTime.of(2021, 3, 20, 0, 0).plusDays(i))
                    .voteEndDate(LocalDateTime.of(2021, 3, 24, 0, 0).plusDays(i))
                    .clubId(clubCreateResponse.getClubId())
                    .build();

            SuggestionCreateResponse response = scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();
            ScheduleSuggestion scheduleSuggestion = scheduleSuggestionRepository.findById(response.getId()).get();
            scheduleSuggestion.updateConfirmTrue();
            scheduleSuggestionRepository.save(scheduleSuggestion);
        }

        for (int i = 0; i < 3; i++) {
            //2021-4월 시작 2021-4월 끝 3개
            SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                    .title("2021-4~2021-4 테스트 제안 제목" + i)
                    .contents("2021-4~2021-4 테스트 제안 내용" + i)
                    .location("2021-4~2021-4 테스트 제안 위치" + i)
                    .minMember(3 + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 4, 21, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 4, 23, 0, 0).plusDays(i))
                    .voteStartDate(LocalDateTime.of(2021, 4, 5, 0, 0).plusDays(i))
                    .voteEndDate(LocalDateTime.of(2021, 4, 8, 0, 0).plusDays(i))
                    .clubId(clubCreateResponse.getClubId())
                    .build();

            scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();
        }
        YearMonth yearMonth = YearMonth.of(2021, 3);
        Collection<EntityModel<SuggestionResponse>> result = scheduleSuggestionService.getSuggestionListConfirm(clubCreateResponse.getClubId(), yearMonth, member.getEmail()).getContent();
        assertEquals(result.size(), 5);
    }

    @DisplayName("스케줄제안 오늘날짜와 같거나 이후인 제안 리스트 조회")
    @Test
    public void 스케줄제안_오늘날짜와_같거나_이후인_제안리스트() {
        Member member = memberRepository.findByEmail("test@example.com").get();
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");
        for (int i = 0; i < 3; i++) {
            //2021-2월 시작 2021-3월 끝 3개
            SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                    .title("2021-2~2021-3 테스트 제안 제목" + i)
                    .contents("2021-2~2021-3 테스트 제안 내용" + i)
                    .location("2021-2~2021-3 테스트 제안 위치" + i)
                    .minMember(3 + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 2, 28, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 3, 2, 0, 0).plusDays(i))
                    .voteStartDate(LocalDateTime.of(2021, 2, 5, 0, 0).plusDays(i))
                    .voteEndDate(LocalDateTime.of(2021, 2, 8, 0, 0).plusDays(i))
                    .clubId(clubCreateResponse.getClubId())
                    .build();

            scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();
        }
        //투표마감 오늘날짜
        SuggestionCreateRequest suggestionCreateRequestToday = SuggestionCreateRequest.builder()
                .title("2021-3~2021-3 테스트 제안 제목")
                .contents("2021-3~2021-3 테스트 제안 내용")
                .location("2021-3~2021-3 테스트 제안 위치")
                .minMember(3)
                .scheduleStartDate(LocalDateTime.of(2021, 3, 27, 0, 0))
                .scheduleEndDate(LocalDateTime.of(2021, 3, 28, 0, 0))
                .voteStartDate(LocalDateTime.of(2021, 3, 8, 0, 0))
                .voteEndDate(LocalDateTime.of(2021, 3, 8, 0, 0))
                .clubId(clubCreateResponse.getClubId())
                .build();

        scheduleSuggestionService.create(suggestionCreateRequestToday, member.getEmail()).getContent();

        for (int i = 0; i < 5; i++) {
            //2021-3월 시작 2021-3월 끝 5개 이 5개만 confirm
            SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                    .title("2021-3~2021-3 테스트 제안 제목" + i)
                    .contents("2021-3~2021-3 테스트 제안 내용" + i)
                    .location("2021-3~2021-3 테스트 제안 위치" + i)
                    .minMember(3 + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 3, 27, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 3, 28, 0, 0).plusDays(i))
                    .voteStartDate(LocalDateTime.of(2021, 3, 5, 0, 0).plusDays(i))
                    .voteEndDate(LocalDateTime.of(2021, 3, 10, 0, 0).plusDays(i))
                    .clubId(clubCreateResponse.getClubId())
                    .build();

            scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();
        }

        for (int i = 0; i < 3; i++) {
            //2021-4월 시작 2021-4월 끝 3개
            SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                    .title("2021-4~2021-4 테스트 제안 제목" + i)
                    .contents("2021-4~2021-4 테스트 제안 내용" + i)
                    .location("2021-4~2021-4 테스트 제안 위치" + i)
                    .minMember(3 + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 4, 21, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 4, 23, 0, 0).plusDays(i))
                    .voteStartDate(LocalDateTime.of(2021, 4, 5, 0, 0).plusDays(i))
                    .voteEndDate(LocalDateTime.of(2021, 4, 8, 0, 0).plusDays(i))
                    .clubId(clubCreateResponse.getClubId())
                    .build();

            scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();
        }

        LocalDate now = LocalDate.of(2021, 3, 8);
        Collection<EntityModel<SuggestionResponse>> result = scheduleSuggestionService.getSuggestionList(clubCreateResponse.getClubId(), now, member.getEmail()).getContent();
        assertEquals(result.size(), 6);
    }

    @DisplayName("스케줄제안 수정 성공")
    @Test
    public void 스케줄제안_수정() {
        Member member = memberRepository.findByEmail("test@example.com").get(); //setUp에서 생성한 멤버
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");
        SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                .title("테스트 제안 제목")
                .contents("테스트 제안 내용")
                .location("테스트 제안 위치")
                .minMember(2)
                .scheduleStartDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .scheduleEndDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .voteStartDate(LocalDateTime.of(2021, 3, 5, 0, 0))
                .voteEndDate(LocalDateTime.of(2021, 3, 8, 0, 0))
                .clubId(clubCreateResponse.getClubId())
                .build();
        SuggestionCreateResponse createResponse = scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();
        String updateTitle = "수정된 테스트 제안 제목";
        String updateContents = "수정된 테스트 제안 내용";
        String updateLocation = "수정된 제안 위치";
        int updateMinMember = 5;

        SuggestionUpdateRequest suggestionUpdateRequest = SuggestionUpdateRequest.builder()
                .title(updateTitle)
                .contents(updateContents)
                .location(updateLocation)
                .minMember(updateMinMember)
                .scheduleStartDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .scheduleEndDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .voteStartDate(LocalDateTime.of(2021, 3, 5, 0, 0))
                .voteEndDate(LocalDateTime.of(2021, 3, 8, 0, 0))
                .build();

        SuggestionResponse result = scheduleSuggestionService.update(createResponse.getId(), suggestionUpdateRequest, member.getEmail()).getContent();
        assertEquals(result.getTitle(), updateTitle);
        assertEquals(result.getContents(), updateContents);
        assertEquals(result.getLocation(), updateLocation);
        assertEquals(result.getMinMember(), updateMinMember);
        assertEquals(result.getMemberName(), member.getName());
        assertEquals(result.getMemberEmail(), member.getEmail());
    }

    @DisplayName("스케줄제안 삭제 성공")
    @Test
    public void 스케줄제안_삭제() {
        Member member = memberRepository.findByEmail("test@example.com").get(); //setUp에서 생성한 멤버
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");
        SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                .title("테스트 제안 제목")
                .contents("테스트 제안 내용")
                .location("테스트 제안 위치")
                .minMember(2)
                .scheduleStartDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .scheduleEndDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .voteStartDate(LocalDateTime.of(2021, 3, 5, 0, 0))
                .voteEndDate(LocalDateTime.of(2021, 3, 8, 0, 0))
                .clubId(clubCreateResponse.getClubId())
                .build();
        SuggestionCreateResponse createResponse = scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();

        SuggestionDeleteResponse result = scheduleSuggestionService.delete(createResponse.getId(), member.getEmail()).getContent();
        assertEquals(result.isSuccess(), true);
        assertEquals(scheduleSuggestionRepository.findById(createResponse.getId()).isEmpty(), true);

    }

    @DisplayName("클럽 스케줄 제안 투표 성공")
    @Test
    public void 클럽스케줄제안_투표성공() {
        Member member = memberRepository.findByEmail("test@example.com").get(); //setUp에서 생성한 멤버
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");
        SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                .title("테스트 제안 제목")
                .contents("테스트 제안 내용")
                .location("테스트 제안 위치")
                .minMember(2)
                .scheduleStartDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .scheduleEndDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .voteStartDate(LocalDateTime.of(2021, 3, 5, 0, 0))
                .voteEndDate(LocalDateTime.of(2021, 3, 8, 0, 0))
                .clubId(clubCreateResponse.getClubId())
                .build();
        SuggestionCreateResponse createResponse = scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();
        SuggestionVoteRequest suggestionVoteRequest = SuggestionVoteRequest.builder()
                .agree(true)
                .build();
        SuggestionVoteResponse result = scheduleSuggestionService.vote(createResponse.getId(), suggestionVoteRequest, "test@example.com").getContent();
        assertEquals(result.isAgree(), true);
    }

    @DisplayName("클럽원이 아닐 경우클럽 스케줄 제안 투표 실패")
    @Test
    public void 클럽원이_아닌경우_클럽스케줄제안_투표실패() {
        Member member = memberRepository.findByEmail("test@example.com").get(); //setUp에서 생성한 멤버
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");

        SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                .title("테스트 제안 제목")
                .contents("테스트 제안 내용")
                .location("테스트 제안 위치")
                .minMember(2)
                .scheduleStartDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .scheduleEndDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .voteStartDate(LocalDateTime.of(2021, 3, 5, 0, 0))
                .voteEndDate(LocalDateTime.of(2021, 3, 8, 0, 0))
                .clubId(clubCreateResponse.getClubId())
                .build();
        SuggestionCreateResponse createResponse = scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();
        SuggestionVoteRequest suggestionVoteRequest = SuggestionVoteRequest.builder()
                .agree(true)
                .build();
        assertThrows(PermissionException.class,
                () -> scheduleSuggestionService.vote(createResponse.getId(), suggestionVoteRequest, "test2@example.com"));
    }

    @DisplayName("클럽 스케줄 제안 투표 중복투표인경우 실패")
    @Test
    public void 클럽스케줄제안_중복투표인경우_실패() {
        Member member = memberRepository.findByEmail("test@example.com").get(); //setUp에서 생성한 멤버
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");
        SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                .title("테스트 제안 제목")
                .contents("테스트 제안 내용")
                .location("테스트 제안 위치")
                .minMember(2)
                .scheduleStartDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .scheduleEndDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .voteStartDate(LocalDateTime.of(2021, 3, 5, 0, 0))
                .voteEndDate(LocalDateTime.of(2021, 3, 8, 0, 0))
                .clubId(clubCreateResponse.getClubId())
                .build();
        SuggestionCreateResponse createResponse = scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();
        SuggestionVoteRequest suggestionVoteRequest = SuggestionVoteRequest.builder()
                .agree(true)
                .build();
        scheduleSuggestionService.vote(createResponse.getId(), suggestionVoteRequest, "test@example.com").getContent();
        assertThrows(DuplicateVoteCheckException.class,
                () -> scheduleSuggestionService.vote(createResponse.getId(), suggestionVoteRequest, "test@example.com"));
    }

    @DisplayName("초대된 클럽원의 경우 클럽 스케줄 제안 투표 성공")
    @Test
    public void 초대된_클럽원은_클럽스케줄제안_투표성공() {
        Member member = memberRepository.findByEmail("test@example.com").get(); //setUp에서 생성한 멤버
        String email3 = "test3@example.com";
        String password3 = "1234";
        String imagePath3 = "imagePath3";
        SignUpRequestDto signUpRequestDto3 = SignUpRequestDto.builder()
                .email(email3)
                .password(password3)
                .name("테스터3")
                .imagePath(imagePath3)
                .build();

        SignUpResponseDto content = memberService.signup(signUpRequestDto3).getContent();
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");

        ClubInviteRequest clubInviteRequest = ClubInviteRequest.builder()
                .memberIds(List.of(content.getId()))
                .build();
        clubService.invite(clubInviteRequest, clubCreateResponse.getClubId(), "test@example.com");

        SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                .title("테스트 제안 제목")
                .contents("테스트 제안 내용")
                .location("테스트 제안 위치")
                .minMember(2)
                .scheduleStartDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .scheduleEndDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .voteStartDate(LocalDateTime.of(2021, 3, 5, 0, 0))
                .voteEndDate(LocalDateTime.of(2021, 3, 8, 0, 0))
                .clubId(clubCreateResponse.getClubId())
                .build();
        SuggestionCreateResponse createResponse = scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();
        SuggestionVoteRequest suggestionVoteRequest = SuggestionVoteRequest.builder()
                .agree(true)
                .build();
        SuggestionVoteResponse result = scheduleSuggestionService.vote(createResponse.getId(), suggestionVoteRequest, "test3@example.com").getContent();
        assertEquals(result.isAgree(), true);
    }

    @DisplayName("클럽 스케줄 제안 투표가 마감 날짜 이전에 찬성 수가 제안의 최소인원 이상이면 confrim이 true로 된다. ")
    @Test
    public void 클럽스케줄제안_투표_confirm_update() {
        Member member = memberRepository.findByEmail("test@example.com").get(); //setUp에서 생성한 멤버
        ClubCreateResponse clubCreateResponse = createClub(member, "testClubName", "밥");
        SuggestionCreateRequest suggestionCreateRequest = SuggestionCreateRequest.builder()
                .title("테스트 제안 제목")
                .contents("테스트 제안 내용")
                .location("테스트 제안 위치")
                .minMember(1)
                .scheduleStartDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .scheduleEndDate(LocalDateTime.of(2021, 3, 10, 0, 0))
                .voteStartDate(LocalDateTime.of(2021, 3, 5, 0, 0))
                .voteEndDate(LocalDateTime.now().plusDays(1))
                .clubId(clubCreateResponse.getClubId())
                .build();
        SuggestionCreateResponse createResponse = scheduleSuggestionService.create(suggestionCreateRequest, member.getEmail()).getContent();
        SuggestionVoteRequest suggestionVoteRequest = SuggestionVoteRequest.builder()
                .agree(true)
                .build();
        SuggestionVoteResponse result = scheduleSuggestionService.vote(createResponse.getId(), suggestionVoteRequest, "test@example.com").getContent();
        ScheduleSuggestion scheduleSuggestion = scheduleSuggestionRepository.findById(createResponse.getId()).get();
        assertEquals(result.isAgree(), true);
        assertEquals(scheduleSuggestion.isConfirm(), true);
    }

    private ClubCreateResponse createClub(Member savedMember, String clubName, String categories) {
        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName(clubName)
                .categories(categories)
                .build();
        return clubService.createClub(clubCreateRequest, savedMember.getEmail());
    }
}