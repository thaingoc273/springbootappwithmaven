package com.example.demotestmaven;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class DemotestmavenApplication {

	@Scheduled(cron = "0 0/5 * * * ?")
	public void scheduleTask() {
		System.out.println("Cron job is running");
	}

	public static void main(String[] args) {
		SpringApplication.run(DemotestmavenApplication.class, args);
		
		Logger logger = LoggerFactory.getLogger(DemotestmavenApplication.class);
		logger.info("Log4j2 is working");
		logger.info(Thread.currentThread().getName());
		MyThread thread1 = new MyThread();
        MyThread thread2 = new MyThread();
        thread1.start(); // Starts a new thread
        thread2.start(); // Starts another thread

		NumberPrinter printer1 = new NumberPrinter("Thread 1");
		NumberPrinter printer2 = new NumberPrinter("Thread 2");
		Thread thread3 = new Thread(printer1);
		Thread thread4 = new Thread(printer2);
		thread3.start();
		thread4.start();
	}
}