package com.example.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class MainController {

    @GetMapping("/")
    public List<String> hello() {
        return Arrays.asList("첫번째 인사", "두번째 인사");
    }
}
