package com.ecommerce.admin.controller;

import com.ecommerce.library.dto.OrderDto;
import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.OrderDetails;
import com.ecommerce.library.repository.OrderRepository;
import com.ecommerce.library.service.OrderService;

import com.ecommerce.library.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Controller
public class OrderDetailsControll {
    private OrderService orderService;
    private OrderRepository orderRepository;
    WalletService walletService;

    @Autowired
    public OrderDetailsControll(OrderService orderService, OrderRepository orderRepository,WalletService walletService) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.walletService=walletService;
    }

    @GetMapping("/orderDetails/{pageNo}")
    public String showOrderDetails(@PathVariable("pageNo") int pageNo, Model model) {
        Page<Order> orders = orderService.findOrderByPageble(pageNo, 10);
        //List<Order>orders=orderService.findAll();
        OrderDto orderDto=new OrderDto();
        model.addAttribute("report",orderDto);
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", orders.getTotalPages());
        return "orders-list";
    }

    @GetMapping("/orderDetailsInfo")
    public String showOrderDetaliInfo(@RequestParam("orderId") Long orderId, Model model) {
        List<OrderDetails> orderDetails = orderService.findOrderByOrderId(orderId);
        Order order = orderService.findById(orderId);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("order", order);
        model.addAttribute("order1", order);
        return "orderDetails";
    }

    @PostMapping("/updateStatus")
    public String showUpdateOrderStaus(@ModelAttribute("order1") Order order) {
        orderService.updateOrderStatus(order);
        if (order.getOrderStatus().equals("Return Accept")) {
            walletService.addToRefundAmount(order.getId());
        }
        return "redirect:/orderDetails/0";
    }


    @GetMapping("/orderStatus/{pageNo}")
    public String showOrderStatus(@PathVariable("pageNo")int pageNo,
                                  @ModelAttribute("report")OrderDto orderDto1,Model model){
        String orderStatus=orderDto1.getOrderStatus();
        if (orderStatus=="full"){
            return "redirect:/orderDetails/0";
        }
        Page<Order> orders = orderService.findOrderByOrderStatusPagable(pageNo,orderStatus);
        List<OrderDetails> orderDetails=orderService.findAllOrder();
        OrderDto orderDto=new OrderDto();
        model.addAttribute("report",orderDto);
        model.addAttribute("orderDetails",orderDetails);
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", orders.getTotalPages());
        return "orders-list";
    }
    @GetMapping("/order-tracking-admin")
    public String orderTracking(@RequestParam("orderId")Long id,
                                  Model model){
        Order order=orderService.findById(id);



        model.addAttribute("order", order);
      ;
        return "page-orders-tracking";
    }


}