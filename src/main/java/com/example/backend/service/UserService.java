package com.example.backend.service;

import com.example.backend.dto.Users;
import com.example.backend.repository.SignUpRepository;
import com.example.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {

    private final SignUpRepository signUpRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    // 생성자 주입
    @Autowired
    public UserService(SignUpRepository signUpRepository, PasswordEncoder passwordEncoder) {
        this.signUpRepository = signUpRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void signUp(Users users) {
        // 네이버 로그인 사용자는 비밀번호를 설정하지 않음
        if (!users.isSocialLogin()) {
            users.setPassword(passwordEncoder.encode(users.getPassword())); // 비밀번호를 암호화해서 저장
        }
        signUpRepository.save(users);
    }

    // userId로 사용자 조회
    public Users findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // DB에서 userId로 사용자 정보 조회
        Users user = signUpRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        // 사용자 정보를 UserDetails 객체로 변환하여 반환
        return new org.springframework.security.core.userdetails.User(
                user.getUserId(),
                user.getPassword(),
                new ArrayList<>()
        );
    }
}