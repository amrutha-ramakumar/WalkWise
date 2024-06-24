package com.ecommerce.library.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CategorySales {
    private Long categoryId;
    private String categoryName;
    private Long totalSold;

    public CategorySales(Long categoryId, String categoryName, Long totalSold) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.totalSold = totalSold;
    }
}

