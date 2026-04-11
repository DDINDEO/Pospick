package com.pospick.pospick.repository;

import com.pospick.pospick.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
