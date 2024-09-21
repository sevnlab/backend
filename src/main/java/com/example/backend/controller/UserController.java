package com.example.backend.controller;

import com.example.backend.config.JwtTokenProvider;
import com.example.backend.dto.Login;
import com.example.backend.dto.Users;
import com.example.backend.repository.SignUpRepository;

import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // 해당 오리진에서의 요청을 허용
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // 회원가입 기능
    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@RequestBody Users user) {

        // log.info 로 변경할것.
        System.out.println("user ======" + user.toString());

        userService.signUp(user);

        System.out.println("test tttt");

        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    // 로그인 기능
    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody Login.req req) {
        System.out.println("user ======" + req.toString());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUserId(), req.getPassword())
            );

            String token = jwtTokenProvider.generateToken(authentication);
            return ResponseEntity.ok(new Login.res(token));
        } catch(BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: 아이디 또는 비밀번호가 일치하지 않습니다.");
        }
//        userService.signIn(user);
    }
}
