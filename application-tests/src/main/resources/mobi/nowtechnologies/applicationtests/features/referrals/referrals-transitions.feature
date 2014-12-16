@Ready44
Feature: Store user's referrals
  Scenario: Activated users flow when older is activated and younger is duplicated
    Given activated user references R0(EMAIL:PENDING), R1(FACEBOOK:PENDING)
    When referenced user activates via FACEBOOK with data U(EMAIL:R0;ID:R1)
    Then in database R0(EMAIL:ACTIVATED), R1(FACEBOOK:DUPLICATED)

  Scenario: Activated users flow when duplicate is placed
    Given activated user references R0(EMAIL:PENDING)
    When referenced user activates via FACEBOOK with data U(EMAIL:R0;ID:ANY)
    Then in database R0(EMAIL:ACTIVATED), R1(FACEBOOK:DUPLICATED)

  Scenario: Activated users flow when activates via email first and social second
    Given activated user references R0(EMAIL:PENDING), R1(GOOGLE_PLUS:PENDING)
    When referenced user activates via EMAIL with data U1(EMAIL:R0;ID:ANY)
    Then in database R0(EMAIL:ACTIVATED), R1(GOOGLE_PLUS:PENDING)
    When referenced user activates via GOOGLE_PLUS with data U2(EMAIL:R0;ID:R1)
    Then in database R0(EMAIL:ACTIVATED), R1(GOOGLE_PLUS:ACTIVATED)
