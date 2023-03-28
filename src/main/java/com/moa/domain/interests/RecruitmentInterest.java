package com.moa.domain.interests;

import com.moa.domain.recruit.Recruitment;
import com.moa.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentInterest {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "RECRUITMENT_ID")
    private Recruitment recruitment;

    public RecruitmentInterest(User user, Recruitment recruitment) {
        this.user = user;
        this.recruitment = recruitment;
    }
}
