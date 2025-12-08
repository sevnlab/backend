package com.example.backend.study.redis.repository;


import com.example.backend.study.redis.entity.RedisCacheUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RedisCacheRepository extends JpaRepository<RedisCacheUser, Long> {

}
