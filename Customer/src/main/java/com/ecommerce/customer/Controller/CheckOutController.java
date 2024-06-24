package com.ecommerce.customer.Controller;

import com.ecommerce.library.dto.AddressDto;
import com.ecommerce.library.model.*;
import com.ecommerce.library.service.AddressService;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.service.OrderService;
import com.ecommerce.library.service.ShoppingCartService;
import com.ecommerce.library.utils.CustomerUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class CheckOutController {

    private AddressService addressService;
    private CustomerService customerService;
    private ShoppingCartService shoppingCartService;
    private OrderService orderService;
    private CustomerUtils customerUtils;
@Autowired
    public CheckOutController(AddressService addressService, CustomerService customerService,
                              OrderService orderService,ShoppingCartService shoppingCartService,CustomerUtils customerUtils) {
        this.addressService = addressService;
        this.customerService= customerService;
        this.shoppingCartService=shoppingCartService;
        this.orderService=orderService;
        this.customerUtils=customerUtils;

    }

    @GetMapping("/checkOut")
    public String showCheckOutPagee(Model model, Principal principal, HttpSession httpSession){
        if (customerUtils.isUserBlocked(principal, httpSession)) return "redirect:/logout";
        String username=principal.getName();
        if(principal==null)
        {
            return "redirect:/login";
        }
        Customer customer=customerService.findByEmail(username);
        List<ShoppingCart> shoppingCarts=shoppingCartService.findShoppingCartByCustomer(username);
        Coupon coupon=new Coupon();
        model.addAttribute("coupon",coupon);

        if(shoppingCarts.isEmpty()){
            return "redirect:/cart?empty";
        }
        for(ShoppingCart cart:shoppingCarts){
            int quantity=cart.getQuantity();
            int productQuantity=cart.getProduct().getCurrentQuantity();
            if(quantity>productQuantity){
                return "redirect:/cart?quantityError";
            }
        }
        double deduction=0.0;
        double total=shoppingCartService.grandTotal(username);
        List<Address> addresses=addressService.findAddressByCustomer(username);
        Integer shippingFee=total<10000?50:0;
        Double payable=total+shippingFee;

        model.addAttribute("customer",customer);
        model.addAttribute("addresses",addresses);
        model.addAttribute("cartItem",shoppingCarts);
        model.addAttribute("deduction",deduction);
        model.addAttribute("total",total);
        model.addAttribute("payable",payable);
        model.addAttribute("shippingFee",shippingFee);
        model.addAttribute("couponApplied",false);
        model.addAttribute("couponRemoved",true);

    return "checkOut";
    }


    @GetMapping("/addAddress")
    public String showAddAddrss(Model model,Principal principal,HttpSession session){
        if (customerUtils.isUserBlocked(principal, session)) return "redirect:/logout";

        if(principal==null){
        return "redirect:/login";
    }
        AddressDto addressDto=new AddressDto();
        model.addAttribute("tittle","Add address");
        model.addAttribute("address",addressDto);
        return "add-address-checkout";
    }


    @PostMapping("/saveAddress")
    public String saveAddress(@ModelAttribute("address") AddressDto addressDto, Principal principal,HttpSession session){
        if (customerUtils.isUserBlocked(principal, session)) return "redirect:/logout";

        if(principal==null){
            return "redirect:/login";
        }
    String username=principal.getName();
    addressService.save(addressDto,username);
        return "redirect:/checkOut";
    }


    @GetMapping("/editAddress")
    public String showEditAddress(@RequestParam("addressId")Long id, Model model,Principal principal,HttpSession session){
        if (customerUtils.isUserBlocked(principal, session)) return "redirect:/logout";
        Optional<Address> address=addressService.findByid(id);
        model.addAttribute("address",address.get());
        return "edit-address-checkout";
    }

    @PostMapping("/updateAddress")
    public String showEditAddress(@ModelAttribute("address")AddressDto addressDto){
        addressService.update(addressDto);
        return "redirect:/checkOut";
    }
    @GetMapping("/rePay")
    public String paymentContinue(@ModelAttribute("orderId")Long id,Model model, Principal principal, HttpSession httpSession){
//        if (customerUtils.isUserBlocked(principal, httpSession)) return "redirect:/logout";
        Order order=orderService.findById(id);
        String username=principal.getName();
        if(principal==null)
        {
            return "redirect:/login";
        }
        Customer customer=customerService.findByEmail(username);
        Coupon coupon=new Coupon();
        model.addAttribute("coupon",coupon);
        List<OrderDetails> orderDetails=orderService.findOrderByOrderId(order.getId());
        double deduction=order.getDeduction();
        double total=order.getGrandTotalPrize();
        Integer shippingFee=order.getShippingFee();
        List<Address> addresses=addressService.findAddressByCustomer(username);
        model.addAttribute("customer",customer);
        model.addAttribute("addresses",addresses);
        model.addAttribute("cartItem",orderDetails);
        model.addAttribute("deduction",deduction);
        model.addAttribute("total",total);
        model.addAttribute("payable",total);
        model.addAttribute("id",id);
        model.addAttribute("shippingFee",shippingFee);
        model.addAttribute("couponApplied",false);
        model.addAttribute("couponRemoved",true);

        return "checkOut";
    }
}
