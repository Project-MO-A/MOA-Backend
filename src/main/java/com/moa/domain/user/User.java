package com.moa.domain.user;

import com.moa.domain.interests.Interests;
import com.moa.domain.interests.RecruitmentInterest;
import com.moa.dto.user.UserUpdateRequest;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USERS")
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(unique = true)
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Interests> interests = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Alarm> alarms = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RecruitmentInterest> recruitmentInterests = new ArrayList<>();

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

    public void changePassword(PasswordEncoder passwordEncoder, final String newPassword) {
        this.password = passwordEncoder.encode(newPassword);
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void addInterests(List<Interests> interests) {
        if (interests != null) {
            this.interests = interests;
            interests.forEach(i -> i.setParent(this));
        }
    }

    public void addAlarm(Alarm alarm) {
        if (!alarms.contains(alarm)) {
            alarms.add(alarm);
        }
    }
    
    public void update(UserUpdateRequest updateRequest) {
        this.name = updateRequest.name();
        this.nickname = updateRequest.nickname();
        this.locationLatitude = updateRequest.locationLatitude();
        this.locationLongitude = updateRequest.locationLongitude();
        this.details = updateRequest.details();
        addInterests(updateRequest.interestsValue());
    }
}
