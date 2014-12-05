@Ready
Feature: Store user's referrals

  Scenario Outline: Activated user sends list of referrals
    Given Activated user with all devices using all formats for all providers supported versions above 6.7 and providers supported communities
    When User wants to send new referral with <Source> and <Contact> for specified community
    And contact <ExistingContact> was in database before with <Source> for this community
    When User invokes post referral command
    Then Response has 200 http response code
    And it should <Exists> with <NewContact>, <Provider> and <State> for this user and community
  Examples:
    | Source      | Contact     | ExistingContact | Exists  | NewContact     | Provider     | State        |
    | FACEBOOK    | SameAsOwner | <null>          | false   | <null>         | <null>       | <null>       |
    | GOOGLE_PLUS | Different   | SameAsContact   | true    | SameAsBefore   | GOOGLE_PLUS  | SameAsBefore |
    | EMAIL       | Different   | <null>          | true    | SameAsContact  | EMAIL        | PENDING      |
