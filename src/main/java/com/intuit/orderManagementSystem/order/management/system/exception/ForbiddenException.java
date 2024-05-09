package com.intuit.orderManagementSystem.order.management.system.exception;

public class ForbiddenException extends RuntimeException{
    public ForbiddenException(String msg){
        super(msg);
    }
}
