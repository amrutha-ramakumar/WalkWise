package com.ecommerce.library.utils;

import com.ecommerce.library.dto.WeeklyEarnings;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class CsvGeneratorWeekly {

    private static final String CSV_HEADER = "Date,Order ID,Product Name,Quantity,Total,Deduction,Paid,Daily Total,Weekly Grand Total\n";
    private static final String DATE_FORMAT = "yyyy-MM-dd (EEEE)";

    public String generateCsv(List<WeeklyEarnings> orders) {
        StringBuilder csvContent = new StringBuilder();
        csvContent.append(CSV_HEADER);

        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        DecimalFormat df = new DecimalFormat("#.00"); // Decimal format for rounding

        String prevDate = "";
        Long prevOrderId = null;
        Double dailyTotal = 0.0;
        Double weeklyGrandTotal = 0.0;

        for (int i = 0; i < orders.size(); i++) {
            WeeklyEarnings order = orders.get(i);
            String dateFormatted = dateFormat.format(order.getOrderDate());

            // Check if date cell needs to be added
            if (!dateFormatted.equals(prevDate)) {
                // Add daily total row before adding a new date row
                if (!prevDate.isEmpty() && dailyTotal != 0.0) {
                    csvContent.append(",,,,,,,,")
                            .append(df.format(dailyTotal)).append("\n");
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
                    .append(order.getTotalPrice()).append(",");

            // Add deduction, shipping fee, and paid for the first occurrence of each order ID
            if (i == 0 || !order.getOrderId().equals(orders.get(i - 1).getOrderId())) {
                csvContent.append(order.getDeduction()).append(",")
                        .append(order.getTotalAmount()).append(",");
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
                weeklyGrandTotal += dailyTotal;
                dailyTotal = 0.0; // Reset daily total for next date
            }
        }

        // Add a row for the weekly grand total
        csvContent.append(",,,,,,,,,")
                .append(df.format(weeklyGrandTotal)).append("\n");

        return csvContent.toString();
    }
}