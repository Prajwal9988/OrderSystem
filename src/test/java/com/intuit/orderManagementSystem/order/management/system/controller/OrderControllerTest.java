package com.intuit.orderManagementSystem.order.management.system.controller;

import com.intuit.orderManagementSystem.order.management.system.exception.IdNotFoundException;
import com.intuit.orderManagementSystem.order.management.system.service.serviceInterface.OrderService;
import com.intuit.orderManagementSystem.order.management.system.utils.Request.OrderRequest;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Orders.OrderDeleteResponse;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Orders.OrderResponse;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Orders.OrderSubmitResponse;
import org.apache.coyote.BadRequestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Test
    public void getOrdersWithValidUserName() throws BadRequestException {
        String userName = "testUser";
        List<OrderResponse> orderResponses = List.of(new OrderResponse(), new OrderResponse());
        ResponseEntity<List<OrderResponse>> expectedResponse = ResponseEntity.ok(orderResponses);
        when(orderService.getOrders(eq(userName), anyInt(), anyInt())).thenReturn(orderResponses);

        ResponseEntity<List<OrderResponse>> actualResponse = orderController.getOrders(userName, 0, 25);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getOrdersWithNullUserName() {
        String userName = null;
        assertThrows(BadRequestException.class, () -> orderController.getOrders(userName, 0, 25));
    }

    @Test
    public void saveOrder() throws IdNotFoundException, BadRequestException {
        String userName = "testUser";
        OrderRequest orderRequest = new OrderRequest();
        OrderResponse orderResponse = new OrderResponse();
        ResponseEntity<OrderResponse> expectedResponse = new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
        when(orderService.saveOrder(eq(userName), eq(orderRequest))).thenReturn(orderResponse);

        ResponseEntity<OrderResponse> actualResponse = orderController.saveOrder(userName, orderRequest);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void saveOrderWithNullUserName() {
        String userName = null;
        OrderRequest orderRequest = new OrderRequest();
        assertThrows(BadRequestException.class, () -> orderController.saveOrder(userName, orderRequest));
    }

    @Test
    public void submitOrderWithValidOrderId() throws BadRequestException {
        String userName = "testUser";
        Long orderId = 1L;
        OrderSubmitResponse orderSubmitResponse = new OrderSubmitResponse();
        ResponseEntity<OrderSubmitResponse> expectedResponse = new ResponseEntity<>(orderSubmitResponse, HttpStatus.CREATED);
        when(orderService.submitOrder(eq(userName), eq(orderId))).thenReturn(orderSubmitResponse);

        ResponseEntity<OrderSubmitResponse> actualResponse = orderController.placeOrder(userName, orderId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void submitOrderWithNullOrderId() {
        String userName = "testUser";
        Long orderId = null;
        assertThrows(BadRequestException.class, () -> orderController.placeOrder(userName, orderId));
    }

    @Test
    public void editOrderWithValidOrderId() throws BadRequestException {
        // Setup
        String userName = "testUser";
        Long orderId = 1L;
        OrderRequest orderRequest = new OrderRequest();
        OrderResponse orderResponse = new OrderResponse();
        ResponseEntity<OrderResponse> expectedResponse = new ResponseEntity<>(orderResponse, HttpStatus.OK);
        when(orderService.editOrder(eq(userName), eq(orderRequest), eq(orderId))).thenReturn(orderResponse);
        ResponseEntity<OrderResponse> actualResponse = orderController.editOrder(userName, orderRequest, orderId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void editOrderWithNullOrderId() {
        String userName = "testUser";
        Long orderId = null;
        OrderRequest orderRequest = new OrderRequest();
        assertThrows(BadRequestException.class, () -> orderController.editOrder(userName, orderRequest, orderId));
    }

    @Test
    public void deleteOrderWithValidOrderId() throws BadRequestException {
        String userName = "testUser";
        Long orderId = 1L;
        OrderDeleteResponse orderDeleteResponse = new OrderDeleteResponse();
        ResponseEntity<OrderDeleteResponse> expectedResponse = new ResponseEntity<>(orderDeleteResponse, HttpStatus.OK);
        when(orderService.deleteOrder(eq(userName), eq(orderId))).thenReturn(orderDeleteResponse);
        ResponseEntity<OrderDeleteResponse> actualResponse = orderController.deleteOrder(userName, orderId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void deleteOrderWithNullOrderId() {
        String userName = "testUser";
        Long orderId = null;
        assertThrows(BadRequestException.class, () -> orderController.deleteOrder(userName, orderId));
    }

}
