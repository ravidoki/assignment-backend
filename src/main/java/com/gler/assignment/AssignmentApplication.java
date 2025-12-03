package com.gler.assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the Weather Forecast API.
 * Provides REST endpoints for fetching and storing weather forecast data.
 */
@SpringBootApplication
public class AssignmentApplication {

	/**
	 * Main method to start the Spring Boot application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(AssignmentApplication.class, args);
	}

}
