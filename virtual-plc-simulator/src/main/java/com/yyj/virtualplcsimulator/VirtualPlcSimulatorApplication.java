package com.yyj.virtualplcsimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class VirtualPlcSimulatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(VirtualPlcSimulatorApplication.class, args);
	}

}