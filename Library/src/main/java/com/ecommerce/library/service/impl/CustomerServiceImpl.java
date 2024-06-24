package com.ecommerce.library.service.impl;

import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.repository.CustomerRepository;
import com.ecommerce.library.repository.RoleRepository;
import com.ecommerce.library.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepository customerRepository;
    private RoleRepository roleRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository,RoleRepository roleRepository) {
        this.customerRepository = customerRepository;
        this.roleRepository=roleRepository;
    }

    @Override
    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public void saveCustomer( CustomerDto customerDto) {

        Customer customer=new Customer();

        customer.setName(customerDto.getName());
        customer.setEmail(customerDto.getEmail());
        customer.setPassword(customerDto.getPassword());
        customer.setMobile(customerDto.getMobile());
        customer.setRoles(Arrays.asList(roleRepository.findByName("CUSTOMER")));
        customer.setActivated(true);
        customer.setBlocked(false);


        customerRepository.save(customer);
    }




    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public void blockById(Long id) {
        Customer customer=customerRepository.getById(id);
        customer.setActivated(false);
        customer.setBlocked(true);
        customerRepository.save(customer);
    }

    @Override
    public void enableById(Long id) {
        Customer customer=customerRepository.getById(id);
        customer.setActivated(true);
        customer.setBlocked(false);
        customerRepository.save(customer);

    }

    @Override
    public List<Customer> findAllActivatedByTrue() {
        return customerRepository.findAllByActivatedTrue();
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }




    @Override
    public Customer getById(Long id) {
        return customerRepository.getById(id);
    }

//    @Override
//    public CustomerDto findById(Long id) {
//        Optional<Customer> customer=customerRepository.findById(id);
//        CustomerDto customerDto=new CustomerDto();
//        customerDto.setCustomer_id(customer.get().getCustomer_id());
//        customerDto.setName(customer.get().getName());
//        customerDto.setEmail(customer.get().getEmail());
//        customerDto.setMobile(customer.get().getMobile());
//        return customerDto;
//    }
    public CustomerDto findById(Long id) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            CustomerDto customerDto = new CustomerDto();
            customerDto.setCustomer_id(customer.getCustomer_id());
            customerDto.setName(customer.getName());
            customerDto.setEmail(customer.getEmail());
            customerDto.setMobile(customer.getMobile());
            return customerDto;
        } else {
            // Handle case where customer is not found
            throw new EntityNotFoundException("Customer not found");
        }
    }

    public void updateCustomerDetails(CustomerDto customerDto) {
        Optional<Customer> customerOpt = customerRepository.findById(customerDto.getCustomer_id());
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setName(customerDto.getName());
            customer.setEmail(customerDto.getEmail());
            customer.setMobile(customerDto.getMobile());
            customerRepository.save(customer);
        } else {
            // Handle case where customer is not found
            throw new EntityNotFoundException("Customer not found");
        }
    }





    @Override
    public void changePassword(Customer customer) {
        Customer customer1=customerRepository.findByEmail(customer.getEmail());
        customer1.setPassword(customer.getPassword());
        customerRepository.save(customer1);
    }
    @Override
    public void updateResetPasswordToken(String token, String email)  {
        Customer customer = customerRepository.findByEmail(email);
        if (customer != null) {
            customer.setResetPasswordToken(token);
            customerRepository.save(customer);
        }
    }

    public Customer getByResetPasswordToken(String token) {
        return customerRepository.findByResetPasswordToken(token);
    }

    @Override
    public void updateReferalCodeToken(String token, String email) {
        Customer customer=customerRepository.findByEmail(email);
        if(customer!=null){
            customer.setReferalToken(token);
            customerRepository.save(customer);
        }
    }

    @Override
    public Customer getByReferalToken(String token) {
        return customerRepository.findByReferalToken(token);
    }

    public void updatePassword(Customer customer, String newPassword) {
        //BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // String encodedPassword = passwordEncoder.encode(newPassword);
        customer.setPassword(newPassword);

        customer.setResetPasswordToken(null);
        customerRepository.save(customer);
    }
}
