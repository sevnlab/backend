package com.example.backend.study.redis.repository;


import com.example.backend.study.redis.entity.RedisCacheUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RedisCacheRepository extends JpaRepository<RedisCacheUser, Long> {

    Optional<RedisCacheUser> findByName(String name);
}
