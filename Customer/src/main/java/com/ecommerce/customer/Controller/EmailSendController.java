package com.ecommerce.customer.Controller;

import com.ecommerce.library.model.UserOtp;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.service.EmailService;
import com.ecommerce.library.service.OtpService;
import com.ecommerce.library.service.UserOtpService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Controller
public class EmailSendController {
    private OtpService otpService;
    private EmailService emailService;
    private UserOtpService userOTPService;

    private CustomerService usersSevice;
    private PasswordEncoder passwordEncoder;

    public EmailSendController(OtpService otpService,
                               EmailService emailService,
                               UserOtpService userOTPService,
                               CustomerService usersSevice,
                               PasswordEncoder passwordEncoder) {
        this.otpService = otpService;
        this.emailService = emailService;
        this.userOTPService = userOTPService;
        this.usersSevice = usersSevice;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/sendVerificationEmailOtp")
    public String sendVerificationEmailOtp(@RequestParam("email")String email,HttpSession session,
            RedirectAttributes redirectAttributes) throws Exception {
        if(usersSevice.findByEmail(email)==null){
            String otp = otpService.generateOTP();
            if(!userOTPService.existsByEmail(email)){
                // new email verification
                UserOtp userOTP =new UserOtp();
                userOTP.setEmail(email);
                userOTP.setOneTimePassword(passwordEncoder.encode(otp));
                userOTP.setCreatedAt(new Date());
                userOTP.setOtpRequestedTime(new Date());
                userOTP.setUpdateOn(new Date());
                try{
                    userOTPService.saveOrUpdate(userOTP);
                }catch(Exception e){
                    e.printStackTrace();
                    throw new Exception("Couldn't finish OTP verification process"+ HttpStatus.BAD_REQUEST);
                }

            }else{
                //code to delete all data related to this email id
                UserOtp userOTP=userOTPService.findByEmail(email);
                userOTP.setOneTimePassword(passwordEncoder.encode(otp));
                userOTP.setOtpRequestedTime(new Date());
                userOTP.setUpdateOn(new Date());
                try{
                    userOTPService.saveOrUpdate(userOTP);
                }catch(Exception e){
                    e.printStackTrace();
                    throw new Exception("Couldn't finish OTP verification process");
                }
            }
            String status = emailService.sendSimpleMail(email,otp);
            if(status.equals("success")){
                session.setAttribute("message","otpsent");
                redirectAttributes.addFlashAttribute("email",email);
                return "redirect:/otpvalidation";

            }else{
                return "redirect:/verifyEmail?error";
            }
        }else{
            return "redirect:/verifyEmail?existUser";
        }

    }


    @PostMapping("/validateOTP")
    public String validateOTP(@ModelAttribute("userOTP") UserOtp userOTPRequest, HttpSession session,
                              RedirectAttributes redirectAttributes, Model model) {
        String email = session.getAttribute("email").toString();
        UserOtp userOTP = userOTPService.findByEmail(userOTPRequest.getEmail());

        if (userOTP != null && passwordEncoder.matches(userOTPRequest.getOneTimePassword(), userOTP.getOneTimePassword())) {
            LocalDateTime otpRequestedTime = userOTP.getOtpRequestedTime().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            LocalDateTime currentTime = LocalDateTime.now();
            long minutesElapsed = ChronoUnit.MINUTES.between(otpRequestedTime, currentTime);

            if (minutesElapsed <= 1) { // OTP is valid
                redirectAttributes.addFlashAttribute("email", userOTP.getEmail());
                return "redirect:/signup";
            } else { // OTP has expired
                redirectAttributes.addFlashAttribute("email", userOTP.getEmail());
                redirectAttributes.addFlashAttribute("expire","Your OTP has been expired");
                return "redirect:/otpvalidation?error=expired";
            }
        } else {
            redirectAttributes.addFlashAttribute("email", userOTP.getEmail());
            redirectAttributes.addFlashAttribute("invalid","Invalid OTP");
            return "redirect:/otpvalidation?error=invalid";
        }
    }


    @PostMapping("/regenerateOTP")
    public String regenerateOTP(@RequestParam("email") String email,HttpSession session,
                                RedirectAttributes redirectAttributes) throws Exception {
        UserOtp userOTP = userOTPService.findByEmail(email);
        if (userOTP != null) {
            String newOTP = otpService.generateOTP();
            userOTP.setOneTimePassword(passwordEncoder.encode(newOTP));
            userOTP.setOtpRequestedTime(new Date());
            userOTP.setUpdateOn(new Date());
            try {
                userOTPService.saveOrUpdate(userOTP);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Couldn't regenerate OTP. Please try again later.");
            }

            String status = emailService.sendSimpleMail(email, newOTP);
            if (status.equals("success")) {
                session.setAttribute("message", "otpresent");
                redirectAttributes.addFlashAttribute("email", email);
                return "redirect:/otpvalidation";
            } else {
                // Handle email sending failure
                redirectAttributes.addFlashAttribute("email", email);
                return "redirect:/otpvalidation?error";
            }
        } else {
            // Handle user not found or no existing OTP record
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/otpvalidation?error=userNotFound";
        }
    }


}
