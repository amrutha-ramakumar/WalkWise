package com.ecommerce.library.service;

import com.ecommerce.library.model.Wallet;
import com.ecommerce.library.model.WalletHistory;

import java.util.List;

public interface WalletService {

    void addToRefundAmount(Long orderId);
    Wallet findByCustomer(Long customerId);
    List<WalletHistory> findAllByCustomer(Long id);

    void setWalletAmount(Long id,double amount);

    void addWalletToReferalEarn(Long customerId);

}
