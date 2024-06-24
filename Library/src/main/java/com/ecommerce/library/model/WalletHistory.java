package com.ecommerce.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name="wallet_history")
public class WalletHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    double amount;
    String trasactionType;
    String trasactionStatus;
    Date transationDate;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "wallet_id",referencedColumnName = "wallet_id")
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id",referencedColumnName = "order_id")
    private Order order;
}

