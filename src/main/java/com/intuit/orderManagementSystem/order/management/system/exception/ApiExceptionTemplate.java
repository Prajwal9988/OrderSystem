package com.intuit.orderManagementSystem.order.management.system.exception;

import org.springframework.http.HttpStatus;


public class ApiExceptionTemplate {
    private final String message;
    private final HttpStatus httpStatus;
    private final String timeStamp;

    public ApiExceptionTemplate(String message, HttpStatus httpStatus, String timeStamp) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatusCode() {
        return httpStatus;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}
