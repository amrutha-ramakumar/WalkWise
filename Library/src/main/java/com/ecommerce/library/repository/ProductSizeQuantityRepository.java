package com.ecommerce.library.repository;

import com.ecommerce.library.model.ProductSizeQuantity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductSizeQuantityRepository extends JpaRepository<ProductSizeQuantity,Long> {
    List<ProductSizeQuantity> findProductSizeQuantitiesByProductId(Long productId);
    ProductSizeQuantity findProductSizeQuantityByProductIdAndSizeId(Long productId,Long sizeId);
}
