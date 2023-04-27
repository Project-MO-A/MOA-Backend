package com.moa.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Popularity {
    @Column(name = "POPULARITY_COUNT")
    private int count;
    @Column(name = "POPULARITY_TOTAL")
    private double totalRate;
    @Column(name = "POPULARITY_RATE")
    private double rate;

    public Popularity() {
        this.count = 0;
        this.totalRate = 0;
        this.rate = 0;
    }

    public void addPopularity(double rate) {
        count++;
        totalRate += rate;
        this.rate = totalRate / count;
    }

    public void updatePopularity(double beforeRate, double updateRate) {
        totalRate += updateRate - beforeRate;
        this.rate = totalRate / count;
    }
}
