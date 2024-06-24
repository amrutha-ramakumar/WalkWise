package com.ecommerce.customer.Controller;


import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.ProductSizeQuantity;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.model.Size;
import com.ecommerce.library.repository.ProductSizeQuantityRepository;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.service.ProductService;
import com.ecommerce.library.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class ShoppingCartController {
    ShoppingCartService shopCartService;
    ProductService productService;
    CustomerService customerService;
    ProductSizeQuantityRepository productSizeQuantityRepository;
//    CoupenService coupenService;

    @Autowired
    public ShoppingCartController(ShoppingCartService shopCartService, ProductService productService,
                                  CustomerService customerService,ProductSizeQuantityRepository productSizeQuantityRepository) {
        this.shopCartService = shopCartService;
        this.productService = productService;
        this.customerService = customerService;
        this.productSizeQuantityRepository =productSizeQuantityRepository;
//        this.coupenService=coupenService;
    }

    @GetMapping("/cart")
    public String showCart(Model model,Principal principal){
    if (principal==null){
        return "redirect:/login";
    }
        String username=principal.getName();
        List<ShoppingCart> shoppingCart=shopCartService.findShoppingCartByCustomer(username);
        List<ProductSizeQuantity> productSizeQuantities=productSizeQuantityRepository.findAll();
        double total=shopCartService.grandTotal(username);
        model.addAttribute("total",total);
        model.addAttribute("shoppingCart",shoppingCart);
        model.addAttribute("productSizeQuantities",productSizeQuantities);

    return "cart";
    }

    @GetMapping("/addToCart")
    public String showAddToCart(@RequestParam("productId")Long id,
                                @RequestParam("sizeId") Long sizeId,
                                @RequestParam("quantity") int quantity,
                                Principal principal)
    {
        if(principal==null){
            return "redirect:/login";
        }

//        int quantity=1;
        String username= principal.getName();
        Customer customer=customerService.findByEmail(username);
//        CustomerDetails customUser= (CustomerDetails) authentication.getPrincipal();
        shopCartService.addItemToCart(id,quantity,sizeId,customer.getCustomer_id());
        return "redirect:/cart";
    }

    @PostMapping("/addToCart")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam("sizeId") Long sizeId,
                            @RequestParam("quantity") int quantity,
                            RedirectAttributes redirectAttributes,Principal principal) {
        if(principal==null){
            return "redirect:/login";
        }
        // Handle the addition of the product to the cart
        String username= principal.getName();
        Customer customer=customerService.findByEmail(username);
//        CustomerDetails customUser= (CustomerDetails) authentication.getPrincipal();
        shopCartService.addItemToCart(productId,quantity,sizeId,customer.getCustomer_id());
        // Redirect or render appropriate view
        return "redirect:/cart";
    }
    @PostMapping("/cart/updateSize")
    public String updateCartSize(@RequestParam("cartItemId") Long cartItemId,
                                 @RequestParam("newSize") Size newSize,
                                 Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        shopCartService.updateCartItemSize(cartItemId, newSize, username);

        return "redirect:/cart";
    }

    @GetMapping("/deleteCartItem")
        public String showDelete(@RequestParam("cartId")Long id,Authentication authentication,Principal principal){
        String username=principal.getName();
        Customer customer=customerService.findByEmail(username);
        shopCartService.deleteById(id,customer.getCustomer_id());
        return "redirect:/cart";
    }

    @GetMapping("/incrementQuantity")
    public String showQuantityIncriment(@RequestParam("cartId")Long id,@RequestParam("productId") Long pId){
        shopCartService.increment(id,pId);
            return "redirect:/cart";
    }

    @GetMapping("/decremetQuantity")
    public String showQuntityDecriment(@RequestParam("cartId")Long id){
        shopCartService.decrement(id);
        return "redirect:/cart";
    }

}
