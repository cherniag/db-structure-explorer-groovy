Feature: Facebook registration success flow
  Scenario: Sign up and apply promo for facebook for the first sign up with success for XML format
    Given First time user with device using XML format for all facebook versions and facebook communities and all devices available
    When User signs up the device
    Then Temporary registration info is available
    When User enters facebook info on his device
    Then User is successfully registered and the promo is applied
    When User tries to get chart
    Then it gets response successfully

  Scenario: Sign up and apply promo for facebook for the first sign up with success for JSON format
    Given First time user with device using JSON format for all facebook versions and facebook communities and all devices available
    When User signs up the device
    Then Temporary registration info is available
    When User enters facebook info on his device
    Then User is successfully registered and the promo is applied
    When User tries to get chart
    Then it gets response successfully

  Scenario: Sign up and apply promo for facebook for the first sign up with success for JSON format and Facebook returns only City value in response
    Given First time user with device using JSON format and Facebook returns only City location value in response for all facebook versions and facebook communities and all devices available
    When User signs up the device
    Then Temporary registration info is available
    When User enters facebook info on his device
    Then User is successfully registered and the promo is applied
    When User tries to get chart
    Then it gets response successfully


