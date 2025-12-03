package com.gler.assignment.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import okhttp3.mockwebserver.MockResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Step definitions for forecast API. Uses a static MockWebServer and registers its URL
 * before Spring context startup via @DynamicPropertySource.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ForcastSteps {

    // Start MockWebServer statically so its port is available before Spring context boots
    private static final okhttp3.mockwebserver.MockWebServer mockWebServer = MockWebServerConfiguration.getMockServer();


    @DynamicPropertySource
    static void registerMockServerProps(DynamicPropertyRegistry registry) {
        // remove trailing slash
        registry.add("openmeteo.base-url", () -> mockWebServer.url("/").toString().replaceAll("/+$", ""));
    }

    // shutdown after all tests (JUnit lifecycle will handle; Cucumber keeps JVM running so safe)
    // You can optionally add @AfterClass in a JUnit runner; not strictly necessary here.

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SharedSteps sharedSteps;

    // instance field (non-static) used by scenario steps
    private ResponseEntity<String> response;

    // store raw inputs for building JSON bodies
    private String tempRaw;
    private String humRaw;
    private String windRaw;

    @Before
    public void beforeScenario() {
        this.response = null;
        this.tempRaw = this.humRaw = this.windRaw = null;
    }

    @Given("the upstream will return success")
    public void upstream_success() {
        String body = """
                        {
                          "hourly": {
                            "temperature_2m": [10, 15, 20, 18],
                            "relative_humidity_2m": [50, 60, 55],
                            "wind_speed_10m": [3, 5, 4]
                          }
                        }
                        """;
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(body));
    }

    @Given("the upstream will be unreachable")
    public void upstream_unreachable() {
        mockWebServer.setDispatcher(new okhttp3.mockwebserver.Dispatcher() {
            @Override
            public okhttp3.mockwebserver.MockResponse dispatch(okhttp3.mockwebserver.RecordedRequest req) {
                return new okhttp3.mockwebserver.MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AT_START);
            }
        });

    }

    @Given("^the forecast request with temperature=([^,]*), humidity=([^,]*), wind=(.*)$")
    public void the_forecast_request(String t, String h, String w) {
        this.tempRaw = normalizeRaw(t);
        this.humRaw = normalizeRaw(h);
        this.windRaw = normalizeRaw(w);
    }

    @Given("the forecast request with invalid json")
    public void the_forecast_request_invalid() {
        // signal invalid json when executing the When step
        this.tempRaw = "__INVALID_JSON__";
    }

    @When("I call the forcast endpoint")
    public void i_call_the_forcast_endpoint() {
        callForcast(false, false);
    }

    @When("I call the forcast endpoint with types as strings")
    public void i_call_the_forcast_endpoint_with_string_types() {
        callForcast(true, false);
    }

    @When("I call the forcast endpoint with invalid json")
    public void i_call_the_forcast_endpoint_with_invalid_json() {
        callForcast(false, true);
    }

    private void callForcast(boolean forceStringTypes, boolean forceInvalidJson) {
        String url = "http://localhost:" + port + "/api/v1/forcast";
        String body;

        if (forceInvalidJson || "__INVALID_JSON__".equals(this.tempRaw)) {
            // intentionally malformed but valid Java string
            body = "{ \"addTemprature\": true, \"addHumidity\": , \"addWindSpeed\": true }";
        } else if (this.tempRaw == null || this.humRaw == null || this.windRaw == null) {
            // MISSING fields -> ensure we send empty JSON object
            body = "{}";
        } else if (forceStringTypes || isQuoted(this.tempRaw) || isQuoted(this.humRaw) || isQuoted(this.windRaw)) {
            String t = buildQuotedOrBool(this.tempRaw);
            String h = buildQuotedOrBool(this.humRaw);
            String w = buildQuotedOrBool(this.windRaw);
            body = String.format("{\"addTemprature\":%s,\"addHumidity\":%s,\"addWindSpeed\":%s}", t, h, w);
        } else {
            body = String.format("{\"addTemprature\":%s,\"addHumidity\":%s,\"addWindSpeed\":%s}",
                    Boolean.valueOf(this.tempRaw), Boolean.valueOf(this.humRaw), Boolean.valueOf(this.windRaw));
        }

        // <-- ADD THIS DIAGNOSTIC LOGGING (important)
        // HTTP POST request details for testing

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> req = new HttpEntity<>(body, headers);
        this.response = restTemplate.postForEntity(url, req, String.class);

        // log response too
        // HTTP response details for testing verification

        // hand over to shared assertions
        sharedSteps.setResponse(this.response);
    }


    // helper: treat quoted values as JSON strings, unquoted as booleans
    private static boolean isQuoted(String s) {
        return s != null && s.startsWith("\"") && s.endsWith("\"");
    }

    private static String buildQuotedOrBool(String raw) {
        if (raw == null) return "null";
        if (isQuoted(raw)) {
            String inner = raw.substring(1, raw.length() - 1).replace("\"", "\\\"");
            return "\"" + inner + "\"";
        } else {
            return Boolean.valueOf(raw).toString();
        }
    }

    private static String normalizeRaw(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.isEmpty()) return null;        // treat empty as missing
        return s.equalsIgnoreCase("null") ? null : s;
    }
}
