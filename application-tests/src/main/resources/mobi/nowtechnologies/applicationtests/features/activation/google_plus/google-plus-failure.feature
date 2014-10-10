@Ready
Feature: Unsuccessful user activation via Google Plus in google plus supported communities
#devices: ANDROID, IOS, WINDOWS_PHONE
#google plus supported versions: 6.0, 6.1
#google plus supported communities: hl_uk, demo, demo2, demo3

  Scenario: User cannot be activated via Google Plus if not all parameters are sent in SIGN_IN_GOOGLE_PLUS
    Given Registered user with all devices using JSON and XML format for all google plus supported versions and google plus supported communities
    When Registered user enters Google Plus credentials and client does not pass required parameter
    Then User gets 400 http error code with message regarding missing parameter
    And In database user account remains unchanged

  Scenario: User cannot be activated via Google Plus if wrong parameters are sent in SIGN_IN_GOOGLE_PLUS
    Given Registered user with all devices using JSON and XML format for all google plus supported versions and google plus supported communities
    When Registered user enters Google Plus credentials and client passes wrong authentication parameter
    Then User gets 401 http error code with message login/pass check failed
    And In database user account remains unchanged