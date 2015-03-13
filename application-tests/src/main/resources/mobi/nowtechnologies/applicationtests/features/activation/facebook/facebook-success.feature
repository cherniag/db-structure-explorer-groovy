@Ready
Feature: Successful user activation via Facebook in facebook supported communities
#devices: ANDROID, IOS, WINDOWS_PHONE
#facebook supported versions: 5.2, 6.0, 6.1
#facebook supported communities: hl_uk, demo, demo2, demo3
  Scenario: Activation of new user via Facebook in facebook supported communities
    Given Registered user with all devices using JSON and XML format for all facebook supported versions and facebook supported communities
    When Registered user enters Facebook credentials
    Then Default promo set in services properties is applied
    And User receives following in the SIGN_IN_FACEBOOK response:
      | status     | paymentType | fullyRegistred | freeTrial | activation | provider | hasAllDetails |
      | SUBSCRIBED | UNKNOWN     | true           | true      | ACTIVATED  | fb       | true          |
    And 'deviceType' field is the same as sent during registration
    And 'deviceUID' field is the same as sent during registration
    And 'username' field is the same as entered Facebook email
    And 'timeOfMovingToLimitedStatusSeconds' and 'nextSubPaymentSeconds' fields are the end date of given promotion
    And 'userDetails' filed contains correct facebook details
    And In database user has username as entered Facebook email
    And In database user has deviceType according to device on which registration is done
    And In database user has ACTIVATED activation status
    And In database user has FACEBOOK provider
    And In database user has last promotion according to promotion settings
    And In database user does not have payment details
    And In database user has log for applied promotion
    And In database user has facebook details the same as specified in facebook account

#  Scenario: Activation of new user via Facebook in facebook supported communities. API version above 6.10
#    Given Registered user with all devices using JSON and XML format for all facebook supported versions above 6.10 and facebook supported communities
#    When Registered user enters Facebook credentials
#    Then User receives additional facebook profile image url in the SIGN_IN_FACEBOOK response