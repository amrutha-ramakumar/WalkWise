package com.ecommerce.library.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MonthlyEarnMap {
    private String date;
    private Double earning;

    public MonthlyEarnMap(String date, Double earning) {
        this.date = date;
        this.earning = earning;
    }
    @Override
    public String toString() {
        return "MonthlyEarnMap{" +
                "date=" + date +
                ", earning=" + earning +
                '}';
    }
}
