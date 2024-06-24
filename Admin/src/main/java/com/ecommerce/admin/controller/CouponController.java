package com.ecommerce.admin.controller;

import com.ecommerce.library.dto.CouponDto;
import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.library.model.Coupon;
import com.ecommerce.library.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CouponController {

    private CouponService couponService;
    @Autowired
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/Coupon")
    public String showCoupon(Model model){

        List<Coupon> coupons=couponService.findAll();
        model.addAttribute("size",coupons.size());
        model.addAttribute("coupons",coupons);
        return "coupon";
    }
    @GetMapping("/addCoupon")
    public String ShowAddCoupon(Model model){
        CouponDto couponDto=new CouponDto();
        model.addAttribute("couponDto",new CouponDto());
        return "add-coupon";
    }
    @PostMapping("/saveCoupon")
    public String saveCoupon(@Valid @ModelAttribute("couponDto") CouponDto couponDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-coupon";
        }
        couponService.save(couponDto);
        return "redirect:/Coupon";
    }

//    @PostMapping("/saveCoupon")
//    public String showSaveCoupon(@ModelAttribute("coupon")CouponDto couponDto){
//        couponService.save(couponDto);
//
//        return "redirect:/Coupon";
//    }

    @GetMapping("/disableCoupon")
    public String showDisableCoupon(@RequestParam("couponId")Long id){

        couponService.disableCoupon(id);
        return "redirect:/Coupon";
    }

    @GetMapping("/enableCoupon")
    public String showEnableCoupon(@RequestParam("couponId")Long id){

        couponService.enableCoupon(id);
        return "redirect:/Coupon";
    }
    @GetMapping("/editCoupon")
    public String showCouponUpdate(@RequestParam("couponId")Long id,Model model){
        Coupon coupon=couponService.findByid(id);
        model.addAttribute("coupon",coupon);
        return "edit-coupon";
    }
    @PostMapping("/updateCoupon")
    public String updateCoupon(@Valid @ModelAttribute("coupon")CouponDto couponDto, BindingResult result,Model model){
        if (result.hasErrors()) {
            return "edit-coupon";
        }
        couponService.updateCoupon(couponDto);
        return "redirect:/Coupon";
    }
}
