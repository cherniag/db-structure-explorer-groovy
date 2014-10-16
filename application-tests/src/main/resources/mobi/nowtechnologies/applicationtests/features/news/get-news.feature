@Ready
Feature: Activated user successfully gets news
  Scenario: Activated o2 user successfully gets news
    Given Activated via OTAC user with all devices using JSON and XML format for all versions and o2 community
    When News message with type 'NEWS', title 'Message title1' and text 'Message body1' exists in database
    When News message with type 'POPUP', title 'Message title2' and text 'Message body2' exists in database
    When News message with type 'NOTIFICATION', title 'Message title3' and text 'Message body3' exists in database
    And User invokes get news command
    Then response has 200 http response code
    And news response should contains 3 news message
    And news message should have the same publish time

    And news message should have message types [NEWS, POPUP, NOTIFICATION]
    And details [Message title1, Message title2, Message title3]
    And bodies [Message body1, Message body2, Message body3]




