package com.pospick.pospick.repository;

import com.pospick.pospick.domain.Order;
import com.pospick.pospick.domain.OrderItem;
import com.pospick.pospick.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);

    List<OrderItem> findByProduct(Product product);
}
