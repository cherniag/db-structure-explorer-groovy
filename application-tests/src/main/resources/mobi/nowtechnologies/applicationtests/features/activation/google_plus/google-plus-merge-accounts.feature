@Ready
Feature: Merge accounts during activation via Google Plus and re-using same emails via Facebook activation
#devices: ANDROID, IOS, WINDOWS_PHONE
#google plus supported versions: 6.0, 6.1
#google plus supported communities: hl_uk, demo, demo2, demo3
  Scenario: User re-activates the same Google Plus account with updated details on same device
    Given Activated via Google Plus user with all devices using JSON and XML format for all google plus supported versions and google plus supported communities
    When User registers using same device
    Then Temporary account is created
    And First account becomes deactivated
    When Registered user enters the same Google Plus credentials with updated details
    Then Temporary account is removed
    And First account becomes active again
    And In database user has updated Google Plus details the same as specified in Google Plus account

  Scenario: User re-activates the same Google Plus account on new device
    Given Activated via Google Plus user with all devices using JSON and XML format for all google plus supported versions and google plus supported communities
    When User registers using new device
    Then Temporary account is created
    And First account remains active
    When Registered user enters the same Google Plus credentials
    Then Temporary account is removed
    And First account is updated with new device uid

  Scenario: User re-activates with new Google Plus account on same device
    Given Activated via Google Plus user with all devices using JSON and XML format for all google plus supported versions and google plus supported communities
    When User registers using same device
    Then Temporary account is created
    And First account becomes deactivated
    When Registered user enters new Google Plus credentials
    Then New user is successfully activated
    And Default promo is applied
    And First account remains deactivated
    And In database new user has Google Plus details the same as specified in new Google Plus account

  Scenario: User re-activates with same Google Plus account on another activated device
    Given Activated via Google Plus user with all devices using JSON and XML format for all google plus supported versions and google plus supported communities and a second activated user
    When User registers using second activated device
    Then Temporary account is created for the second device
    And Second account becomes deactivated
    When Registered user enters first Google Plus credentials
    Then Temporary account is removed for the second device
    And Second account remains deactivated
    And First account is updated with second device uid

  Scenario: User with Facebook provider re-activates via Google Plus
    Given Activated via Facebook user with all devices using JSON and XML format for all google plus supported versions and google plus supported communities
    When User registers using same device
    Then Temporary account is created
    And First account becomes deactivated
    When Registered user enters Google Plus credentials with different email
    Then New user is successfully activated
    And Default promo is applied
    And First account remains deactivated
    And In database new user has Google Plus details the same as specified in Google Plus account

  Scenario: User with Google Plus provider re-activates via Facebook
    Given Activated via Google Plus user with all devices using JSON and XML format for all google plus supported versions and google plus supported communities
    When User registers using same device
    Then Temporary account is created
    And First account becomes deactivated
    When Registered user enters Facebook credentials with different email
    Then New user is successfully activated
    And Default promo is applied
    And First account remains deactivated
    And In database new user has Facebook details the same as specified in Facebook account

  Scenario: User with Facebook provider re-activates via Google Plus with same email
    Given Activated via Facebook user with all devices using JSON and XML format for all google plus supported versions and google plus supported communities
    When User registers using same device
    Then Temporary account is created
    And First account becomes deactivated
    When Registered user enters Google Plus credentials with same email as first account has
    Then Temporary account is removed
    And First account becomes active again
    And First account is updated with provider Google Plus
    And In database first user has details the same as specified in Google Plus account

  Scenario: User with Google Plus provider re-activates via Facebook with same email
    Given Activated via Google Plus user with all devices using JSON and XML format for all google plus supported versions and google plus supported communities
    When User registers using same device
    Then Temporary account is created
    And First account becomes deactivated
    When Registered user enters Facebook credentials with same email as first account has
    Then Temporary account is removed
    And First account becomes active again
    And First account is updated with provider Facebook
    And In database first user has details the same as specified in Facebook account

  Scenario: User with Facebook provider re-activates on same device with second activated Google Plus account
    Given Activated via Facebook user with all devices using JSON and XML format for all google plus supported versions and google plus supported communities with a second user activated via Google Plus
    When User registers using same device
    Then Temporary account is created
    And First account becomes deactivated
    When Registered user enters Google Plus credentials of second activated user
    Then Temporary account is removed
    And First account remains deactivated
    And Second account is updated with first device uid

  Scenario: User with Google Plus provider re-activates on same device with second activated Facebook account
    Given Activated via Google Plus user with all devices using JSON and XML format for all google plus supported versions and google plus supported communities with a second user activated via Facebook
    When User registers using same device
    Then Temporary account is created
    And First account becomes deactivated
    When Registered user enters Facebook credentials of second activated user
    Then Temporary account is removed
    And First account remains deactivated
    And Second account is updated with first device uid