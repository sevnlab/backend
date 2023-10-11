package com.example.backend.repository;

import com.example.backend.dto.User;
import com.example.backend.dto.UserPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignUpRepository extends JpaRepository<User, UserPrimaryKey> {


}
