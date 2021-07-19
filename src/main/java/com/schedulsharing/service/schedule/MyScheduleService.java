package com.schedulsharing.service.schedule;

import com.schedulsharing.domain.member.Member;
import com.schedulsharing.domain.member.repository.MemberRepository;
import com.schedulsharing.domain.schedule.MySchedule;
import com.schedulsharing.domain.schedule.repository.myschedule.MyScheduleRepository;
import com.schedulsharing.excpetion.PermissionException;
import com.schedulsharing.service.member.exception.MemberNotFoundException;
import com.schedulsharing.service.schedule.exception.MyScheduleNotFoundException;
import com.schedulsharing.web.dto.resource.MyScheduleResource;
import com.schedulsharing.web.schedule.my.dto.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MyScheduleService {
    private final MyScheduleRepository myScheduleRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public EntityModel<MyScheduleCreateResponse> create(MyScheduleCreateRequest myScheduleCreateRequest, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        MySchedule mySchedule = MySchedule.createMySchedule(myScheduleCreateRequest, member);
        MySchedule savedMySchedule = myScheduleRepository.save(mySchedule);
        MyScheduleCreateResponse myScheduleCreateResponse = modelMapper.map(savedMySchedule, MyScheduleCreateResponse.class);

        return MyScheduleResource.createMyScheduleLink(myScheduleCreateResponse);
    }

    @Transactional(readOnly = true)
    public EntityModel<MyScheduleResponse> getMySchedule(Long myScheduleId, String email) {
        MySchedule mySchedule = myScheduleRepository.findById(myScheduleId).orElseThrow(MyScheduleNotFoundException::new);
        MyScheduleResponse myScheduleResponse = modelMapper.map(mySchedule, MyScheduleResponse.class);

        return MyScheduleResource.getMyScheduleLink(myScheduleResponse);
    }

    @Transactional(readOnly = true)
    public CollectionModel<EntityModel<MyScheduleResponse>> getMyScheduleList(YearMonth yearMonth, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        List<MySchedule> myScheduleList = myScheduleRepository.findAllByEmail(member.getEmail(), yearMonth);
        for (MySchedule mySchedule : myScheduleList) {
            if (!member.getEmail().equals(mySchedule.getMember().getEmail())) {
                throw new PermissionException();
            }
        }
        List<MyScheduleResponse> myScheduleResponseList = myScheduleList.stream()
                .map(mySchedule -> modelMapper.map(mySchedule, MyScheduleResponse.class))
                .collect(Collectors.toList());
        return MyScheduleResource.getMyScheduleListLink(myScheduleResponseList, member.getEmail());
    }

    public EntityModel<MyScheduleUpdateResponse> update(Long myScheduleId, MyScheduleUpdateRequest updateRequest, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        MySchedule mySchedule = myScheduleRepository.findById(myScheduleId).orElseThrow(MyScheduleNotFoundException::new);
        if (!member.equals(mySchedule.getMember())) {
            throw new PermissionException();
        }
        mySchedule.update(updateRequest);
        MyScheduleUpdateResponse myScheduleUpdateResponse = modelMapper.map(mySchedule, MyScheduleUpdateResponse.class);
        return MyScheduleResource.updateMyScheduleLink(myScheduleUpdateResponse);
    }

    public EntityModel<MyScheduleDeleteResponse> delete(Long myScheduleId, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        MySchedule mySchedule = myScheduleRepository.findById(myScheduleId).orElseThrow(MyScheduleNotFoundException::new);
        if (!member.equals(mySchedule.getMember())) {
            throw new PermissionException();
        }
        myScheduleRepository.deleteById(myScheduleId);
        MyScheduleDeleteResponse myScheduleDeleteResponse = MyScheduleDeleteResponse.builder()
                .message("나의 스케줄을 삭제하였습니다.")
                .success(true)
                .build();
        return MyScheduleResource.deleteMyScheduleLink(myScheduleId, myScheduleDeleteResponse);
    }
}
