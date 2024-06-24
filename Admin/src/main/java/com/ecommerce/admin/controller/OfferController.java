package com.ecommerce.admin.controller;

import com.ecommerce.library.dto.OfferDto;
import com.ecommerce.library.model.Category;
import com.ecommerce.library.model.Offer;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.service.CategoryService;
import com.ecommerce.library.service.OfferService;
import com.ecommerce.library.service.ProductService;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class OfferController {
    private OfferService offerService;
    private CategoryService categoryService;
    private ProductService productService;
    public OfferController(OfferService offerService,ProductService productService,CategoryService categoryService) {
        this.offerService = offerService;
        this.productService=productService;
        this.categoryService=categoryService;
    }

    @GetMapping("/offer/{pageNo}")
    public String showOffer(@PathVariable("pageNo") int pageNo,Model model){
        Page<Offer> offers=offerService.findAllOffer(pageNo);
        model.addAttribute("offers",offers);
        model.addAttribute("size",offers.getSize());
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages",offers.getTotalPages());
        return "offer";
    }

    @GetMapping("/productOffer")
    public String addProductOffer(Model model){
        OfferDto offerDto=new OfferDto();
        model.addAttribute("offerDto",offerDto);
        List<Product> products=productService.findAll();
        model.addAttribute("products",products);
        return "addProductOffer";
    }

    @GetMapping("/categoryOffer")
    public String addCategoryOffer(Model model){
        OfferDto offerDto=new OfferDto();
        model.addAttribute("offerDto",offerDto);
        List<Category> categories=categoryService.findAllByActivatedTrue();
        model.addAttribute("categories",categories);
        return "addCategoryOffer";
    }

    @PostMapping("/saveOffer")
    public String saveOffer(@Valid @ModelAttribute("offerDto") OfferDto offerDto,BindingResult result,Model model){
        if (result.hasErrors()) {
            if ("Product Offer".equals(offerDto.getOfferType())) {
                model.addAttribute("products", productService.findAll());
                return "addProductOffer";
            } else {
                model.addAttribute("categories", categoryService.findAllByActivatedTrue());
                return "addCategoryOffer";
            }
        }
        offerService.saveOffer(offerDto);
        return "redirect:/offer/0";
    }

    @GetMapping("/updateOffer")
    public String editOffer(@RequestParam("offerId")Long id, Model model){
        Offer offer=offerService.findById(id);
        model.addAttribute("offer",offer);
        return "edit-offer";
    }
    @PostMapping("/updateOffer")
    public String updateOffer(@Valid @ModelAttribute("offer")OfferDto offerDto, BindingResult result){
        if (result.hasErrors()) {
            return "edit-offer"; // Ensure this matches the Thymeleaf template name
        }
        offerService.updateOffer(offerDto);
        return "redirect:/offer/0";
    }

    @GetMapping("/enable-offer")
    public String enableOffer(@RequestParam("offerId")Long id){
        offerService.enableOffer(id);
        return "redirect:/offer/0";
    }

    @GetMapping("/disable-offer")
    public String disableOffer(@RequestParam("offerId")Long id){
        offerService.disableOffer(id);
        return "redirect:/offer/0";
    }
}


