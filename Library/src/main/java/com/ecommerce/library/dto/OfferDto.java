package com.ecommerce.library.dto;

import com.ecommerce.library.model.Category;
import com.ecommerce.library.model.Product;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OfferDto {
    private Long id;

    @NotBlank(message = "OfferName is Required")
    private String offerName;

    @NotBlank(message = "Description is Required")
    private String description;

    @Min(value = 1,message = "Minimum 1 percentage is needed")
    @Max(value = 80,message = "Maximum 80 percentage is needed")
    private int offerPercentage;

    private String offerType;

    private Category category;

    private boolean active;

    private Product product;
}
