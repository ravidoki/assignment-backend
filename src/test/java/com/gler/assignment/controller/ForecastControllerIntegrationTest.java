package com.gler.assignment.controller;

import com.gler.assignment.dto.ForecastRequest;
import com.gler.assignment.error.AssignmentException;
import com.gler.assignment.model.ForecastRecord;
import com.gler.assignment.service.ForecastService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

/**
 * Unit test for AssignmentException handling in ForecastController.
 * Tests that AssignmentException is properly thrown by the controller.
 */
@ExtendWith(MockitoExtension.class)
class ForecastControllerIntegrationTest {

    @Mock
    private ForecastService forecastService;

    @InjectMocks
    private ForecastController forecastController;

    @Test
    void postForecast_shouldThrowAssignmentException_whenServiceFails() {
        // Given
        ForecastRequest request = new ForecastRequest();
        request.setAddTemprature(true);
        request.setAddHumidity(true);
        request.setAddWindSpeed(true);

        when(forecastService.fetchAndStore(anyBoolean(), anyBoolean(), anyBoolean()))
            .thenThrow(new AssignmentException("Business logic error occurred"));

        // When & Then
        assertThatThrownBy(() -> forecastController.post(request))
            .isInstanceOf(AssignmentException.class)
            .hasMessage("Business logic error occurred");
    }

    @Test
    void postForecast_shouldThrowAssignmentExceptionWithCause_whenServiceFails() {
        // Given
        ForecastRequest request = new ForecastRequest();
        request.setAddTemprature(false);
        request.setAddHumidity(true);
        request.setAddWindSpeed(false);

        AssignmentException exception = new AssignmentException("Processing failed",
            new IllegalArgumentException("Invalid data"));

        when(forecastService.fetchAndStore(anyBoolean(), anyBoolean(), anyBoolean()))
            .thenThrow(exception);

        // When & Then
        assertThatThrownBy(() -> forecastController.post(request))
            .isInstanceOf(AssignmentException.class)
            .hasMessage("Processing failed")
            .hasCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void postForecast_shouldReturnForecastRecord_whenServiceSucceeds() {
        // Given
        ForecastRequest request = new ForecastRequest();
        request.setAddTemprature(true);
        request.setAddHumidity(true);
        request.setAddWindSpeed(true);

        ForecastRecord expectedRecord = new ForecastRecord(LocalDate.now(), 25.0, 80.0, 15.0);

        when(forecastService.fetchAndStore(true, true, true))
            .thenReturn(expectedRecord);

        // When
        var response = forecastController.post(request);

        // Then
        assert response.getStatusCode().is2xxSuccessful();
        assert response.getBody() != null;
        assert response.getBody().getMaxTemperature() == 25.0;
        assert response.getBody().getMaxHumidity() == 80.0;
        assert response.getBody().getMaxWindSpeed() == 15.0;
    }
}
