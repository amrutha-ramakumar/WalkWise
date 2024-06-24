//package com.ecommerce.library.utils;
//
//import com.ecommerce.library.dto.DailyEarning;
//import org.springframework.stereotype.Component;
//
//import java.text.SimpleDateFormat;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Component
//public class CsvGeneratorDaily {
//    private static final String CSV_HEADER = "Order ID,Product ID,Product Name,Description,Unit Price,Quantity,Total,Deduction,Shipping Fee,Payable\n";
//    private static final String DATE_FORMAT = "yyyy-MM-dd";
//
//    public String generateCsv(List<DailyEarning> orderDetails) {
//        StringBuilder csvContent = new StringBuilder();
//        csvContent.append(CSV_HEADER);
//
//        Map<Long, List<DailyEarning>> groupedOrders = orderDetails.stream()
//                .collect(Collectors.groupingBy(DailyEarning::getOrderId));
//
//        Double grandTotal = 0.0;
//
//        for (Map.Entry<Long, List<DailyEarning>> entry : groupedOrders.entrySet()) {
//            List<DailyEarning> details = entry.getValue();
//            DailyEarning firstDetail = details.get(0);
//
//            for (DailyEarning detail : details) {
//                csvContent.append(detail.getOrderId().toString()).append(",");
//                csvContent.append(detail.getProductId().toString()).append(",");
//                csvContent.append(detail.getProductName()).append(",");
//                csvContent.append(detail.getDescription()).append(",");
//                csvContent.append(detail.getUnitPrice().toString()).append(",");
//                csvContent.append(detail.getQuantity().toString()).append(",");
//                csvContent.append(detail.getTotal().toString()).append(",");
//
//                if (detail == firstDetail) {
//                    csvContent.append(detail.getDeduction().toString()).append(",");
//                    csvContent.append(detail.getShippingFee().toString()).append(",");
//                    csvContent.append(detail.getTotalAmount().toString()).append("\n");
//                } else {
//                    csvContent.append(",,,\n"); // Empty cells for merged columns
//                }
//            }
//
//            grandTotal += firstDetail.getTotalAmount();
//        }
//
//        // Add a row for the grand total
//        csvContent.append(",,,,,,,,Grand Total,").append(grandTotal.toString()).append("\n");
//
//        return csvContent.toString();
//    }
//}


package com.ecommerce.library.utils;

import com.ecommerce.library.dto.DailyEarning;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CsvGeneratorDaily {
    private static final String CSV_HEADER = "Order ID,Product ID,Product Name,Description,Unit Price,Quantity,Total,Deduction,Payable\n";

    public String generateCsv(List<DailyEarning> orderDetails) {
        StringBuilder csvContent = new StringBuilder();
        csvContent.append(CSV_HEADER);

        Map<Long, List<DailyEarning>> groupedOrders = orderDetails.stream()
                .collect(Collectors.groupingBy(DailyEarning::getOrderId));

        Double grandTotal = 0.0;

        for (Map.Entry<Long, List<DailyEarning>> entry : groupedOrders.entrySet()) {
            List<DailyEarning> details = entry.getValue();
            DailyEarning firstDetail = details.get(0);

            for (int i = 0; i < details.size(); i++) {
                DailyEarning detail = details.get(i);
                if (i == 0) {
                    // First row for this order
                    csvContent.append(detail.getOrderId().toString()).append(",");
                } else {
                    // Merged cell, leave empty
                    csvContent.append(",");
                }
                csvContent.append(detail.getProductId().toString()).append(",");
                csvContent.append(detail.getProductName()).append(",");
                csvContent.append(detail.getShortDescription()).append(",");
                csvContent.append(detail.getUnitPrice().toString()).append(",");
                csvContent.append(detail.getQuantity().toString()).append(",");
                csvContent.append(detail.getTotal().toString()).append(",");

                if (i == 0) {
                    // Only add these columns for the first product in the order
                    csvContent.append(detail.getDeduction().toString()).append(",");
                    csvContent.append(detail.getTotalAmount().toString()).append("\n");
                } else {
                    // Merged cells, leave empty
                    csvContent.append(",,,\n");
                }
            }

            grandTotal += firstDetail.getTotalAmount();
        }

        // Add a row for the grand total
        csvContent.append(",,,,,,,,Grand Total,").append(grandTotal.toString()).append("\n");

        return csvContent.toString();
    }
}
