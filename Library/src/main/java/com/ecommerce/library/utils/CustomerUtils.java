package com.ecommerce.library.utils;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class CustomerUtils {
    private final CustomerService customerService;

    public CustomerUtils(CustomerService customerService) {
        this.customerService = customerService;
    }

    public boolean isUserBlocked(Principal principal, HttpSession session) {
        if (principal != null) {
            Customer customer = customerService.findByEmail(principal.getName());
            if (customer.isBlocked()) {
                session.removeAttribute("username");
                return true;
            }
        }
        return false;
    }
}
