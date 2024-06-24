package com.ecommerce.customer.config;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Role;
import com.ecommerce.library.repository.CustomerRepository;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerDetailService implements UserDetailsService {

    private CustomerRepository customerRepository;


    public CustomerDetailService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(email);
        List<GrantedAuthority> authorities = new ArrayList<>();

            if(customer !=null) {
                if (customer.isActivated()) {
                    for (Role role : customer.getRoles()) {
                        authorities.add(new SimpleGrantedAuthority(role.getName()));
                    }
                }
                if(customer.isBlocked()){
                    throw new LockedException("User is Blocked");
                }
            }else{
                throw new UsernameNotFoundException("Invalid username or password.");
            }

        return new CustomerDetails(
                customer.getEmail(),
                customer.getPassword(),
                authorities,
                customer.getCustomer_id(),
                customer.getName(),
                customer.getMobile(),
                customer.isActivated(),
                customer.isBlocked());
    }
}
