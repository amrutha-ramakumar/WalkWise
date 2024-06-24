package com.ecommerce.library.service.impl;

import com.ecommerce.library.dto.CategorySales;
import com.ecommerce.library.dto.ProductSales;
import com.ecommerce.library.model.Order;
import com.ecommerce.library.repository.OrderRepository;
import com.ecommerce.library.service.DashBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashBoardService {

    private final OrderRepository orderRepository;

    @Autowired
    public DashboardServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public double findCurrentMonthOrder(Date startDate, Date endDate) {
        List<Order> orderList=orderRepository.findByOrderDateBetween(startDate,endDate);
        double ordersTotalPrice=0;
        if(!orderList.isEmpty()){
            for(Order orders:orderList) {

                ordersTotalPrice = ordersTotalPrice + orders.getGrandTotalPrize();
            }
        }
        return ordersTotalPrice;
    }

    @Override
    public long findOrdersTotal() {
        return orderRepository.count();
    }

    @Override
    public int findOrdersPending() {
        return orderRepository.countByIsAcceptIsFalse();
    }



    @Override
    public List<Object[]> retrieveDailyEarnings(int currentYr, int currentMnt) {
        return orderRepository.dailyEarnings(currentYr,currentMnt);
    }
    @Override
    public List<Object[]> retriveMontlyEarning(int currentYr){
        return orderRepository.monthlyEarnings(currentYr);
    }

    @Override
    public List<Object[]> retriveYearlyEarning() {
        return orderRepository.yearlyEarnings();
    }


    @Override
    public List<Object[]> findTotalPricesByPayment() {
        return orderRepository.findTotalPricesByPaymentMethod();
    }


//    Best selling

    @Override
    public List<ProductSales> getTopSellingProducts() {
        List<Object[]> results = orderRepository.findTopSellingProducts();
        List<ProductSales> topSellingProducts = new ArrayList<>();

        for (Object[] result : results) {
            Long productId = ((Number) result[0]).longValue();
            String productName = (String) result[1];
            Long totalSold = ((Number) result[2]).longValue();
            System.out.println("productId"+productId);

            topSellingProducts.add(new ProductSales(productId, productName, totalSold));
        }
        return topSellingProducts;
    }

    @Override
    public List<CategorySales> getTopSellingCategories() {
        List<Object[]> results = orderRepository.findTopSellingCategories();
        List<CategorySales> topSellingCategories = new ArrayList<>();

        for (Object[] result : results) {
            Long categoryId = ((Number) result[0]).longValue();
            String categoryName = (String) result[1];
            Long totalSold = ((Number) result[2]).longValue();
            topSellingCategories.add(new CategorySales(categoryId, categoryName, totalSold));
        }

        return topSellingCategories;
    }

}
