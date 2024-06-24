package com.ecommerce.library.service;

import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.model.Size;

import java.util.List;

public interface ShoppingCartService {
//    ShoppingCart addItemToCart(Long productId, int quantity, Long id);

    ShoppingCart addItemToCart(Long productId, int quantity,Long sizeId, Long customerId);
     void updateCartItemSize(Long cartItemId, Size newSize, String username) ;

    List<ShoppingCart> findShoppingCartByCustomer(String email);

    Double grandTotal(String username);

    void deleteById(long id,Long customerId);

    void increment(Long id,Long productid);

    void decrement(Long id);

}

