Feature: Successful registration flow for all communities
#devices: ANDROID, IOS, WINDOWS_PHONE
#all versions: 3.6, 3.7, 4.0, 4.2, 5.0, 5.2, 6.0, 6.1
#all communities: o2, vf_nz, hl_uk, hl_uk, demo, demo2, demo3

  Scenario: Registration of new user
    Given First time user with all devices using JSON and XML format for all api versions and all communities
    When User registers using device
    Then Temporary account is created
    And User receives following in SIGN_UP_DEVICE response:
      | status  | paymentType | timeOfMovingToLimitedStatusSeconds | fullyRegistred | freeTrial | nextSubPaymentSeconds | activation | hasAllDetails | phoneNumber |
      | LIMITED | UNKNOWN     | 0                                  | false          | false     | 0                     | REGISTERED | false         | null        |
    And 'deviceType' field is the same as sent during registration
    And 'deviceUID' field is the same as sent during registration
    And 'username' field is the same as 'deviceUID' sent during registration
    And In database user has username and deviceUID as deviceUID sent during registration
    And In database user has deviceType according to device on which registration is done
    And In database user has REGISTERED activation status
    And In database user does not have provider
    And In database user does not have last promotions
    And In database user does not have payment details

  Scenario: Re-registration of user on same device
    Given Registered user with all devices using JSON and XML format for all api versions and all communities
    When User registers using same device
    Then In database new temporary account does not appear
    And In database user account remains unchanged

  Scenario: Re-registration of user after entering number
    Given Entered phone number user with all devices using JSON and XML format for api versions 5.0 and below and above 6.0 and o2 and vodafone communities
    When User registers using same device
    Then In database new temporary account does not appear
    And In database user account remains unchanged

  Scenario: Re-registration of user after activation
    Given Activated user with all devices using JSON and XML format for all api versions and all communities
    When User registers using same device and username set after activation
    Then In database new temporary account does not appear
    And In database user account remains unchanged
