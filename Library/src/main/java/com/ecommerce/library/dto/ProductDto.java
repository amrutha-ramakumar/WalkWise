package com.ecommerce.library.dto;


import com.ecommerce.library.model.Category;
import com.ecommerce.library.model.Image;
import com.ecommerce.library.model.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    @NotBlank(message = "name Required")
    @jakarta.validation.constraints.Size(min = 3, message = "Invalid name")
    private String name;
    @NotBlank(message = "BrandName Required")
    @jakarta.validation.constraints.Size(min = 3, message = "Invalid name")
    private String brand;
    @NotBlank(message = "LongDescription Required")
    @jakarta.validation.constraints.Size(min = 3, message = "Invalid name")
    private String longDescription;
    @NotBlank(message = "ShortDescription Required")
    @jakarta.validation.constraints.Size(min = 3, message = "Invalid name")
    private String shortDescription;
    private int currentQuantity;
    @Min(value = 1, message = "Can't be null")
    private double costPrice;
    @Min(value = 1, message = "Can't be null")
    private double salePrice;
    private List<Image> image;
    private Category category;
    private boolean activated;
    private List<Long> sizes = new ArrayList<>();

    private Map<Long, Integer> sizeQuantities = new HashMap<>();

    // Getters and Setters

    public void setQuantities(List<Integer> quantities) {
        for (int i = 0; i < sizes.size(); i++) {
            sizeQuantities.put(sizes.get(i), quantities.get(i));
        }
    }

}

