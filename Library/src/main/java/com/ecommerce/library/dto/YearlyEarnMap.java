package com.ecommerce.library.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class YearlyEarnMap {
    private int year;
    private Double earning;

    public YearlyEarnMap(int year, Double earning) {
        this.year = year;
        this.earning = earning;
    }

    @Override
    public String toString() {
        return "YearlyEarnMap{" +
                "year=" + year +
                ", earning=" + earning +
                '}';
    }
}
