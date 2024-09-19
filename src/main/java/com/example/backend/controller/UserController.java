package com.example.backend.controller;

import com.example.backend.dto.Users;
import com.example.backend.repository.SignUpRepository;

import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // 해당 오리진에서의 요청을 허용
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signUp")
    public String signUp(@RequestBody Users user) {

        // log.info 로 변경할것.
        System.out.println("user ======" + user.toString());

        userService.signUp(user);

        System.out.println("test tttt");

        return "회원가입이 완료되었습니다.";
    }

    @PostMapping("/signIn")
    public String signIn(@RequestBody Users user) {
        System.out.println("user ======" + user.toString());

        userService.signIn(user);


        System.out.println("test tttt");

        return "회원가입이 완료되었습니다.";
    }
}
