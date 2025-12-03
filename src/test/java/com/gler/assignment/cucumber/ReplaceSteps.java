package com.gler.assignment.cucumber;

import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Step definitions for the Replace endpoint.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReplaceSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SharedSteps sharedSteps;

    private String text;

    @Given("the text {string}")
    public void the_text(String input) {
        this.text = input;
    }

    @When("I call the replace endpoint")
    public void i_call_the_replace_endpoint() {
        try {
            String encoded = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String url = "http://localhost:" + port + "/api/v1/replace?text=" + encoded;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            sharedSteps.setResponse(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // common Then steps are in SharedSteps
}
