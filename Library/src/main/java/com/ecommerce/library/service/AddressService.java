package com.ecommerce.library.service;

import com.ecommerce.library.dto.AddressDto;
import com.ecommerce.library.model.Address;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressService {
    Address save(AddressDto addressDto, String email);

    Optional<Address> findByid(Long id);

    Address update(AddressDto addressDto);

    Address findByIdOrder(Long id);

    List<Address> findAddressByCustomer(String email);

    void deleteAddress(Long id);

    void updateDefaultAddress( Long addressId,String email);

    Optional<Address> findDefaultBillingAddressByCustomerId(Long customerId);
}
