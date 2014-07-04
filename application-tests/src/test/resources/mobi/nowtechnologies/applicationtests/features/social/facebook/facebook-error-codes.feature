Feature: Facebook error codes for not valid flows
  Scenario: Sign up and try to apply init promo with empty email
    Given First time user with device using JSON format for 5.2 version and hl_uk community and ANDROID device
    When User signs up the device
    Then Temporary registration info is available
    When User enters facebook info on his device and facebook returns empty email
    Then User gets 403 http error code and 662 error code and email is not specified message

  Scenario: Sign up and try to apply init promo with different id
    Given First time user with device using JSON format for 5.2 version and hl_uk community and ANDROID device
    When User signs up the device
    Then Temporary registration info is available
    When User enters facebook info on his device and facebook returns the response with different id
    Then User gets 403 http error code and 661 error code and invalid user facebook id message

  Scenario: Sign up and try to apply init promo with invalid access token
    Given First time user with device using JSON format for 5.2 version and hl_uk community and ANDROID device
    When User signs up the device
    Then Temporary registration info is available
    When User enters facebook info on his device and facebook returns the response with invalid access token
    Then User gets 403 http error code and 660 error code and invalid authorization token message