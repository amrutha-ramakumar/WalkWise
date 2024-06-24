package com.ecommerce.customer.Controller;

import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.service.WalletService;
import com.ecommerce.library.utils.Utility;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import net.bytebuddy.utility.RandomString;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

@Controller
public class ReferalController {
    private CustomerService customerService;
    private JavaMailSender mailSender;
    private WalletService walletService;

    public ReferalController(CustomerService customerService,JavaMailSender mailSender,WalletService walletService) {
        this.customerService = customerService;
        this.mailSender=mailSender;
        this.walletService=walletService;
    }

    @PostMapping("/sendReferal")
    public String sendReferal(HttpServletRequest request, Model model, Principal principal) throws MessagingException, UnsupportedEncodingException {
        String email = request.getParameter("email");
        String token = RandomString.make(30);
        String username=principal.getName();

        customerService.updateReferalCodeToken(token,username);
        String referalLink = Utility.getSiteURL(request) + "/referal_link?token=" + token;
        sendEmail(email, referalLink);
        model.addAttribute("message", "We have sent a referal link to  email. Please check.");
        return "redirect:/account";
    }
    @GetMapping("/referal_link")
    public String showReferalLogin(@Param(value = "token") String token, Model model) {
        CustomerDto customerDto=new CustomerDto();

        model.addAttribute("token", token);

        model.addAttribute("customerDto",customerDto);

        return "referal-signUp";

    }

    @PostMapping("/registration1")
    public String showRegisterReferralUser(@Valid @ModelAttribute("customerDto")CustomerDto customerDto,
                                          BindingResult result,
                                          HttpServletRequest request,
                                          HttpSession session,
                                          Model model){
        if (result.hasErrors()) {
            model.addAttribute("customerDto", customerDto);
            return "referal-signUp";
        }
        String token = request.getParameter("token");
        Customer customer=customerService.getByReferalToken(token);
        Customer customer1=customerService.findByEmail(customerDto.getEmail());

        if(customer1!=null){
            model.addAttribute("customerDto",customerDto);
            model.addAttribute("errors","Email has been registered");
            return "redirect:/referal_link?exist&token="+token;
        }
        else if(customerDto.getPassword().equals(customerDto.getRepeatPassword())) {
            customerDto.setPassword(customerDto.getPassword());
            customerService.saveCustomer(customerDto);
            walletService.addWalletToReferalEarn(customer.getCustomer_id());
            session.removeAttribute("error");
            model.addAttribute("success", "Registered Successfully");
        } else{
//            session.removeAttribute("success");
            model.addAttribute("errors", "Password is not same");
            model.addAttribute("customerDto", customerDto);
            return "redirect:/referal_link?success&token="+token;
        }
        return "referal-signUp";
    }
//    @PostMapping("/registration1")
//    public String showRegisterReferalUser(@Valid @ModelAttribute("customerDto") CustomerDto customerDto,
//                                          BindingResult result, HttpServletRequest request, Model model) {
//        if (result.hasErrors()) {
//            model.addAttribute("customerDto", customerDto);
//            return "referal-signUp";
//        }
//
//        String token = request.getParameter("token");
//        Customer existingCustomer = customerService.findByEmail(customerDto.getEmail());
//        if (existingCustomer != null) {
//            model.addAttribute("errors", "Email has been registered");
//            return "redirect:/referal_link?exist&token=" + token;
//        }
//
//        if (!customerDto.getPassword().equals(customerDto.getRepeatPassword())) {
//            model.addAttribute("errors", "Passwords do not match");
//            return "referal-signUp";
//        }
//
//        try {
//            Customer referalCustomer = customerService.getByReferalToken(token);
//            customerService.saveCustomer(customerDto);
//            walletService.addWalletToReferalEarn(referalCustomer.getCustomer_id());
//
//            model.addAttribute("success", "Registered successfully");
//            return "redirect:/referal_link?success";
//        } catch (Exception e) {
//            e.printStackTrace();
//            model.addAttribute("errors", "Server error, please try again later");
//            return "referal-signUp";
//        }
//    }

    public void sendEmail(String recipientEmail, String link)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("contact@walkwise.com", "Walkwise");
        helper.setTo(recipientEmail);

        String subject = "Here's the link to login walkwise";

        String content = "<p>Hello,</p>"
                + "<p>You have requested to Login Walkwise.</p>"
                + "<p>Click the link below to login and signup page:</p>"
                + "<p><a href=\"" + link + "\">Walkwise</a></p>"
                + "<br>"
                + "<p>Ignore this email if you already have account, "
                + "or you have not made the request.</p>";

        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);
    }
}
