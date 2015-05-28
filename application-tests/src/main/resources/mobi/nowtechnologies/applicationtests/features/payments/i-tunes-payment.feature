@Ready
Feature: Process iTunes subscription

  Scenario: Activated limited user sends valid Apple Store receipt to old version enpoint and payment verification occurs immediately
    Given Activated user with IOS device using all formats for all versions bellow 6.12 and mtv1 community
    And User is on LIMITED state
    When Subscribes in iTunes on product id of existing recurrent payment policy
    And Sends ACC_CHECK request with provided valid Apple Store receipt
    Then Response should have 200 http status
    And Client should have 'SUBSCRIBED' status
    And Next sub payment should be the same as expiration date of receipt
    And Payment type in response should be 'ITUNES_SUBSCRIPTION'
    And Current payment details should not exist

  Scenario: Activated limited user sends valid Apple Store receipt to new version enpoint and payment details are created
    Given Activated user with IOS device using all formats for all versions above 6.12 and mtv1 community
    And User is on LIMITED state
    When Subscribes in iTunes on product id of existing recurrent payment policy
    And Sends ACC_CHECK request with provided valid Apple Store receipt
    Then Response should have 200 http status
    And Response header 'Expires' should be in the future
    And Client should have 'LIMITED' status
    And Next sub payment should be in the past
    And User should have active current payment details
    And Payment type of current payment details should be 'iTunesSubscription'
    And Payment policy of current payment details should be the same as subscribed

  Scenario: Activated limited user sends creates iTunes payment details and sends ACC_CHECK again
    Given Activated user with IOS device using all formats for all versions above 6.12 and mtv1 community
    And User is on LIMITED state
    When Subscribes in iTunes on product id of existing recurrent payment policy
    And Sends ACC_CHECK request with provided valid Apple Store receipt
    Then Response should have 200 http status
    And Response header 'Expires' should be in the future
    And Client should have 'LIMITED' status
    And Next sub payment should be in the past
    And User should have active current payment details
    # Assume that CreatePaymentJob has not started yet
    When Sends ACC_CHECK request without Apple Store receipt
    Then Response should have 200 http status
    And Response header 'Expires' should be in the future
    And Client should have 'LIMITED' status
    And Next sub payment should be in the past
    And User should have active current payment details

  Scenario: Activated free trial user sends valid Apple Store receipt to new version enpoint and payment details are created
    Given Activated user with IOS device using all formats for all versions above 6.12 and mtv1 community
    And User is on free trial
    When Subscribes in iTunes on product id of existing recurrent payment policy
    And Sends ACC_CHECK request with provided valid Apple Store receipt
    Then Response should have 200 http status
    And Response header 'Expires' should be in the future
    And Free trial is skipped
    # Assume that WeeklyUpdateJob haven't updated user's status to LIMITED yet
    And Client should have 'SUBSCRIBED' status
    And User should have active current payment details
    And Payment type of current payment details should be 'iTunesSubscription'
    And Payment policy of current payment details should be the same as subscribed

  Scenario: Activated user with stored Apple Store receipt doesn't send it to new version enpoint but iTunes payment details are created - migration of subscribed users
    Given Activated user with IOS device using all formats for all versions above 6.12 and mtv1 community
    And User is subscribed on iTunes via existing recurrent payment policy without payment details
    When Sends ACC_CHECK request without Apple Store receipt
    Then Response should have 200 http status
    And Response header 'Expires' should be 'Thu, 01 Jan 1970 00:00:00 GMT'
    And Payment type in response should be 'ITUNES_SUBSCRIPTION'
    And User should have active current payment details
    And Payment type of current payment details should be 'iTunesSubscription'
    And Payment policy of current payment details should be the same as subscribed

  Scenario: Activated user with stored Apple Store receipt doesn't send it to new version enpoint but iTunes payment details are created - migration of limited users
    Given Activated user with IOS device using all formats for all versions above 6.12 and mtv1 community
    And User was subscribed on iTunes via existing recurrent payment policy without payment details
    When Sends ACC_CHECK request without Apple Store receipt
    Then Response should have 200 http status
    And Response header 'Expires' should be in the future
    And Payment type in response should be 'ITUNES_SUBSCRIPTION'
    And User should have active current payment details
    And Payment type of current payment details should be 'iTunesSubscription'
    And Payment policy of current payment details should be the same as subscribed

  Scenario: Activated user with active ITunes payment details sends new Apple Store receipt to new version enpoint with the same product id
    Given Activated user with IOS device using all formats for all versions above 6.12 and mtv1 community
    And User is subscribed on iTunes via existing recurrent payment policy with payment details
    When User sends in ACC_CHECK request new valid Apple Store receipt with the same product id
    Then Response should have 200 http status
    And Response header 'Expires' should be 'Thu, 01 Jan 1970 00:00:00 GMT'
    And Payment type in response should be 'ITUNES_SUBSCRIPTION'
    And User should have the same active current payment details
    And Apple Store receipt in current payment details should be updated with the last one

  Scenario: Activated user with active ITunes payment details sends new Apple Store receipt to new version enpoint with new product id
    Given Activated user with IOS device using all formats for all versions above 6.12 and mtv1 community
    And User is subscribed on iTunes via existing recurrent payment policy with payment details
    When User sends in ACC_CHECK request new valid Apple Store receipt with new product id
    Then Response should have 200 http status
    And Response header 'Expires' should be 'Thu, 01 Jan 1970 00:00:00 GMT'
    And Payment type in response should be 'ITUNES_SUBSCRIPTION'
    And User should have new active current payment details with new Apple Store receipt and new product id