package com.intuit.orderManagementSystem.order.management.system.utils.Mappers.Interface;

public interface GenericEntityToResponseMapper<Entity, Response> {
    public Response MapToResponse(Entity entity);
}
