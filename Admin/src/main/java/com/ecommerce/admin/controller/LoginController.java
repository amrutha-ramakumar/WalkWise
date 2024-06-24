package com.ecommerce.admin.controller;
// Import statements

import com.ecommerce.library.dto.AdminDto;
import com.ecommerce.library.model.Admin;
import com.ecommerce.library.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class LoginController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Method to handle login page request
    @RequestMapping("/login")
    public String login(Model model) {
        // Add title attribute to the model
        model.addAttribute("title", "Login");
        // Return the login view
        return "login";
    }

    // Method to handle home page request
//    @RequestMapping("/index")
//    public String home(Model model) {
//        // Add title attribute to the model
//        model.addAttribute("title", "Home Page");
//        // Check if the user is authenticated
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
//            // Redirect to login page if not authenticated
//            return "redirect:/login";
//        }
//        // Return the home page view
//        return "index";
//    }


    // Method to handle registration page request
    @GetMapping("/register")
    public String register(Model model){
        // Add title attribute to the model
        model.addAttribute("title","Register");
        // Add adminDto attribute to the model for form submission
        model.addAttribute("adminDto",new AdminDto()) ;
        // Return the registration view
        return "register";
    }

    // Method to handle forgot password page request
    @GetMapping("/forgot-password")
    public String forgotPassword(Model model){
        // Return the forgot password view
        return "forgot-password" ;
    }

    // Method to handle new admin registration
    @PostMapping("/register-new")
    public String addNewAdmin(@Valid @ModelAttribute("adminDto")AdminDto adminDto,
                              BindingResult result,
                              Model model
    ){
        try{
            // Check for validation errors
            if(result.hasErrors()){
                // If there are errors, return to the registration page with error messages
                model.addAttribute("adminDto",adminDto);
                result.toString();
                return "register";
            }
            // Check if the username is already registered
            String username=adminDto.getUsername();
            Admin admin=adminService.findByUsername(username);
            if(admin != null){
                model.addAttribute("adminDto",adminDto);
                System.out.println("admin is not null");
                model.addAttribute("emailError","Your email has been registered!");
                return "register";
            }
            // Check if passwords match
            if(adminDto.getPassword().equals(adminDto.getRepeatPassword())){
                // Encode the password and save the new admin
                adminDto.setPassword(passwordEncoder.encode(adminDto.getPassword())) ;
                adminService.save(adminDto);
                System.out.println("Succes");
                model.addAttribute("success","Registered Successfully!");
                model.addAttribute("adminDto",new AdminDto());
            }
            else {
                // If passwords don't match, return to the registration page with error message
                model.addAttribute("adminDto",adminDto);
                model.addAttribute("passwordError","Your password may be wrong, Check again!");
                System.out.println("Password is not same");
                return "register";
            }
        }
        catch(Exception e){
            // Handle exceptions
            e.printStackTrace();
            model.addAttribute("errors","The server has been wrong!");
        }
        // Return the registration page
        return "register" ;
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Model model) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.setInvalidateHttpSession(true);
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        model.addAttribute("logout","You have been logged out");
        return "/login";
    }
}
