@Ready55
Feature: Transport API call for the GET_STREAMZINE command sending special client header
  Scenario: device sends GET_STREAMZINE command with correct parameters and gets correct response
    Given First time user with device using JSON and XML formats for all streamzine supported versions starting from 6.3 and streamzine supported communities and for all devices available
    And update is prepared
    And user does not send 'If-Modified-Since' header
    When user invokes get streamzine command
    Then response has 200 http response code
    And user sends 'If-Modified-Since' header and it is less than update timestamp
    When user invokes get streamzine command
    Then response has 200 http response code
    And user sends 'If-Modified-Since' header and it is bigger than update timestamp
    When user invokes get streamzine command
    Then response has 304 http response code

  Scenario: device sends GET_STREAMZINE command with correct parameters and gets correct response
    Given First time user with device using JSON and XML formats for all streamzine supported versions starting before 6.3 and streamzine supported communities and for all devices available
    And update is prepared
    And user does not send 'If-Modified-Since' header
    When user invokes get streamzine command for old clients
    Then response has 200 http response code
    And user sends 'If-Modified-Since' header and it is less than update timestamp
    When user invokes get streamzine command for old clients
    Then response has 200 http response code
    And user sends 'If-Modified-Since' header and it is bigger than update timestamp
    When user invokes get streamzine command for old clients
    Then response has 200 http response code

  Scenario: device sends GET_STREAMZINE command with correct parameters and gets correct response
    Given First time user with device using JSON and XML formats for all streamzine supported versions starting from 6.3 and streamzine supported communities and for all devices available
    And update is prepared
    And user sends 'If-Modified-Since' header and it has '#CORRUPTED#' value
    When user invokes get streamzine command
    Then response has 200 http response code
    And update time is the same
