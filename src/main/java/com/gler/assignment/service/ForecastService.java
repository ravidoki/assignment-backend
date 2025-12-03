package com.gler.assignment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gler.assignment.error.AssignmentException;
import com.gler.assignment.error.UpstreamUnavailableException;
import com.gler.assignment.model.ForecastRecord;
import com.gler.assignment.repository.ForecastRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Iterator;

/**
 * Service performing external API call to Open-Meteo and storing computed maxima
 * into the database using {@link ForecastRecordRepository}.
 */
@Service
@Slf4j
public class ForecastService {

    /** WebClient for making HTTP requests to external APIs */
    private final WebClient webClient;

    /** Repository for persisting forecast records */
    private final ForecastRecordRepository repository;

    /** JSON mapper for parsing API responses */
    private final ObjectMapper mapper = new ObjectMapper();

    /** Base URL for Open-Meteo API, configurable via properties */
    @Value("${openmeteo.base-url:https://api.open-meteo.com}")
    private String baseUrl;

    /**
     * Constructor with dependency injection.
     * @param webClientBuilder builder for creating WebClient instance
     * @param repository repository for data persistence
     * @param baseUrl base URL for external API (injected via @Value)
     */
    public ForecastService(WebClient.Builder webClientBuilder,
                          ForecastRecordRepository repository,
                          @Value("${openmeteo.base-url:https://api.open-meteo.com}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.repository = repository;
    }

    /**
     * Fetches hourly data from the upstream API and computes maxima for requested metrics.
     *
     * @param addTemprature include temperature maxima
     * @param addHumidity include humidity maxima
     * @param addWindSpeed include wind speed maxima
     * @return persisted {@link ForecastRecord}
     * @throws UpstreamUnavailableException if the upstream API is unreachable or returns an error
     * @throws RuntimeException for parsing or other unexpected errors
     */
    public ForecastRecord fetchAndStore(boolean addTemprature, boolean addHumidity, boolean addWindSpeed) {
        log.info("Starting forecast fetch and store operation: temperature={}, humidity={}, windSpeed={}",
                addTemprature, addHumidity, addWindSpeed);

        // Build API URL with Berlin coordinates (latitude=52.52, longitude=13.41)
        String url = "/v1/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,wind_speed_10m&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m";
        log.info("Using Open-Meteo base URL: {}", baseUrl);
        log.info("Full request URL: {}{}", baseUrl, url);

        String body;
        try {
            body = webClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(
                            status -> status.is5xxServerError() || status.is4xxClientError(),
                            resp -> Mono.error(new UpstreamUnavailableException("Upstream API returned " + resp.statusCode().value()))
                    )
                    .bodyToMono(String.class)
                    .block();

        } catch (WebClientRequestException ex) {
            log.error("Network error calling upstream Open-Meteo API: {}", ex.getMessage(), ex);
            throw new UpstreamUnavailableException("Connection to the upstream is unreachable", ex);
        } catch (RuntimeException ex) {
            // Catch reactor/netty wrapping exceptions (be defensive)
            log.error("Unexpected runtime error while calling upstream Open-Meteo API: {}", ex.getMessage(), ex);
            throw new UpstreamUnavailableException("Connection to the upstream is unreachable", ex);
        }

        if (body == null) {
            log.error("Received null response body from Open-Meteo API");
            throw new UpstreamUnavailableException("Empty response from upstream");
        }

        log.info("Successfully received response from Open-Meteo API, response length: {} characters", body.length());

        try {
            log.info("Parsing JSON response from Open-Meteo API");

            // Parse JSON response
            JsonNode root = mapper.readTree(body);
            JsonNode hourly = root.path("hourly");

            // Initialize variables for calculated maxima
            Double maxTemp = null;
            Double maxHumidity = null;
            Double maxWind = null;

            // Calculate maxima for requested metrics only
            if (Boolean.TRUE.equals(addTemprature) && hourly.has("temperature_2m")) {
                maxTemp = findMax(hourly.get("temperature_2m"));
                log.info("Calculated max temperature: {}°C", maxTemp);
            }
            if (Boolean.TRUE.equals(addHumidity) && hourly.has("relative_humidity_2m")) {
                maxHumidity = findMax(hourly.get("relative_humidity_2m"));
                log.info("Calculated max humidity: {}%", maxHumidity);
            }
            if (Boolean.TRUE.equals(addWindSpeed) && hourly.has("wind_speed_10m")) {
                maxWind = findMax(hourly.get("wind_speed_10m"));
                log.info("Calculated max wind speed: {} km/h", maxWind);
            }

            // Create and persist forecast record for today's date
            LocalDate date = LocalDate.now(); // store against today's date
            ForecastRecord forecastRecord = new ForecastRecord(date, maxTemp, maxHumidity, maxWind);
            ForecastRecord savedRecord = repository.save(forecastRecord);

            log.info("Successfully saved forecast record with ID: {} for date: {}", savedRecord.getId(), date);
            return savedRecord;

        } catch (UpstreamUnavailableException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("Error parsing JSON response from Open-Meteo API: {}", e.getMessage(), e);
            throw new AssignmentException("Error parsing upstream response", e);
        }
    }

    /**
     * Find the maximum numeric value inside a JSON array node.
     *
     * @param array JSON array node containing numeric values
     * @return maximum value or null if none found
     */
    private Double findMax(JsonNode array) {
        double max = Double.NEGATIVE_INFINITY;

        // Iterate through all elements in the JSON array
        for (Iterator<JsonNode> it = array.elements(); it.hasNext(); ) {
            JsonNode n = it.next();
            if (n.isNumber()) {
                double v = n.asDouble();
                if (v > max) max = v;
            }
        }

        // Return null if no numeric values found, otherwise return the maximum
        return max == Double.NEGATIVE_INFINITY ? null : max;
    }
}
