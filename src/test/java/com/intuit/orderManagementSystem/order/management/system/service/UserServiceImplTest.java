package com.intuit.orderManagementSystem.order.management.system.service;

import com.intuit.orderManagementSystem.order.management.system.entity.User;
import com.intuit.orderManagementSystem.order.management.system.exception.IdNotFoundException;
import com.intuit.orderManagementSystem.order.management.system.repository.UserRepository;
import com.intuit.orderManagementSystem.order.management.system.service.serviceImpl.UserServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final String USERNAME = "TEST";
    private User user;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testGetUser(){
        user = new User(1L, USERNAME, "tst", "user", "22323", "ABC");
        when(userRepository.findByUserName(eq(USERNAME))).thenReturn(Optional.of(user));
        User result = userService.getUser("TEST");
        assertEquals(result, user);
        verify(userRepository, times(1)).findByUserName(USERNAME);
    }

    @Test
    public void testGetUserWithEmptyResult(){
        when(userRepository.findByUserName(eq(USERNAME))).thenReturn(Optional.empty());

        IdNotFoundException exception = assertThrows(IdNotFoundException.class,
                () -> userService.getUser("TEST"));
        Assert.assertEquals("User with this username not found", exception.getMessage());
    }
}
