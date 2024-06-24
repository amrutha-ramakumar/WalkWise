package com.ecommerce.library.service;

import com.ecommerce.library.model.WishList;

import java.util.List;

public interface WishListService {
    WishList addToWishList(String email,Long productId);
    List<WishList> findWishlistByCustomer(String email);
    void DeletefromWishList(Long id);
}
