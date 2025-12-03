package com.gler.assignment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for forecast API.
 * All boolean fields are mandatory (must not be null).
 */
@Schema(description = "Request specifying which metrics to include in the forecast stored result")
public class ForecastRequest {

    @Schema(description = "Include temperature maxima", example = "true")
    @NotNull
    private Boolean addTemprature;

    @Schema(description = "Include humidity maxima", example = "true")
    @NotNull
    private Boolean addHumidity;

    @Schema(description = "Include wind speed maxima", example = "true")
    @NotNull
    private Boolean addWindSpeed;

    public Boolean getAddTemprature() {
        return addTemprature;
    }

    public void setAddTemprature(Boolean addTemprature) {
        this.addTemprature = addTemprature;
    }

    public Boolean getAddHumidity() {
        return addHumidity;
    }

    public void setAddHumidity(Boolean addHumidity) {
        this.addHumidity = addHumidity;
    }

    public Boolean getAddWindSpeed() {
        return addWindSpeed;
    }

    public void setAddWindSpeed(Boolean addWindSpeed) {
        this.addWindSpeed = addWindSpeed;
    }
}
