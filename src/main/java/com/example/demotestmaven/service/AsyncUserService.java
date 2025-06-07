package com.example.demotestmaven.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.demotestmaven.entity.User;
import com.example.demotestmaven.repository.UserRepository;

@Service
public class AsyncUserService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(AsyncUserService.class);

    @Async("threadPoolTaskExecutor")
    public void saveUsers(User user) {
        // logger.info("Thread: {} and User: {} starting to save", Thread.currentThread().getName(), user.getUsername());
        userRepository.save(user);
        logger.info("Thread: {} and User: {} saved", Thread.currentThread().getName(), user.getUsername());
    }
}