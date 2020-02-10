package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@AllArgsConstructor
@Data
public class UserStatusDto {

    private UUID id;

    private String lastStatus;

    private String newStatus;

}
