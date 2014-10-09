@Facebook
Feature: Unsuccessful user activation via Facebook in facebook supported communities
#devices: ANDROID, IOS, WINDOWS_PHONE
#facebook supported versions: 5.2, 6.0, 6.1
#facebook supported communities: hl_uk, demo, demo2, demo3

  Scenario: User cannot be activated via Facebook if not all required parameters are sent in SIGN_IN_FACEBOOK
    Given Registered user with all devices using JSON and XML format for all facebook supported versions and facebook supported communities
    When Registered user enters Facebook credentials and client does not pass required parameter
    Then User gets 500 http error code for api version 5.2 and 400 http error code for all facebook supported versions above 6.0 with message regarding missing parameter
    And In database user account remains unchanged

  Scenario: User cannot be activated via Facebook if wrong parameters are sent in SIGN_IN_FACEBOOK
    Given Registered user with all devices using JSON and XML format for all facebook supported versions and facebook supported communities
    When Registered user enters Facebook credentials and client passes wrong authentication parameter
    Then User gets 401 http error code with message "Bad user credentials" for api version 5.2 and message "user login/pass check failed" for all facebook supported versions above 6.0
    And In database user account remains unchanged