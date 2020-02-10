package com.example.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
class ApiException {

    private final String message;

    private final HttpStatus httpStatus;
}
