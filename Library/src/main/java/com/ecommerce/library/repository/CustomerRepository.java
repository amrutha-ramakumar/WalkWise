package com.ecommerce.library.repository;

import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.library.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Customer findByEmail(String email);
    @Query(value = "select * from cutomer where is_activated = true", nativeQuery = true)
    List<Customer> findAllByActivatedTrue();

    public Customer findByResetPasswordToken(String token);

    public Customer findByReferalToken(String token);


}
