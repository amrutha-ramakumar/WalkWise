package com.ecommerce.library.service.impl;

import com.ecommerce.library.dto.AddressDto;
import com.ecommerce.library.model.Address;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.repository.AddressRepository;
import com.ecommerce.library.service.AddressService;
import com.ecommerce.library.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class AddressServiceImpl implements AddressService {

    private CustomerService customerService;

    private AddressRepository addressRepository;

    public AddressServiceImpl(CustomerService customerService, AddressRepository addressRepository) {
        this.customerService = customerService;
        this.addressRepository = addressRepository;
    }

    @Override
    public Address save(AddressDto addressDto, String email) {
        Customer customer=customerService.findByEmail(email);
        Address address=new Address();
        address.setAddressLine1(addressDto.getAddressLine1());
        address.setAddressLine2(addressDto.getAddressLine2());
        address.setCity(addressDto.getCity());
        address.setDistrict(addressDto.getDistrict());
        address.setState(addressDto.getState());
        address.setCountry(addressDto.getCountry());
        address.setPincode(addressDto.getPincode());
        address.setCustomer(customer);
        if (addressDto.isIsdefault()) {
            // Set the new address as default
            address.setIsdefault(true);

            // Find and unset any existing default address for the customer
            customer.getAddress().stream()
                    .filter(Address::isIsdefault)
                    .findFirst()
                    .ifPresent(defaultAddress -> {
                        defaultAddress.setIsdefault(false);
                        addressRepository.save(defaultAddress);
                    });
        }
        return addressRepository.save(address);
    }

    @Override
    public Optional<Address> findByid(Long id) {  return addressRepository.findById(id);}

    @Override
    public Address update(AddressDto addressDto) {
        Address address=addressRepository.getReferenceById(addressDto.getId());
        address.setAddressLine1(addressDto.getAddressLine1());
        address.setAddressLine2(addressDto.getAddressLine2());
        address.setCity(addressDto.getCity());
        address.setDistrict(addressDto.getDistrict());
        address.setState(addressDto.getState());
        address.setCountry(addressDto.getCountry());
        address.setPincode(addressDto.getPincode());
        return addressRepository.save(address);
    }

    @Override
    public Address findByIdOrder(Long id) {    return addressRepository.getReferenceById(id);    }

    @Override
    public List<Address> findAddressByCustomer(String email) {    return addressRepository.findAddressByCustomer(email);  }

    @Override
    public void deleteAddress(Long id) {    addressRepository.deleteById(id);}

    @Override
    public void updateDefaultAddress( Long addressId,String email) {        addressRepository.setDefaultAddressForCustomer(addressId,email);}

    @Override
    public Optional<Address> findDefaultBillingAddressByCustomerId(Long customerId) {
        return Optional.empty();
    }


}
