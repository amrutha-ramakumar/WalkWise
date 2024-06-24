package com.ecommerce.admin.controller;

import com.ecommerce.library.dto.*;
import com.ecommerce.library.service.DashBoardService;
import com.ecommerce.library.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashBoardController {
    private DashBoardService dashBoardService;
    private OrderService orderService;


    public DashBoardController(DashBoardService dashBoardService, OrderService orderService) {
        this.dashBoardService = dashBoardService;
        this.orderService = orderService;
    }

    @RequestMapping(value={"/","/index"})
    public String home(Model model, Principal principal) throws JsonProcessingException {
        if (principal == null) {
            return "redirect:/login";
        }
        model.addAttribute("title", "Home Page");

        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || authentication instanceof AnonymousAuthenticationToken){
            return "redirect:/login";
        }
        /* Earning card*/
        YearMonth currentYear=YearMonth.now();
        LocalDate localStartDate = LocalDate.of(currentYear.getYear(), currentYear.getMonthValue(), 1);
        LocalDate localEndDate = currentYear.atEndOfMonth();
        Date startDate = java.sql.Date.valueOf(localStartDate);
        Date endDate = java.sql.Date.valueOf(localEndDate);
        double newMonth=dashBoardService.findCurrentMonthOrder(startDate,endDate);
        double currentMonthEarning = Math.round(newMonth * 100.0) / 100.0;
        Month currentMonth=currentYear.getMonth();
        model.addAttribute("currentMonth",currentMonth);
        model.addAttribute("currentMonthEarning",currentMonthEarning);

        LocalDate localStartDateYearly = LocalDate.of(currentYear.getYear(),Month.JANUARY,1);
        LocalDate localEndDateYearly = LocalDate.of(currentYear.getYear(),Month.DECEMBER,31);
        Date startDateYearly = java.sql.Date.valueOf(localStartDateYearly);
        Date endDateYearly = java.sql.Date.valueOf(localEndDateYearly);
        double newYear=dashBoardService.findCurrentMonthOrder(startDateYearly,endDateYearly);
        double currentYearlyEarning = Math.round(newYear * 100.0) / 100.0;
        int year=currentYear.getYear();
        model.addAttribute("currentYear",year);
        model.addAttribute("currentYearlyEarning",currentYearlyEarning);

        int totalOrders= (int) dashBoardService.findOrdersTotal();
        model.addAttribute("totalOrders",totalOrders);
        int totalPendingOrders= (int) dashBoardService.findOrdersPending();
        model.addAttribute("totalPendingOrders",totalPendingOrders);
        int progress=0;
        if(totalOrders!=0) {
            progress = (totalPendingOrders * 100) / totalOrders;
        }else{
            progress=0;
        }
        model.addAttribute("progress",progress);
        /*End Earning card*/

        /* Earning chart*/

        LocalDate now = LocalDate.now();
        int currentYears = now.getYear();
        int currentMonths = now.getMonthValue();

        List<Object[]> dailyEarnings = dashBoardService.retrieveDailyEarnings(currentYears, currentMonths);
        List<String> dailyLabels = dailyEarnings.stream().map(e -> e[0].toString()).collect(Collectors.toList());
        List<Double> dailyData = dailyEarnings.stream().map(e -> ((Number) e[1]).doubleValue()).collect(Collectors.toList());

        List<Object[]> monthlyEarnings = dashBoardService.retriveMontlyEarning(currentYears);
        List<String> monthlyLabels = monthlyEarnings.stream().map(e -> e[0].toString()).collect(Collectors.toList());
        List<Double> monthlyData = monthlyEarnings.stream().map(e -> ((Number) e[1]).doubleValue()).collect(Collectors.toList());

        List<Object[]> yearlyEarnings = dashBoardService.retriveYearlyEarning();
        List<String> yearlyLabels = yearlyEarnings.stream().map(e -> e[0].toString()).collect(Collectors.toList());
        List<Double> yearlyData = yearlyEarnings.stream().map(e -> ((Number) e[1]).doubleValue()).collect(Collectors.toList());

        model.addAttribute("dailyLabels", dailyLabels);
        model.addAttribute("dailyData", dailyData);
        model.addAttribute("monthlyLabels", monthlyLabels);
        model.addAttribute("monthlyData", monthlyData);
        model.addAttribute("yearlyLabels", yearlyLabels);
        model.addAttribute("yearlyData", yearlyData);

        /* Earning chart End */

        /* Pie chart Start*/
        List<Object[]> priceByPayMethod=dashBoardService.findTotalPricesByPayment();
        List<TotelPriceByPayment> totalPriceByPaymentList=new ArrayList<>();
        for(Object[] obj: priceByPayMethod){
            String payMethod= (String) obj[0];
            System.out.println("payMethod"+payMethod);
            Double amount= (Double) obj[1];
            TotelPriceByPayment totalPriceByPayment=new TotelPriceByPayment(payMethod,amount);
            totalPriceByPaymentList.add(totalPriceByPayment);
        }

        ObjectMapper objectMapper2=new ObjectMapper();
        String totalPriceByPayment = objectMapper2.writeValueAsString(totalPriceByPaymentList);
        model.addAttribute("totalPriceByPayment",totalPriceByPayment);

        //    Best Selling
        List<ProductSales> topSellingProducts = dashBoardService.getTopSellingProducts();
        model.addAttribute("topSellingProducts", topSellingProducts);

        List<CategorySales> topSellingCategories = dashBoardService.getTopSellingCategories();
        model.addAttribute("topSellingCategories", topSellingCategories);


        return "index";

    }



}
