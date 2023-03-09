package com.moa.domain.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "USERS")
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    private String email;
    private String password;
    private String name;
    private String nickname;
    @Column(columnDefinition = "decimal(18,10)")
    private double locationLatitude;
    @Column(columnDefinition = "decimal(18,10)")
    private double locationLongitude;
    private int popularity;
    @Lob
    @Column(columnDefinition = "CLOB")
    private String details;

    private String refreshToken;

    @Builder
    public User(String email, String password, String name, String nickname, double locationLatitude, double locationLongitude, int popularity, String details) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.popularity = popularity;
        this.details = details;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
