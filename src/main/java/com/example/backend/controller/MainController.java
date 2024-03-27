package com.example.backend.controller;

import com.example.backend.dto.Users;
import com.example.backend.repository.SignUpRepository;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // 해당 오리진에서의 요청을 허용
public class MainController {
    @Autowired
    private SignUpRepository signUpRepository;

    @GetMapping("/test")
    public String test () {

        return "zzzz";
    }

    @PostMapping("/signUp")
    public String signUp(HttpServletRequest request, @RequestBody Users user) {
        System.out.println("user ======" + user.toString());


        signUpRepository.save(user); // Insert 작업

        System.out.println("test tttt");

        return "회원가입이 완료되었습니다.";
    }
}
