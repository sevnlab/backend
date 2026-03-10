package com.example.backend.service;

import com.example.backend.dto.SignInRequest;
import com.example.backend.dto.SignInResponse;
import com.example.backend.entity.Member;
import com.example.backend.repository.SignUpRepository;
import com.example.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
//public class UserService implements UserDetailsService {
public class UserService {

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
    public void signUp(Member member) {
        // 네이버 로그인 사용자는 비밀번호를 설정하지 않음
//        if (!users.isSocialLogin()) {
//            users.setPassword(passwordEncoder.encode(users.getPassword())); // 비밀번호를 암호화해서 저장
//        }
        signUpRepository.save(member);
    }

    /**
     * 로그인 시도
     */
    public SignInResponse signIn(SignInRequest request) {
        Member member = signUpRepository.findByMemberIdAndPassword(request.getMemberId(), request.getPassword())
                .orElseThrow(() -> new UsernameNotFoundException("아이디 또는 비밀번호가 올바르지 않습니다."));

        SignInResponse response = new SignInResponse();
        response.setMemberId(member.getMemberId());
        response.setName(member.getName());
        response.setEmail(member.getEmail());

        return response;
    }

    // userId로 사용자 조회
    public Member findById(String id) {
        return userRepository.findByMemberId(id);
    }

//    @Override
//    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
//        // DB에서 userId로 사용자 정보 조회
//        Member user = signUpRepository.findByUserId(userId)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
//
//        // 사용자 정보를 UserDetails 객체로 변환하여 반환
//        return new org.springframework.security.core.userdetails.User(
////                user.getUserId(),
//                user.getPassword(),
//                new ArrayList<>()
//        );
//    }
}
