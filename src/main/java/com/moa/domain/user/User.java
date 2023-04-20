package com.moa.domain.user;

import com.moa.domain.interests.Interests;
import com.moa.domain.interests.RecruitmentInterest;
import com.moa.domain.link.Link;
import com.moa.dto.user.UserInfoUpdateRequest;
import com.moa.dto.user.UserProfileUpdateRequest;
import com.moa.dto.user.UserUpdateRequest;
import com.moa.global.exception.service.InvalidRequestException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.moa.global.exception.ErrorCode.USER_MISMATCH_PASSWORD;

@Entity
@Table(name = "USERS")
@Getter
@DynamicUpdate
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
    private Double locationLatitude;
    @Column(columnDefinition = "decimal(18,10)")
    private Double locationLongitude;
    @Embedded
    private Popularity popularity;
    @Lob
    @Column(columnDefinition = "CLOB")
    private String details;

    private String imageUrl;

    private String refreshToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Interests> interests = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Link> link = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Alarm> alarms = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RecruitmentInterest> recruitmentInterests = new ArrayList<>();

    @Builder
    public User(String email, String password, Popularity popularity, String name, String nickname, double locationLatitude, double locationLongitude, String details, String imageUrl) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.popularity = popularity != null ? popularity : new Popularity();
        this.details = details;
        this.imageUrl = imageUrl;
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

    public void update(UserProfileUpdateRequest request, String imageUrl) {
        if (request != null) {
            this.locationLatitude = validateDouble(request.locationLatitude(), this.locationLatitude);
            this.locationLongitude = validateDouble(request.locationLongitude(), this.locationLongitude);
            this.details = validateStringValue(request.details(), this.details);
            addInterests(request.stringToInterests());
            addLinks(request.stringToLink());
        }
        this.imageUrl = imageUrl;
    }

    public void update(UserInfoUpdateRequest request, PasswordEncoder passwordEncoder) {
        updatePassword(request, passwordEncoder);
        this.name = validateStringValue(request.name(), this.name);
        this.nickname = validateStringValue(request.nickname(), this.nickname);
    }

    private void updatePassword(UserInfoUpdateRequest request, PasswordEncoder passwordEncoder) {
        if (StringUtils.hasText(request.currentPassword())) {
            if (!checkPasswordChangeValue(request, passwordEncoder)) {
                throw new InvalidRequestException(USER_MISMATCH_PASSWORD);
            }
            this.password = passwordEncoder.encode(request.newPassword());
        }
    }

    public void update(UserUpdateRequest updateRequest) {
        this.name = validateStringValue(updateRequest.name(), this.name);
        this.nickname = validateStringValue(updateRequest.nickname(), this.nickname);
        this.locationLatitude = validateDouble(updateRequest.locationLatitude(), this.locationLatitude);
        this.locationLongitude = validateDouble(updateRequest.locationLongitude(), this.locationLongitude);
        this.details = validateStringValue(updateRequest.details(), this.details);
        addInterests(updateRequest.interestsValue());
        addLinks(updateRequest.linksValue());
    }

    public void addInterests(List<Interests> interests) {
        if (interests != null) {
            this.interests.clear();
            this.interests.addAll(interests);
            interests.forEach(i -> i.setParent(this));
        }
    }

    private void addLinks(List<Link> links) {
        if (links != null) {
            this.link.clear();
            this.link.addAll(links);
            links.forEach(l -> l.setParent(this));
        }
    }

    public void addRecruitmentInterests(RecruitmentInterest recruitmentInterest) {
        this.recruitmentInterests.add(recruitmentInterest);
    }

    public void addAlarm(Alarm alarm) {
        if (!alarms.contains(alarm)) {
            alarms.add(alarm);
        }
    }

    private boolean checkPasswordChangeValue(UserInfoUpdateRequest request, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(request.currentPassword(), this.password);
    }

    private String validateStringValue(String value, String origin) {
        if (StringUtils.hasText(value) && !value.equals(origin)) {
            return value;
        }
        return origin;
    }

    private Double validateDouble(Double value, Double origin) {
        if (value != null && !value.equals(origin)) {
            return value;
        }
        return origin;
    }
}
