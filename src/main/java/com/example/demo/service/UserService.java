package com.example.demo.service;

import com.example.demo.dao.UserDao;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserStatusDto;
import com.example.demo.exception.EmailNotValidException;
import com.example.demo.exception.PhoneNumberNotValidException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(@Qualifier("postgres") UserDao userDao) {
        this.userDao = userDao;
    }

    public UUID addUser(User user) throws EmailNotValidException, PhoneNumberNotValidException {
        return userDao.addUser(user);
    }

    public UserDto getUser(UUID id) throws UserNotFoundException {
        return userDao.getUser(id);
    }

    public UserStatusDto changeStatus(UUID id, String status) throws InterruptedException, UserNotFoundException {
        return userDao.changeStatus(id, status);
    }
}
