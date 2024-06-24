package com.ecommerce.library.service;


import com.ecommerce.library.dto.CustomEarning;
import com.ecommerce.library.dto.DailyEarning;
import com.ecommerce.library.dto.WeeklyEarnings;
import com.ecommerce.library.dto.YearlyEarning;
import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.OrderDetails;
import com.ecommerce.library.model.ShoppingCart;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order saveOrder(ShoppingCart shoppingCart, String email, Long addressId, String paymentMethod, Double grandTotel,Double deduct,double total);

    List<OrderDetails> findAllOrder();

    List<OrderDetails> findOrderDetailsByCustomer(String email);

    Page<OrderDetails> findOrderDetailsByCustomerPagable(int pageNo, String email);

   Optional<Order> findByOrderId(Long orderid);

    List<OrderDetails> findOrderByOrderId(Long orderid);


    Optional<OrderDetails> findOrderDetailsById(Long id);

    Order findById(long id);

    void updateOrderStatus(Order order);

    List<Order> findAll();

    void cancelOrder(Long id);

    void returnOrder(Long id);
    List<Order> findOrderByCustomer(String email);

    Page<Order> findOrderByPageble(int page,int size);
    Page<Order> findOrderByCustomerPagable(int pageNo, String email);
    Page<Order> findOrderByOrderStatusPagable(int pageNo,String status);


    List<DailyEarning> getCurrentDayOrders();
    List<WeeklyEarnings> getLastWeekOrders();
    List<YearlyEarning> getYearlyOrders();
    List<CustomEarning> getOrdersWithinDateRange(Date startDate, Date endDate);
    CustomEarning mapToDTO(Object[] result);
    void  updatePaymentMethod(String paymentMethod,Long orderId);
}
