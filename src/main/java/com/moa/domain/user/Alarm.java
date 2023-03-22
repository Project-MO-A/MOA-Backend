package com.moa.domain.user;

import com.moa.domain.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ALARMS")
@Entity
public class Alarm extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ALARM_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @Column(nullable = false)
    private boolean confirm;
    private String message;
    private String redirectURI;

    @Builder
    public Alarm(User user, AlarmType alarmType, Long relateId) {
        this.alarmType = alarmType;
        setUser(user);
        this.message = alarmType.getMessage(relateId);
        this.redirectURI = alarmType.getRedirectURI(relateId);
        this.confirm = false;
    }

    public void setUser(User user) {
        this.user = user;
        user.addAlarm(this);
    }

    public void checkAlarm() {
        this.confirm = true;
    }
}
