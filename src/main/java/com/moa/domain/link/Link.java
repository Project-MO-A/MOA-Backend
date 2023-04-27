package com.moa.domain.link;

import com.moa.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "USER_LINK")
@Getter
@NoArgsConstructor
public class Link {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    public Link(String url, User user) {
        this.url = url;
        this.user = user;
    }

    public Link(String url) {
        this.url = url;
    }

    public void setParent(User user) {
        this.user = user;
    }
}
