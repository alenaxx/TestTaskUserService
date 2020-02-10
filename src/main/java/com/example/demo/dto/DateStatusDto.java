package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class DateStatusDto {

    public Long date;

    public String status;

    public UUID id;
}
