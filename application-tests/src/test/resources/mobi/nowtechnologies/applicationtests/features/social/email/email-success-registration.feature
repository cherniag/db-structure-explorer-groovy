Feature: Email registration success flows
  Scenario: Sign up and apply promo using email
    Given First time user with device using JSON format for all email versions and email communities and all devices available
    When User signs up the device
    Then Temporary registration info is available and the status is LIMITED and username is the same as device uid
    When User mobile calls server to generate the email
    Then registration info is in PENDING_ACTIVATION status
    And email was sent
    When User hits the link in email
    Then User has SUBSCRIBED status
    And registration info is in ACTIVATED status

  Scenario: Sign up and not completed activation via email
    Given First time user with device using JSON format for all email versions and email communities and all devices available
    When User signs up the device
    Then Temporary registration info is available and the status is LIMITED and username is the same as device uid
    When User mobile calls server to generate the email
    Then registration info is in PENDING_ACTIVATION status
    And email was sent
    When User signs up the device
    And registration info is in REGISTERED status

  Scenario: Register with different email on the same device
    Given First time user with device using JSON format for all email versions and email communities and all devices available
    When User signs up the device
    Then Temporary registration info is available and the status is LIMITED and username is the same as device uid
    When User mobile calls server to generate the email
    Then registration info is in PENDING_ACTIVATION status
    And email was sent
    When User hits the link in email
    Then User has SUBSCRIBED status
    And registration info is in ACTIVATED status
    Then User signs up the device
    And User has LIMITED status
    And device uid of previous user contains _disabled_at_ value
    And user changes email
    When User mobile calls server to generate the email
    Then registration info is in PENDING_ACTIVATION status
    And email was sent
    When User hits the link in email
    Then User has SUBSCRIBED status
    And registration info is in ACTIVATED status
    And user has username as new email

  Scenario: Register with different devices but the same email
    Given First time user with device using JSON format for all email versions and email communities and all devices available
    When User signs up the device
    Then Temporary registration info is available and the status is LIMITED and username is the same as device uid
    When User mobile calls server to generate the email
    Then registration info is in PENDING_ACTIVATION status
    And email was sent
    When User hits the link in email
    Then User has SUBSCRIBED status
    And registration info is in ACTIVATED status
    And user changes device
    Then User signs up the device
    And User has LIMITED status
    When User mobile calls server to generate the email
    Then email was sent
    And registration info is in PENDING_ACTIVATION status
    When User hits the link in email
    Then User has SUBSCRIBED status
    And registration info is in ACTIVATED status
    And user has username as new email





