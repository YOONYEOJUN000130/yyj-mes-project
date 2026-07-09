package com.yyj.mes_server.global.init;

import com.yyj.mes_server.domain.process.entity.ProductionProcess;
import com.yyj.mes_server.domain.process.repository.ProductionProcessRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProcessDataInitializer implements CommandLineRunner {

    private final ProductionProcessRepository productionProcessRepository;

    public ProcessDataInitializer(ProductionProcessRepository productionProcessRepository) {
        this.productionProcessRepository = productionProcessRepository;
    }

    @Override
    public void run(String... args) {
        if (productionProcessRepository.count() > 0) {
            return;
        }

        productionProcessRepository.saveAll(List.of(
                new ProductionProcess("A", "A 공정", "부품 투입 및 전처리 공정", 1),
                new ProductionProcess("B", "B 공정", "조립 및 체결 공정", 2),
                new ProductionProcess("C", "C 공정", "검사 및 출하 준비 공정", 3)
        ));
    }
}