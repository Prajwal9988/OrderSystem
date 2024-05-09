package com.intuit.orderManagementSystem.order.management.system.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalApiException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ApiExceptionTemplate exceptionTemplate = new ApiExceptionTemplate(
                e.getMessage(),
                httpStatus,
                LocalDateTime.now().toString()
        );
        log.error("Validation Exception occurred{} ", e.getMessage(), e);
        return new ResponseEntity<>(exceptionTemplate, httpStatus);
    }

    @ExceptionHandler(IdNotFoundException.class)
    public ResponseEntity<Object> handleIdNotFoundException (IdNotFoundException e){
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ApiExceptionTemplate exceptionTemplate = new ApiExceptionTemplate(
               e.getMessage(),
                httpStatus,
                LocalDateTime.now().toString()
       );
        log.error("resource not found exception{} ", e.getMessage(), e);
        return new ResponseEntity<>(exceptionTemplate, httpStatus);
    }

    @ExceptionHandler(UnexpectedException.class)
    public ResponseEntity<Object> handleUnexpectedException (UnexpectedException e){
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiExceptionTemplate exceptionTemplate = new ApiExceptionTemplate(
                e.getMessage(),
                httpStatus,
                LocalDateTime.now().toString()
        );
        log.error("Unexpected Exception occured{} ", e.getMessage(), e);
        return new ResponseEntity<>(exceptionTemplate, httpStatus);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalStateException (IllegalStateException e){
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        ApiExceptionTemplate exceptionTemplate = new ApiExceptionTemplate(
                e.getMessage(),
                httpStatus,
                LocalDateTime.now().toString()
        );
        log.error("Illegal state exception occured {} ", e.getMessage(), e);
        return new ResponseEntity<>(exceptionTemplate, httpStatus);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException (BadRequestException e){
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ApiExceptionTemplate exceptionTemplate = new ApiExceptionTemplate(
                e.getMessage(),
                httpStatus,
                LocalDateTime.now().toString()
        );
        log.error("Bad request exception occurred {} ", e.getMessage(), e);
        return new ResponseEntity<>(exceptionTemplate, httpStatus);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbiddenException (ForbiddenException e){
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        ApiExceptionTemplate exceptionTemplate = new ApiExceptionTemplate(
                e.getMessage(),
                httpStatus,
                LocalDateTime.now().toString()
        );
        log.error("User was forbidden to perform the action {} ", e.getMessage(), e);
        return new ResponseEntity<>(exceptionTemplate, httpStatus);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<Object> handleWrongProperty (PropertyReferenceException e){
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ApiExceptionTemplate exceptionTemplate = new ApiExceptionTemplate(
                e.getMessage(),
                httpStatus,
                LocalDateTime.now().toString()
        );
        log.error("Wrong property exception {} ", e.getMessage(), e);
        return new ResponseEntity<>(exceptionTemplate, httpStatus);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAnyException(Exception e){
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        log.error(e.toString());
        ApiExceptionTemplate exceptionTemplate = new ApiExceptionTemplate(
                e.getMessage(),
                httpStatus,
                LocalDateTime.now().toString()
        );
        log.error("Something went wrong. {} ", e.getMessage(), e);
        return new ResponseEntity<>(exceptionTemplate, httpStatus);
    }

}
