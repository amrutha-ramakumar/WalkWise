package com.ecommerce.customer.Controller;

import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Category;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.service.CategoryService;
import com.ecommerce.library.service.ProductService;
import org.springframework.boot.autoconfigure.batch.BatchTransactionManager;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FilterController {

    ProductService productService;
    CategoryService categoryService;

    public FilterController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/high-price-low/{pageNo}")
    public String filterHighPrice(@PathVariable("pageNo") int pageNo,Model model){
        Page<ProductDto> products=productService.getAllProductsHighToLow(pageNo);
        List<Category> categories=categoryService.findAllByActivatedTrue();
        model.addAttribute("products",products);
        model.addAttribute("caregories",categories);
        model.addAttribute("size", products.getSize());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("currentFilter", "high-price-low");
        model.addAttribute("totalPages", products.getTotalPages());
        return "filter-page";
    }
    @GetMapping("/low-price-high/{pageNo}")
    public String filterLowerPrice(@PathVariable("pageNo") int pageNo,Model model){
        Page<ProductDto> products=productService.getAllProductsLowtoHigh(pageNo);
        List<Category> categories=categoryService.findAllByActivatedTrue();
        model.addAttribute("products",products);
        model.addAttribute("caregories",categories);
        model.addAttribute("size", products.getSize());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("currentFilter", "low-price-high");
        model.addAttribute("totalPages", products.getTotalPages());
        return "filter-page";
    }

    @GetMapping("/new-arrivals/{pageNo}")
    public String newArrivals(@PathVariable("pageNo") int pageNo,Model model){
        Page<ProductDto> products=productService.getAllProductsLatest(pageNo);
        List<Category> categories=categoryService.findAllByActivatedTrue();
        model.addAttribute("products",products);
        model.addAttribute("caregories",categories);
        model.addAttribute("size", products.getSize());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("currentFilter", "new-arrivals");
        model.addAttribute("totalPages", products.getTotalPages());
        return "filter-page";
    }
    @GetMapping("/new-AtoZ/{pageNo}")
    public String newAtoZ(@PathVariable("pageNo") int pageNo,Model model){
        Page<ProductDto> products=productService.getAllProductsAToZ(pageNo);
        List<Category> categories=categoryService.findAllByActivatedTrue();
        model.addAttribute("products",products);
        model.addAttribute("caregories",categories);
        model.addAttribute("size", products.getSize());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("currentFilter", "/new-AtoZ/");
        model.addAttribute("totalPages", products.getTotalPages());
        return "filter-page";
    }
    @GetMapping("/new-ZtoA/{pageNo}")
    public String newZtoA(@PathVariable("pageNo") int pageNo,Model model){
        Page<ProductDto> products=productService.getAllProductsZToA(pageNo);
        List<Category> categories=categoryService.findAllByActivatedTrue();
        model.addAttribute("products",products);
        model.addAttribute("caregories",categories);
        model.addAttribute("size", products.getSize());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("currentFilter", "new-ZtoA");
        model.addAttribute("totalPages", products.getTotalPages());
        return "filter-page";
    }
//    @GetMapping("/popularity")
//    public  String popularity(Model model){
//        List<Product> products=productService.filterPopularity();
//        List<Category> categories=categoryService.findAllByActivatedTrue();
//        model.addAttribute("products",products);
//        model.addAttribute("caregories",categories);
//        return "filter-page";
//    }


}
