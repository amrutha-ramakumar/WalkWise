package com.ecommerce.customer.Controller;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.utils.Utility;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

@Controller
public class ResetPasswordController {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/resetPassword")
    public String showForgotPasswordForm() {
        return "change-password-form";
    }

    @PostMapping("/resetPassword")
    public String processForgotPassword(HttpServletRequest request, Model model) throws MessagingException, UnsupportedEncodingException {
        String email = request.getParameter("email");
        String token = RandomString.make(30);

            customerService.updateResetPasswordToken(token, email);
            String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token;
            sendEmail(email, resetPasswordLink);
            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");

        return "redirect:/account";

    }

    @GetMapping("/reset_password")
        public String showResetPasswordForm(@Param(value = "token") String token, Model model,
                                            Principal principal) {
            Customer customer = customerService.getByResetPasswordToken(token);
            model.addAttribute("token", token);

            if (customer == null) {
                model.addAttribute("message", "Invalid Token");
                return "message";
            }
            String email=principal.getName();
            Customer customer1=customerService.findByEmail(email);
            model.addAttribute("customer",customer1);

            return "reset-password-form";

    }

    @PostMapping("/reset_password")
    public String processResetPassword(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        Customer customer = customerService.getByResetPasswordToken(token);
        model.addAttribute("title", "Reset your password");

        if (customer == null) {
            model.addAttribute("message", "Invalid Token");
            return "message";
        } else {
            customerService.updatePassword(customer, password);

            model.addAttribute("message", "You have successfully changed your password.");
        }

        return "redirect:/login";
    }

    public void sendEmail(String recipientEmail, String link)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

//        helper.setFrom("contact@shopme.com", "WalkWise");
        helper.setFrom("amrutharamakumar@gmail.com", "WalkWise");
        helper.setTo(recipientEmail);

        String subject = "Here's the link to reset your password";

        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";

        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);
    }

}
