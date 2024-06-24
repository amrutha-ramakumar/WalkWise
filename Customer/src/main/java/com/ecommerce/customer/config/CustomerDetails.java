package com.ecommerce.customer.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;


public class CustomerDetails extends User {

    private Long customer_id;
    private String name;
    private long mobile;
    private boolean is_activated;
    private boolean is_blocked;

    public CustomerDetails(String email, String password, Collection<? extends GrantedAuthority> authorities,
                           Long customer_id,String name, Long mobile,boolean is_activated, boolean is_blocked) {
        super(email, password, authorities);
        this.customer_id=customer_id;
        this.name=name;
        this.mobile = mobile;
        this.is_activated= is_activated;
        this.is_blocked = is_blocked;
    }
}
