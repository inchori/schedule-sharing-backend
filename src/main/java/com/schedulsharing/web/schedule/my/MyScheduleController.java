package com.schedulsharing.web.schedule.my;

import com.schedulsharing.web.schedule.my.dto.*;
import com.schedulsharing.web.dto.resource.MyScheduleResource;
import com.schedulsharing.service.schedule.MyScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("api/myschedule")
@RequiredArgsConstructor
public class MyScheduleController {
    private final MyScheduleService myScheduleService;

    @PostMapping
    public MyScheduleCreateResponse createMySchedule(@RequestBody @Valid MyScheduleCreateRequest createRequest,
                                           Authentication authentication, Errors errors) {
        return myScheduleService.create(createRequest, authentication.getName());
    }

    @GetMapping("/{id}")
    public MyScheduleResponse getMySchedule(@PathVariable("id") Long id, Authentication authentication) {
        return myScheduleService.getMySchedule(id, authentication.getName());
    }

    @GetMapping("/list")
    public List<MyScheduleResponse> getMyScheduleList(@RequestParam("yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth,
                                                      Authentication authentication) {
        return myScheduleService.getMyScheduleList(yearMonth, authentication.getName());
    }

    @PutMapping("/{id}")
    public MyScheduleUpdateResponse updateMySchedule(@PathVariable("id") Long id,
                                                     @RequestBody @Valid MyScheduleUpdateRequest updateRequest,
                                                     Authentication authentication) {
        return myScheduleService.update(id, updateRequest, authentication.getName());
    }

    @DeleteMapping("/{id}")
    public MyScheduleDeleteResponse deleteMySchedule(@PathVariable("id") Long id, Authentication authentication) {
        return myScheduleService.delete(id, authentication.getName());
    }
}