@Ready
Feature: Extend SIGN_UP Device API request by additional APPSFLYER_UID param starting from 6.6 version

  Scenario: device sends appsflyer uid for all versions bellow 6,6 and appsflyer data should not be created
    Given First time user with all devices using JSON and XML formats with all versions bellow 6.6 and all communities
    When User registers using device with appsflyer uid
    Then User should have REGISTERED activation status in database
    And appsflyer data should not be created


  Scenario: device sends appsflyer uid for all versions above 6.6 and appsflyer data should be created
    Given First time user with all devices using JSON and XML formats with all versions above 6.6 and all communities
    When User registers using device with appsflyer uid
    Then User should have REGISTERED activation status in database
    And appsflyer data should be created
    And appsflyer uid in db should be the same as sent during sign up


  Scenario: device does not send appsflyer uid for all versions above 6.6 and appsflyer data should not be created
    Given First time user with all devices using JSON and XML formats with all versions above 6.6 and all communities
    When User registers using device without appsflyer uid
    Then User should have REGISTERED activation status in database
    And appsflyer data should not be created


  Scenario: device re-sends appsflyer uid for all versions above 6.6 and appsflyer data should be created
    Given Registered user with all devices using JSON and XML formats with all versions above 6.6 and all communities
    When User registers again using device with new appsflyer uid
    Then User should have REGISTERED activation status in database
    And appsflyer data should be re-created
    And appsflyer uid in db should be the same as sent during last sign up


  Scenario: device does not re-sends appsflyer uid for all versions above 6.6 and appsflyer data should be created
    Given Registered user with all devices using JSON and XML formats with all versions above 6.6 and all communities
    When User registers again using device without appsflyer uid
    Then User should have REGISTERED activation status in database
    And appsflyer data should be re-created
    And appsflyer uid in db should be as old user's appsflyer uid