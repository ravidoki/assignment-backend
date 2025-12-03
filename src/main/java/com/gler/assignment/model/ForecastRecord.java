package com.gler.assignment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

/**
 * JPA entity storing forecast results for a given date.
 * Each record contains optional maxima for temperature, humidity and wind speed.
 */
@Entity
@Schema(description = "Database entity representing daily forecast maxima")
public class ForecastRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Date for which the maxima were computed", example = "2025-09-11")
    private LocalDate date;

    @Schema(description = "Maximum temperature found in the data", example = "23.5")
    private Double maxTemperature;

    @Schema(description = "Maximum relative humidity found in the data", example = "85.0")
    private Double maxHumidity;

    @Schema(description = "Maximum wind speed found in the data", example = "12.4")
    private Double maxWindSpeed;

    public ForecastRecord() {
    }

    public ForecastRecord(LocalDate date, Double maxTemperature, Double maxHumidity, Double maxWindSpeed) {
        this.date = date;
        this.maxTemperature = maxTemperature;
        this.maxHumidity = maxHumidity;
        this.maxWindSpeed = maxWindSpeed;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Double getMaxTemperature() {
        return maxTemperature;
    }

    public Double getMaxHumidity() {
        return maxHumidity;
    }

    public Double getMaxWindSpeed() {
        return maxWindSpeed;
    }
}
