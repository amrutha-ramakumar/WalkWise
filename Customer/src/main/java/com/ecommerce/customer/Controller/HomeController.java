package com.ecommerce.customer.Controller;

import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.*;
import com.ecommerce.library.repository.ProductRepository;
import com.ecommerce.library.repository.ProductSizeQuantityRepository;
import com.ecommerce.library.repository.WalletHistoryRepository;
import com.ecommerce.library.repository.WalletRepository;
import com.ecommerce.library.service.*;
import com.ecommerce.library.utils.CustomerUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {
    private ProductService productService;
    private CategoryService categoryService;
    private ImageService imageService;
    private SizeService sizeService;
    private CustomerService customerService;
    private ProductRepository productRepository;
    private WalletRepository walletRepository;
    private WalletHistoryRepository walletHistoryRepository;
    private ProductSizeQuantityRepository productSizeQuantityRepository;
    private CustomerUtils customerUtils;
    private OrderService orderService;
    
    public HomeController(ProductService productService, CategoryService categoryService,WalletRepository walletRepository,
                          CustomerService customerService, ImageService imageService,ProductSizeQuantityRepository productSizeQuantityRepository,
                          SizeService sizeService,OrderService orderService,ProductRepository productRepository,
                          WalletHistoryRepository walletHistoryRepository, CustomerUtils customerUtils
    ) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.customerService=customerService;
        this.imageService=imageService;
        this.sizeService=sizeService;
        this.orderService = orderService;
        this.productRepository = productRepository;
        this.productSizeQuantityRepository=productSizeQuantityRepository;
        this.walletRepository=walletRepository;
        this.walletHistoryRepository=walletHistoryRepository;
        this.customerUtils=customerUtils;
    }

    @GetMapping({"/","index"})
    public String showMainPage(Model model, Principal principal, HttpSession session) {
        if (customerUtils.isUserBlocked(principal, session)) return "redirect:/logout";
        List<ProductDto> products=productService.findAllProducts();
//        List<Category> categories = categoryService.findAllByActivatedTrue();
        List<Product> productDtos = productService.findAll();
        List<String> brands=productRepository.findDistinctBrands();
//        model.addAttribute("categories", categories); // Corrected typo: "caregories" -> "categories"
        model.addAttribute("products", productDtos);
        model.addAttribute("product",products);
//        model.addAttribute("categories",categories);
        model.addAttribute("brands",brands);
        model.addAttribute("tittle","main");

        return "index";
    }




    @GetMapping("/home")
    public String showhome(Model model, Principal principal, HttpSession session) {
        try {
            if (customerUtils.isUserBlocked(principal, session)) return "redirect:/logout";
            if (principal != null) {
                Customer customer = customerService.findByEmail(principal.getName());
                if (customer.isBlocked()) {
                    session.removeAttribute("username");
                    return "redirect:/logout"; // Redirect to logout endpoint
                }

                session.setAttribute("username", principal.getName());
                model.addAttribute("tittle", "shop");
//                List<String> brands=productRepository.findDistinctBrands();
                List<Category> categories = categoryService.findAll();
                List<Product> productDtos = productService.findAll();
                model.addAttribute("categories", categories); // Corrected typo: "caregories" -> "categories"
                model.addAttribute("products", productDtos);
//                model.addAttribute("brands",brands);
                return "home";
            } else {
                session.removeAttribute("username");
                return "login";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "login";
    }
    @GetMapping("/shops/{pageNo}")
    public String showshop(@PathVariable("pageNo") int pageNo, Model model,Principal principal,HttpSession session){
        model.addAttribute("tittle","shop");
        if (customerUtils.isUserBlocked(principal, session)) return "redirect:/logout";
        if(principal!=null) {
            List<Category> categories = categoryService.findAll();
            Page<ProductDto> productDtos = productService.getProductsShop(pageNo);
            model.addAttribute("caregories", categories);
            model.addAttribute("products", productDtos);
            model.addAttribute("size", productDtos.getSize());
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("totalPages", productDtos.getTotalPages());

            return "shop";
        }
        else {
            session.removeAttribute("username");
            return "login";
        }
    }


    @GetMapping("/product-details/{id}")
    public String showProductDetails(@PathVariable("id") Long id, Model model,Principal principal,HttpSession session) {
        if (customerUtils.isUserBlocked(principal, session)) return "redirect:/logout";
        if(principal!=null) {
            Product product = productService.getProductById(id);
            List<ProductSizeQuantity> productSizeQuantities = productSizeQuantityRepository.findProductSizeQuantitiesByProductId(id);
            model.addAttribute("product", product);
            List<Image> images = imageService.findProductImages(id);
            System.out.println(product);
            Long categoryId = product.getCategory().getId();
            model.addAttribute("product", product);
            model.addAttribute("category", categoryId);
            model.addAttribute("images", images);
            model.addAttribute(productSizeQuantities);
            List<Product> productDtos = productService.findAll();
            model.addAttribute("products", productDtos);
            return "product-details";
        }
        else {
            session.removeAttribute("username");
            return "login";
        }
    }


    @GetMapping("/account")
    public String showAccount(Model model,Principal principal,HttpSession session){
        if (customerUtils.isUserBlocked(principal, session)) return "redirect:/logout";
        if(principal!=null) {
            String username = principal.getName();
            Customer customer = customerService.findByEmail(username);
            Wallet wallet = walletRepository.findByCustomer(customer.getCustomer_id());
            List<WalletHistory> walletHistories = walletHistoryRepository.findAllByCustomer(customer.getCustomer_id());
            model.addAttribute("customer", customer);
            model.addAttribute("name", customer.getName());
            model.addAttribute("address", customer.getAddress());
            model.addAttribute("wallet", wallet);
            model.addAttribute("walletHistories", walletHistories);
            return "account";
        }
        else {
            session.removeAttribute("username");
            return "login";
        }
    }
//    private boolean isUserBlocked(Principal principal, HttpSession session) {
//        if (principal != null) {
//            Customer customer = customerService.findByEmail(principal.getName());
//            if (customer.isBlocked()) {
//                session.removeAttribute("username");
//                return true;
//            }
//        }
//        return false;
//    }


}











//package com.ecommerce.customer.Controller;
//
//import com.ecommerce.library.dto.ProductDto;
//import com.ecommerce.library.model.*;
//import com.ecommerce.library.repository.ProductRepository;
//import com.ecommerce.library.repository.ProductSizeQuantityRepository;
//import com.ecommerce.library.repository.WalletHistoryRepository;
//import com.ecommerce.library.repository.WalletRepository;
//import com.ecommerce.library.service.*;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.data.domain.Page;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import java.security.Principal;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Controller
//public class HomeController {
//    private ProductService productService;
//    private CategoryService categoryService;
//    private ImageService imageService;
//    private SizeService sizeService;
//    private CustomerService customerService;
//    private ProductRepository productRepository;
//    private WalletRepository walletRepository;
//    private WalletHistoryRepository walletHistoryRepository;
//    private ProductSizeQuantityRepository productSizeQuantityRepository;
//    OrderService orderService;
//    public HomeController(ProductService productService, CategoryService categoryService,WalletRepository walletRepository,
//                          CustomerService customerService, ImageService imageService,ProductSizeQuantityRepository productSizeQuantityRepository,
//                          SizeService sizeService,OrderService orderService,ProductRepository productRepository,
//                          WalletHistoryRepository walletHistoryRepository
//                         ) {
//        this.productService = productService;
//        this.categoryService = categoryService;
//        this.customerService=customerService;
//        this.imageService=imageService;
//        this.sizeService=sizeService;
//        this.orderService = orderService;
//        this.productRepository = productRepository;
//        this.productSizeQuantityRepository=productSizeQuantityRepository;
//        this.walletRepository=walletRepository;
//        this.walletHistoryRepository=walletHistoryRepository;
//    }
//
//    @GetMapping({"/","index"})
//    public String showMainPage(Model model) {
//
//        List<ProductDto> products=productService.findAllProducts();
////        List<Category> categories = categoryService.findAllByActivatedTrue();
//        List<Product> productDtos = productService.findAll();
//        List<String> brands=productRepository.findDistinctBrands();
////        model.addAttribute("categories", categories); // Corrected typo: "caregories" -> "categories"
//        model.addAttribute("products", productDtos);
//        model.addAttribute("product",products);
////        model.addAttribute("categories",categories);
//        model.addAttribute("brands",brands);
//        model.addAttribute("tittle","main");
//
//        return "index";
//    }
//
//
//
//
//    @GetMapping("/home")
//    public String showhome(Model model, Principal principal, HttpSession session) {
//        try {
//            if (principal != null) {
//                Customer customer = customerService.findByEmail(principal.getName());
//                if (customer.isBlocked()) {
//                    session.removeAttribute("username");
//                    return "redirect:/logout"; // Redirect to logout endpoint
//                }
//
//                session.setAttribute("username", principal.getName());
//                model.addAttribute("tittle", "shop");
////                List<String> brands=productRepository.findDistinctBrands();
//                List<Category> categories = categoryService.findAll();
//                List<Product> productDtos = productService.findAll();
//                model.addAttribute("categories", categories); // Corrected typo: "caregories" -> "categories"
//                model.addAttribute("products", productDtos);
////                model.addAttribute("brands",brands);
//                return "home";
//            } else {
//                session.removeAttribute("username");
//                return "login";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "login";
//    }
//    @GetMapping("/shops/{pageNo}")
//    public String showshop(@PathVariable("pageNo") int pageNo, Model model,Principal principal,HttpSession session){
//        model.addAttribute("tittle","shop");
//        if(principal!=null) {
//            handleBlockedCustomer(principal.getName(), session);
//            List<Category> categories = categoryService.findAll();
//            Page<ProductDto> productDtos = productService.getProductsShop(pageNo);
//            model.addAttribute("caregories", categories);
//            model.addAttribute("products", productDtos);
//            model.addAttribute("size", productDtos.getSize());
//            model.addAttribute("currentPage", pageNo);
//            model.addAttribute("totalPages", productDtos.getTotalPages());
//
//            return "shop";
//        }
//        else {
//            session.removeAttribute("username");
//            return "login";
//        }
//    }
//
//
//    @GetMapping("/product-details/{id}")
//        public String showProductDetails(@PathVariable("id") Long id, Model model,Principal principal,HttpSession session) {
//        if(principal!=null) {
//            handleBlockedCustomer(principal.getName(),session);
//            Product product = productService.getProductById(id);
//            List<ProductSizeQuantity> productSizeQuantities = productSizeQuantityRepository.findProductSizeQuantitiesByProductId(id);
//            model.addAttribute("product", product);
//            List<Image> images = imageService.findProductImages(id);
//            System.out.println(product);
//            Long categoryId = product.getCategory().getId();
//            model.addAttribute("product", product);
//            model.addAttribute("category", categoryId);
//            model.addAttribute("images", images);
//            model.addAttribute(productSizeQuantities);
//            List<Product> productDtos = productService.findAll();
//            model.addAttribute("products", productDtos);
//            return "product-details";
//        }
//        else {
//            session.removeAttribute("username");
//            return "login";
//        }
//    }
//
//
//    @GetMapping("/account")
//    public String showAccount(Model model,Principal principal,HttpSession session){
//        if(principal!=null) {
//            handleBlockedCustomer(principal.getName(),session);
//            String username = principal.getName();
//            Customer customer = customerService.findByEmail(username);
//            Wallet wallet = walletRepository.findByCustomer(customer.getCustomer_id());
//            List<WalletHistory> walletHistories = walletHistoryRepository.findAllByCustomer(customer.getCustomer_id());
//            model.addAttribute("customer", customer);
//            model.addAttribute("name", customer.getName());
//            model.addAttribute("address", customer.getAddress());
//            model.addAttribute("wallet", wallet);
//            model.addAttribute("walletHistories", walletHistories);
//            return "account";
//        }
//        else {
//            session.removeAttribute("username");
//            return "login";
//        }
//    }
//    public String handleBlockedCustomer(String email, HttpSession session) {
//        Customer customer = customerService.findByEmail(email);
//        if (customer.isBlocked()) {
//            session.removeAttribute("username");
//            return "redirect:/logout";
//        }
//        return "continue"; // Or any other string indicating the user is not blocked
//    }
//
//
//}
//
//
//
//
//
//
//
