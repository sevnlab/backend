package com.example.backend.controller;

import com.example.backend.dto.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // 해당 오리진에서의 요청을 허용
public class MainController {
    @PostMapping("/signUp")
    public List<String> signUp(HttpServletRequest request, @RequestBody User user) {

        System.out.println("zdsadasdasdsa" + request);
        System.out.println("user조회111"+ user.getEmail());
        System.out.println("user조회222"+ user.getPassword());
        System.out.println("user조회333"+ user.getName());

        return Arrays.asList("success");
    }
}
