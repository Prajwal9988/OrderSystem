package com.intuit.orderManagementSystem.order.management.system.utils.Mappers.Interface;

public interface GenericRequestToEntityMapper<Entity, Request> {
    public Entity RequestToEntity(Request request);
}
