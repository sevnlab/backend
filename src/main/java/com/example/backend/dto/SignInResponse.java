package com.example.backend.dto;

import lombok.Data;

@Data
public class SignInResponse {

    private String memberId;
    private String name;
    private String email;
}
