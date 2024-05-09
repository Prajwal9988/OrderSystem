package com.intuit.orderManagementSystem.order.management.system.service.serviceImpl;

import com.intuit.orderManagementSystem.order.management.system.entity.User;
import com.intuit.orderManagementSystem.order.management.system.exception.IdNotFoundException;
import com.intuit.orderManagementSystem.order.management.system.repository.UserRepository;
import com.intuit.orderManagementSystem.order.management.system.service.serviceInterface.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUser(String userName) throws IdNotFoundException{
        Optional<User> user = userRepository.findByUserName(userName);
        log.info("User name : {} ", userName);
        if(user.isEmpty()) throw new IdNotFoundException("User with this username not found");
        return user.get();
    }
}
