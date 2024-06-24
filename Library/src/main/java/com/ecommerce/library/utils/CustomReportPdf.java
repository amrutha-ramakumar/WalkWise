package com.ecommerce.library.utils;
import com.ecommerce.library.dto.*;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Getter
@Setter

@NoArgsConstructor
public class CustomReportPdf {
    public void generate(List<CustomEarning> orders, HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Title
        Font fontTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN, 20, Font.BOLD);
        Paragraph titleParagraph = new Paragraph("Sales Report", fontTitle);
        titleParagraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(titleParagraph);

        // Add space
        document.add(new Paragraph(" "));

        // Create table with 8 columns
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Add table headers
        table.addCell("Date");
        table.addCell("Order ID");
        table.addCell("Product Name");
        table.addCell("Quantity");
        table.addCell("Total");
        table.addCell("Deduction");
//        table.addCell("Shipping Fee");
        table.addCell("Paid");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd (EEEE)");
        DecimalFormat df = new DecimalFormat("#.00");

        String prevDate = "";
        Long prevOrderId = null;
        Double dailyTotal = 0.0;
        Double weeklyGrandTotal = 0.0;

        for (int i = 0; i < orders.size(); i++) {
            CustomEarning order = orders.get(i);
            String dateFormatted = dateFormat.format(order.getOrderDate());

            // Check if date cell needs to be added
            if (!dateFormatted.equals(prevDate)) {
                // Add daily total row before adding a new date row
                if (!prevDate.isEmpty() && dailyTotal != 0.0) {
                    PdfPCell dailyTotalCell = new PdfPCell(new Phrase("Daily Total"));
                    dailyTotalCell.setColspan(7);
                    table.addCell(dailyTotalCell);
                    table.addCell(df.format(dailyTotal));
                    weeklyGrandTotal += dailyTotal;
                    dailyTotal = 0.0;
                }

                PdfPCell dateCell = new PdfPCell(new Phrase(dateFormatted));
                dateCell.setRowspan(getRowSpanForDate(orders, order.getOrderDate()));
                table.addCell(dateCell);
                prevDate = dateFormatted;
            }

            // Check if order ID cell needs to be added
            if (!order.getOrderId().equals(prevOrderId)) {
                PdfPCell orderIdCell = new PdfPCell(new Phrase(order.getOrderId().toString()));
                orderIdCell.setRowspan(getRowSpanForOrderId(orders, order.getOrderId()));
                table.addCell(orderIdCell);
                prevOrderId = order.getOrderId();
            }

            // Add product details
            table.addCell(order.getProductName());
            table.addCell(order.getQuantity().toString());
            table.addCell(order.getTotalPrice().toString());

            // Add deduction, shipping fee, and paid for the first occurrence of each order ID
            if (i == 0 || !order.getOrderId().equals(orders.get(i - 1).getOrderId())) {
                table.addCell(df.format(order.getDeduction())); // Format deduction
//                table.addCell(df.format(order.getShippingFee())); // Format shipping fee
                table.addCell(df.format(order.getTotalAmount())); // Format paid value
                dailyTotal += order.getTotalAmount();
            } else {
                table.addCell("");
//                table.addCell("");
                table.addCell("");
            }

            // Check if it's the last order or the date changes in the next iteration
            if (i == orders.size() - 1 || !dateFormat.format(orders.get(i + 1).getOrderDate()).equals(dateFormatted)) {
                PdfPCell dailyTotalCell = new PdfPCell(new Phrase("Daily Total"));
                dailyTotalCell.setColspan(6);
                table.addCell(dailyTotalCell);
                table.addCell(df.format(dailyTotal));
                weeklyGrandTotal += dailyTotal;
                dailyTotal = 0.0;
            }
        }

        // Add a row for the weekly grand total
        PdfPCell cell = new PdfPCell(new Phrase("Grand Total"));
        cell.setColspan(6);
        table.addCell(cell);
        table.addCell(df.format(weeklyGrandTotal));

        // Add table to the document
        document.add(table);
        document.close();
    }

    // Helper method to calculate rowspan for a specific date
    private int getRowSpanForDate(List<CustomEarning> orders, Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String targetDate = dateFormat.format(date);
        int rowSpan = 0;

        for (CustomEarning order : orders) {
            if (dateFormat.format(order.getOrderDate()).equals(targetDate)) {
                rowSpan++;
            }
        }

        return rowSpan;
    }

    // Helper method to calculate rowspan for a specific order ID
    private int getRowSpanForOrderId(List<CustomEarning> orders, Long orderId) {
        int rowSpan = 0;

        for (CustomEarning order : orders) {
            if (order.getOrderId().equals(orderId)) {
                rowSpan++;
            }
        }

        return rowSpan;
    }

}
