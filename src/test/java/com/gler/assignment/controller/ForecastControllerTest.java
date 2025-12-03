package com.gler.assignment.controller;


import com.gler.assignment.dto.ForecastRequest;
import com.gler.assignment.error.AssignmentException;
import com.gler.assignment.model.ForecastRecord;
import com.gler.assignment.service.ForecastService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ForecastControllerTest {
    ForecastService mockService = Mockito.mock(ForecastService.class);
    ForecastController controller = new ForecastController(mockService);

    @Test
    void postForecastHappyPath() {
        ForecastRequest req = new ForecastRequest();
        req.setAddTemprature(true);
        req.setAddHumidity(true);
        req.setAddWindSpeed(true);

        ForecastRecord saved = new ForecastRecord(LocalDate.now(), 20.0, 80.0, 10.0);
        Mockito.when(mockService.fetchAndStore(true, true, true)).thenReturn(saved);

        ResponseEntity<ForecastRecord> resp = controller.post(req);
        assertEquals(200, resp.getStatusCode().value());
        assertNotNull(resp.getBody());
        assertEquals(20.0, resp.getBody().getMaxTemperature());
    }

    @Test
    void postForecast_shouldThrowAssignmentException_whenServiceFails() {
        // Given
        ForecastRequest req = new ForecastRequest();
        req.setAddTemprature(true);
        req.setAddHumidity(true);
        req.setAddWindSpeed(true);

        Mockito.when(mockService.fetchAndStore(true, true, true))
            .thenThrow(new AssignmentException("Service processing failed"));

        // When & Then
        assertThatThrownBy(() -> controller.post(req))
            .isInstanceOf(AssignmentException.class)
            .hasMessage("Service processing failed");
    }
}
