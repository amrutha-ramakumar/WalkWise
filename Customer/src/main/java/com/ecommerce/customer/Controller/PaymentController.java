package com.ecommerce.customer.Controller;

import com.ecommerce.customer.config.CustomerDetails;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.model.Wallet;
import com.ecommerce.library.service.*;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Map;

@Controller
public class PaymentController {
    OrderService orderService;
    AddressService addressService;
    ShoppingCartService shopCartService;
    CustomerService customerService;
    WalletService walletService;

    @Autowired
    public PaymentController(OrderService orderService, AddressService addressService,
                           ShoppingCartService shopCartService, CustomerService customerService, WalletService walletService) {
        this.orderService = orderService;
        this.addressService = addressService;
        this.shopCartService = shopCartService;
        this.customerService= customerService;
        this.walletService=walletService;
    }
    @PostMapping("/createPayment")
    @ResponseBody
    public String showOnlinePayment(Principal principal, Authentication authentication,
                                    @RequestBody Map<String, Object> data) throws RazorpayException {

        Customer customUser=customerService.findByEmail(principal.getName());
        Long id=customUser.getCustomer_id();
        String username=principal.getName();
        String paymentMethod = data.get("paymentMethod").toString();
        Long address_id=Long.parseLong(data.get("addressId").toString());
        Double amount= Double.valueOf(data.get("amount").toString());
        Double deduct=Double.valueOf(data.get("deduct").toString());
        Double total=Double.valueOf(data.get("total").toString());
        System.out.println(paymentMethod);

        if(paymentMethod.equals("online_payment")) {
//            ShoppingCart shopingCart = new ShoppingCart();
            var client = new RazorpayClient("rzp_test_NeTo17DpgmKAdp", "F4PUWaYVd3HAjf2oTcpYS7qt");
            JSONObject object = new JSONObject();
            object.put("amount", amount * 100);
            object.put("currency", "INR");
            object.put("receipt", "receipt#1");
            com.razorpay.Order order = client.orders.create(object);
            JSONObject response = new JSONObject();
            response.put("status", "created");
            response.put("id", (Object) order.get("id"));
            response.put("amount", (Object) order.get("amount"));
            return response.toString();

        }
        if(paymentMethod.equals("wallet")){
            Wallet wallet=walletService.findByCustomer(id);
            if(wallet.getBalance()<amount){
                JSONObject response=new JSONObject();
                response.put("status","noWallet");
                return response.toString();
            }
            else{
//                ShoppingCart shoppingCart = new ShoppingCart();
//                orderService.saveOrder(shoppingCart, username, address_id,paymentMethod,amount,deduct,total);
//                walletService.setWalletAmount(id,amount);
                JSONObject response=new JSONObject();
                response.put("status","wallet");
                return response.toString();
            }

        }
        else {
            JSONObject option = new JSONObject();
            if (amount>1000) {
//                ShoppingCart shoppingCart = new ShoppingCart();
//                orderService.saveOrder(shoppingCart, username, address_id, paymentMethod, amount, deduct,total);
                option.put("status", "cash");
            }
            else
                option.put("status","noCash");
            return option.toString();
        }

    }

//    @PostMapping("/saveOrder")
//    @ResponseBody
//    public String saveOrder(@RequestBody Map<String, Object> data, Principal principal, Authentication authentication) {
//        CustomerDetails customUser = (CustomerDetails) authentication.getPrincipal();
//        String username = principal.getName();
//        String paymentMethod = data.get("paymentMethod").toString();
//        Long address_id = Long.parseLong(data.get("addressId").toString());
//        Double amount = Double.valueOf(data.get("amount").toString());
//        Double deduct = Double.valueOf(data.get("deduct").toString());
//        ShoppingCart shoppingCart = new ShoppingCart();
//        Order order=orderService.saveOrder(shoppingCart, username, address_id, paymentMethod, amount, deduct);
//        JSONObject response = new JSONObject();
//        response.put("status", "success");
//        return response.toString();
//    }
    @PostMapping("/saveOrder")
    @ResponseBody
    public String saveOrder(@RequestBody Map<String, Object> data, Principal principal, Authentication authentication) {
        CustomerDetails customUser = (CustomerDetails) authentication.getPrincipal();
        String username = principal.getName();
        String paymentMethod = data.get("paymentMethod").toString();
        Long address_id = Long.parseLong(data.get("addressId").toString());
        Double amount = Double.valueOf(data.get("amount").toString());
        Double deduct = Double.valueOf(data.get("deduct").toString());
        Double total=Double.valueOf(data.get("total").toString());
        ShoppingCart shoppingCart = new ShoppingCart();

        // Placeholder for orderService.saveOrder()
         Order order = orderService.saveOrder(shoppingCart, username, address_id, paymentMethod, amount, deduct,total);

        JSONObject response = new JSONObject();
        response.put("status", "success");
        return response.toString();
    }

    @PostMapping("/paymentPending")
    @ResponseBody
    public String paymentPending(@RequestBody Map<String,Object> data,Principal principal, Authentication authentication){
         CustomerDetails customUser = (CustomerDetails) authentication.getPrincipal();
         String username = principal.getName();
         String paymentMethod = "paymentPending";
         Long address_id = Long.parseLong(data.get("addressId").toString());
         Double amount = Double.valueOf(data.get("amount").toString());
         Double deduct = Double.valueOf(data.get("deduct").toString());
        Double total=Double.valueOf(data.get("total").toString());
         ShoppingCart shoppingCart = new ShoppingCart();

         // Placeholder for orderService.saveOrder()
         Order order = orderService.saveOrder(shoppingCart, username, address_id, paymentMethod, amount, deduct,total);

         JSONObject response = new JSONObject();
         response.put("status", "pending");
         return response.toString();
     }
    @PostMapping("/updateOrder")
    @ResponseBody
    public String updateOrder(@RequestBody Map<String, Object> data, Principal principal, Authentication authentication) {
        Long orderId=Long.parseLong(data.get("id").toString());
        String paymentMethod=data.get("paymentMethod").toString();
        orderService.updatePaymentMethod(paymentMethod,orderId);
        JSONObject response = new JSONObject();
        response.put("status", "success");
        return response.toString();
    }
}
