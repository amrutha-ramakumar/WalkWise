package com.ecommerce.customer.Controller;

import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.library.model.Category;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.UserOtp;
import com.ecommerce.library.repository.CustomerRepository;
import com.ecommerce.library.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AuthController {
    private CustomerService customerService;
    private CustomerRepository customerRepository;

@Autowired
    public AuthController(CustomerService customerService, CustomerRepository customerRepository) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
    }


    @GetMapping("/signup")
    public  String showSignUp(Model model){
        String email = (String) model.asMap().get("email");
        CustomerDto customerDto=new CustomerDto();
        customerDto.setEmail(email);
        model.addAttribute("customerDto",customerDto);
        return "register";
    }


    @PostMapping("/registration")
    public String saveRegister(@Valid @ModelAttribute("customerDto") CustomerDto customerDto,
                               BindingResult result, Model model, RedirectAttributes redirectAttributes) {


        try {
            if(result.hasErrors()){
                model.addAttribute("customerDto",customerDto);
                result.toString();
                return "register";
            }
            Customer customer=customerService.findByEmail(customerDto.getEmail());
            if(customer!=null){
                model.addAttribute("customerDto",customerDto);
                model.addAttribute("errors","Email has been registered");
                return "register";
            }
            if(customerDto.getPassword().equals(customerDto.getRepeatPassword())) {
                customerService.saveCustomer(customerDto);
                model.addAttribute("success", "Registered Successfully");
                return "redirect:/login?success";
            }
            else {
                model.addAttribute("errors", "Password is not same");
                model.addAttribute("customerDto", customerDto);
                return "register";
            }
        }
        catch (Exception e){
            e.printStackTrace();
            model.addAttribute("errors", "Server is error, try again later!");
        }
        redirectAttributes.addAttribute("email", customerDto.getEmail());
        return "redirect:/signup";

    }

    @GetMapping("/login")
    public String showLoginPage(HttpServletRequest request){
        HttpSession session= request.getSession();
        Object attribute=session.getAttribute("userLoginID");
        if(attribute!=null) {
            return "redirect:/home";
        }
        return "login";
    }


    @GetMapping("/verifyEmail")
    public String showVerifyEmail(){
        return "verifyEmail";
    }


    @GetMapping("/otpvalidation")
    public String showotpvalidationPage(Model model, HttpSession session){
        String email = (String) model.asMap().get("email");
        UserOtp userOTP = new UserOtp();
        userOTP.setEmail(email);
        session.setAttribute("email",email);
        model.addAttribute("userOTP",userOTP);
        return "otpvalidation";
    }

}
