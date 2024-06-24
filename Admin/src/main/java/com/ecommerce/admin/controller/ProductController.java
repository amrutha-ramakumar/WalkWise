package com.ecommerce.admin.controller;


import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.*;
import com.ecommerce.library.service.CategoryService;
import com.ecommerce.library.service.ImageService;
import com.ecommerce.library.service.ProductService;
import com.ecommerce.library.service.SizeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@Controller
public class ProductController {

    // Autowired services
    private ProductService productService;
    private CategoryService categoryService;
    private ImageService imageService;
    private SizeService sizeService;

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService,
                             SizeService sizeService, ImageService imageService) {

        // Initialize services
        this.sizeService=sizeService;
        this.imageService=imageService;
        this.productService = productService;
        this.categoryService = categoryService;
    }



    // Method to display paginated product list
    @GetMapping("/products/{pageNo}")
    public String allProducts(@PathVariable("pageNo") int pageNo, Model model, Principal principal) {
        // Check authentication
        if (principal == null) {
            return "redirect:/login";
        }

        // Retrieve paginated product list
        Page<ProductDto> products = productService.getAllProducts(pageNo);

        // Add attributes to the model
        model.addAttribute("title", "Manage Products");
        model.addAttribute("size", products.getSize());
        model.addAttribute("products", products);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", products.getTotalPages());

        return "products";
    }


    // Method to search for products and display results
    @GetMapping("/search-products/{pageNo}")
    public String searchProduct(@PathVariable("pageNo") int pageNo,
                                @RequestParam(value = "keyword") String keyword,
                                Model model, Principal principal) {
        // Check authentication
        if (principal == null) {
            return "redirect:/login";
        }

        // Search for products based on keyword and paginate the results
        Page<ProductDto> products = productService.searchProducts(pageNo, keyword);

        // Add attributes to the model
        model.addAttribute("title", "Result Search Products");
        model.addAttribute("size", products.getSize());
        model.addAttribute("products", products);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", products.getTotalPages());

        return "result-products";
    }


    // Method to display the add product page
    @GetMapping("/add-product")
    public String addProductPage(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        model.addAttribute("productDto", new ProductDto());
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        model.addAttribute("sizes", sizeService.allSizeVariations());

        return "add-product";
    }


    @PostMapping("/save-product")
    public String saveProduct(@Valid @ModelAttribute("productDto") ProductDto productDto,
                              BindingResult bindingResult,
                              @RequestParam("imageProduct") MultipartFile[] imageGallery,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllByActivatedTrue());
            model.addAttribute("sizes", sizeService.allSizeVariations());
            return "add-product";
        }

        try {
            productService.save(productDto, imageGallery);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryService.findAllByActivatedTrue());
            model.addAttribute("sizes", sizeService.allSizeVariations());
            return "add-product";
        }

        return "redirect:/products/0?success";
    }

//    @PostMapping("/save-product")
//    public String saveProduct(@Valid @ModelAttribute("productDto") ProductDto productDto,
//                              BindingResult bindingResult,
//                              @RequestParam("imageProduct") MultipartFile[] imageFiles,
//                              Model model) {
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("categories", categoryService.findAllByActivatedTrue());
//            model.addAttribute("sizes", sizeService.allSizeVariations());
//            return "add-product";
//        }
//
//        try {
//            productService.save(productDto, imageFiles);
//        } catch (Exception e) {
//            model.addAttribute("error", e.getMessage());
//            model.addAttribute("categories", categoryService.findAllByActivatedTrue());
//            model.addAttribute("sizes", sizeService.allSizeVariations());
//            return "add-product";
//        }
//
//        return "redirect:/products/0?success";
//    }
    @GetMapping("/update-product/{id}")
    public String updateProductForm(@PathVariable("id") long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        List<Category> categories = categoryService.findAllByActivatedTrue();
        List<Size> sizes = sizeService.allSizeVariations();
        ProductDto productDto = productService.findById(id);
//        Product product=productService.findBYId(id);
        List<Image> images = imageService.findProductImages(id);

//        Map<Long, Integer> sizeQuantities = new HashMap<>();
//        for (ProductSizeQuantity psq : product.getProductSizesQuantity()) {
//            sizeQuantities.put(psq.getSize().getId(), psq.getQuantity());
//        }
//        productDto.setSizeQuantities(sizeQuantities);

        model.addAttribute("title", "Update Product");
        model.addAttribute("categories", categories);
        model.addAttribute("images", images);
        model.addAttribute("sizes", sizes);
        model.addAttribute("productDto", productDto);
        model.addAttribute("imagesize", images.size() - 1);
        return "update-product";
    }

    @PostMapping("/update-product/{id}")
    public String updateProduct(@ModelAttribute("productDto") ProductDto productDto,
                                @RequestParam("imageProduct") List<MultipartFile> imageProduct,
                                RedirectAttributes redirectAttributes, Principal principal) {
        try {
            if (principal == null) {
                return "redirect:/login";
            }

            productService.update(imageProduct, productDto);
            redirectAttributes.addFlashAttribute("success", "Update successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error server, please try again!");
        }
        return "redirect:/products/0";
    }



//    // Method to display the update product form
//    @GetMapping("/update-product/{id}")
//    public String updateProductForm(@PathVariable("id") long id, Model model) {
//        // Check authentication
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
//            return "redirect:/login";
//        }
//
//        // Retrieve categories, sizes, product details, and images
//        List<Category> categories = categoryService.findAllByActivatedTrue();
//        List<Size> sizes = sizeService.allSizeVariations();
//        ProductDto productDto = productService.findById(id);
//        List<Image> images =imageService.findProductImages(id);
//
//        // Add attributes to the model
//        model.addAttribute("title", "Update Product");
//        model.addAttribute("categories", categories);
//        model.addAttribute("images", images);
//        model.addAttribute("sizes",sizes);
//        model.addAttribute("productDto", productDto);
//        model.addAttribute("imagesize",images.size()-1);
//        return "update-product";
//    }
//
//
//    // Method to handle updating a product
//    @PostMapping("/update-product/{id}")
//    public String updateProduct(@ModelAttribute("productDto") ProductDto productDto,
//                                @RequestParam("imageProduct") List<MultipartFile> imageProduct,
//                                @RequestParam("sizes") List<Long> sizes_id,
//                                RedirectAttributes redirectAttributes,Principal principal) {
//        try {
//            // Check authentication
//            if (principal == null) {
//                return "redirect:/login";
//            }
//
//            // Update the product
//            productService.update(imageProduct, productDto,sizes_id);
//            redirectAttributes.addFlashAttribute("success", "Update successfully!");
//        } catch (Exception e) {
//            e.printStackTrace();
//            redirectAttributes.addFlashAttribute("error", "Error server, please try again!");
//        }
//        return "redirect:/products/0";
//    }



    @GetMapping("/delete-image/{imageName}/{id}")
    public String delete(@PathVariable("imageName")String imageName,@PathVariable("id")Long id,RedirectAttributes redirectAttributes){
        // Delete the product
        imageService.deleteImage(imageName,id);

        redirectAttributes.addFlashAttribute("success","Product deleted");

        return "redirect:/update-product/{id}";
    }


    // Method to disable a product
    @GetMapping("/disable-product/{id}")
    public String disable(@PathVariable("id")long id,RedirectAttributes redirectAttributes,Principal principal){
        try{
            // Check authentication
            if (principal == null) {
                return "redirect:/login";
            }

            // Disable the product
            productService.disable(id);
            redirectAttributes.addFlashAttribute("success","Product Disabled");
        }
        catch (Exception e){
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error","Disable Failed");
        }
        return "redirect:/products/0";
    }

    // Method to enable a product
    @GetMapping("/enable-product/{id}")
    public String enable(@PathVariable("id")long id, RedirectAttributes redirectAttributes,Principal principal){
        try {
            // Check authentication
            if (principal == null) {
                return "redirect:/login";
            }

            // Enable the product
            productService.enable(id);
            redirectAttributes.addFlashAttribute("success", "Product Enabled");
        }
        catch (Exception e){
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error","Enable Failed");
        }
        return "redirect:/products/0";
    }

    // Method to delete a product
    @GetMapping("/delete-product/{id}")
    public String delete(@PathVariable("id")long id,RedirectAttributes redirectAttributes){
        // Delete the product
        productService.deleteProduct(id);

        redirectAttributes.addFlashAttribute("success","Product deleted");

        return "redirect:/products/0";
    }



}




























//package com.ecommerce.admin.controller;
//
//// Import statements
//import com.ecommerce.library.dto.CategoryDto;
//import com.ecommerce.library.dto.ProductDto;
//import com.ecommerce.library.model.Category;
//import com.ecommerce.library.model.Image;
//import com.ecommerce.library.model.Product;
//import com.ecommerce.library.model.Size;
//import com.ecommerce.library.service.CategoryService;
//import com.ecommerce.library.service.ImageService;
//import com.ecommerce.library.service.ProductService;
//import com.ecommerce.library.service.SizeService;
//import jakarta.servlet.http.HttpSession;
//import jakarta.transaction.Transactional;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.data.domain.Page;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.core.parameters.P;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.awt.*;
//import java.security.Principal;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//
//@Controller
//public class ProductController {
//
//    // Autowired services
//    private ProductService productService;
//    private CategoryService categoryService;
//    private ImageService imageService;
//    private SizeService sizeService;
//
//    @Autowired
//    public ProductController(ProductService productService, CategoryService categoryService,
//                             SizeService sizeService, ImageService imageService) {
//
//        // Initialize services
//        this.sizeService=sizeService;
//        this.imageService=imageService;
//        this.productService = productService;
//        this.categoryService = categoryService;
//    }
//
//
//
//    // Method to display paginated product list
//    @GetMapping("/products/{pageNo}")
//    public String allProducts(@PathVariable("pageNo") int pageNo, Model model, Principal principal) {
//        // Check authentication
//        if (principal == null) {
//            return "redirect:/login";
//        }
//
//        // Retrieve paginated product list
//        Page<ProductDto> products = productService.getAllProducts(pageNo);
//
//        // Add attributes to the model
//        model.addAttribute("title", "Manage Products");
//        model.addAttribute("size", products.getSize());
//        model.addAttribute("products", products);
//        model.addAttribute("currentPage", pageNo);
//        model.addAttribute("totalPages", products.getTotalPages());
//
//        return "products";
//    }
//
//
//    // Method to search for products and display results
//    @GetMapping("/search-products/{pageNo}")
//    public String searchProduct(@PathVariable("pageNo") int pageNo,
//                                @RequestParam(value = "keyword") String keyword,
//                                Model model, Principal principal) {
//        // Check authentication
//        if (principal == null) {
//            return "redirect:/login";
//        }
//
//        // Search for products based on keyword and paginate the results
//        Page<ProductDto> products = productService.searchProducts(pageNo, keyword);
//
//        // Add attributes to the model
//        model.addAttribute("title", "Result Search Products");
//        model.addAttribute("size", products.getSize());
//        model.addAttribute("products", products);
//        model.addAttribute("currentPage", pageNo);
//        model.addAttribute("totalPages", products.getTotalPages());
//
//        return "result-products";
//    }
//
//
//    // Method to display the add product page
//    @GetMapping("/add-product")
//    public String addProductPage(Model model, Authentication authentication) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return "redirect:/login";
//        }
//
//        model.addAttribute("productDto", new ProductDto());
//        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
//        model.addAttribute("sizes", sizeService.allSizeVariations());
//
//        return "add-product";
//    }
//
//
//    @PostMapping("/save-product")
//    public String saveProduct(@Valid @ModelAttribute("productDto") ProductDto productDto,
//                              BindingResult bindingResult,
//                              @RequestParam("imageProduct") MultipartFile[] imageFiles,
//                              Model model) {
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("categories", categoryService.findAllByActivatedTrue());
//            model.addAttribute("sizes", sizeService.allSizeVariations());
//            return "add-product";
//        }
//
//        try {
//            productService.save(productDto, imageFiles);
//        } catch (Exception e) {
//            model.addAttribute("error", e.getMessage());
//            model.addAttribute("categories", categoryService.findAllByActivatedTrue());
//            model.addAttribute("sizes", sizeService.allSizeVariations());
//            return "add-product";
//        }
//
//        return "redirect:/products/0?success";
//    }
//
//
//
//
//    // Method to display the update product form
//    @GetMapping("/update-product/{id}")
//    public String updateProductForm(@PathVariable("id") long id, Model model) {
//        // Check authentication
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
//            return "redirect:/login";
//        }
//
//        // Retrieve categories, sizes, product details, and images
//        List<Category> categories = categoryService.findAllByActivatedTrue();
//        List<Size> sizes = sizeService.allSizeVariations();
//        ProductDto productDto = productService.findById(id);
//        List<Image> images =imageService.findProductImages(id);
//
//        // Add attributes to the model
//        model.addAttribute("title", "Update Product");
//        model.addAttribute("categories", categories);
//        model.addAttribute("images", images);
//        model.addAttribute("sizes",sizes);
//        model.addAttribute("productDto", productDto);
//        model.addAttribute("imagesize",images.size()-1);
//        return "update-product";
//    }
//
//
//    // Method to handle updating a product
//    @PostMapping("/update-product/{id}")
//    public String updateProduct(@ModelAttribute("productDto") ProductDto productDto,
//                                @RequestParam("imageProduct") List<MultipartFile> imageProduct,
//                                @RequestParam("sizes") List<Long> sizes_id,
//                                RedirectAttributes redirectAttributes,Principal principal) {
//        try {
//            // Check authentication
//            if (principal == null) {
//                return "redirect:/login";
//            }
//
//            // Update the product
//            productService.update(imageProduct, productDto,sizes_id);
//            redirectAttributes.addFlashAttribute("success", "Update successfully!");
//        } catch (Exception e) {
//            e.printStackTrace();
//            redirectAttributes.addFlashAttribute("error", "Error server, please try again!");
//        }
//        return "redirect:/products/0";
//    }
//
//
//
//    @GetMapping("/delete-image/{imageName}/{id}")
//    public String delete(@PathVariable("imageName")String imageName,@PathVariable("id")Long id,RedirectAttributes redirectAttributes){
//        // Delete the product
//        imageService.deleteImage(imageName,id);
//
//        redirectAttributes.addFlashAttribute("success","Product deleted");
//
//        return "redirect:/update-product/{id}";
//    }
//
//
//    // Method to disable a product
//    @GetMapping("/disable-product/{id}")
//    public String disable(@PathVariable("id")long id,RedirectAttributes redirectAttributes,Principal principal){
//        try{
//            // Check authentication
//            if (principal == null) {
//                return "redirect:/login";
//            }
//
//            // Disable the product
//            productService.disable(id);
//            redirectAttributes.addFlashAttribute("success","Product Disabled");
//        }
//        catch (Exception e){
//            e.printStackTrace();
//            redirectAttributes.addFlashAttribute("error","Disable Failed");
//        }
//        return "redirect:/products/0";
//    }
//
//    // Method to enable a product
//    @GetMapping("/enable-product/{id}")
//    public String enable(@PathVariable("id")long id, RedirectAttributes redirectAttributes,Principal principal){
//        try {
//            // Check authentication
//            if (principal == null) {
//                return "redirect:/login";
//            }
//
//            // Enable the product
//            productService.enable(id);
//            redirectAttributes.addFlashAttribute("success", "Product Enabled");
//        }
//        catch (Exception e){
//            e.printStackTrace();
//            redirectAttributes.addFlashAttribute("error","Enable Failed");
//        }
//        return "redirect:/products/0";
//    }
//
//    // Method to delete a product
//    @GetMapping("/delete-product/{id}")
//    public String delete(@PathVariable("id")long id,RedirectAttributes redirectAttributes){
//        // Delete the product
//        productService.deleteProduct(id);
//
//        redirectAttributes.addFlashAttribute("success","Product deleted");
//
//        return "redirect:/products/0";
//    }
//
//
//
//}
