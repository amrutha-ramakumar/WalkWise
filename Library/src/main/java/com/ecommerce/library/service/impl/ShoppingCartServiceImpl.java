package com.ecommerce.library.service.impl;
import com.ecommerce.library.model.*;
import com.ecommerce.library.repository.ProductSizeQuantityRepository;
import com.ecommerce.library.service.SizeService;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.library.repository.CustomerRepository;
import com.ecommerce.library.repository.ShoppingCartRepository;
import com.ecommerce.library.service.ProductService;
import com.ecommerce.library.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private ShoppingCartRepository shoppingCartRepository;
    private CustomerRepository customerRepository;
    private SizeService sizeService;
    ProductSizeQuantityRepository productSizeQuantityRepository;
    private  ProductService productService;
    public ShoppingCartServiceImpl(ShoppingCartRepository shoppingCartRepository,CustomerRepository customerRepository,
                                   SizeService sizeService,ProductService productService,ProductSizeQuantityRepository productSizeQuantityRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.customerRepository=customerRepository;
        this.productService=productService;
        this.sizeService=sizeService;
        this.productSizeQuantityRepository=productSizeQuantityRepository;
    }

//    @Override
//    public ShoppingCart addItemToCart(Long productId, int quantity, Long customerId) {
//        ShoppingCart shoppingCart= shoppingCartRepository.findByUsersProduct(customerId,productId);
//        Product product=productService.findBYId(productId);
//
//        if(shoppingCart != null){
//            int oldQuantity=shoppingCart.getQuantity();
//            shoppingCart.setQuantity(oldQuantity+quantity);
//            shoppingCart.setUnitPrice(product.getSalePrice());
//            Double totalPrice=product.getSalePrice() *  (oldQuantity+quantity);
//            shoppingCart.setTotalPrice(totalPrice);
//            shoppingCart.setDeleted(false);
//        }
//        else {
//            shoppingCart=new ShoppingCart();
//            shoppingCart.setProduct(product);
//            shoppingCart.setCustomer(customerRepository.getById(customerId));
//            shoppingCart.setQuantity(quantity);
//            shoppingCart.setUnitPrice(product.getSalePrice());
//            double totelPrice = shoppingCart.getUnitPrice() * shoppingCart.getQuantity();
//            shoppingCart.setTotalPrice(totelPrice);
//            shoppingCart.setDeleted(false);
//        }
//        return shoppingCartRepository.save(shoppingCart);
//    }

    @Override
    public ShoppingCart addItemToCart(Long productId, int quantity,Long sizeId,  Long customerId) {
        ShoppingCart shoppingCart= shoppingCartRepository.findByUsersProduct(customerId,productId);
        Product product=productService.findBYId(productId);
        Size size=sizeService.findById(sizeId);
        if(shoppingCart != null){
            int oldQuantity=shoppingCart.getQuantity();
            shoppingCart.setQuantity(oldQuantity+quantity);
            shoppingCart.setUnitPrice(product.getSalePrice());
            shoppingCart.setSize(size);
            Double totalPrice=product.getSalePrice() *  (oldQuantity+quantity);
            shoppingCart.setTotalPrice(totalPrice);
            shoppingCart.setDeleted(false);
        }
        else {
            shoppingCart=new ShoppingCart();
            shoppingCart.setProduct(product);
            shoppingCart.setCustomer(customerRepository.getById(customerId));
            shoppingCart.setQuantity(quantity);
            shoppingCart.setSize(size);
            shoppingCart.setUnitPrice(product.getSalePrice());
            double totelPrice = shoppingCart.getUnitPrice() * shoppingCart.getQuantity();
            shoppingCart.setTotalPrice(totelPrice);
            shoppingCart.setDeleted(false);
        }
        return shoppingCartRepository.save(shoppingCart);
    }
    @Override
    public List<ShoppingCart> findShoppingCartByCustomer(String email) {
        return shoppingCartRepository.findShoppingCartByCustomer(email);
    }

    @Override
    public Double grandTotal(String username) {
        Customer customer=customerRepository.findByEmail(username);
        if (customer !=null){
            Long customerId= customer.getCustomer_id();
            return shoppingCartRepository.findGrandTotal(customerId);
        }
        return 0.0;
    }

    @Override
    public void deleteById(long id,Long customerId) {
        ShoppingCart shoppingCart=shoppingCartRepository.getReferenceById(id);

        shoppingCart.setDeleted(true);
        shoppingCartRepository.save(shoppingCart);

    }

    @Override
    public void increment(Long id,Long productid) {
        ShoppingCart shoppingCart1=shoppingCartRepository.getReferenceById(id);
//        Product product=productService.getProductById(productid);
        ProductSizeQuantity product=productSizeQuantityRepository.findProductSizeQuantityByProductIdAndSizeId(productid,shoppingCart1.getSize().getId());
        if(product.getQuantity()>shoppingCart1.getQuantity()) {
            shoppingCart1.setQuantity(shoppingCart1.getQuantity() + 1);
            shoppingCart1.setTotalPrice(shoppingCart1.getQuantity() * shoppingCart1.getUnitPrice());
            shoppingCartRepository.save(shoppingCart1);
        }
    }

    @Override
    public void decrement(Long id) {
        ShoppingCart shopingCart1=shoppingCartRepository.getReferenceById(id);
        if(shopingCart1.getQuantity()>1) {
            shopingCart1.setQuantity(shopingCart1.getQuantity() - 1);
            shopingCart1.setTotalPrice(shopingCart1.getQuantity() * shopingCart1.getUnitPrice());
            shoppingCartRepository.save(shopingCart1);
        }
    }
    public void updateCartItemSize(Long cartItemId, Size newSize, String username) {
        ShoppingCart cartItem = shoppingCartRepository.findByIdAndCustomerUsername(cartItemId, username);
        if (cartItem != null) {
            cartItem.setSize(newSize);
            shoppingCartRepository.save(cartItem);
        }
    }

}
