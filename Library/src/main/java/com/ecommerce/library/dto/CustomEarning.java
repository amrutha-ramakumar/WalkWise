package com.ecommerce.library.dto;
import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomEarning {
    private Date orderDate;
    private Long orderId;
    private Long productId;
    private String productName;
    private String productDescription;
    private Double unitPrice;
    private Integer quantity;
    private Double totalPrice;
    private Double deduction;
    private Double totalAmount;

}
