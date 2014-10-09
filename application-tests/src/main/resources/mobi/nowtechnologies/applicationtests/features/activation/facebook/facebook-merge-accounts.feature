@Facebook
Feature: Merge accounts during activation via Facebook
#devices: ANDROID, IOS, WINDOWS_PHONE
#facebook supported versions: 5.2, 6.0, 6.1
#facebook supported communities: hl_uk, demo, demo2, demo3

  Scenario: User re-activates the same facebook account with updated details on same device
    Given Activated via Facebook user with all devices using JSON and XML format for all facebook supported versions and facebook supported communities
    When User registers using same device
    Then Temporary account is created
    And First account becomes deactivated
    When Registered user enters the same Facebook credentials with updated details
    Then Temporary account is removed
    And First account becomes active again
    And In database user has updated facebook details the same as specified in facebook account

  Scenario: User re-activates the same facebook account on new device
    Given Activated via Facebook user with all devices using JSON and XML format for all facebook supported versions and facebook supported communities
    When User registers using new device
    Then Temporary account is created
    And First account remains active
    When Registered user enters the same Facebook credentials
    Then Temporary account is removed
    And First account is updated with new device uid

  Scenario: User re-activates with new facebook account on same device
    Given Activated via Facebook user with all devices using JSON and XML format for all facebook supported versions and facebook supported communities
    When User registers using same device
    Then Temporary account is created
    And First account becomes deactivated
    When Registered user enters new Facebook credentials
    Then New user is successfully activated
    And First account remains deactivated
    And In database new user has facebook details the same as specified in new facebook account

  Scenario: User re-activates with same facebook account on another activated device
    Given Activated via Facebook user with all devices using JSON and XML format for all facebook supported versions and facebook supported communities with a second activated user
    When User registers using second activated device
    Then Temporary account is created
    And Second account becomes deactivated
    When Registered user enters first Facebook credentials
    Then Temporary account is removed
    And Second account remains deactivated
    And First account is updated with second device uid