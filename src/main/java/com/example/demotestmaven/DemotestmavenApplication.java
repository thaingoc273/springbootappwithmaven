package com.example.demotestmaven;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class DemotestmavenApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemotestmavenApplication.class, args);
		System.out.println("Hello World");
		Logger logger = LoggerFactory.getLogger(DemotestmavenApplication.class);
		logger.info("Log4j2 is working");
	}
}