package com.schedulsharing.web.schedule.my;

import com.schedulsharing.web.schedule.my.dto.MyScheduleCreateRequest;
import com.schedulsharing.web.schedule.my.dto.MyScheduleCreateResponse;
import com.schedulsharing.web.schedule.my.dto.MyScheduleUpdateRequest;
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

@RestController
@RequestMapping("api/myschedule")
@RequiredArgsConstructor
public class MyScheduleController {
    private final MyScheduleService myScheduleService;

    @PostMapping
    public ResponseEntity createMySchedule(@RequestBody @Valid MyScheduleCreateRequest createRequest,
                                           Authentication authentication, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        MyScheduleCreateResponse myScheduleCreateResponse = myScheduleService.create(createRequest, authentication.getName());

        return ResponseEntity.created(MyScheduleResource.getCreatedUri(myScheduleCreateResponse.getMyScheduleId())).body(myScheduleCreateResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity getMySchedule(@PathVariable("id") Long id, Authentication authentication) {
        return ResponseEntity.ok(myScheduleService.getMySchedule(id, authentication.getName()));
    }

    @GetMapping("/list")
    public ResponseEntity getMyScheduleList(@RequestParam("yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth,
                                            Authentication authentication) {
        return ResponseEntity.ok(myScheduleService.getMyScheduleList(yearMonth, authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateMySchedule(@PathVariable("id") Long id,
                                           @RequestBody @Valid MyScheduleUpdateRequest updateRequest,
                                           Authentication authentication) {
        return ResponseEntity.ok(myScheduleService.update(id, updateRequest, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteMySchedule(@PathVariable("id") Long id, Authentication authentication) {
        return ResponseEntity.ok(myScheduleService.delete(id, authentication.getName()));
    }
}