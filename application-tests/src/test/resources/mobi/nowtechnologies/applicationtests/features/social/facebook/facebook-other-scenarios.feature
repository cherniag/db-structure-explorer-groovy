Feature: Facebook registration success flow

  Scenario: Sign up with facebook with the same account but with different devices
    Given First time user with device using XML format for all facebook versions and facebook communities and all devices available
    When User signs up the device and enters facebook info on his device
    Then User is successfully registered and the promo is applied
    When User signs in the with different device using same account
    Then user account info is not changed

  Scenario: Sign up with facebook with the same device but with different accounts
    Given First time user with device using JSON format for all facebook versions and facebook communities and all devices available
    When User signs up the device and enters facebook info on his device
    Then User is successfully registered and the promo is applied
    When User signs in the with different account using same device
    Then user account info is updated
