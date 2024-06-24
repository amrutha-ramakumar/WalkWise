package com.ecommerce.customer.Controller;

import com.ecommerce.library.dto.AddressDto;
import com.ecommerce.library.dto.CustomerDto;
import com.ecommerce.library.model.Address;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.repository.CustomerRepository;
import com.ecommerce.library.service.AddressService;
import com.ecommerce.library.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
public class AccountController {

    private AddressService addressService;

    private CustomerService customerService;
    private CustomerRepository customerRepository;

    public AccountController(AddressService addressService, CustomerService customerService, CustomerRepository customerRepository) {
        this.addressService = addressService;
        this.customerService = customerService;
        this.customerRepository = customerRepository;
    }


    @GetMapping("/addCustomerAddress")
    public String showAccountAddAddress(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        AddressDto addressDto = new AddressDto();
        model.addAttribute("address", addressDto);
        return "add-account-address";
    }

    @PostMapping("/saveNewAddress")
    public String showSaveAccountaddress(@ModelAttribute("address")AddressDto addressDto, Principal principal){
        String username=principal.getName();
        addressService.save(addressDto,username);
        return "redirect:/account";
    }

    @GetMapping("/editCustomerAddress")
    public String showEditcustomerAddress(@RequestParam("addressId")Long id, Model model){
        Optional<Address> address=addressService.findByid(id);
        model.addAttribute("address",address.get());
        return "edit-account-address";
    }

    @PostMapping("/updateaccountAddress")
    public String showupdateCustomerAddress(@ModelAttribute("address")AddressDto addressDto){
        addressService.update(addressDto);
        return "redirect:/account";
    }

    @GetMapping("/removeCustomerAddress")
    public String delete(@RequestParam("addressId")Long id, RedirectAttributes redirectAttributes){
        // Delete the product
        addressService.deleteAddress(id);

        redirectAttributes.addFlashAttribute("success","Address deleted");

        return "redirect:/account";
    }
//setting address as default
    @GetMapping("/setAsDefault")
    public String defaultAddress(@RequestParam("addressId")Long id,Principal principal){
        String username=principal.getName();
        addressService.updateDefaultAddress(id,username);
        return "redirect:/account";
    }

    @GetMapping("/editCustomerDetails")
    public String showEditCustomerDetails(@RequestParam("customerid") Long id, Model model) {
        CustomerDto customer = customerService.findById(id);
        model.addAttribute("customer", customer);
        return "edit-customer-details"; // Ensure this is the correct template name
    }

    @PostMapping("/updateCustomerDetails")
    public String updateCustomerDetails(@ModelAttribute("customer") CustomerDto customer) {
        customerService.updateCustomerDetails(customer);
        return "redirect:/account"; // Redirect to the appropriate page after update
    }

}
