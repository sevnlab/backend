package com.example.backend.repository;

import com.example.backend.dto.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByUserId(String userId);  // userId로 사용자 조회
}