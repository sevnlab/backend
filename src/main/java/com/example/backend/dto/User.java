package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.io.Serializable;

@Getter
@Setter
@ToString
public class User {
    private String email = "";
    private String password = "";
    private String name = "";
}
