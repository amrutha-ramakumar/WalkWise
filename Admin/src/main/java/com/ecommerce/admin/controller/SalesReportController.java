package com.ecommerce.admin.controller;


import com.ecommerce.library.dto.*;
import com.ecommerce.library.service.OrderService;
import com.ecommerce.library.service.PdfService;
import com.ecommerce.library.utils.*;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
    public class SalesReportController {
    private PdfService pdfService;
    private OrderService orderService;
    private CsvGeneratorDaily csvGeneratorDaily;
    private CsvGeneratorYearly csvGeneratorYearly;
    private CsvGeneratorWeekly csvGeneratorWeekly;

    private CsvGeneratorCustom csvGeneratorCustom;

    @Autowired
    public SalesReportController(PdfService pdfService,OrderService orderService
                         ,CsvGeneratorDaily csvGeneratorDaily
                         ,CsvGeneratorYearly csvGeneratorYearly
                         ,CsvGeneratorWeekly csvGeneratorWeekly
                         ,CsvGeneratorCustom csvGeneratorCustom
                         ) {
        this.pdfService = pdfService;
        this.orderService=orderService;
        this.csvGeneratorDaily=csvGeneratorDaily;
        this.csvGeneratorYearly=csvGeneratorYearly;
        this.csvGeneratorWeekly=csvGeneratorWeekly;
        this.csvGeneratorCustom=csvGeneratorCustom;

    }
    @GetMapping("/pdfReport")
    public void generatePdf(@ModelAttribute("report") OrderDto orderDto,
                            @RequestParam(value = "startDate", required = false) String startDate,
                            @RequestParam(value = "endDate", required = false) String endDate,
                            HttpServletResponse response) throws DocumentException, IOException {
        String value=orderDto.getPdfReport();
        if(value.equals("daily")){
            response.setContentType("application/pdf");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date());
            String headerkey = "Content-Disposition";
            String headervalue = "attachment; filename=pdf_" + currentDateTime + ".pdf";
            response.setHeader(headerkey, headervalue);
            Date date=new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            List<DailyEarning> orders=orderService.getCurrentDayOrders();
            PdfGenerator pdfGenerator=new PdfGenerator();
            pdfGenerator.setOrders(orders);
            pdfGenerator.generateDailyOrderPdf(orders,response);

        }
        if(value.equals("weekly")){
            response.setContentType("application/pdf");
            DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD:HH:MM:SS");
            String currentDateTime = dateFormat.format(new Date());
            System.out.println(currentDateTime);
            String headerkey = "Content-Disposition";
            String headervalue = "attachment; filename=pdf_" + currentDateTime + ".pdf";
            response.setHeader(headerkey, headervalue);
            Date date=new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);

            WeeklyPdfGenerator pdfGenerator=new WeeklyPdfGenerator();

            List<WeeklyEarnings> orders = orderService.getLastWeekOrders();
            pdfGenerator.generate(orders,response);
        }
        if(value.equals("yearly")){
            response.setContentType("application/pdf");
            DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD:HH:MM:SS");
            String currentDateTime = dateFormat.format(new Date());
            System.out.println(currentDateTime);
            String headerkey = "Content-Disposition";
            String headervalue = "attachment; filename=pdf_" + currentDateTime + ".pdf";
            response.setHeader(headerkey, headervalue);
            Date date=new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            List<YearlyEarning> yearlyEarnings = orderService.getYearlyOrders();
            YearlyReportPdf yearlyReportPdf = new YearlyReportPdf();
            yearlyReportPdf.generate(yearlyEarnings,response);
        }
        if(value.equals("custom")){
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date());
            System.out.println(currentDateTime);
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=pdf_" + currentDateTime + ".pdf";
            response.setHeader(headerKey, headerValue);
            try {
                Date parsedStartDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
                Date parsedEndDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(parsedEndDate);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                Date adjustedEndDate = calendar.getTime();

                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Date endOfDayEndDate = calendar.getTime();

                List<CustomEarning> customEarnings = orderService.getOrdersWithinDateRange(parsedStartDate, endOfDayEndDate);
                CustomReportPdf customReportPdf = new CustomReportPdf();
                customReportPdf.generate(customEarnings,response);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    @GetMapping("/csvReport")
    public ResponseEntity<byte[]> generateCsvFile(@ModelAttribute("report") OrderDto orderDto,
                                                  @RequestParam(value = "csvStartDate", required = false) String startDate,
                                                  @RequestParam(value = "csvEndDate", required = false) String endDate) {
        String value=orderDto.getCsvReport();
        System.out.println("Start ="+startDate);
        System.out.println("End ="+endDate);
        if(value.equals("daily")){
            List<DailyEarning> dailyEarnings = orderService.getCurrentDayOrders();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "dailyreport.csv");

            byte[] csvBytes = csvGeneratorDaily.generateCsv(dailyEarnings).getBytes();

            return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
        }
        else if(value.equals(("yearly"))){
            List<YearlyEarning> monthlyearnings=orderService.getYearlyOrders();
            HttpHeaders headers=new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment","monthlyReport.csv");
            byte[] csvBytes=csvGeneratorYearly.generateCsv(monthlyearnings).getBytes();
            return new ResponseEntity<>(csvBytes,headers,HttpStatus.OK);
        }
        else if(value.equals("weekly")){
            List<WeeklyEarnings> weeklyEarnings=orderService.getLastWeekOrders();
            HttpHeaders headers=new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment","weeklyReport.csv");
            byte[] csvBytes=csvGeneratorWeekly.generateCsv(weeklyEarnings).getBytes();
            return new ResponseEntity<>(csvBytes,headers,HttpStatus.OK);

        }
        else{
            try {
                Date parsedStartDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
                Date parsedEndDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
                System.out.println("Start ="+parsedStartDate);
                System.out.println("End ="+parsedEndDate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(parsedEndDate);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                Date adjustedEndDate = calendar.getTime();

                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Date endOfDayEndDate = calendar.getTime();
                List<CustomEarning> customEarnings=orderService.getOrdersWithinDateRange(parsedStartDate, endOfDayEndDate);
                HttpHeaders headers=new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment","weeklyReport.csv");
                byte[] csvBytes=csvGeneratorCustom.generateCsv(customEarnings).getBytes();
                return new ResponseEntity<>(csvBytes,headers,HttpStatus.OK);
            }catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}