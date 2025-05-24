package com.example.demotestmaven;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DemotestmavenApplicationTests {

	@Test
	void contextLoads() {
	}

}
