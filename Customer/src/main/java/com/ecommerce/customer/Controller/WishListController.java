package com.ecommerce.customer.Controller;

import com.ecommerce.library.model.ProductSizeQuantity;
import com.ecommerce.library.model.WishList;
import com.ecommerce.library.repository.ProductSizeQuantityRepository;
import com.ecommerce.library.service.WishListService;
import com.ecommerce.library.utils.CustomerUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class WishListController {
    private WishListService wishListService;
    private ProductSizeQuantityRepository productSizeQuantityRepository;
    private CustomerUtils customerUtils;
    public WishListController(WishListService wishListService,CustomerUtils customerUtils
                              ,ProductSizeQuantityRepository productSizeQuantityRepository) {
        this.wishListService = wishListService;
        this.productSizeQuantityRepository=productSizeQuantityRepository;
        this.customerUtils =customerUtils;
    }
    @GetMapping("/wishList")
    public String showwishList(Principal principal, Model model, HttpSession session){
        if (customerUtils.isUserBlocked(principal, session)) return "redirect:/logout";
        if(principal==null){
            return "redirect:/login";
        }
        String username=principal.getName();
        List<WishList> wishLists=wishListService.findWishlistByCustomer(username);
        List<ProductSizeQuantity> productSizeQuantities=productSizeQuantityRepository.findAll();
        model.addAttribute("wishList",wishLists);
        model.addAttribute("productSizes",productSizeQuantities);
        return "wish-list";
    }
    @GetMapping("/addToWishList")
    public String showAddtowishList(@RequestParam("productId")Long id, Principal principal){
        if(principal==null){
            return "redirect:/login";
        }
        String username=principal.getName();
        wishListService.addToWishList(username,id);
        return "redirect:/wishList";
    }
    @GetMapping("/removeFromWishList")
    public String showRemoveFromWishList(@RequestParam("wishListId")Long id){

        wishListService.DeletefromWishList(id);
        return "redirect:/wishList";
    }

}
