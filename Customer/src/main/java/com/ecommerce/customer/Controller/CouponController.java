package com.ecommerce.customer.Controller;

import com.ecommerce.library.dto.CouponDto;
import com.ecommerce.library.model.Address;
import com.ecommerce.library.model.Coupon;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.service.AddressService;
import com.ecommerce.library.service.CouponService;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.service.ShoppingCartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class CouponController {
    ShoppingCartService shopCartService;
    CouponService couponService;
    AddressService addressService;
    CustomerService customerService;

    public CouponController(ShoppingCartService shopCartService, CouponService couponService,
                            AddressService addressService, CustomerService customerService) {
        this.shopCartService = shopCartService;
        this.couponService = couponService;
        this.addressService = addressService;
        this.customerService = customerService;
    }
//    @PostMapping("/applyCoupon")
//    public String showApplyCoupon(@ModelAttribute("coupon") CouponDto couponDto, Principal principal,
//                                  Model model){
//
//        String username=principal.getName();
//        Customer customer=customerService.findByEmail(username);
//        List<ShoppingCart> shopingCarts=shopCartService.findShoppingCartByCustomer(username);
//        Coupon coupon=couponService.findByCouponCode(couponDto.getCouponcode());
//        if(coupon.isExpired()==true){
//            return "redirect:/checkOut?expired";
//        }
//        if(coupon==null){
//            return "redirect:/checkOut";
//        }
//
//        double grandTotal=shopCartService.grandTotal(username);
//        List<Address> addresses=addressService.findAddressByCustomer(username);
//        double payableAmount;
//        if(grandTotal<coupon.getMinimumOrderAmount())
//        {
//            model.addAttribute("errorMessage","order amount is less. ");
//            model.addAttribute("addresses",addresses);
//            model.addAttribute("cartItem",shopingCarts);
//            model.addAttribute("total",grandTotal);
//            model.addAttribute("customer",customer);
//            model.addAttribute("payable",grandTotal);
//            return "checkOut";
//        }
//        double offerPercentage= coupon.getOfferPercentage();
//        double offer= (grandTotal * offerPercentage) / 100;
//        if(offer<coupon.getMaximumOfferAmount()) {
//            payableAmount  = grandTotal - offer;
//        }
//        else{
//            payableAmount=grandTotal-100;
//        }
//        couponService.decreaseCoupon(coupon.getId());
//        model.addAttribute("addresses",addresses);
//        model.addAttribute("cartItem",shopingCarts);
//        model.addAttribute("total",grandTotal);
//        model.addAttribute("customer",customer);
//        model.addAttribute("payable",payableAmount);
//        System.out.println(payableAmount);
//        return "checkOut";
//    }
@PostMapping("/applyCoupon")
public String applyCoupon(@ModelAttribute("coupon") CouponDto couponDto, Principal principal,
                          Model model) {

    String username = principal.getName();
    Customer customer = customerService.findByEmail(username);
    List<ShoppingCart> shoppingCarts = shopCartService.findShoppingCartByCustomer(username);
    Coupon coupon = couponService.findByCouponCode(couponDto.getCouponcode());

    if (coupon == null) {
        return "redirect:/checkOut?invalid";
    }

    if (coupon.isExpired()) {
        return "redirect:/checkOut?expired";
    }

    double grandTotal = shopCartService.grandTotal(username);
    List<Address> addresses = addressService.findAddressByCustomer(username);
    double payableAmount=grandTotal;
    double deduction=0;
    Integer shippingFee=0;
    if (grandTotal < coupon.getMinimumOrderAmount()) {
        shippingFee=payableAmount<10000?50:0;
        Double payable=payableAmount+shippingFee;
        model.addAttribute("errorMessage", "Order amount is less than the minimum required.");
        model.addAttribute("addresses", addresses);
        model.addAttribute("cartItem", shoppingCarts);
        model.addAttribute("total", grandTotal);
        model.addAttribute("deduction",deduction);
        model.addAttribute("customer", customer);
        model.addAttribute("payable", payable);
        model.addAttribute("shippingFee",shippingFee);
        return "redirect:/checkOut";
    }

    double offerPercentage = coupon.getOfferPercentage();
    deduction = (grandTotal * offerPercentage) / 100;

    if (deduction < coupon.getMaximumOfferAmount()) {
        payableAmount = grandTotal - deduction;
    } else {
        payableAmount = grandTotal - coupon.getMaximumOfferAmount();
    }
    deduction = Math.round(deduction * 100.0) / 100.0;
    payableAmount = Math.round(payableAmount * 100.0) / 100.0;

    couponService.decreaseCoupon(coupon.getId());
    shippingFee=payableAmount<10000?50:0;
    Double payable=payableAmount+shippingFee;
    model.addAttribute("addresses", addresses);
    model.addAttribute("cartItem", shoppingCarts);
    model.addAttribute("total", grandTotal);
    model.addAttribute("customer", customer);
    model.addAttribute("payable", payable);
    model.addAttribute("deduction",deduction);
    model.addAttribute("couponCode", coupon.getCouponcode()); // Store the applied coupon code
    model.addAttribute("coupon",coupon);
    model.addAttribute("shippingFee",shippingFee);
    model.addAttribute("couponApplied", true); // Indicate that a coupon is applied
    model.addAttribute("couponRemoved",false);
    System.out.println(payableAmount);
    return "checkOut";
}

//    @PostMapping("/removeCoupon")
//    public String removeCoupon(Principal principal, Model model, RedirectAttributes redirectAttributes) {
//
//        String username = principal.getName();
//        Customer customer = customerService.findByEmail(username);
//        List<ShoppingCart> shoppingCarts = shopCartService.findShoppingCartByCustomer(username);
//
//        double grandTotal = shopCartService.grandTotal(username);
//        List<Address> addresses = addressService.findAddressByCustomer(username);
//
//        model.addAttribute("addresses", addresses);
//        model.addAttribute("cartItem", shoppingCarts);
//        model.addAttribute("total", grandTotal);
//        model.addAttribute("customer", customer);
//        model.addAttribute("payable", grandTotal);
//        model.addAttribute("couponApplied", false); // Indicate that no coupon is applied
////        redirectAttributes.addFlashAttribute("couponRemoved", true);
//
//        return "checkOut";
//    }

@PostMapping("/removeCoupon")
public String removeCoupon(@RequestParam("couponid")Long couponid,Principal principal, Model model) {
    String username = principal.getName();
    Customer customer = customerService.findByEmail(username);
    List<ShoppingCart> shoppingCarts = shopCartService.findShoppingCartByCustomer(username);
    couponService.increaseCoupon(couponid);
    Coupon coupon=new Coupon();
    model.addAttribute("coupon",coupon);
    double offer=0;
    for(ShoppingCart cart:shoppingCarts){
        int quantity=cart.getQuantity();
        int productQuantity=cart.getProduct().getCurrentQuantity();
        if(quantity>productQuantity){
            return "redirect:/cart?quantityError";
        }
    }

    double total=shopCartService.grandTotal(username);
    List<Address> addresses=addressService.findAddressByCustomer(username);
    Integer shippingFee=total<10000?50:0;
    Double payable=total+shippingFee;
    model.addAttribute("customer",customer);
    model.addAttribute("addresses",addresses);
    model.addAttribute("cartItem",shoppingCarts);
    model.addAttribute("total",total);
    model.addAttribute("deduction",offer);
    model.addAttribute("payable",payable);
    model.addAttribute("shippingFee",shippingFee);
    model.addAttribute("couponApplied",false);
    model.addAttribute("couponRemoved",true);
    return "checkOut"; // Ensure this is the correct name of your HTML template
}

}
