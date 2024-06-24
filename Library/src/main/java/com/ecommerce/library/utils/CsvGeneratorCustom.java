package com.ecommerce.library.utils;

import com.ecommerce.library.dto.CustomEarning;
import com.ecommerce.library.dto.DailyEarning;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvGeneratorCustom {
    private static final String CSV_HEADER = "Start Date,End Date,Total Earning,Total Orders,Total Deduction,Delivered Orders,Cancelled Orders\n";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public String generateCsv(List<CustomEarning> orders) {
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("Date,Order ID,Product Name,Quantity,Total,Deduction,Paid,Daily Total,Weekly Grand Total\n");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd (EEEE)");
        DecimalFormat df = new DecimalFormat("#.00");

        String prevDate = "";
        Long prevOrderId = null;
        Double dailyTotal = 0.0;
        Double weeklyGrandTotal = 0.0;
        List<Double> dailyTotals = new ArrayList<>();

        for (int i = 0; i < orders.size(); i++) {
            CustomEarning order = orders.get(i);
            String dateFormatted = dateFormat.format(order.getOrderDate());

            // Check if date cell needs to be added
            if (!dateFormatted.equals(prevDate)) {
                // Add daily total row before adding a new date row
                if (!prevDate.isEmpty() && dailyTotal != 0.0) {
                    csvContent.append(",,,,,,,,")
                            .append(df.format(dailyTotal)).append("\n");
                    dailyTotals.add(dailyTotal); // Store the daily total
                    weeklyGrandTotal += dailyTotal;
                    dailyTotal = 0.0; // Reset daily total for next date
                }
                csvContent.append(dateFormatted).append(",");
                prevDate = dateFormatted;
            } else {
                csvContent.append(",");
            }

            // Check if order ID cell needs to be added
            if (!order.getOrderId().equals(prevOrderId)) {
                csvContent.append(order.getOrderId()).append(",");
                prevOrderId = order.getOrderId();
            } else {
                csvContent.append(",");
            }

            csvContent.append(order.getProductName()).append(",")
                    .append(order.getQuantity()).append(",")
                    .append(df.format(order.getTotalPrice())).append(","); // Format total price

            // Add deduction, shipping fee, and paid for the first occurrence of each order ID
            if (i == 0 || !order.getOrderId().equals(orders.get(i - 1).getOrderId())) {
                csvContent.append(df.format(order.getDeduction())).append(",") // Format deduction
                        .append(df.format(order.getTotalAmount())).append(","); // Format paid value
                dailyTotal += order.getTotalAmount(); // Add to daily total only for the first occurrence
            } else {
                // Skip adding deduction, shipping fee, and paid for subsequent occurrences of the same order ID
                csvContent.append(",, ,");
            }

            csvContent.append("\n");

            // Check if it's the last order or the date changes in the next iteration
            if (i == orders.size() - 1 || !dateFormat.format(orders.get(i + 1).getOrderDate()).equals(dateFormatted)) {
                // Add daily total row
                csvContent.append(",,,,,,,,")
                        .append(df.format(dailyTotal)).append("\n");
                dailyTotals.add(dailyTotal); // Store the daily total
                weeklyGrandTotal += dailyTotal;
                dailyTotal = 0.0; // Reset daily total for next date
            }
        }

        // Calculate the weekly grand total as the sum of all daily totals
        Double grandTotal = dailyTotals.stream().mapToDouble(Double::doubleValue).sum();

        // Add a row for the weekly grand total
        csvContent.append(",,,,,,,,,")
                .append(df.format(grandTotal)).append("\n");

        return csvContent.toString();
    }
}
