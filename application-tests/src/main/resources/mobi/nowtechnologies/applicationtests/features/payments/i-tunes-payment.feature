@Ready
Feature: Process iTunes subscription

  Scenario: Activated user sends valid Apple Store receipt and payment verification occurs immediately
    Given Activated user with IOS device using all formats for all versions bellow 6.12 and mtv1 community
    When Client is on Preview state
    And Subscribes via iTunes using existing recurrent payment policy
    And Sends ACC_CHECK request with provided valid Apple Store receipt
    Then Response should have 200 http status
    And Client should have 'SUBSCRIBED' status
    And Next sub payment should be the same as expiration date of receipt
    And Payment type should be 'ITUNES_SUBSCRIPTION'
    And Current payment details should not exist

  Scenario: Activated user sends valid Apple Store receipt and payment details are created
    Given Activated user with IOS device using all formats for all versions above 6.12 and mtv1 community
    When Client is on Preview state
    And Subscribes via iTunes using existing recurrent payment policy
    And Sends ACC_CHECK request with provided valid Apple Store receipt
    Then Response should have 200 http status
    And Response header 'Expires' should be in the future
    And Client should have 'LIMITED' status
    And Next sub payment should be in the past
    And User should have active current payment details
    And Payment type of current payment details should be 'ITUNES_SUBSCRIPTION'
    And Payment policy of current payment details should be the same as subscribed
