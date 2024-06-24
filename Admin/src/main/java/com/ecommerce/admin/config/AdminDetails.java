package com.ecommerce.admin.config;
// Import statements
import com.ecommerce.library.model.Admin;
import com.ecommerce.library.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AdminDetails implements UserDetails {

    private Admin admin;

    // Method to retrieve authorities (roles) granted to the admin
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Create a list to store authorities
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // Iterate through roles assigned to the admin
        for(Role role: admin.getRoles()){
            // Add each role as a SimpleGrantedAuthority to the authorities list
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    // Method to retrieve the admin's password
    @Override
    public String getPassword() {
        return admin.getPassword();
    }

    // Method to retrieve the admin's username
    @Override
    public String getUsername() {
        return admin.getUsername();
    }

    // Method to indicate if the admin's account is not expired
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Method to indicate if the admin's account is not locked
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Method to indicate if the admin's credentials are not expired
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Method to indicate if the admin's account is enabled
    @Override
    public boolean isEnabled() {
        return true;
    }
}
