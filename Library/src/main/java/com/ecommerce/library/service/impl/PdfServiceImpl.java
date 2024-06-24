package com.ecommerce.library.service.impl;

import com.ecommerce.library.model.Address;
import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.OrderDetails;
import com.ecommerce.library.repository.AddressRepository;
import com.ecommerce.library.repository.ImageRepository;
import com.ecommerce.library.service.PdfService;
import com.lowagie.text.*;
import java.util.List;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class PdfServiceImpl implements PdfService {
    private org.slf4j.Logger logger= LoggerFactory.getLogger(PdfServiceImpl.class);
    private final AddressRepository addressRepository; // Assume you have this repository
    private final ImageRepository imageRepository;     // Assume you have this repository

    public PdfServiceImpl(AddressRepository addressRepository, ImageRepository imageRepository) {
        this.addressRepository = addressRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    public ByteArrayInputStream createPdf() {
        logger.info("Crate Pdf Started");
        String tittle="PDF heading";
        String content="pdf content";

        ByteArrayOutputStream out=new ByteArrayOutputStream();
        Document document=new Document();
        PdfWriter.getInstance(document,out);

        document.open();

        Font tittleFont= FontFactory.getFont(FontFactory.HELVETICA_BOLD,25);
        Paragraph tittlePara=new Paragraph(tittle,tittleFont);
        tittlePara.setAlignment(Element.ALIGN_CENTER);
        document.add(tittlePara);

        Font paraFont=FontFactory.getFont(FontFactory.HELVETICA,18);
        Paragraph paragraph=new Paragraph(content,paraFont);
        document.add(paragraph);

        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }






        private static final String IMAGE_BASE_PATH = "Customer/src/main/resources/static/imgs/images/";
    @Override
    public ByteArrayInputStream generateInvoice(Order order, List<OrderDetails> orderDetails) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Add title
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Tax Invoice/Bill of Supply/Cash Memo", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(Chunk.NEWLINE);

            PdfPTable orderInfoTable = new PdfPTable(2);
            orderInfoTable.setWidthPercentage(100);
            orderInfoTable.setWidths(new int[]{1, 2});

            orderInfoTable.addCell(createCell("Order ID:", PdfPCell.ALIGN_LEFT,new Font(Font.HELVETICA, 12, Font.BOLD)));
            orderInfoTable.addCell(createCell(order.getIdOrders().toString(), PdfPCell.ALIGN_LEFT,new Font(Font.HELVETICA, 12, Font.BOLD)));

//            orderInfoTable.addCell(createCell("Delivered Date:", PdfPCell.ALIGN_LEFT,new Font(Font.HELVETICA, 12, Font.BOLD)));
//            orderInfoTable.addCell(createCell(order.getDeliveryDate().toString(), PdfPCell.ALIGN_LEFT,new Font(Font.HELVETICA, 12, Font.BOLD)));

            orderInfoTable.addCell(createCell("Delivered Date:", PdfPCell.ALIGN_LEFT, new Font(Font.HELVETICA, 12, Font.BOLD)));
            String deliveryDate = order.getDeliveryDate() != null ? order.getDeliveryDate().toString() : "Deliver soon";
            orderInfoTable.addCell(createCell(deliveryDate, PdfPCell.ALIGN_LEFT, new Font(Font.HELVETICA, 12, Font.BOLD)));

            orderInfoTable.addCell(createCell("Order status:", PdfPCell.ALIGN_LEFT,new Font(Font.HELVETICA, 12, Font.BOLD)));
            orderInfoTable.addCell(createCell(order.getOrderStatus().toString(), PdfPCell.ALIGN_LEFT,new Font(Font.HELVETICA, 12, Font.BOLD)));

            document.add(orderInfoTable);
            document.add(Chunk.NEWLINE);

            // Fetch billing and shipping addresses
            String billingAddress = fetchBillingAddress(order.getCustomer().getCustomer_id());
            String shippingAddress = fetchShippingAddress(order.getShippingAddress());

            // Add Sold By, Billing Address, and Shipping Address
            PdfPTable addressTable = new PdfPTable(2);
            addressTable.setWidthPercentage(100);
            addressTable.setWidths(new int[]{1, 1});

            PdfPCell soldByCell = new PdfPCell();
            soldByCell.addElement(new Paragraph("Sold By:", new Font(Font.HELVETICA, 12, Font.BOLD)));
            soldByCell.addElement(new Paragraph("Walkwise"));
            soldByCell.addElement(new Paragraph("Rajajinagar,Bangalore 560022"));
            soldByCell.addElement(new Paragraph("Karnataka, 560022, IN"));
            soldByCell.addElement(new Paragraph("PAN No: AALCR3173P"));
            soldByCell.addElement(new Paragraph("GST Reg No: 29AALCR3173P1ZJ"));
            soldByCell.setBorder(Rectangle.NO_BORDER);

            PdfPCell billingShippingCell = new PdfPCell();
            billingShippingCell.addElement(new Paragraph("Billing Address:", new Font(Font.HELVETICA, 12, Font.BOLD)));
            billingShippingCell.addElement(new Paragraph(billingAddress));

            billingShippingCell.addElement(Chunk.NEWLINE);
            billingShippingCell.addElement(new Paragraph("Shipping Address:", new Font(Font.HELVETICA, 12, Font.BOLD)));
            billingShippingCell.addElement(new Paragraph(shippingAddress));
            billingShippingCell.setBorder(Rectangle.NO_BORDER);

            addressTable.addCell(soldByCell);
            addressTable.addCell(billingShippingCell);

            document.add(addressTable);
            document.add(Chunk.NEWLINE);

            // Add Order Information

            // Add order items table (Image, Description, Unit Price, Quantity, Total)
            PdfPTable orderTable = new PdfPTable(5);
            orderTable.setWidthPercentage(100);
            orderTable.setWidths(new int[]{2, 4, 1, 1, 2});

            addTableHeader(orderTable);

            for (OrderDetails orderDetail : orderDetails) {
                addOrderRows(orderTable, orderDetail);
            }

            document.add(orderTable);
            document.add(Chunk.NEWLINE);

            // Add totals (Total Amount, Shipping Fee, Deduction)
            PdfPTable totalsTable = new PdfPTable(2);
            totalsTable.setWidthPercentage(100);
            totalsTable.setWidths(new int[]{2, 1});

            totalsTable.addCell(createCell("Shipping Fee:", PdfPCell.ALIGN_RIGHT));
            totalsTable.addCell(createCell(String.valueOf(order.getShippingFee()), PdfPCell.ALIGN_RIGHT));

            totalsTable.addCell(createCell("Deduction:", PdfPCell.ALIGN_RIGHT));
            totalsTable.addCell(createCell(String.valueOf(order.getDeduction()), PdfPCell.ALIGN_RIGHT));

            totalsTable.addCell(createCell("Grand Total:", PdfPCell.ALIGN_RIGHT, new Font(Font.HELVETICA, 12, Font.BOLD)));
            totalsTable.addCell(createCell(String.valueOf(order.getGrandTotalPrize()), PdfPCell.ALIGN_RIGHT, new Font(Font.HELVETICA, 12, Font.BOLD)));

            document.add(totalsTable);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

//    @Override
//    public ByteArrayInputStream generateInvoice(Order order, List<OrderDetails> orderDetails) {
//        Document document = new Document();
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//        try {
//            PdfWriter.getInstance(document, out);
//            document.open();
//
//            // Add title
//            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
//            Paragraph title = new Paragraph("Tax Invoice/Bill of Supply/Cash Memo", titleFont);
//            title.setAlignment(Element.ALIGN_CENTER);
//            document.add(title);
//
//            document.add(Chunk.NEWLINE);
//
//            // Fetch billing and shipping addresses
//            String billingAddress = fetchBillingAddress(order.getCustomer().getCustomer_id());
//            String shippingAddress = String.valueOf(order.getShippingAddress());
//
//            // Add Sold By, Billing Address, and Shipping Address
//            PdfPTable addressTable = new PdfPTable(2);
//            addressTable.setWidthPercentage(100);
//            addressTable.setWidths(new int[]{1, 1});
//
//            PdfPCell soldByCell = new PdfPCell();
//            soldByCell.addElement(new Paragraph("Sold By:", new Font(Font.HELVETICA, 12, Font.BOLD)));
//            soldByCell.addElement(new Paragraph("RETAILEZ PRIVATE LIMITED"));
//            soldByCell.addElement(new Paragraph("Municipal No. 422-1, Industrial Suburb, 1st Stage"));
//            soldByCell.addElement(new Paragraph("Rajajinagar, Ward No 10, Bangalore 560022"));
//            soldByCell.addElement(new Paragraph("Bangalore, Karnataka, 560022, IN"));
//            soldByCell.addElement(new Paragraph("PAN No: AALCR3173P"));
//            soldByCell.addElement(new Paragraph("GST Registration No: 29AALCR3173P1ZJ"));
//            soldByCell.setBorder(Rectangle.NO_BORDER);
//
//            PdfPCell billingShippingCell = new PdfPCell();
//            billingShippingCell.addElement(new Paragraph("Billing Address:", new Font(Font.HELVETICA, 12, Font.BOLD)));
//            billingShippingCell.addElement(new Paragraph(billingAddress));
//            billingShippingCell.addElement(new Paragraph(billingAddress));
//            billingShippingCell.addElement(new Paragraph(billingAddress));
//
//            billingShippingCell.addElement(Chunk.NEWLINE);
//            billingShippingCell.addElement(new Paragraph("Shipping Address:", new Font(Font.HELVETICA, 12, Font.BOLD)));
//            billingShippingCell.addElement(new Paragraph(shippingAddress));
//            billingShippingCell.setBorder(Rectangle.NO_BORDER);
//
//            addressTable.addCell(soldByCell);
//            addressTable.addCell(billingShippingCell);
//
//            document.add(addressTable);
//            document.add(Chunk.NEWLINE);
//
//            // Add Order Information
//            PdfPTable orderInfoTable = new PdfPTable(2);
//            orderInfoTable.setWidthPercentage(100);
//            orderInfoTable.setWidths(new int[]{1, 2});
//
//            orderInfoTable.addCell(createCell("Order ID:", PdfPCell.ALIGN_LEFT));
//            orderInfoTable.addCell(createCell(order.getId().toString(), PdfPCell.ALIGN_LEFT));
//
//            orderInfoTable.addCell(createCell("Delivered Date:", PdfPCell.ALIGN_LEFT));
//            orderInfoTable.addCell(createCell(order.getDeliveryDate().toString(), PdfPCell.ALIGN_LEFT));
//
//            document.add(orderInfoTable);
//            document.add(Chunk.NEWLINE);
//
//            // Add order items table (Image, Description, Unit Price, Quantity, Total)
//            PdfPTable orderTable = new PdfPTable(5);
//            orderTable.setWidthPercentage(100);
//            orderTable.setWidths(new int[]{2, 4, 1, 1, 2});
//
//            addTableHeader(orderTable);
//
//            for (OrderDetails orderDetail : orderDetails) {
//                addOrderRows(orderTable, orderDetail);
//            }
//
//            document.add(orderTable);
//            document.add(Chunk.NEWLINE);
//
//            // Add totals (Total Amount, Shipping Fee, Deduction)
//            PdfPTable totalsTable = new PdfPTable(2);
//            totalsTable.setWidthPercentage(100);
//            totalsTable.setWidths(new int[]{2, 1});
//
////            totalsTable.addCell(createCell("Subtotal:", PdfPCell.ALIGN_RIGHT));
////            totalsTable.addCell(createCell(String.valueOf(order.getSubtotal()), PdfPCell.ALIGN_RIGHT));
//
//            totalsTable.addCell(createCell("Shipping Fee:", PdfPCell.ALIGN_RIGHT));
//            totalsTable.addCell(createCell(String.valueOf(order.getShippingFee()), PdfPCell.ALIGN_RIGHT));
//
//            totalsTable.addCell(createCell("Deduction:", PdfPCell.ALIGN_RIGHT));
//            totalsTable.addCell(createCell(String.valueOf(order.getDeduction()), PdfPCell.ALIGN_RIGHT));
//
//            totalsTable.addCell(createCell("Grand Total:", PdfPCell.ALIGN_RIGHT, new Font(Font.HELVETICA, 12, Font.BOLD)));
//            totalsTable.addCell(createCell(String.valueOf(order.getGrandTotalPrize()), PdfPCell.ALIGN_RIGHT, new Font(Font.HELVETICA, 12, Font.BOLD)));
//
//            document.add(totalsTable);
//
//            document.close();
//
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//
//        return new ByteArrayInputStream(out.toByteArray());
//    }

    private String fetchBillingAddress(Long customerId) {
        Optional<Address> addressOpt = addressRepository.findDefaultBillingAddressByCustomerId(customerId);
        if (addressOpt.isPresent()) {
            Address address = addressOpt.get();
            return formatAddress(address);
        } else {
            return "No billing address found.";
        }
    }

    private String formatAddress(Address address) {
        return String.format("%s\n%s\n%s,  %s\n%s - %d",
                address.getAddressLine1(),
                address.getAddressLine2() != null ? address.getAddressLine2() : "",
                address.getCity(),
//                address.getDistrict(),
                address.getState(),
                address.getCountry(),
                address.getPincode()
//                address.getCustomer().getName() // Assuming the Customer entity has a name field
        );
    }
    private String fetchShippingAddress(Address shippingAddress) {
        if (shippingAddress != null) {
            return formatAddress(shippingAddress);
        } else {
            return "No shipping address found.";
        }
    }


    private void addTableHeader(PdfPTable table) {
        Stream.of("Image", "Description", "Unit Price", "Quantity", "Total")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    Font headFont = new Font(Font.HELVETICA, 12, Font.BOLD);
                    header.setPhrase(new Phrase(columnTitle, headFont));
                    table.addCell(header);
                });
    }

    private void addOrderRows(PdfPTable table, OrderDetails orderDetail) {
        try {
            // Fetch first image name from the database
            String imageName = orderDetail.getProduct().getImage().get(0).getName();
            if (imageName != null) {
                // Load image from file system
                Image img = Image.getInstance(IMAGE_BASE_PATH + imageName);
                img.scaleAbsolute(40, 40);
                PdfPCell imageCell = new PdfPCell(img);
                table.addCell(imageCell);
            } else {
                table.addCell("");
            }

            // Description
            table.addCell(orderDetail.getProduct().getShortDescription());

            // Unit Price
            table.addCell(String.valueOf(orderDetail.getUnitPrice()));

            // Quantity
            table.addCell(String.valueOf(orderDetail.getQuantity()));

            // Total
            table.addCell(String.valueOf(orderDetail.getTotalPrice()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private String fetchFirstImageName(Long productId) {
//        // Assume there is a method or service to get the first image name by product ID
//        Optional<Image> image = imageRepository.findFirstByProductId(productId);
//        return image.map(Image::getName).orElse(null);
//    }

    private PdfPCell createCell(String content, int alignment) {
        return createCell(content, alignment, new Font(Font.HELVETICA, 10));
    }

    private PdfPCell createCell(String content, int alignment, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }
}
