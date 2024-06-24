package com.ecommerce.library.service;

import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.OrderDetails;


import java.io.ByteArrayInputStream;
import java.util.List;

public interface PdfService {
    ByteArrayInputStream createPdf();
    ByteArrayInputStream generateInvoice(Order order, List<OrderDetails> orderDetails);
}
