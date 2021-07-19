package com.schedulsharing.web.advice;

import com.schedulsharing.excpetion.BusinessException;
import com.schedulsharing.excpetion.PermissionException;
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
                .error(ex.getClass().getSimpleName())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<ApiError> permisstionExceptionHandler(PermissionException ex) {
        ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .error(ex.getClass().getSimpleName())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }
}
