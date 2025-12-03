package com.gler.assignment.service;


import com.gler.assignment.error.AssignmentException;
import com.gler.assignment.model.ForecastRecord;
import com.gler.assignment.repository.ForecastRecordRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

class ForcastServiceTest {

    private static MockWebServer mockWebServer;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void fetchAndStore_parsesMaxValuesAndSaves() {
        String body = """
                {
                  "hourly": {
                    "temperature_2m": [10, 15, 20, 18],
                    "relative_humidity_2m": [50, 60, 55],
                    "wind_speed_10m": [3, 5, 4]
                  }
                }""";
        mockWebServer.enqueue(new MockResponse().setBody(body).setResponseCode(200));

        String baseUrl = mockWebServer.url("/").toString();

        WebClient.Builder builder = WebClient.builder().baseUrl(baseUrl);
        ForecastRecordRepository mockRepo = Mockito.mock(ForecastRecordRepository.class);
        Mockito.when(mockRepo.save(any(ForecastRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ForecastService svc = new ForecastService(builder, mockRepo, baseUrl);

        ForecastRecord rec = svc.fetchAndStore(true, true, true);
        assertNotNull(rec);
        assertEquals(20.0, rec.getMaxTemperature());
        assertEquals(60.0, rec.getMaxHumidity());
        assertEquals(5.0, rec.getMaxWindSpeed());
        assertNotNull(rec.getDate());
    }

    @Test
    void fetchAndStore_shouldThrowAssignmentException_whenJsonParsingFails() {
        // Given - invalid JSON response
        String invalidJson = "{ invalid json content";
        mockWebServer.enqueue(new MockResponse().setBody(invalidJson).setResponseCode(200));

        String baseUrl = mockWebServer.url("/").toString();

        WebClient.Builder builder = WebClient.builder().baseUrl(baseUrl);
        ForecastRecordRepository mockRepo = Mockito.mock(ForecastRecordRepository.class);

        ForecastService svc = new ForecastService(builder, mockRepo, baseUrl);

        // When & Then
        AssignmentException exception = assertThrows(AssignmentException.class,
            () -> svc.fetchAndStore(true, true, true));

        assertEquals("Error parsing upstream response", exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    void fetchAndStore_shouldThrowAssignmentException_whenUnexpectedErrorOccurs() {
        // Given - valid JSON but with unexpected structure
        String malformedJson = """
            {
              "hourly": {
                "temperature_2m": "not-an-array",
                "relative_humidity_2m": [50, 60],
                "wind_speed_10m": [3, 5]
              }
            }""";
        mockWebServer.enqueue(new MockResponse().setBody(malformedJson).setResponseCode(200));

        String baseUrl = mockWebServer.url("/").toString();

        WebClient.Builder builder = WebClient.builder().baseUrl(baseUrl);
        ForecastRecordRepository mockRepo = Mockito.mock(ForecastRecordRepository.class);

        ForecastService svc = new ForecastService(builder, mockRepo, baseUrl);

        // When & Then - should throw AssignmentException for JSON parsing issues
        assertThatThrownBy(() -> svc.fetchAndStore(true, true, true))
            .isInstanceOf(AssignmentException.class)
            .hasMessage("Error parsing upstream response")
            .hasCauseInstanceOf(Exception.class);
    }
}
