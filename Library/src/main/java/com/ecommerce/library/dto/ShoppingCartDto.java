package com.ecommerce.library.dto;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Product;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShoppingCartDto {
    private int id;
    private Customer customer;
    private Product product;@Min(value = 1)
    @Min(value = 1)
    @Max(value = 10,message = "Only 10 piece allowed")
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private boolean deleted;
}
