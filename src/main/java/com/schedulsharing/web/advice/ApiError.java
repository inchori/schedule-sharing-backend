package com.schedulsharing.web.advice;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    private HttpStatus httpStatus;
    private String error;
    private String message;
}
