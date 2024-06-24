package com.ecommerce.library.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;
    private String idOrders;
    private Date orderDate;
    private Date deliveryDate;
    private String orderStatus;
    private double grandTotalPrize;
    private double deduction;
    private int quantity;
    private String paymentMethod;
    private int shippingFee;
    private boolean isAccept;

//    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JoinColumn(name = "size_id", referencedColumnName = "size_id")
//    private Size size;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id",referencedColumnName = "address_id")
    private Address shippingAddress;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    private List<OrderDetails> orderDetailList;
}
