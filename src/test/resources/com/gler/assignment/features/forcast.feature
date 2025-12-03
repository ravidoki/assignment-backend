Feature: Forecast API

  Background:
    Given the upstream will return success

  Scenario: Successful fetch and store
    Given the forecast request with temperature=true, humidity=true, wind=true
    When I call the forcast endpoint
    Then the response status should be 200
    And the response contains maxTemperature

  Scenario: Missing parameters should return 400
    Given the forecast request with temperature=, humidity=, wind=
    When I call the forcast endpoint
    Then the response status should be 400

  Scenario: Partial missing parameters should return 400
    Given the forecast request with temperature=true, humidity=, wind=true
    When I call the forcast endpoint
    Then the response status should be 400

  Scenario: Wrong types (strings instead of booleans) should return 400
    Given the forecast request with temperature="yes", humidity="no", wind="true"
    When I call the forcast endpoint with types as strings
    Then the response status should be 400

  Scenario: Invalid JSON should return 400
    Given the forecast request with invalid json
    When I call the forcast endpoint with invalid json
    Then the response status should be 400

  Scenario: Upstream unreachable returns 502
    Given the upstream will be unreachable
    And the forecast request with temperature=true, humidity=true, wind=true
    When I call the forcast endpoint
    Then the response status should be 502
