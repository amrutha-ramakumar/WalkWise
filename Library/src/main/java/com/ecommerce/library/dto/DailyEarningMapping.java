package com.ecommerce.library.dto;

import lombok.Data;

@Data
public class DailyEarningMapping {
    private String date;
    private Double earnings;

    public DailyEarningMapping(String date, Double earnings) {
        this.date = date;
        this.earnings = earnings;
    }
}
