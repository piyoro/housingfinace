package com.kakaopay.hf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HousingFinanceApplication {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(HousingFinanceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(HousingFinanceApplication.class, args);
	}
}
