Feature: Extend SIGN_UP Device API request by additional xtify_token param starting from 6,1 version

  Scenario: device sends xtify token for the 6,0 version and device user data will not be created
    Given First time user with device using JSON and XML formats for 6.0 version and partner communities and for all devices available
    When User registers using device with token
    Then User should have REGISTERED activation status in database
    And device user data should not be created

  Scenario: device sends xtify token for the 6,1 version and device user data will be created
    Given First time user with device using JSON and XML formats for all versions above 6.1 and partner communities and for all devices available
    When User registers using device with token
    Then User should have REGISTERED activation status in database
    And device user data should be created with xtify user sent

  Scenario: device does not send xtify token for the 6,1 version and device user data will not be created
    Given First time user with device using JSON and XML formats for all versions above 6.1 and partner communities and for all devices available
    When User registers using device sending empty xtify token
    Then User should have REGISTERED activation status in database
    And device user data should not be created