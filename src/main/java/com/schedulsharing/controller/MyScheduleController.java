package com.schedulsharing.controller;

import com.schedulsharing.dto.yearMonth.YearMonthRequest;
import com.schedulsharing.dto.MySchedule.MyScheduleCreateRequest;
import com.schedulsharing.dto.MySchedule.MyScheduleCreateResponse;
import com.schedulsharing.dto.MySchedule.MyScheduleUpdateRequest;
import com.schedulsharing.dto.resource.MyScheduleResource;
import com.schedulsharing.service.MyScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

        EntityModel<MyScheduleCreateResponse> entityModel = myScheduleService.create(createRequest, authentication.getName());

        return ResponseEntity.created(MyScheduleResource.getCreatedUri(entityModel.getContent().getMyScheduleId())).body(entityModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity getMySchedule(@PathVariable("id") Long id, Authentication authentication) {
        return ResponseEntity.ok(myScheduleService.getMySchedule(id, authentication.getName()));
    }

    @GetMapping("/yearMonth")
    public ResponseEntity getMyScheduleList(@RequestParam YearMonthRequest yearMonthRequest, Authentication authentication) {
        return ResponseEntity.ok(myScheduleService.getMyScheduleList(yearMonthRequest, authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateMySchedule(@PathVariable("id") Long id,
                                           @RequestBody @Valid MyScheduleUpdateRequest updateRequest, Authentication authentication) {
        return ResponseEntity.ok(myScheduleService.update(id, updateRequest, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteMySchedule(@PathVariable("id") Long id, Authentication authentication) {
        return ResponseEntity.ok(myScheduleService.delete(id, authentication.getName()));
    }
}
