package com.yyj.mes_server.domain.process.repository;

import com.yyj.mes_server.domain.process.entity.ProductionProcess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductionProcessRepository extends JpaRepository<ProductionProcess, Long> {

    Optional<ProductionProcess> findByCode(String code);
}