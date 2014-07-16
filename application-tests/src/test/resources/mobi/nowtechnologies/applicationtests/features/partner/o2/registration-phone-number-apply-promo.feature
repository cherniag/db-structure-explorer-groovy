Feature: Registration, phone number and apply init promo
  @TestActivation
  Scenario: Registration, enter phone number and apply init promo 2
    Given First time user with device using JSON format for all o2 versions and o2 community and all devices available
    When User registers using device
    Then User should be registered in system
    When User sends o2 valid phone number
    Then User should be registered in ENTERED_NUMBER state
