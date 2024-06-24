package com.ecommerce.library.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProductSales {
    private Long productId;
    private String productName;
    private Long totalSold;

    public ProductSales(Long productId, String productName, Long totalSold) {
        this.productId = productId;
        this.productName = productName;
        this.totalSold = totalSold;
    }
}
