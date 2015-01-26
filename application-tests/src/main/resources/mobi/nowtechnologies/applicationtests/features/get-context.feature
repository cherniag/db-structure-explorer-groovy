@NotReady
Feature:
  Scenario: context
    Given First time user with all devices using JSON and XML formats with all versions above 6.7 and get context supported communities available
    When user invokes get context command
    Then response has 200 http response code

