package com.ecommerce.customer.Controller;

import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.OrderDetails;
import com.ecommerce.library.repository.OrderDetailsRepository;
import com.ecommerce.library.repository.OrderRepository;
import com.ecommerce.library.service.OrderService;
import com.ecommerce.library.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
public class InvoiceController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private OrderRepository orderService;
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    public InvoiceController(PdfService pdfService, OrderRepository orderService, OrderDetailsRepository orderDetailsRepository) {
        this.pdfService = pdfService;
        this.orderService = orderService;
        this.orderDetailsRepository = orderDetailsRepository;
    }



    @GetMapping("/downloadInvoice")
    public ResponseEntity<InputStreamResource> downloadInvoice(@RequestParam Long orderId) {
        Order order =  orderService.getReferenceById(orderId);
        List<OrderDetails> orderDetails = orderDetailsRepository.findByOrderId(orderId);

        ByteArrayInputStream bis = pdfService.generateInvoice(order, orderDetails);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=invoice.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
