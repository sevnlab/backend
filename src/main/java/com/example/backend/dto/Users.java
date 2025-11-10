package com.example.backend.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.intellij.lang.annotations.Pattern;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity // JPA ¿¡¼­´Â Entity ¸¦ »ç¿ëÇÔÀ¸·Î½á JPA ¿£ÅÍÆ¼·Î °ü¸®ÇØ¾ßÇÔ
@Table(name = "USERS") // Assuming the table is named 'users' in your database
public class Users implements Serializable {
    @Serial
    private static final long serialVersionUID = 5149112013190869890L;

    @Id
    @Column(name = "USERID", nullable = false)
    // À¯È¿¼º Ãß°¡ÇÒ°Í @validatin
//    @Pattern(regexp = "^[¤¡-¤¾°¡-ÆRa-z0-9-_]{2,10}$", message = "´Ð³×ÀÓÀº Æ¯¼ö¹®ÀÚ¸¦ Á¦¿ÜÇÑ 2~10ÀÚ¸®¿©¾ß ÇÕ´Ï´Ù.")
    private String userId;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "PASSWORD", nullable = true)
    private String password; // ºñ¹øÀº ³ªÁß¿¡ ÇØ½ÃÅ°·Î ÀúÀåÇØ¾ßÇÔ

    @Column(name = "NAME", nullable = true)
    private String name;

    @Column(name = "MOBILE", nullable = true)
    private String mobile;

    @Column(name = "BIRTH", nullable = true)
    private String BIRTH;

    @Column(name = "ADDRESS", nullable = true)
    private String ADDRESS;

    @Column(name = "ZIPCODE", nullable = true)
    private String ZIPCODE;

    @Column(name = "GENDER", nullable = true)
    private String GENDER;

    @Column(name = "IS_SOCIAL_LOGIN", nullable = true)
    private boolean isSocialLogin;
}