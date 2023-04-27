package com.moa.domain.interests;

import com.moa.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
public class Interests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INTERESTS_ID")
    private Long id;

    @Column(name = "INTERESTS_NAME")
    private String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    public Interests(String name) {
        this.name = name;
    }

    public void setParent(User user) {
        this.user = user;
    }
}
