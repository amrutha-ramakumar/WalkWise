package com.ecommerce.customer.Controller;

import com.ecommerce.library.model.*;
import com.ecommerce.library.repository.OrderDetailsRepository;
import com.ecommerce.library.repository.SizeRepository;
import com.ecommerce.library.service.*;

import com.ecommerce.library.utils.CustomerUtils;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    OrderService orderService;
    AddressService addressService;
    ShoppingCartService shopCartService;
    CustomerService customerService;
    WalletService walletService;
    SizeRepository sizeRepository;
    private CustomerUtils customerUtils;
    @Autowired
    public OrderController(OrderService orderService, AddressService addressService,SizeRepository sizeRepository,CustomerUtils customerUtils,
                        ShoppingCartService shopCartService, CustomerService customerService, WalletService walletService) {
        this.orderService = orderService;
        this.addressService = addressService;
        this.shopCartService = shopCartService;
        this.customerService= customerService;
        this.walletService=walletService;
        this.sizeRepository=sizeRepository;
        this.customerUtils=customerUtils;
    }


    @GetMapping("/orderConfirm")
    public String showOrderConfirm(){
        return "order-confirm";
    }

    @GetMapping("/order")
    public String showOrder(@RequestParam("pageNo")int pageNo, Model model, Principal principal,HttpSession session) {
        if (customerUtils.isUserBlocked(principal, session)) return "redirect:/logout";
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Page<Order> orders = orderService.findOrderByCustomerPagable(pageNo,username);
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", orders.getTotalPages());

        return "orders";
    }

    @GetMapping("/ordersDetails")
    public String showOrderDetails(@RequestParam("orderId") Long id, Model model, Principal principal, HttpSession session) {
        if (customerUtils.isUserBlocked(principal, session)) return "redirect:/logout";
        Order order=orderService.findById(id);
        List<OrderDetails> orderDetails = orderService.findOrderByOrderId(id);
        List<Size> sizes=sizeRepository.findAll();
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("order",order);
        model.addAttribute("sizes",sizes);
        return "order-details";
    }

    @PostMapping("/verify-payment")
    @ResponseBody
    public String showVerifyPayment(@RequestBody Map<String,Object> data){

        return "done";
    }

    @GetMapping("/cancelOrder")
    public String showCancelOrder(@ModelAttribute("orderId")Long id,HttpSession session,Principal principal){
        if (customerUtils.isUserBlocked(principal, session)) return "redirect:/logout";

        Order order=orderService.findById(id);
        orderService.cancelOrder(id);
        if (order.getPaymentMethod().equals("online_payment") || order.getPaymentMethod().equals("wallet")) {
            if (order.getOrderStatus().equals("Cancel")) {
                walletService.addToRefundAmount(order.getId());
            }
        }
        return "redirect:/order?pageNo=0";
    }

    @GetMapping("/returnOrder")
    public String showReturnOrder(@ModelAttribute("orderId")Long id){
        Order order=orderService.findById(id);
        orderService.returnOrder(id);

        return "redirect:/order?pageNo=0";
    }

    @GetMapping("/order-tracking")
    public String orderTracking(@ModelAttribute("orderId")Long id,Model model,HttpSession session,Principal principal){
        if (customerUtils.isUserBlocked(principal, session)) return "redirect:/logout";
        Order order=orderService.findById(id);
        model.addAttribute("order",order);

        return "orders-tracking";
    }

}
