//first one
package com.ecommerce.library.utils;
import com.ecommerce.library.dto.YearlyEarning;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class YearlyReportPdf {
    public void generate(List<YearlyEarning> orders, HttpServletResponse response)
            throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font fontTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN, 20, Font.BOLD);
        Paragraph titleParagraph = new Paragraph("Yearly Sales Report", fontTitle);
        titleParagraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(titleParagraph);

        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        table.addCell("Category");
        table.addCell("Product");
        table.addCell("Ordered Quantity");
        table.addCell("Ordered Total Price");

        DecimalFormat df = new DecimalFormat("#.00");

        String prevCategory = "";
        Double categoryTotal = 0.0;
        Double grandTotal = 0.0;
        int categoryStartIndex = -1;

        for (int i = 0; i < orders.size(); i++) {
            YearlyEarning order = orders.get(i);
            String currentCategory = order.getCategoryName();

            if (!currentCategory.equals(prevCategory)) {
                if (categoryStartIndex != -1) {
                    PdfPCell categoryTotalCell = new PdfPCell(new Phrase("Category Total"));
                    categoryTotalCell.setColspan(3);
                    table.addCell(categoryTotalCell);
                    table.addCell(df.format(categoryTotal));
                    categoryTotal = 0.0;
                }

                PdfPCell categoryCell = new PdfPCell(new Phrase(order.getCategoryName()));
                categoryStartIndex = i;
                int rowSpan = getRowSpanForCategory(orders, currentCategory);
                categoryCell.setRowspan(rowSpan);
                table.addCell(categoryCell);

                prevCategory = currentCategory;
            }

            table.addCell(order.getProductName());
            table.addCell(order.getOrderedQuantity().toString());
            table.addCell(df.format(order.getOrderedTotalPrice()));

            categoryTotal += order.getOrderedTotalPrice();
            grandTotal += order.getOrderedTotalPrice();
        }

        if (categoryStartIndex != -1) {
            PdfPCell categoryTotalCell = new PdfPCell(new Phrase("Category Total"));
            categoryTotalCell.setColspan(3);
            table.addCell(categoryTotalCell);
            table.addCell(df.format(categoryTotal));
        }

        PdfPCell grandTotalCell = new PdfPCell(new Phrase("Grand Total"));
        grandTotalCell.setColspan(3);
        table.addCell(grandTotalCell);
        table.addCell(df.format(grandTotal));

        document.add(table);
        document.close();
    }

    // Helper method to calculate rowspan for a specific category
    private int getRowSpanForCategory(List<YearlyEarning> orders, String category) {
        int rowSpan = 0;
        for (YearlyEarning order : orders) {
            if (order.getCategoryName().equals(category)) {
                rowSpan++;
            }
        }
        return rowSpan;
    }
}



//order amount
//package com.ecommerce.library.utils;
//
//import com.ecommerce.library.dto.YearlyEarning;
//import com.lowagie.text.*;
//import com.lowagie.text.pdf.PdfPCell;
//import com.lowagie.text.pdf.PdfPTable;
//import com.lowagie.text.pdf.PdfWriter;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.io.IOException;
//import java.text.DecimalFormat;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Getter
//@Setter
//@NoArgsConstructor
//public class YearlyReportPdf {
//    public void generate(List<YearlyEarning> orders, HttpServletResponse response)
//            throws DocumentException, IOException {
//        Document document = new Document();
//        PdfWriter.getInstance(document, response.getOutputStream());
//        document.open();
//
//        Font fontTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN, 20, Font.BOLD);
//        Paragraph titleParagraph = new Paragraph("Yearly Sales Report", fontTitle);
//        titleParagraph.setAlignment(Paragraph.ALIGN_CENTER);
//        document.add(titleParagraph);
//
//        document.add(new Paragraph(" "));
//
//        PdfPTable table = new PdfPTable(4);
//        table.setWidthPercentage(100);
//        table.setSpacingBefore(10f);
//        table.setSpacingAfter(10f);
//
//        table.addCell("Category");
//        table.addCell("Product");
//        table.addCell("Ordered Quantity");
//        table.addCell("Ordered Total Price");
//
//        DecimalFormat df = new DecimalFormat("#.00");
//
//        String prevCategory = "";
//        Double categoryTotal = 0.0;
//        Double grandTotal = 0.0;
//        int categoryStartIndex = -1;
//
//        Map<String, Integer> categorySalesCount = new HashMap<>(); // To track sales count per category
//        Double overallOrderAmount = 0.0;
//
//        for (int i = 0; i < orders.size(); i++) {
//            YearlyEarning order = orders.get(i);
//            String currentCategory = order.getCategoryName();
//
//            if (!currentCategory.equals(prevCategory)) {
//                if (categoryStartIndex != -1) {
//                    PdfPCell categoryTotalCell = new PdfPCell(new Phrase("Category Total"));
//                    categoryTotalCell.setColspan(3);
//                    table.addCell(categoryTotalCell);
//                    table.addCell(df.format(categoryTotal));
//                    categoryTotal = 0.0;
//                }
//
//                PdfPCell categoryCell = new PdfPCell(new Phrase(order.getCategoryName()));
//                categoryStartIndex = i;
//                int rowSpan = getRowSpanForCategory(orders, currentCategory);
//                categoryCell.setRowspan(rowSpan);
//                table.addCell(categoryCell);
//
//                prevCategory = currentCategory;
//            }
//
//            table.addCell(order.getProductName());
//            table.addCell(order.getOrderedQuantity().toString());
//            table.addCell(df.format(order.getOrderedTotalPrice()));
//
//            categoryTotal += order.getOrderedTotalPrice();
//            grandTotal += order.getOrderedTotalPrice();
//            overallOrderAmount += order.getOrderedTotalPrice();
//
//            // Increment sales count for the current category
//            categorySalesCount.put(currentCategory, categorySalesCount.getOrDefault(currentCategory, 0) + 1);
//        }
//
//        if (categoryStartIndex != -1) {
//            PdfPCell categoryTotalCell = new PdfPCell(new Phrase("Category Total"));
//            categoryTotalCell.setColspan(3);
//            table.addCell(categoryTotalCell);
//            table.addCell(df.format(categoryTotal));
//        }
//
//        PdfPCell grandTotalCell = new PdfPCell(new Phrase("Grand Total"));
//        grandTotalCell.setColspan(3);
//        table.addCell(grandTotalCell);
//        table.addCell(df.format(grandTotal));
//
//        document.add(table);
//
//        document.add(new Paragraph(" ")); // Add space before the summary table
//
//        // Create summary table
//        PdfPTable summaryTable = new PdfPTable(2);
//        summaryTable.setWidthPercentage(50f);
//        summaryTable.setSpacingBefore(20f);
//
//        // Add sales count per category to the summary table
//        for (Map.Entry<String, Integer> entry : categorySalesCount.entrySet()) {
//            summaryTable.addCell(entry.getKey() + " Sales Count");
//            summaryTable.addCell(entry.getValue().toString());
//        }
//
//        // Add overall count, overall deduction, and overall order amount to the summary table
//        summaryTable.addCell("Overall Count");
//        summaryTable.addCell(String.valueOf(orders.size()));
//        summaryTable.addCell("Overall Order Amount");
//        summaryTable.addCell(df.format(overallOrderAmount));
//
//        document.add(summaryTable);
//
//        document.close();
//    }
//
//    // Helper method to calculate rowspan for a specific category
//    private int getRowSpanForCategory(List<YearlyEarning> orders, String category) {
//        int rowSpan = 0;
//        for (YearlyEarning order : orders) {
//            if (order.getCategoryName().equals(category)) {
//                rowSpan++;
//            }
//        }
//        return rowSpan;
//    }
//}







//count of category seperate
//package com.ecommerce.library.utils;
//
//import com.ecommerce.library.dto.YearlyEarning;
//import com.lowagie.text.*;
//import com.lowagie.text.pdf.PdfPCell;
//import com.lowagie.text.pdf.PdfPTable;
//import com.lowagie.text.pdf.PdfWriter;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.io.IOException;
//import java.text.DecimalFormat;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Getter
//@Setter
//@NoArgsConstructor
//public class YearlyReportPdf {
//    public void generate(List<YearlyEarning> orders, HttpServletResponse response)
//            throws DocumentException, IOException {
//        Document document = new Document();
//        PdfWriter.getInstance(document, response.getOutputStream());
//        document.open();
//
//        Font fontTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN, 20, Font.BOLD);
//        Paragraph titleParagraph = new Paragraph("Yearly Sales Report", fontTitle);
//        titleParagraph.setAlignment(Paragraph.ALIGN_CENTER);
//        document.add(titleParagraph);
//
//        document.add(new Paragraph(" "));
//
//        PdfPTable table = new PdfPTable(4);
//        table.setWidthPercentage(100);
//        table.setSpacingBefore(10f);
//        table.setSpacingAfter(10f);
//
//        table.addCell("Category");
//        table.addCell("Product");
//        table.addCell("Ordered Quantity");
//        table.addCell("Ordered Total Price");
//
//        DecimalFormat df = new DecimalFormat("#.00");
//
//        String prevCategory = "";
//        Double categoryTotal = 0.0;
//        Double grandTotal = 0.0;
//        int categoryStartIndex = -1;
//
//        Map<String, Integer> categorySalesCount = new HashMap<>(); // To track sales count per category
//
//        for (int i = 0; i < orders.size(); i++) {
//            YearlyEarning order = orders.get(i);
//            String currentCategory = order.getCategoryName();
//
//            if (!currentCategory.equals(prevCategory)) {
//                if (categoryStartIndex != -1) {
//                    PdfPCell categoryTotalCell = new PdfPCell(new Phrase("Category Total"));
//                    categoryTotalCell.setColspan(3);
//                    table.addCell(categoryTotalCell);
//                    table.addCell(df.format(categoryTotal));
//                    categoryTotal = 0.0;
//                }
//
//                PdfPCell categoryCell = new PdfPCell(new Phrase(order.getCategoryName()));
//                categoryStartIndex = i;
//                int rowSpan = getRowSpanForCategory(orders, currentCategory);
//                categoryCell.setRowspan(rowSpan);
//                table.addCell(categoryCell);
//
//                prevCategory = currentCategory;
//            }
//
//            table.addCell(order.getProductName());
//            table.addCell(order.getOrderedQuantity().toString());
//            table.addCell(df.format(order.getOrderedTotalPrice()));
//
//            categoryTotal += order.getOrderedTotalPrice();
//            grandTotal += order.getOrderedTotalPrice();
//
//            // Increment sales count for the current category
//            categorySalesCount.put(currentCategory, categorySalesCount.getOrDefault(currentCategory, 0) + 1);
//        }
//
//        if (categoryStartIndex != -1) {
//            PdfPCell categoryTotalCell = new PdfPCell(new Phrase("Category Total"));
//            categoryTotalCell.setColspan(3);
//            table.addCell(categoryTotalCell);
//            table.addCell(df.format(categoryTotal));
//        }
//
//        PdfPCell grandTotalCell = new PdfPCell(new Phrase("Grand Total"));
//        grandTotalCell.setColspan(3);
//        table.addCell(grandTotalCell);
//        table.addCell(df.format(grandTotal));
//
//        document.add(table);
//
//        document.add(new Paragraph(" ")); // Add space before the summary table
//
//        // Create summary table
//        PdfPTable summaryTable = new PdfPTable(2);
//        summaryTable.setWidthPercentage(50f);
//        summaryTable.setSpacingBefore(20f);
//
//        // Add sales count per category to the summary table
//        for (Map.Entry<String, Integer> entry : categorySalesCount.entrySet()) {
//            summaryTable.addCell(entry.getKey() + " Sales Count");
//            summaryTable.addCell(entry.getValue().toString());
//        }
//
//        document.add(summaryTable);
//
//        document.close();
//    }
//
//    // Helper method to calculate rowspan for a specific category
//    private int getRowSpanForCategory(List<YearlyEarning> orders, String category) {
//        int rowSpan = 0;
//        for (YearlyEarning order : orders) {
//            if (order.getCategoryName().equals(category)) {
//                rowSpan++;
//            }
//        }
//        return rowSpan;
//    }
//}





