package com.example.backend.repository;


import com.example.backend.dto.Users;
import com.example.backend.dto.UsersPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;


//JpaRepository<[사용자 객체], [사용자 테이블 기본키의 데이터 타입]>을 추가합니다.
//JpaRepository가 들어가있으면 단순한 CRUD(Create, Read, Update, Delete)는 처리가 가능합니다.
public interface SignUpRepository extends JpaRepository<Users, UsersPrimaryKey> {

    // userId로 사용자 검색
//    Optional<Users> findByUserId(String userId);
}
