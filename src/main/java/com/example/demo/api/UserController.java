package com.example.demo.api;

import com.example.demo.exception.ApiExceptionHandle;
import com.example.demo.exception.EmailNotValidException;
import com.example.demo.exception.PhoneNumberNotValidException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("user")
@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UUID addUser(@RequestBody User user) throws EmailNotValidException, PhoneNumberNotValidException {

        return userService.addUser(user);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity getUser(@PathVariable("id") UUID id) {
        try {
            return ResponseEntity.ok(userService.getUser(id));
        } catch (UserNotFoundException e) {
            return new ApiExceptionHandle().UserNotFoundException();
        }
    }

    @PutMapping(path = "/{id}/{status}")
    public ResponseEntity changeUserStatus(@PathVariable("id") UUID id, @PathVariable("status") String status) throws InterruptedException, UserNotFoundException {
        return ResponseEntity.ok(userService.changeStatus(id, status));
    }


}
