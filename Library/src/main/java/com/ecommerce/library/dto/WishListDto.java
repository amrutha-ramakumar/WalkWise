package com.ecommerce.library.dto;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Product;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class WishListDto {
    private Long id;
    private Customer customer;
    private Product product;
    private Long size;
    private boolean deleted;
}
