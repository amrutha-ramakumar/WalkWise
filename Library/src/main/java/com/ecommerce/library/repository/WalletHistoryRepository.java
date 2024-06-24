package com.ecommerce.library.repository;

import com.ecommerce.library.model.WalletHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletHistoryRepository extends JpaRepository<WalletHistory,Long> {

    @Query("select wh from WalletHistory  wh where wh.wallet.customer.customer_id=?1 order by wh.transationDate desc limit 5")
    List<WalletHistory> findAllByCustomer(Long id);


}
