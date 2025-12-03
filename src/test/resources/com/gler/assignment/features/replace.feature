Feature: Text Replacement API
  As a user I want to transform text according to rules

  Scenario Outline: Replace characters
    Given the text "<input>"
    When I call the replace endpoint
    Then the response status should be <status>
    And the response body should be "<body>"

    Examples:
      | input                        | status | body               |
      | a                            | 400    |                    |
      | ab                           | 200    |                     |
      | abc                          | 200    | *b$                |
      | elephant                     | 200    | *lephan$           |
      | abc#20xyz                    | 200    | *bc#20xy$          |