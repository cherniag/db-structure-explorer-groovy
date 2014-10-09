@Facebook
Feature: Specific error codes for different Facebook responses
#devices: ANDROID, IOS, WINDOWS_PHONE
#facebook supported versions: 5.2, 6.0, 6.1
#facebook supported communities: hl_uk, demo, demo2, demo3

  Scenario: User cannot be activated via Facebook if Facebook returns empty email
    Given Registered user with all devices using JSON and XML format for all facebook supported versions and facebook supported communities
    When Registered user enters Facebook credentials and facebook returns empty email
    Then User gets 403 http error code and 662 error code and email is not specified message
    And In database user account remains unchanged

  Scenario: User cannot be activated via Facebook if Facebook returns invalid facebook id
    Given Registered user with all devices using JSON and XML format for all facebook supported versions and facebook supported communities
    When Registered user enters Facebook credentials and facebook returns invalid facebook id
    Then User gets 403 http error code and 661 error code and invalid user facebook id message
    And In database user account remains unchanged

  Scenario: User cannot be activated via Facebook if Facebook returns invalid access token
    Given Registered user with all devices using JSON and XML format for all facebook supported versions and facebook supported communities
    When Registered user enters Facebook credentials and facebook returns invalid access token
    Then User gets 403 http error code and 660 error code and invalid authorization token message
    And In database user account remains unchanged

#add scenarios for activation status verification