package com.ecommerce.library.utils;

import com.ecommerce.library.dto.YearlyEarning;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Component
public class CsvGeneratorYearly {
    public String generateCsv(List<YearlyEarning> orders) {
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("Category,Product,Ordered Quantity,Ordered Total Price,Category Total,Grand Total\n");

        DecimalFormat df = new DecimalFormat("#.00");

        String prevCategory = "";
        Double categoryTotal = 0.0;
        Double grandTotal = 0.0;

        for (YearlyEarning order : orders) {
            String currentCategory = order.getCategoryName();

            if (!currentCategory.equals(prevCategory)) {
                if (!prevCategory.isEmpty()) {
                    csvContent.append(",,,,").append(df.format(categoryTotal)).append("\n");
                    categoryTotal = 0.0;
                }

                csvContent.append(order.getCategoryName()).append(",");
                prevCategory = currentCategory;
            } else {
                csvContent.append(",");
            }

            csvContent.append(order.getProductName()).append(",")
                    .append(order.getOrderedQuantity()).append(",")
                    .append(df.format(order.getOrderedTotalPrice())).append("\n");

            categoryTotal += order.getOrderedTotalPrice();
            grandTotal += order.getOrderedTotalPrice();
        }

        if (!prevCategory.isEmpty()) {
            csvContent.append(",,,,").append(df.format(categoryTotal)).append("\n");
        }

        csvContent.append(",,,,,".repeat(1)).append(df.format(grandTotal)).append("\n");

        return csvContent.toString();
    }
}