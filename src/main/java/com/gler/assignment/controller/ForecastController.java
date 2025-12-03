package com.gler.assignment.controller;

import com.gler.assignment.dto.ForecastRequest;
import com.gler.assignment.model.ForecastRecord;
import com.gler.assignment.service.ForecastService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for weather forecast API.
 * Exposes /forecast endpoint which validates request and triggers fetch/store process.
 */
@RestController
@RequestMapping("/api/v1")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Forecast", description = "Forecast and weather related operations")
@Slf4j
public class ForecastController {

    /** Service for handling forecast operations */
    private final ForecastService svc;

    /**
     * Constructor for dependency injection.
     * @param s the forecast service
     */
    public ForecastController(ForecastService s) {
        this.svc = s;
    }

    /**
     * Accepts a request body indicating which metrics to fetch and store.
     *
     * @param request validated {@link ForecastRequest} containing boolean flags to include metrics
     * @return saved {@link ForecastRecord} containing maxima for requested metrics
     */
    @Operation(summary = "Fetch forecast and store maxima", description = "Calls external weather API, extracts maxima and persists them")
    @PostMapping("/forcast")
    public ResponseEntity<ForecastRecord> post(@Valid @RequestBody ForecastRequest request) {
        // Log incoming request parameters for debugging
        log.info("Received forecast request: temperature={}, humidity={}, windSpeed={}",
                request.getAddTemprature(), request.getAddHumidity(), request.getAddWindSpeed());

        // Delegate to service layer for business logic
        ForecastRecord result = svc.fetchAndStore(request.getAddTemprature(), request.getAddHumidity(), request.getAddWindSpeed());

        // Log successful completion
        log.info("Successfully processed forecast request, stored record with ID: {}", result.getId());
        return ResponseEntity.ok(result);
    }
}
