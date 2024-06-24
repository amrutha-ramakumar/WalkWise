package com.ecommerce.library.repository;

import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails,Long> {

   // @Query(value = "SELECT o.order_id AS o_order_id, od.order_id AS od_order_id, o.*, od.* FROM order_details o JOIN orders od ON o.order_id = od.order_id", nativeQuery = true)

    @Query("SELECT d from OrderDetails d" )
    List<OrderDetails> findAllOrder();
//
    @Query("select od from OrderDetails od where od.order.customer.email=?1")
    List<OrderDetails> findOrderDetailsByCustomer(String email);
//
    List<OrderDetails> findByOrderId(Long Id);


    Optional<OrderDetails> findById(Long id);


}
