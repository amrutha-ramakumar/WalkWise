//first one
package com.ecommerce.library.utils;

import com.ecommerce.library.dto.DailyEarning;
import com.lowagie.text.*;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PdfGenerator {

    private List<DailyEarning> orders;

    public void generateDailyOrderPdf(List<DailyEarning> orders, HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        Font fontTiltle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        fontTiltle.setSize(20);
        Paragraph paragraph = new Paragraph("Daily report", fontTiltle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));


        PdfPTable table = new PdfPTable(8); // 10 columns
        table.setWidthPercentage(100f);
        table.setWidths(new int[]{1,1, 3, 1, 1, 1,1, 1});
        table.setSpacingBefore(10);
        table.addCell("Order ID");
        table.addCell("Product ID");
        table.addCell("Product Name");
//            table.addCell("Description");
        table.addCell("Unit Price");
        table.addCell("Quantity");
        table.addCell("Total");
        table.addCell("Deduction");
//        table.addCell("Shipping Fee");
        table.addCell("Payable");

        Map<Long, List<DailyEarning>> groupedOrders = orders.stream()
                .collect(Collectors.groupingBy(DailyEarning::getOrderId));

        Double grandTotal = 0.0;

        for (Map.Entry<Long, List<DailyEarning>> entry : groupedOrders.entrySet()) {
            List<DailyEarning> orderDetails = entry.getValue();
            DailyEarning firstOrder = orderDetails.get(0);

            int rowCount = orderDetails.size();
            PdfPCell orderIdCell = new PdfPCell(new Phrase(firstOrder.getOrderId().toString()));
            orderIdCell.setRowspan(rowCount);
            table.addCell(orderIdCell);

            for (DailyEarning order : orderDetails) {
                table.addCell(order.getProductId().toString());
                table.addCell(order.getProductName());
//                    table.addCell(order.getDescription());
                table.addCell(order.getUnitPrice().toString());
                table.addCell(order.getQuantity().toString());
                table.addCell(order.getTotal().toString());

                if (order == firstOrder) {
                    PdfPCell deductionCell = new PdfPCell(new Phrase(order.getDeduction().toString()));
                    deductionCell.setRowspan(rowCount);
                    table.addCell(deductionCell);

//                    PdfPCell shippingFeeCell = new PdfPCell(new Phrase(order.getShippingFee().toString()));
//                    shippingFeeCell.setRowspan(rowCount);
//                    table.addCell(shippingFeeCell);

                    PdfPCell payableCell = new PdfPCell(new Phrase(order.getTotalAmount().toString()));
                    payableCell.setRowspan(rowCount);
                    table.addCell(payableCell);
                }
            }

            grandTotal += firstOrder.getTotalAmount();
        }

        // Add a row for the grand total
        PdfPCell cell = new PdfPCell(new Phrase("Grand Total"));
        cell.setColspan(7); // Span across 9 columns
        table.addCell(cell);
        table.addCell(grandTotal.toString());

        document.add(table);
        document.close();
    }
}


//count of product as overall sales count
//package com.ecommerce.library.utils;
//
//import com.ecommerce.library.dto.DailyEarning;
//import com.lowagie.text.*;
//import com.lowagie.text.pdf.PdfPCell;
//import com.lowagie.text.pdf.PdfPTable;
//import com.lowagie.text.pdf.PdfWriter;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//public class PdfGenerator {
//
//    private List<DailyEarning> orders;
//
//    public void generateDailyOrderPdf(List<DailyEarning> orders, HttpServletResponse response) throws DocumentException, IOException {
//        Document document = new Document();
//        PdfWriter.getInstance(document, response.getOutputStream());
//        document.open();
//        Font fontTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
//        fontTitle.setSize(20);
//        Paragraph paragraph = new Paragraph("Daily Report", fontTitle);
//        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
//        document.add(paragraph);
//        document.add(new Paragraph(" "));
//        document.add(new Paragraph(" "));
//
//        PdfPTable table = new PdfPTable(8); // 8 columns
//        table.setWidthPercentage(100f);
//        table.setWidths(new int[]{1, 1, 3, 1, 1, 1, 1, 1});
//        table.setSpacingBefore(10);
//        table.addCell("Order ID");
//        table.addCell("Product ID");
//        table.addCell("Product Name");
//        table.addCell("Unit Price");
//        table.addCell("Quantity");
//        table.addCell("Total");
//        table.addCell("Deduction");
//        table.addCell("Payable");
//
//        Map<Long, List<DailyEarning>> groupedOrders = orders.stream()
//                .collect(Collectors.groupingBy(DailyEarning::getOrderId));
//
//        Double grandTotal = 0.0;
//        Double totalDiscount = 0.0;
//        int totalSalesCount = 0;
//
//        for (Map.Entry<Long, List<DailyEarning>> entry : groupedOrders.entrySet()) {
//            List<DailyEarning> orderDetails = entry.getValue();
//            DailyEarning firstOrder = orderDetails.get(0);
//
//            int rowCount = orderDetails.size();
//            PdfPCell orderIdCell = new PdfPCell(new Phrase(firstOrder.getOrderId().toString()));
//            orderIdCell.setRowspan(rowCount);
//            table.addCell(orderIdCell);
//
//            for (DailyEarning order : orderDetails) {
//                table.addCell(order.getProductId().toString());
//                table.addCell(order.getProductName());
//                table.addCell(order.getUnitPrice().toString());
//                table.addCell(order.getQuantity().toString());
//                table.addCell(order.getTotal().toString());
//
//                if (order == firstOrder) {
//                    PdfPCell deductionCell = new PdfPCell(new Phrase(order.getDeduction().toString()));
//                    deductionCell.setRowspan(rowCount);
//                    table.addCell(deductionCell);
//
//                    PdfPCell payableCell = new PdfPCell(new Phrase(order.getTotalAmount().toString()));
//                    payableCell.setRowspan(rowCount);
//                    table.addCell(payableCell);
//                }
//            }
//
//            grandTotal += firstOrder.getTotalAmount();
//            totalDiscount += firstOrder.getDeduction();
//            totalSalesCount += rowCount;
//        }
//
//        // Add a row for the grand total
//        PdfPCell cell = new PdfPCell(new Phrase("Grand Total"));
//        cell.setColspan(7);
//        table.addCell(cell);
//        table.addCell(grandTotal.toString());
//
//        document.add(table);
//
//        // Create a table for overall totals
//        PdfPTable summaryTable = new PdfPTable(2);
//        summaryTable.setWidthPercentage(50f);
//        summaryTable.setSpacingBefore(20);
//
//        // Add cells for the summary
//        summaryTable.addCell("Overall Sales Count");
//        summaryTable.addCell(String.valueOf(totalSalesCount));
//
//        summaryTable.addCell("Overall Order Amount");
//        summaryTable.addCell(String.valueOf(grandTotal));
//
//        summaryTable.addCell("Overall Discount");
//        summaryTable.addCell(String.valueOf(totalDiscount));
//
//        document.add(summaryTable);
//
//        document.close();
//    }
//}

//order count as overall sales count
//package com.ecommerce.library.utils;
//
//import com.ecommerce.library.dto.DailyEarning;
//import com.lowagie.text.*;
//import com.lowagie.text.pdf.PdfPCell;
//import com.lowagie.text.pdf.PdfPTable;
//import com.lowagie.text.pdf.PdfWriter;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//public class PdfGenerator {
//
//    private List<DailyEarning> orders;
//
//    public void generateDailyOrderPdf(List<DailyEarning> orders, HttpServletResponse response) throws DocumentException, IOException {
//        Document document = new Document();
//        PdfWriter.getInstance(document, response.getOutputStream());
//        document.open();
//        Font fontTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
//        fontTitle.setSize(20);
//        Paragraph paragraph = new Paragraph("Daily Report", fontTitle);
//        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
//        document.add(paragraph);
//        document.add(new Paragraph(" "));
//        document.add(new Paragraph(" "));
//
//        PdfPTable table = new PdfPTable(8); // 8 columns
//        table.setWidthPercentage(100f);
//        table.setWidths(new int[]{1, 1, 3, 1, 1, 1, 1, 1});
//        table.setSpacingBefore(10);
//        table.addCell("Order ID");
//        table.addCell("Product ID");
//        table.addCell("Product Name");
//        table.addCell("Unit Price");
//        table.addCell("Quantity");
//        table.addCell("Total");
//        table.addCell("Deduction");
//        table.addCell("Payable");
//
//        Map<Long, List<DailyEarning>> groupedOrders = orders.stream()
//                .collect(Collectors.groupingBy(DailyEarning::getOrderId));
//
//        Double grandTotal = 0.0;
//        Double totalDiscount = 0.0;
//        int totalOrderCount = groupedOrders.size();  // Count of unique orders
//
//        for (Map.Entry<Long, List<DailyEarning>> entry : groupedOrders.entrySet()) {
//            List<DailyEarning> orderDetails = entry.getValue();
//            DailyEarning firstOrder = orderDetails.get(0);
//
//            int rowCount = orderDetails.size();
//            PdfPCell orderIdCell = new PdfPCell(new Phrase(firstOrder.getOrderId().toString()));
//            orderIdCell.setRowspan(rowCount);
//            table.addCell(orderIdCell);
//
//            for (DailyEarning order : orderDetails) {
//                table.addCell(order.getProductId().toString());
//                table.addCell(order.getProductName());
//                table.addCell(order.getUnitPrice().toString());
//                table.addCell(order.getQuantity().toString());
//                table.addCell(order.getTotal().toString());
//
//                if (order == firstOrder) {
//                    PdfPCell deductionCell = new PdfPCell(new Phrase(order.getDeduction().toString()));
//                    deductionCell.setRowspan(rowCount);
//                    table.addCell(deductionCell);
//
//                    PdfPCell payableCell = new PdfPCell(new Phrase(order.getTotalAmount().toString()));
//                    payableCell.setRowspan(rowCount);
//                    table.addCell(payableCell);
//                }
//            }
//
//            grandTotal += firstOrder.getTotalAmount();
//            totalDiscount += firstOrder.getDeduction();
//        }
//
//        // Add a row for the grand total
//        PdfPCell cell = new PdfPCell(new Phrase("Grand Total"));
//        cell.setColspan(7);
//        table.addCell(cell);
//        table.addCell(grandTotal.toString());
//
//        document.add(table);
//
//        // Create a table for overall totals
//        PdfPTable summaryTable = new PdfPTable(2);
//        summaryTable.setWidthPercentage(50f);
//        summaryTable.setSpacingBefore(20);
//
//        // Add cells for the summary
//        summaryTable.addCell("Overall Sales Count");
//        summaryTable.addCell(String.valueOf(totalOrderCount));
//
//        summaryTable.addCell("Overall Order Amount");
//        summaryTable.addCell(String.valueOf(grandTotal));
//
//        summaryTable.addCell("Overall Discount");
//        summaryTable.addCell(String.valueOf(totalDiscount));
//
//        document.add(summaryTable);
//
//        document.close();
//    }
//}


