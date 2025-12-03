package com.gler.assignment.cucumber;

import okhttp3.mockwebserver.MockWebServer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;

/**
 * Starts a single MockWebServer early (static init) and registers its base URL
 * as the 'openmeteo.base-url' property via @DynamicPropertySource so Spring beans
 * (WebClient) pick it up when the test context is created.
 */
public class MockWebServerConfiguration {

    public static final MockWebServer MOCK_SERVER;

    static {
        try {
            MOCK_SERVER = new MockWebServer();
            MOCK_SERVER.start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to start MockWebServer for tests", e);
        }
    }

    // Ensure Spring picks up the mock URL before context creation
    @DynamicPropertySource
    static void registerMockServerProps(DynamicPropertyRegistry registry) {
        // strip trailing slash to match expected base-url format
        registry.add("openmeteo.base-url", () -> MOCK_SERVER.url("/").toString().replaceAll("/+$", ""));
    }

    // Helper so steps can reference the same MockWebServer instance:
    public static MockWebServer getMockServer() {
        return MOCK_SERVER;
    }
}
