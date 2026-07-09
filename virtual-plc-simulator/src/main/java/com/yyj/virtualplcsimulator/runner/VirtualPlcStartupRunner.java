package com.yyj.virtualplcsimulator.runner;

import com.yyj.virtualplcsimulator.client.MesServerClient;
import com.yyj.virtualplcsimulator.client.dto.PlcEventRequest;
import com.yyj.virtualplcsimulator.client.dto.PlcEventResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Component
public class VirtualPlcStartupRunner {

    private static final int PRODUCT_COUNT = 3;
    private static final double NG_PROBABILITY = 0.15;
    private static final double MISSING_PROCESS_PROBABILITY = 0.10;

    private final MesServerClient mesServerClient;
    private final Random random = new Random();

    public VirtualPlcStartupRunner(MesServerClient mesServerClient) {
        this.mesServerClient = mesServerClient;
    }

    @Scheduled(fixedRate = 60000, initialDelay = 3000)
    public void run() {
        LocalDateTime baseTime = LocalDateTime.now();
        String batchTimestamp = baseTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        System.out.println("[Virtual PLC] 주기적 생산 이벤트 생성 시작");
        System.out.println("batchTimestamp = " + batchTimestamp);

        for (int productIndex = 1; productIndex <= PRODUCT_COUNT; productIndex++) {
            String productSerialNo = createProductSerialNo(batchTimestamp, productIndex);
            sendProductEvents(batchTimestamp, productSerialNo, productIndex, baseTime.plusMinutes(productIndex));
        }

        System.out.println("[Virtual PLC] 주기적 생산 이벤트 생성 완료");
    }

    private void sendProductEvents(
            String batchTimestamp,
            String productSerialNo,
            int productIndex,
            LocalDateTime startTime
    ) {
        double aCycleTime = randomCycleTime();
        double bCycleTime = randomCycleTime();
        double cCycleTime = randomCycleTime();

        List<ProcessEventPlan> processPlans = List.of(
                new ProcessEventPlan("A", "EQ-A-01", aCycleTime, startTime),
                new ProcessEventPlan("B", "EQ-B-01", bCycleTime, startTime.plusSeconds(Math.round(aCycleTime))),
                new ProcessEventPlan("C", "EQ-C-01", cCycleTime, startTime.plusSeconds(Math.round(aCycleTime + bCycleTime)))
        );

        for (ProcessEventPlan processPlan : processPlans) {
            if (shouldSkipProcess()) {
                System.out.println("[Virtual PLC] 공정 이벤트 미수신 시나리오 발생");
                System.out.println("productSerialNo = " + productSerialNo);
                System.out.println("processCode = " + processPlan.processCode());
                continue;
            }

            String result = randomResult();

            PlcEventRequest event = createEvent(
                    batchTimestamp,
                    productIndex,
                    productSerialNo,
                    processPlan.processCode(),
                    processPlan.equipmentCode(),
                    processPlan.cycleTimeSeconds(),
                    result,
                    processPlan.occurredAt()
            );

            PlcEventResponse response = mesServerClient.sendPlcEvent(event);

            System.out.println("[Virtual PLC] MES 서버 응답");
            System.out.println("productSerialNo = " + event.productSerialNo());
            System.out.println("processCode = " + event.processCode());
            System.out.println("cycleTimeSeconds = " + event.cycleTimeSeconds());
            System.out.println("result = " + event.result());
            System.out.println("eventId = " + response.eventId());
            System.out.println("message = " + response.message());
        }
    }

    private PlcEventRequest createEvent(
            String batchTimestamp,
            int productIndex,
            String productSerialNo,
            String processCode,
            String equipmentCode,
            Double cycleTimeSeconds,
            String result,
            LocalDateTime occurredAt
    ) {
        return new PlcEventRequest(
                "EVT-SIM-" + batchTimestamp + "-" + productIndex + "-" + processCode,
                "LINE-1",
                equipmentCode,
                processCode,
                productSerialNo,
                "PROCESS_COMPLETED",
                1,
                cycleTimeSeconds,
                result,
                "RUNNING",
                occurredAt
        );
    }

    private String createProductSerialNo(String batchTimestamp, int productIndex) {
        String suffix = batchTimestamp.substring(batchTimestamp.length() - 4) + String.format("%02d", productIndex);
        return "YYJ-" + suffix;
    }

    private double randomCycleTime() {
        double value = 18.0 + (random.nextDouble() * 52.0);
        return Math.round(value * 10) / 10.0;
    }

    private String randomResult() {
        if (random.nextDouble() < NG_PROBABILITY) {
            return "NG";
        }

        return "OK";
    }

    private boolean shouldSkipProcess() {
        return random.nextDouble() < MISSING_PROCESS_PROBABILITY;
    }

    private record ProcessEventPlan(
            String processCode,
            String equipmentCode,
            Double cycleTimeSeconds,
            LocalDateTime occurredAt
    ) {
    }
}