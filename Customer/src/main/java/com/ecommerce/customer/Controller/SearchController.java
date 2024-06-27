package com.ecommerce.customer.Controller;

import com.ecommerce.library.dto.CategoryDto;
import com.ecommerce.library.model.Category;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.service.CategoryService;
import com.ecommerce.library.service.ProductService;
import com.ecommerce.library.utils.CustomerUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {
    private ProductService productService;
    private CategoryService categoryService;

    private CustomerUtils customerUtils;
    public SearchController(ProductService productService, CategoryService categoryService,CustomerUtils customerUtils) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.customerUtils=customerUtils;
    }

//    @GetMapping("/search")
//    public String showSearch(@RequestParam("key")String key, Model model, Principal principal,HttpSession session){
//        if (customerUtils.isUserBlocked(principal, session)) return "redirect:/logout";
//        List<Product> products = productService.searchProduct(key);
//        model.addAttribute("products",products);
//        Category category=new Category();
//        model.addAttribute("categories1",category);
//        List<Category> categories=categoryService.findAllByActivatedTrue();
//        model.addAttribute("categories",categories);
//        model.addAttribute("size",products.size());
//        return "search_result";
//    }
    @GetMapping("/search/{pageNo}")
    public String searchProducts(@RequestParam("key") String key, @RequestParam("categoryName") String categoryName,HttpServletRequest request,
                                 @PathVariable("pageNo") int pageNo, Principal principal, Model model, HttpSession session) {
        if (customerUtils.isUserBlocked(principal, session)) return "redirect:/logout";
        Page<Product> products = productService.searchProductsByCategoryAndName(key, categoryName,pageNo);
        List<Category> categories = categoryService.findAllByActivatedTrue();
        if (products.getTotalElements()==0){
            model.addAttribute("categories", categories);
            return "not-found";
        }
        else {
            String baseUrl = request.getRequestURL().toString();
            String currentPageUrl = baseUrl + "?categoryName=" + categoryName;

            session.setAttribute("name", categoryName);
            session.setAttribute("currentPageUrl", baseUrl);

            model.addAttribute("products", products);
            model.addAttribute("size", products.getTotalElements());
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("totalPages", products.getTotalPages());
            model.addAttribute("categories", categories);

            CategoryDto category1 = new CategoryDto();
            model.addAttribute("categories1", category1);

            return "search_result";
        }
    }

//    @GetMapping("/category")
//    public String showCategoryFilterHome(@RequestParam("categoryName") String categoryName, Model model,
//                                         @RequestParam(defaultValue = "0") int page,
//                                         @RequestParam(defaultValue = "3") int size,
//                                         HttpSession session, HttpServletRequest request, Principal principal){
//        if (customerUtils.isUserBlocked(principal, session)) return "redirect:/logout";
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Product> products=productService.findAllByCategoryName(categoryName,pageable);
////        int size=products.size();
//        session.setAttribute("name", categoryName);
//        session.setAttribute("currentPageUrl", request.getRequestURL().toString());
//        model.addAttribute("products",products);
//        model.addAttribute("size", products.getTotalElements());
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", products.getTotalPages());
//
//        CategoryDto category1 = new CategoryDto();
//        model.addAttribute("categories1", category1);
//        List<Category> categories = categoryService.findAllByActivatedTrue();
//        model.addAttribute("categories", categories);
//        session.setAttribute("name",categoryName);
//        session.setAttribute("currentPageUrl", request.getRequestURL().toString());
//        model.addAttribute("products",products);
//        model.addAttribute("size",size);
//        return "search_result";
//    }
@GetMapping("/category")
public String showCategoryFilterHome(@RequestParam("categoryName") String categoryName,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "3") int size,
                                     Model model, HttpSession session, HttpServletRequest request, Principal principal) {
    if (customerUtils.isUserBlocked(principal, session)) return "redirect:/logout";

    Pageable pageable = PageRequest.of(page, size);
    Page<Product> products = productService.findAllByCategoryName(categoryName, pageable);
    CategoryDto category1 = new CategoryDto();
    model.addAttribute("categories1", category1);
    List<Category> categories = categoryService.findAllByActivatedTrue();
    if (products.getTotalElements()==0){
        model.addAttribute("categories", categories);
        model.addAttribute("categoryName",categoryName);
        return "not-found";
    }
    String baseUrl = request.getRequestURL().toString();
    String currentPageUrl = baseUrl + "?categoryName=" + categoryName;

    session.setAttribute("name", categoryName);
    session.setAttribute("currentPageUrl", baseUrl);

    model.addAttribute("products", products);
    model.addAttribute("size", products.getTotalElements());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", products.getTotalPages());
    model.addAttribute("categories", categories);

    model.addAttribute("categoryName",categoryName);


    return "search_result";
}

//    @GetMapping("/filterSearch")
//    public String filterSearch(@RequestParam("key") String key,
//                               Model model, HttpSession session,Principal principal) {
//        if (customerUtils.isUserBlocked(principal, session)) return "redirect:/logout";
//        Object lastPortion1 = session.getAttribute("currentPageUrl");
//        System.out.println(lastPortion1);
//        String lastPortion=lastPortion1.toString();
//        String[] parts = lastPortion.split("/");
//        String lastSegment = parts[parts.length - 1];
//        System.out.println("Last portion: " + lastSegment);
//        System.out.println("key: " + key);
//        List<Product> products=new ArrayList<>();
//        switch (lastSegment){
//            case "popular":
//                products=productService.findTopSellingProductsWithKeyword(key);
//                break;
//            case "random":
//                products=productService.randomProductWithKeyword(key);
//                break;
//            case "newArrival":
//                products=productService.filterByIdDescendingWithKeyword(key);
//                break;
//            case "searchCategoryHome": {
//                String categoryName=session.getAttribute("name").toString();
//                products = productService.findProductsByCategoryNameAndKeyword(categoryName, key);
//                break;
//            }
//            default:
//                products = productService.searchProduct(key);
//
//        }
//
//        model.addAttribute("products",products);
//        Category category=new Category();
//        model.addAttribute("categories1",category);
//        List<Category> categories=categoryService.findAllByActivatedTrue();
//        model.addAttribute("categories",categories);
//        model.addAttribute("size",products.size());
//        model.addAttribute("hidePagination", true);
//        return "search_result";
//    }
@GetMapping("/filterSearch")
public String filterSearch(@RequestParam("key") String key,
                           Model model, HttpSession session, Principal principal) {
    if (customerUtils.isUserBlocked(principal, session)) return "redirect:/logout";

    // Retrieve the currentPageUrl attribute from the session
    Object lastPortion1 = session.getAttribute("currentPageUrl");

    // Check if the attribute is null and provide a default value or handle the case appropriately
    String lastPortion;
    if (lastPortion1 == null) {
        // Handle the case where currentPageUrl is not set in the session
        // Use a default value or take appropriate action
        lastPortion = "default"; // Replace "default" with an appropriate default value
    } else {
        lastPortion = lastPortion1.toString();
    }

    // Split the lastPortion to get the last segment
    String[] parts = lastPortion.split("/");
    String lastSegment = parts[parts.length - 1];

    System.out.println("Last portion: " + lastSegment);
    System.out.println("key: " + key);

    // Retrieve products based on the last segment
    List<Product> products = new ArrayList<>();
    switch (lastSegment) {
        case "popular":
            products = productService.findTopSellingProductsWithKeyword(key);
            break;
        case "random":
            products = productService.randomProductWithKeyword(key);
            break;
        case "newArrival":
            products = productService.filterByIdDescendingWithKeyword(key);
            break;
        case "searchCategoryHome":
            String categoryName = session.getAttribute("name").toString();
            products = productService.findProductsByCategoryNameAndKeyword(categoryName, key);
            break;
        default:
            products = productService.searchProduct(key);
    }
    List<Category> categories = categoryService.findAllByActivatedTrue();
    if (products.size()==0){
        model.addAttribute("categories", categories);
        return "not-found";
    }
    // Add attributes to the model
    model.addAttribute("products", products);
    Category category = new Category();
    model.addAttribute("categories1", category);
    model.addAttribute("categories", categories);

    model.addAttribute("size", products.size());
    model.addAttribute("hidePagination", true);

    return "search_result";
}


}
