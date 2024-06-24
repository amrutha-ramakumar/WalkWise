package com.ecommerce.library.service.impl;

import com.ecommerce.library.model.Product;
import com.ecommerce.library.model.WishList;
import com.ecommerce.library.repository.CustomerRepository;
import com.ecommerce.library.repository.WishListRepository;
import com.ecommerce.library.service.ProductService;
import com.ecommerce.library.service.WishListService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishListServiceImpl implements WishListService {

    private WishListRepository wishListRepository;
    private ProductService productService;
    private CustomerRepository customerRepository;


    public WishListServiceImpl(WishListRepository wishListRepository, ProductService productService,
                               CustomerRepository customerRepository) {
        this.wishListRepository = wishListRepository;
        this.productService = productService;
        this.customerRepository=customerRepository;
    }

    @Override
    public WishList addToWishList(String email, Long productId) {
        WishList wishList=wishListRepository.findProductByCustomer(email,productId);
        Product product=productService.getProductById(productId);
        if (wishList==null) {


            wishList = new WishList();
            wishList.setProduct(product);
            wishList.setCustomer(customerRepository.findByEmail(email));
            wishList.setSize(1L);
            wishList.setDeleted(false);
        }
        return wishListRepository.save(wishList);
    }

    @Override
    public List<WishList> findWishlistByCustomer(String email) {

        return wishListRepository.findWishlistByCustomer(email);
    }

    @Override
    public void DeletefromWishList(Long id) {
        WishList wishList=wishListRepository.getReferenceById(id);
        wishList.setDeleted(true);
        wishListRepository.save(wishList);

    }
}
