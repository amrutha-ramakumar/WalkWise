package com.ecommerce.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyEarnings {
    private Date orderDate;
    private Long orderId;
    private Long productId;
    private String productName;
    private String shortDescription;
    private Double unitPrice;
    private Integer quantity;
    private Double totalPrice;
    private Double deduction;
//    private Double shippingFee;
    private Double totalAmount;
}
