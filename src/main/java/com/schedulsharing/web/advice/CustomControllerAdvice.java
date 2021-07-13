package com.schedulsharing.web.advice;

import com.schedulsharing.excpetion.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomControllerAdvice {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> businessExceptionHandler(BusinessException ex) {
        ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                //TODO error의 원인이 되는 class를 error에 담아주면 좋을 것 같다.
                .error("BadRequest")
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
