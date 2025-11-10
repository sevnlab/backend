package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Login {
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class req {
        private String userId;
        private String password;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class res {
        private String token; // JWT ÅäÅ«
    }


}
