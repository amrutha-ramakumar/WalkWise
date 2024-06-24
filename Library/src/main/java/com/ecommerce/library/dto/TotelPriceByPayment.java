package com.ecommerce.library.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data


@NoArgsConstructor
public class TotelPriceByPayment {
    private String payMethod;
    private Double amount;

    public TotelPriceByPayment(String payMethod, Double amount) {
        this.payMethod = payMethod;
        this.amount = amount;
    }
}
