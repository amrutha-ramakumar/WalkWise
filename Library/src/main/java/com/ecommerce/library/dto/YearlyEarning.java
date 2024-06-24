package com.ecommerce.library.dto;
import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class YearlyEarning {
    private String categoryName;
    private String productName;
    private Long orderedQuantity;
    private Double orderedTotalPrice;
}
