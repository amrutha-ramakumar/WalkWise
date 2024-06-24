package com.ecommerce.library.service.impl;


import com.ecommerce.library.dto.CustomEarning;
import com.ecommerce.library.dto.DailyEarning;
import com.ecommerce.library.dto.WeeklyEarnings;
import com.ecommerce.library.dto.YearlyEarning;
import com.ecommerce.library.model.*;
import com.ecommerce.library.repository.*;
import com.ecommerce.library.service.AddressService;
import com.ecommerce.library.service.OrderService;
import com.ecommerce.library.service.ShoppingCartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private OrderRepository orderRepository;
    private OrderDetailsRepository orderDetailsRepository;
    private CustomerRepository customerRepository;
    private ShoppingCartRepository shopingCartRepository;
    private ProductSizeQuantityRepository productSizeQuantityRepository;
    private AddressService addressService;
    private ShoppingCartService shopCartService;
    private ProductRepository productRepository;
    private AddressRepository addressRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderDetailsRepository orderDetailsRepository,
                            CustomerRepository customerRepository, ShoppingCartRepository shopingCartRepository,
                            AddressService addressService, ShoppingCartService shopCartService,
                            ProductRepository productRepository, AddressRepository addressRepository,
                            ProductSizeQuantityRepository productSizeQuantityRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailsRepository = orderDetailsRepository;
        this.customerRepository = customerRepository;
        this.shopingCartRepository = shopingCartRepository;
        this.addressService = addressService;
        this.shopCartService = shopCartService;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
        this.productSizeQuantityRepository = productSizeQuantityRepository;
    }

    @Override
    public Order saveOrder(ShoppingCart shopingCart, String email, Long addressId, String paymentMethod,
                           Double grandTotel,Double deduct,double total) {

        Order order = new Order();
        order.setIdOrders(generateRandomNumericString(6));
        order.setOrderDate(new Date());
        if(paymentMethod== "paymentPending"){
            order.setOrderStatus("OrderPending");
        }
        else {
            order.setOrderStatus("Pending");
        }
//        order.setSize(shopingCart.getSize());
//        order.setQuantity(shopingCart.getQuantity());
        order.setCustomer(customerRepository.findByEmail(email));
        order.setGrandTotalPrize(grandTotel);
        order.setPaymentMethod(paymentMethod);
        if (total<10000)
            order.setShippingFee(50);
        else
            order.setShippingFee(0);
        double roundedBalance = Math.round(deduct * 100.0) / 100.0;
        order.setDeduction(roundedBalance);
        order.setShippingAddress(addressRepository.getReferenceById(addressId));
        orderRepository.save(order);
        List<ShoppingCart> shopingCarts = shopCartService.findShoppingCartByCustomer(email);
        for (ShoppingCart cart : shopingCarts) {
            OrderDetails orderDetails = new OrderDetails();
            orderDetails.setProduct(cart.getProduct());
            orderDetails.setOrder(order);
            orderDetails.setQuantity(cart.getQuantity());
            orderDetails.setSize(cart.getSize().getId());
            orderDetails.setUnitPrice(cart.getUnitPrice());
            orderDetails.setTotalPrice(cart.getTotalPrice());
            orderDetailsRepository.save(orderDetails);
            Product product = cart.getProduct();
            ProductSizeQuantity productSizeQuantity=productSizeQuantityRepository.findProductSizeQuantityByProductIdAndSizeId(cart.getProduct().getId(),cart.getSize().getId());
            int quantity = productSizeQuantity.getQuantity() - cart.getQuantity();
            int currentQuantity = product.getCurrentQuantity() - cart.getQuantity();
            if(quantity<0){
                quantity=0;
            }
            if(currentQuantity<0){
                currentQuantity=0;
            }
            productSizeQuantity.setQuantity(quantity);
            product.setCurrentQuantity(currentQuantity);
            productSizeQuantityRepository.save(productSizeQuantity);
            productRepository.save(product);
            cart.setDeleted(true);
            shopingCartRepository.save(cart);
        }

        return order;
    }

    private String generateRandomNumericString(int length) {
        String characterPool = "0123456789";
        Random random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(characterPool.charAt(random.nextInt(characterPool.length())));
        }
        return stringBuilder.toString();
    }

    @Override
    public List<OrderDetails> findAllOrder() {
        return orderDetailsRepository.findAllOrder();
    }

    @Override
    public List<OrderDetails> findOrderDetailsByCustomer(String email) {
        return orderDetailsRepository.findOrderDetailsByCustomer(email);
    }

    @Override
    public Optional<Order> findByOrderId(Long orderid) {
        return orderRepository.findById(orderid);
    }

    @Override
    public List<OrderDetails> findOrderByOrderId(Long orderid) {
        return orderDetailsRepository.findByOrderId(orderid);
    }

    @Override
    public Optional<OrderDetails> findOrderDetailsById(Long id) {
        return orderDetailsRepository.findById(id);
    }

    @Override
    public Order findById(long id) {
        return orderRepository.getReferenceById(id);
    }

    @Override
    public void updateOrderStatus(Order order) {

        Order order1 = orderRepository.getReferenceById(order.getId());

        order1.setOrderStatus(order.getOrderStatus());
        if(Objects.equals(order.getOrderStatus(), "Delivered")){
            order1.setDeliveryDate(new Date());
        }
        orderRepository.save(order1);
        if(order.getOrderStatus().equals("Return Accept")){
            List<OrderDetails> orderDetails=orderDetailsRepository.findByOrderId(order.getId());
            for(OrderDetails orders:orderDetails){
                Long productId=orders.getProduct().getId();
                Product product=productRepository.getReferenceById(productId);
                ProductSizeQuantity productSizeQuantity=productSizeQuantityRepository.findProductSizeQuantityByProductIdAndSizeId(productId,orders.getSize());
                productSizeQuantity.setQuantity(productSizeQuantity.getQuantity()+orders.getQuantity());
                productSizeQuantityRepository.save(productSizeQuantity);
                product.setCurrentQuantity(product.getCurrentQuantity()+orders.getQuantity());
                productRepository.save(product);
            }
        }
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAllByDate();
    }

    @Override
    public void cancelOrder(Long id) {
        Order order = orderRepository.getReferenceById(id);
        order.setOrderStatus("Cancel");
        orderRepository.save(order);
        List<OrderDetails> orderDetails=orderDetailsRepository.findByOrderId(id);
        for(OrderDetails orders:orderDetails){
            Long productId=orders.getProduct().getId();
            Product product=productRepository.getReferenceById(productId);
            ProductSizeQuantity productSizeQuantity=productSizeQuantityRepository.findProductSizeQuantityByProductIdAndSizeId(productId,orders.getSize());
            productSizeQuantity.setQuantity(productSizeQuantity.getQuantity() +orders.getQuantity());
            productSizeQuantityRepository.save(productSizeQuantity);
            product.setCurrentQuantity(product.getCurrentQuantity()+orders.getQuantity());
            productRepository.save(product);
        }
    }

    @Override
    public void returnOrder(Long id) {
        Order order = orderRepository.getReferenceById(id);
        order.setOrderStatus("Return Pending");
        orderRepository.save(order);
    }

    @Override
    public List<Order> findOrderByCustomer(String email) {
        // Pageable pageable=PageRequest.of(pageNo,6);
        // Page<Order> orders=this.orderRepository.findOrderByCustomer(email,pageable);
        return orderRepository.findOrderByCustomer(email);
    }

    @Override
    public Page<Order> findOrderByCustomerPagable(int pageNo, String email) {
        Pageable pageable=PageRequest.of(pageNo,6);
        Page<Order> orders=this.orderRepository.findOrderByCustomerPagable(pageable,email);
        return orders;
    }

    @Override
    public Page<OrderDetails> findOrderDetailsByCustomerPagable(int pageNo, String email) {
        Pageable pageable=PageRequest.of(pageNo,6);
        Page<OrderDetails> orders=this.orderRepository.findOrderDetailsByCustomerPagable(pageable,email);
        return orders;
    }

    @Override
    public Page<Order> findOrderByOrderStatusPagable(int pageNo, String status) {
        Pageable pageable=PageRequest.of(pageNo,6);
        Page<Order> orders=this.orderRepository.findOrderByOrderStatusPagable(pageable,status);
        return orders;
    }

    @Override
    public Page<Order> findOrderByPageble(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = this.orderRepository.findOrderByPagable(pageable);
        return orders;
    }

    @Override
    public List<DailyEarning> getCurrentDayOrders() {
        List<Object[]> result = orderRepository.currentDayOrders();
        List<DailyEarning> orders = new ArrayList<>();

        for (Object[] row : result) {
            Long orderId = (Long) row[0];
            Long productId = ((Long) row[1]);
            String productName = (String) row[2];
            String description = (String) row[3];
            Double unitPrice = (Double) row[4];
            Integer quantity = (Integer) row[5];
            Double total = (Double) row[6];
            Double deduction = (Double) row[7];
//            Double shippingFee = (Double) row[8];
            Double totalAmount = (Double) row[8];

            orders.add(new DailyEarning(orderId, productId, productName, description, unitPrice, quantity, total, deduction, totalAmount));
        }
        return orders;
    }

    @Override
    public List<WeeklyEarnings> getLastWeekOrders() {
        List<Object[]> results = orderRepository.lastWeekOrders();
        List<WeeklyEarnings> orders = new ArrayList<>();

        for (Object[] result : results) {
            WeeklyEarnings order = new WeeklyEarnings();
            order.setOrderDate((Date) result[0]);
            order.setOrderId(((Long) result[1]));
            order.setProductId(((Long) result[2]));
            order.setProductName((String) result[3]);
            order.setShortDescription((String) result[4]);
            order.setUnitPrice((Double) result[5]);
            order.setQuantity((Integer) result[6]);
            order.setTotalPrice((Double) result[7]);
            order.setDeduction((Double) result[8]);
//            order.setShippingFee((Double) result[9]);
            order.setTotalAmount((Double) result[9]);
            orders.add(order);
        }
        return orders;
    }

    @Override
    public List<YearlyEarning> getYearlyOrders() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<Object[]> results = orderRepository.findYearlyOrdersWithCategory(currentYear);
        List<YearlyEarning> orders = new ArrayList<>();

        for (Object[] result : results) {
            YearlyEarning order = new YearlyEarning();
            order.setCategoryName((String) result[0]);
            order.setProductName((String) result[1]);
            order.setOrderedQuantity((Long) result[2]);
            order.setOrderedTotalPrice((Double) result[3]);
            orders.add(order);
        }

        return orders;
    }

    @Override
    public List<CustomEarning> getOrdersWithinDateRange(Date startDate, Date endDate) {
        List<Object[]> results = orderRepository.findOrdersWithinDateRange(startDate, endDate);
        return results.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public CustomEarning mapToDTO(Object[] result) {
        CustomEarning dto = new CustomEarning();
        dto.setOrderDate((Date) result[0]);
        dto.setOrderId((Long) result[1]);
        dto.setProductId((Long) result[2]);
        dto.setProductName((String) result[3]);
        dto.setProductDescription((String) result[4]);
        dto.setUnitPrice((Double) result[5]);
        dto.setQuantity((Integer) result[6]);
        dto.setTotalPrice((Double) result[7]);
        dto.setDeduction((Double) result[8]);
//        dto.setShippingFee((Double) result[9]);
        dto.setTotalAmount((Double) result[9]);
        return dto;
    }

    @Override
    public void updatePaymentMethod(String paymentMethod, Long orderId) {
        Order order=orderRepository.getById(orderId);
        order.setPaymentMethod(paymentMethod);
        order.setOrderStatus("Pending");
        orderRepository.save(order);
    }

}












