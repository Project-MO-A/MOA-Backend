package com.moa.domain.possible;

import com.moa.domain.recruit.Recruitment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PossibleTimeSelector {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TIME_SELECTOR_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECRUIMENT_ID")
    private Recruitment recruitment;

    @OneToMany(mappedBy = "selector", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PossibleTime> possibleTime = new ArrayList<>();

    private LocalDateTime selectStartDate;

    @Builder
    public PossibleTimeSelector(Recruitment recruitment, List<PossibleTime> possibleTime, LocalDateTime selectStartDate) {
        this.recruitment = recruitment;
        this.possibleTime = possibleTime;
        this.selectStartDate = selectStartDate;
    }
}
