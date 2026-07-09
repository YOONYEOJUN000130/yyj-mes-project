package com.yyj.mes_server.domain.product.repository;

import com.yyj.mes_server.domain.product.entity.FinishedProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FinishedProductRepository extends JpaRepository<FinishedProduct, Long> {

    boolean existsByProductSerialNo(String productSerialNo);

    List<FinishedProduct> findAllByOrderByCompletedAtDesc();
}