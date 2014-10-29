@Ready
Feature: Transport API call for the GET_STREAMZINE command
  Scenario: device sends GET_STREAMZINE command with incorrect (not supported) community
    Given First time user with device using JSON format for all streamzine supported versions below 6.3 and streamzine supported communities and for all devices available
    When user invokes get streamzine command with incorrect community
    Then user gets 404 code in response
