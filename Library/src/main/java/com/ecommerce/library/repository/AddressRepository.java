package com.ecommerce.library.repository;

import com.ecommerce.library.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {
    @Query("select a from Address a where a.customer.email=?1")
    List<Address> findAddressByCustomer(String email);


    @Modifying
    @Transactional
    @Query("UPDATE Address a SET a.isdefault = (CASE WHEN a.id = ?1 THEN true ELSE false END) WHERE a.customer.email = ?2")
    void setDefaultAddressForCustomer(Long addressId, String email);


    @Query("SELECT a FROM Address a WHERE a.customer.customer_id = :customerId AND a.isdefault = true")
    Optional<Address> findDefaultBillingAddressByCustomerId(@Param("customerId") Long customerId);
}
