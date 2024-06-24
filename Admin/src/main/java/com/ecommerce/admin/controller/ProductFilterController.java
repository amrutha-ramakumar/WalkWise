package com.ecommerce.admin.controller;

import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
public class ProductFilterController {
    ProductService productService;

    public ProductFilterController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/filterLowToHigh/{pageNo}")
    public String filterLowTohigh(@PathVariable("pageNo") int pageNo, Model model, Principal principal) {
        // Check authentication
        if (principal == null) {
            return "redirect:/login";
        }

        // Retrieve paginated product list
        Page<ProductDto> products = productService.getAllProductsLowtoHigh(pageNo);

        // Add attributes to the model
        model.addAttribute("title", "Manage Products");
        model.addAttribute("size", products.getSize());
        model.addAttribute("products", products);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", products.getTotalPages());

        return "ProductFilter";
    }

    @GetMapping("/filterHighToLow/{pageNo}")
    public String filterHighToLow(@PathVariable("pageNo") int pageNo, Model model, Principal principal) {
        // Check authentication
        if (principal == null) {
            return "redirect:/login";
        }
        // Retrieve paginated product list
        Page<ProductDto> products = productService.getAllProductsHighToLow(pageNo);

        // Add attributes to the model
        model.addAttribute("title", "Manage Products");
        model.addAttribute("size", products.getSize());
        model.addAttribute("products", products);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", products.getTotalPages());

        return "ProductFilter";
    }

    @GetMapping("/filterAToZ/{pageNo}")
    public String filterAToZ(@PathVariable("pageNo") int pageNo, Model model, Principal principal) {
        // Check authentication
        if (principal == null) {
            return "redirect:/login";
        }
        // Retrieve paginated product list
        Page<ProductDto> products = productService.getAllProductsAToZ(pageNo);

        // Add attributes to the model
        model.addAttribute("title", "Manage Products");
        model.addAttribute("size", products.getSize());
        model.addAttribute("products", products);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", products.getTotalPages());

        return "ProductFilter";
    }

    @GetMapping("/filterZToA/{pageNo}")
    public String filterZToA(@PathVariable("pageNo") int pageNo, Model model, Principal principal) {
        // Check authentication
        if (principal == null) {
            return "redirect:/login";
        }
        // Retrieve paginated product list
        Page<ProductDto> products = productService.getAllProductsZToA(pageNo);

        // Add attributes to the model
        model.addAttribute("title", "Manage Products");
        model.addAttribute("size", products.getSize());
        model.addAttribute("products", products);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", products.getTotalPages());

        return "ProductFilter";
    }

    @GetMapping("/filterLatest/{pageNo}")
    public String filterLatest(@PathVariable("pageNo") int pageNo, Model model, Principal principal) {
        // Check authentication
        if (principal == null) {
            return "redirect:/login";
        }
        // Retrieve paginated product list
        Page<ProductDto> products = productService.getAllProductsLatest(pageNo);

        // Add attributes to the model
        model.addAttribute("title", "Manage Products");
        model.addAttribute("size", products.getSize());
        model.addAttribute("products", products);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", products.getTotalPages());

        return "ProductFilter";
    }
}
