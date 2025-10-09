package com.example.backend.controller;

import com.example.backend.dto.User;
import com.example.backend.repository.SignUpRepository;
import com.example.backend.service.SignUpService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // 해당 오리진에서의 요청을 허용
public class MainController {
    @Autowired
    private SignUpRepository signUpRepository;

    @PostMapping("/signUp")
    public String signUp(HttpServletRequest request, @RequestBody User user) {
        signUpRepository.save(user); // Insert 작업

//        현재 아래링크 참조해서 진행중
//        https://mun9659.tistory.com/m/15

        return "회원가입이 완료되었습니다.";
    }
}
