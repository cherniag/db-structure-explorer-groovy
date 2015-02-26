@Ready
Feature: Urban airship token update during user merge process.

  Scenario Outline: Merge urban airship token when user signs up and activates twice
    Given User is signed up from all devices using all formats for all providers supported versions above 6.9 and providers supported communities
      And Urban airship token is sent via Account Check
      And User is activated via <Provider>
    When User signs up again
      And  New urban airship token is sent via Account Check
      And User activated via the same social profile
    Then Urban airship token for old user is updated with the new value
  Examples:
  | Provider   |
  | Facebook   |
  | GooglePlus |
