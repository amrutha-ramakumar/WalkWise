package com.ecommerce.library.repository;

import com.ecommerce.library.model.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListRepository extends JpaRepository<WishList,Long> {
    @Query("select wl from WishList wl where wl.customer.email=?1 and wl.product.id=?2 and wl.deleted=false")
    WishList findProductByCustomer(String email,Long productId);

    @Query("select wl from WishList wl where wl.customer.email=?1 and wl.deleted=false")
    List<WishList> findWishlistByCustomer(String email);
}
