package com.yyj.mes_server.domain.plc.repository;

import com.yyj.mes_server.domain.plc.entity.PlcEventLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface PlcEventLogRepository extends JpaRepository<PlcEventLog, Long> {

    Optional<PlcEventLog> findByEventId(String eventId);

    boolean existsByEventId(String eventId);

    List<PlcEventLog> findAllByOrderByReceivedAtDesc();

    List<PlcEventLog> findByProductSerialNo(String productSerialNo);
}