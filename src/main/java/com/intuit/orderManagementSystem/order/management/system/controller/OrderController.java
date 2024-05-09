package com.intuit.orderManagementSystem.order.management.system.controller;

import com.intuit.orderManagementSystem.order.management.system.exception.IdNotFoundException;
import com.intuit.orderManagementSystem.order.management.system.service.serviceInterface.OrderService;
import com.intuit.orderManagementSystem.order.management.system.utils.Request.OrderRequest;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Orders.OrderDeleteResponse;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Orders.OrderResponse;
import com.intuit.orderManagementSystem.order.management.system.utils.Response.Orders.OrderSubmitResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;


    @GetMapping("/{userName}")
    public ResponseEntity<List<OrderResponse>> getOrders(@PathVariable String userName,
                                                         @RequestParam(defaultValue = "0") Integer pageNumber,
                                                         @RequestParam(required = false, defaultValue = "25") Integer pageSize) throws BadRequestException {
        if(userName == null || userName.equals("")) throw new BadRequestException("Username cannot be empty");
        List<OrderResponse> orderResponses = orderService.getOrders(userName, pageNumber, pageSize);
        return ResponseEntity.ok(orderResponses);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> saveOrder(@RequestHeader String userName, @Validated  @RequestBody OrderRequest orderRequest) throws BadRequestException, IdNotFoundException {
        if(userName == null) throw new BadRequestException("username not found");
        OrderResponse orderResponse = orderService.saveOrder(userName, orderRequest);
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }

    @PostMapping(value = "/submit/{orderId}")
    public ResponseEntity<OrderSubmitResponse> placeOrder(@RequestHeader String userName,  @PathVariable Long orderId) throws BadRequestException {
        if(orderId == null) throw new BadRequestException("Order id not found");
        OrderSubmitResponse orderSubmitResponse = orderService.submitOrder(userName, orderId);
        return new ResponseEntity<>(orderSubmitResponse, HttpStatus.CREATED);
    }

    @PutMapping(value="/{orderId}")
    public ResponseEntity<OrderResponse> editOrder(@RequestHeader String userName, @Validated @RequestBody OrderRequest orderRequest, @PathVariable Long orderId) throws BadRequestException {
        if(orderId == null) throw new BadRequestException("Order id not found");
        OrderResponse orderResponse = orderService.editOrder(userName, orderRequest, orderId);
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }

    @DeleteMapping(value="/{orderId}")
    public ResponseEntity<OrderDeleteResponse> deleteOrder(@RequestHeader String userName, @PathVariable Long orderId) throws BadRequestException {
        if(orderId == null) throw new BadRequestException("Order id not found");
        OrderDeleteResponse orderDeleteResponse = orderService.deleteOrder(userName, orderId);
        return new ResponseEntity<>(orderDeleteResponse, HttpStatus.OK);
    }
}
