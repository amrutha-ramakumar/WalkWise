package com.ecommerce.library.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor

public class DailyReport {
    private Date date;

    private double earnings;

    private Long totelOrder;

    public DailyReport(Date date, double earnings, Long totelOrder) {
        this.date = date;
        this.earnings = earnings;
        this.totelOrder=totelOrder;

    }
}
