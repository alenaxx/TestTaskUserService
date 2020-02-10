package com.example.demo.dao;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserStatusDto;
import com.example.demo.exception.EmailNotValidException;
import com.example.demo.exception.PhoneNumberNotValidException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.User;

import java.util.UUID;

public interface UserDao {

    UUID addUser(UUID id, User user) throws EmailNotValidException, PhoneNumberNotValidException;

    default UUID addUser(User user) throws EmailNotValidException, PhoneNumberNotValidException {
        UUID id = UUID.randomUUID();
        return addUser(id, user);
    }

    UserDto getUser(UUID id) throws UserNotFoundException;

    UserStatusDto changeStatus(UUID id, String status) throws InterruptedException, UserNotFoundException;

}
