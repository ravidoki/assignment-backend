package com.gler.assignment.cucumber;

import com.gler.assignment.AssignmentApplication;
import okhttp3.mockwebserver.MockWebServer;
import org.springframework.boot.test.context.SpringBootTest;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Cucumber + Spring Test bootstrap. Ensures the MockWebServer base URL
 * is registered as a property before Spring builds the test ApplicationContext.
 */
@CucumberContextConfiguration
@SpringBootTest(classes = AssignmentApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {

    // Important: dynamic property registration happens before the Spring TestContext is created.
    @DynamicPropertySource
    static void registerMockServer(DynamicPropertyRegistry registry) {
        MockWebServer mock = MockWebServerConfiguration.getMockServer();
        // Normalize URL (no trailing slash)
        String baseUrl = mock.url("/").toString().replaceAll("/+$", "");
        registry.add("openmeteo.base-url", () -> baseUrl);
        // Dynamic property registration for Open-Meteo base URL
    }
}
