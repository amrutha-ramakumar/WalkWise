package com.ecommerce.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyEarning {
    private Long orderId;
    private Long productId;
    private String productName;
    private String shortDescription;
    private Double unitPrice;
    private Integer quantity;
    private Double total;
    private Double deduction;
//    private Double shippingFee;
    private Double totalAmount;


}
