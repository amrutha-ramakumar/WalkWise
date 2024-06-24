package com.ecommerce.admin.controller;

import com.ecommerce.library.dto.CategoryDto;
import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Category;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Image;
import com.ecommerce.library.model.Size;
import com.ecommerce.library.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class CustomerController {
    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomerService customerService;

    public CustomerController(BCryptPasswordEncoder passwordEncoder, CustomerService customerService) {
        this.passwordEncoder = passwordEncoder;
        this.customerService = customerService;
    }

    @GetMapping("/customers")
    public String customerList(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        List<Customer> customers = customerService.findAll();
        model.addAttribute("customers", customers);
        model.addAttribute("size", customers.size());
        return "customers";
    }

    @GetMapping("/disable-customer/{id}")
    public String disable(@PathVariable("id")long id,RedirectAttributes redirectAttributes,Principal principal){
        try{
            if (principal == null) {
                return "redirect:/login";
            }
            customerService.blockById(id);
            redirectAttributes.addFlashAttribute("success","Customer Disabled");
        }
        catch (Exception e){
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error","Disable Failed");
        }
        return "redirect:/customers";
    }

    @GetMapping("/enable-customer/{id}")
    public String enable(@PathVariable("id")long id, RedirectAttributes redirectAttributes,Principal principal){
        try {
            if (principal == null) {
                return "redirect:/login";
            }
            customerService.enableById(id);
            redirectAttributes.addFlashAttribute("success", "Customer Enabled");
        }
        catch (Exception e){
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error","Enable Failed");
        }
        return "redirect:/customers";
    }


}



