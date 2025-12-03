package com.gler.assignment.cucumber;

import io.cucumber.java.en.Then;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Shared step definitions for all Cucumber tests.
 * IMPORTANT:
 *  - No @Component or @Service annotation.
 *  - No @ScenarioScoped.
 *  - Cucumber will automatically instantiate and manage this class.
 */
public class SharedSteps {

    private ResponseEntity<String> response;

    public void setResponse(ResponseEntity<String> response) {
        this.response = response;
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer status) {
        assertNotNull(response, "Response must not be null");
        assertEquals(status.intValue(), response.getStatusCode().value());
        
    }

    @Then("the response body should be {string}")
    public void the_response_body_should_be(String expected) {
        String actual = response == null ? "" : response.getBody();
        if (actual == null) actual = "";
        assertEquals(expected, actual);
    }

    @Then("the response contains maxTemperature")
    public void the_response_contains_maxTemperature() throws Exception {
        assertNotNull(response);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.getBody());
        assertTrue(json.has("maxTemperature"), "Expected maxTemperature field");
    }
}
