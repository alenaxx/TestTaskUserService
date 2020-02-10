package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class ApiExceptionHandle {

    public ResponseEntity UserNotFoundException() {
        ApiException apiException = new ApiException(
                "User not found",
                HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(
                apiException,
                HttpStatus.BAD_REQUEST);
    }


}
