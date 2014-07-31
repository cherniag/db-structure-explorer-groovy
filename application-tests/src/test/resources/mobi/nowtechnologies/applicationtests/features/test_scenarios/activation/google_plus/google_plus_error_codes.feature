Feature: Specific error codes for different Google Plus responses
#devices: ANDROID, IOS, WINDOWS_PHONE
#google plus supported versions: 6.0, 6.1
#google plus supported communities: hl_uk, demo, demo2, demo3

  Scenario: User cannot be activated via Google Plus if Google Plus returns empty email
    Given Registered user with all devices using JSON and XML format for all google plus supported versions and google plus supported communities
    When Registered user enters Google Plus credentials and Google Plus returns empty email
    Then User gets 403 http error code and 762 error code and email is not specified message
    And In database user account remains unchanged

  Scenario: User cannot be activated via Google Plus if Google Plus returns invalid Google Plus user id
    Given Registered user with all devices using JSON and XML format for all google plus supported versions and google plus supported communities
    When Registered user enters Google Plus credentials and Google Plus returns invalid Google Plus user id
    Then User gets 403 http error code and 761 error code and invalid user Google Plus user id message
    And In database user account remains unchanged

  Scenario: User cannot be activated via Google Plus if Google Plus returns invalid access token
    Given Registered user with all devices using JSON and XML format for all google plus supported versions and google plus supported communities
    When Registered user enters Google Plus credentials and Google Plus returns invalid access token
    Then User gets 403 http error code and 760 error code and invalid authorization token message
    And In database user account remains unchanged

#add scenarios for activation status verification