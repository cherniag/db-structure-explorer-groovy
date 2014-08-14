Feature: Unsuccessful registration flow for all communities
#devices: ANDROID, IOS, WINDOWS_PHONE
#all api versions: 3.6, 3.7, 4.0, 4.2, 5.0, 5.2, 6.0, 6.1
#all communities: o2, vf_nz, hl_uk, hl_uk, demo, demo2, demo3

  Scenario: User cannot be registered if not all required parameters are sent in SIGN_UP_DEVICE
    Given First time user with all devices using JSON and XML format for all api versions and all communities
    When User registers and client does not pass required parameter
    Then User gets 500 http error code for api versions below 6.0 and 400 http error code for api versions 6.0 and above with message regarding missing parameter
    And In database new account does not appear