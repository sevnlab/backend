package com.example.backend.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "USERS") // Assuming the table is named 'users' in your database
public class Users implements Serializable {
    @Serial
    private static final long serialVersionUID = 5149112013190869890L;
    @Id
    @Column(name = "USERID", nullable = false)
    private String userId;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    private String password; // 비번은 나중에 해시키로 저장해야함

    @Column(name = "NAME", nullable = false)
    private String name;

    // Constructors, getters, and setters
}