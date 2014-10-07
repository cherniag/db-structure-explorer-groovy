@Ready
Feature: Registration, phone number and activate

  Scenario: Registration, enter phone number and apply init promo
    Given First time user with device using JSON and XML formats for all versions and o2 community and all devices available
    When User registers using device
    Then User should be registered in system

    When User sends o2 valid phone number
    Then User should receive ENTERED_NUMBER activation status in phone number response
    And User should have ENTERED_NUMBER activation status in database

    When User sends valid OTAC for applying promo
    Then User should receive ACTIVATED activation status in activation response
    And promo should be applied
    And User should have ACTIVATED activation status in database


  Scenario: Registration, enter phone number and auto opt in for 3G
    Given First time user with device using JSON and XML formats for all versions and o2 community and all devices available
    When User registers using device
    Then User should be registered in system

    When User sends o2 valid phone number with provider O2 and segment CONSUMER and tariff _3G
    Then User should receive ENTERED_NUMBER activation status in phone number response
    And User should have ENTERED_NUMBER activation status in database

    When User sends valid OTAC for applying promo
    Then User should receive ACTIVATED activation status in activation response
    And promo should be applied
    And promo should have AUDIO media type
    And User should have ACTIVATED activation status in database


  Scenario: Registration, enter phone number and auto opt in for 4G
    Given First time user with device using JSON and XML formats for all versions and o2 community and all devices available
    When User registers using device
    Then User should be registered in system

    When User sends o2 valid phone number with provider O2 and segment CONSUMER and tariff _4G
    Then User should receive ENTERED_NUMBER activation status in phone number response
    And User should have ENTERED_NUMBER activation status in database

    When User sends valid OTAC for applying promo
    Then User should receive ACTIVATED activation status in activation response
    And promo should be applied
    And promo should have VIDEO_AND_AUDIO media type
    And User should have ACTIVATED activation status in database
