package com.example.backend.service;

import com.example.backend.dto.Users;
import com.example.backend.repository.SignUpRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private SignUpRepository signUpRepository;

    @Transactional
    public void signUp(Users users) {
        signUpRepository.save(users);
    }




}
