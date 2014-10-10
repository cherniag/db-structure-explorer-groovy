@Ready
Feature: Successful activation via Google Plus in google plus supported communities
#devices: ANDROID, IOS, WINDOWS_PHONE
#google plus supported versions: 6.0, 6.1
#google plus supported communities: hl_uk, demo, demo2, demo3
  Scenario: Activation of new user via Google Plus in google plus supported communities
    Given Registered user with all devices using JSON and XML format for all google plus supported versions and google plus supported communities
    When Registered user enters Google Plus credentials
    Then Default promo set in services properties is applied
    And  User receives following in the SIGN_IN_GOOGLE_PLUS response:
      | status     | paymentType | fullyRegistred | freeTrial | activation | provider    | hasAllDetails |
      | SUBSCRIBED | UNKNOWN     | true           | true      | ACTIVATED  | gp | true          |
    And 'deviceType' field is the same as sent during registration
    And 'deviceUID' field is the same as sent during registration
    And 'username' field is the same as entered Google Plus email
    And 'timeOfMovingToLimitedStatusSeconds' and 'nextSubPaymentSeconds' fields are the end date of given promotion
    And 'userDetails' filed contains all specified Google Plus details
    And In database user has username as entered Google Plus email
    And In database user has deviceType according to device on which registration is done
    And In database user has ACTIVATED activation status
    And In database user has GOOGLE_PLUS provider
    And In database user has last promotion according to promotion settings
    And In database user does not have payment details
    And In database user has log for applied promotion
    And In database user has Google Plus details the same as specified in Google Plus account