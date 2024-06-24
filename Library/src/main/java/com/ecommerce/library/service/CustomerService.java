package com.ecommerce.library.service;

import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.library.model.Customer;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Customer findByEmail(String email);

    void saveCustomer(CustomerDto customerDto);

    List<Customer> findAll();

    void blockById(Long id);

    void enableById(Long id);

    List<Customer> findAllActivatedByTrue();

    Customer getById(Long id);

    CustomerDto findById(Long id);

    void updateCustomerDetails(CustomerDto customerDto);

    void changePassword(Customer customer);

    void updateResetPasswordToken(String token, String email);

    Customer getByResetPasswordToken(String token);

    void updateReferalCodeToken(String token, String email);

    Customer getByReferalToken(String token);

    void updatePassword(Customer customer, String newPassword);
}
