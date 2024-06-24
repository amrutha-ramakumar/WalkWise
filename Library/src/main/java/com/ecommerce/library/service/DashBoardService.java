package com.ecommerce.library.service;

import com.ecommerce.library.dto.CategorySales;
import com.ecommerce.library.dto.ProductSales;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface DashBoardService {
    double findCurrentMonthOrder(Date startDate, Date endDate );

    long findOrdersTotal();

    int findOrdersPending();

    List<Object[]> retrieveDailyEarnings(int currentYr, int currentMnt);

    List<Object[]> retriveYearlyEarning();

    List<Object[]> findTotalPricesByPayment();
    List<Object[]> retriveMontlyEarning(int currentYr);

    List<ProductSales> getTopSellingProducts();

    List<CategorySales> getTopSellingCategories();
}
