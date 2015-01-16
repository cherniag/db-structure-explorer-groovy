@Ready
Feature: Activated user successfully gets news
  Scenario Outline: Activated o2 user successfully gets news
    Given Activated via OTAC user with all devices using JSON and XML format for all versions and o2 community
    When News message with type '<DbMessageType>', title '<DbMessageTitle>' and text '<DbMessageBody>' exists in database
    And User invokes get news command
    Then response has 200 http response code
    And news response should contains 1 news message
    And news message should have the same publish time
    And news message should have message type '<ResponseMessageType>'
    And news message should have detail '<ResponseMessageTitle>'
    And news message should have body '<ResponseMessageBody>'

  Examples:
    | DbMessageType | DbMessageTitle | DbMessageBody | ResponseMessageType | ResponseMessageTitle | ResponseMessageBody |
    | NEWS          | Message title1 | Message body1 | NEWS                | Message title1       | Message body1       |
    | POPUP         | Message title3 | Message body3 | POPUP               | Message title3       | Message body3       |
    | NOTIFICATION  | Message title2 | Message body2 | NOTIFICATION        | Message title2       | Message body2       |

