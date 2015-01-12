Feature: Successful o2Psms payment creation/re-creation and execution

  Scenario Outline: Free Trial user subscribes with one of o2Psms payment details
    #Free Trial user:
    #tb_users.status = 10,
    #(tb_users.nextSubPayment = tb_users.freeTrialExpiredMillis) > current date
    #tb_users.currentPaymentDetailsId = NULL
    Given Activated <tariff> O2 CONSUMER user on Free Trial with all devices using all formats for all supported versions and o2 community
    When User choose o2Psms <option> option on web portal and confirms subscription
    Then New active payment details are created with user's phone number and are linked to user
    And In database new payment details have paymentType as o2Psms, lastPaymentStatus as NONE, paymentPolicyId as id of payment policy for selected option, retriesOnError as set in application.properties
    And User account has current payment details as new payment details id
    When Current date becomes as user's next payment time minus advanced payment time set in Payment Policy
    ## unix_timestamp(now()) = tb_users.NextSubPayment - tb_paymentPolicy.advanced_payment_seconds
    Then Pending payment is created and linked to user, current user's payment details and payment policy same as set in payment details
    And Pending payment has payment system as o2Psms, type as FIRST and amount, currencyISO, duration as set in correspondent payment policy
    And User's payment details last status is changed to AWAITING
    When Payment is successfully confirmed by o2Psms
    Then User's payment details status is changed to SUCCESSFUL with made attempts as 1
    And New submitted payment is created and linked to user and current user's payment details
    And New submitted payment has payment system as o2Psms, type as FIRST, status as SUCCESSFUL and amount, currencyISO, duration of correspondent payment policy
    And User's NextSubPayment is prolonged from current date to correspondent duration from submitted payment
  Examples:
    |tariff| option          |
    |_3G   |AUDIO-1          |
    |_3G   |AUDIO-2          |
    |_3G   |AUDIO-3          |
    |_4G   |AUDIO-1          |
    |_4G   |AUDIO-2          |
    |_4G   |AUDIO-3          |
    |_4G   |VIDEO_AND_AUDIO-1|
    |_4G   |VIDEO_AND_AUDIO-2|
    |_4G   |VIDEO_AND_AUDIO-3|

  Scenario Outline: Free Trial user with active o2Psms payment details unsubscribes
    #Free Trial user with active payment details:
    #tb_users.status = 10,
    #(tb_users.nextSubPayment = tb_users.freeTrialExpiredMillis) > current date
    #tb_users.currentPaymentDetailsId = tb_paymentDetails.i
    #tb_users.last_successful_payment_details_id = NULL
    #tb_users.last_subscribed_payment_system = NULL
    #tb_paymentDetails.lastPaymentStatus = NONE
    #tb_paymentDetails.activated = TRUE
    Given Activated <tariff> O2 CONSUMER user on Free Trial with active o2Psms <option> payment details with all devices using all formats for all supported versions and o2 community
    When User choose to unsubscribe on web portal and confirms unscubscribing
    Then Current payment details becomes deactivated with description "Unsubscribed by user manually via web portal"
    When Current date becomes as user's next payment time
    Then User is moved to Preview mode
    #tb_users.status=11, tb_users.nextSubPayment = tb_users.freeTrialExpiredMillis < current date
  Examples:
    |tariff| option          |
    |_3G   |AUDIO-1          |
    |_3G   |AUDIO-2          |
    |_3G   |AUDIO-3          |
    |_4G   |AUDIO-1          |
    |_4G   |AUDIO-2          |
    |_4G   |AUDIO-3          |
    |_4G   |VIDEO_AND_AUDIO-1|
    |_4G   |VIDEO_AND_AUDIO-2|
    |_4G   |VIDEO_AND_AUDIO-3|

  Scenario Outline: Free Trial user with active o2Psms payment details re-subscribes with different o2Psms payment details
    Given Activated <tariff> O2 CONSUMER user on Free Trial with active o2Psms <option> payment details with all devices using all formats for all supported versions and  o2 community
    When User choose new <new_option> option as payment option on web portal and confirms subscription
    Then New active payment details are created with user's phone number and are linked to user
    And Current payment details becomes deactivated with description "Commit new payment details"
    And In database new payment details have paymentType as o2Psms, lastPaymentStatus as NONE, paymentPolicyId as id of payment policy for selected option, retriesOnError as set in application.properties
    And User has current payment details as new payment details
    When Current date becomes as user's next payment time minus advanced payment time set in Payment Policy
    Then Pending payment is created and linked to user, current user's payment details and payment policy same as set in payment details
    And Pending payment has payment system as o2Psms, type as FIRST and amount, currencyISO, duration as set in correspondent payment policy
    And User's payment details last status is changed to AWAITING
    When Payment is successfully confirmed by o2Psms
    Then User's payment details status is changed to SUCCESSFUL with made attempts as 1
    And New submitted payment is created and linked to user and current user's payment details
    And New submitted payment has payment system as o2Psms, type as FIRST, status as SUCCESSFUL and amount, currencyISO, duration of correspondent payment policy
    And User's NextSubPayment is prolonged from current date to correspondent duration from submitted payment
  Examples:
    |tariff| option          |new_option       |
    |_3G   |AUDIO-1          |AUDIO-2          |
    |_3G   |AUDIO-2          |AUDIO-3          |
    |_3G   |AUDIO-3          |AUDIO-1          |
    |_4G   |AUDIO-1          |AUDIO-2          |
    |_4G   |AUDIO-2          |AUDIO-3          |
    |_4G   |AUDIO-3          |VIDEO_AND_AUDIO-1|
    |_4G   |VIDEO_AND_AUDIO-1|VIDEO_AND_AUDIO-2|
    |_4G   |VIDEO_AND_AUDIO-2|VIDEO_AND_AUDIO-3|
    |_4G   |VIDEO_AND_AUDIO-3|AUDIO-1          |

  Scenario Outline: User on active subscription via o2Psms unsubscribes
    #Acrive subscription:
    #tb_users.status = 10,
    #tb_users.nextSubPayment < current date
    #tb_users.freeTrialExpiredMillis > current date
    #tb_users.currentPaymentDetailsId = tb_paymentDetails.i
    #tb_users.last_successful_payment_details_id = tb_users.currentPaymentDetailsId
    #tb_users.last_subscribed_payment_system = o2Psms
    #tb_paymentDetails.lastPaymentStatus = SUCCESSFUL
    #tb_paymentDetails.activated = TRUE
    Given Activated <tariff> O2 CONSUMER user on subscription with active o2Psms <option> payment details with all devices using all formats for all supported versions and o2 community
    When User choose to unsubscribe on web portal and confirms unscubscribing
    Then Current payment details becomes deactivated with description "Unsubscribed by user manually via web portal"
    When Current date becomes as user's NextSubPayment
    Then User is moved to Preview mode
  Examples:
    |tariff| option          |
    |_3G   |AUDIO-1          |
    |_3G   |AUDIO-2          |
    |_3G   |AUDIO-3          |
    |_4G   |AUDIO-1          |
    |_4G   |AUDIO-2          |
    |_4G   |AUDIO-3          |
    |_4G   |VIDEO_AND_AUDIO-1|
    |_4G   |VIDEO_AND_AUDIO-2|
    |_4G   |VIDEO_AND_AUDIO-3|

  Scenario Outline: User on active subscription via o2Psms re-subscribes with different o2Psms payment details
    Given Activated <tariff> O2 CONSUMER user on subscription with active o2Psms <option> payment details with all devices using all formats for all supported versions and  o2 community
    When User choose new <new_option> option as payment option on web portal and confirms subscription
    Then New active payment details are created with user's phone number and are linked to user
    And Current payment details becomes deactivated with description "Commit new payment details"
    And In database new payment details have paymentType as o2Psms, lastPaymentStatus as NONE, paymentPolicyId as id of payment policy for selected option, retriesOnError as set in application.properties
    And User has current payment details as new payment details
    When Current date becomes as user's next payment time minus advanced payment time set in Payment Policy
    Then Pending payment is created and linked to user, current user's payment details and payment policy same as set in payment details
    And Pending payment has payment system as o2Psms, type as REGULAR and amount, currencyISO, duration as set in correspondent payment policy
    And User's payment details last status is changed to AWAITING
    When Payment is successfully confirmed by o2Psms
    Then User's payment details status is changed to SUCCESSFUL with made attempts as 1
    And New submitted payment is created and linked to user and current user's payment details
    And New submitted payment has payment system as o2Psms, type as REGULAR, status as SUCCESSFUL and amount, currencyISO, duration of correspondent payment policy
    And User's NextSubPayment is prolonged from current date to correspondent duration from submitted payment
  Examples:
    |tariff| option          |new_option       |
    |_3G   |AUDIO-1          |AUDIO-2          |
    |_3G   |AUDIO-2          |AUDIO-3          |
    |_3G   |AUDIO-3          |AUDIO-1          |
    |_4G   |AUDIO-1          |AUDIO-2          |
    |_4G   |AUDIO-2          |AUDIO-3          |
    |_4G   |AUDIO-3          |VIDEO_AND_AUDIO-1|
    |_4G   |VIDEO_AND_AUDIO-1|VIDEO_AND_AUDIO-2|
    |_4G   |VIDEO_AND_AUDIO-2|VIDEO_AND_AUDIO-3|
    |_4G   |VIDEO_AND_AUDIO-3|AUDIO-1          |

  Scenario Outline: User on Preview mode after Free Trial subscribes via o2Psms
    #Preview mode after Free Trial:
    #tb_users.status = 11,
    #(tb_users.nextSubPayment = tb_users.freeTrialExpiredMillis) < current date
    #tb_users.currentPaymentDetailsId = NULL
    Given Activated <tariff> O2 CONSUMER user on Preview mode without payment details with all devices using all formats for all supported versions and o2 community
    When User choose o2Psms <option> option on web portal and confirms subscription
    Then New active payment details are created with user's phone number and are linked to user
    And In database new payment details have paymentType as o2Psms, lastPaymentStatus as NONE, paymentPolicyId as id of payment policy for selected option, retriesOnError as set in application.properties
    And User account has current payment details as new payment details id
    When Payment job starts payment
    Then Pending payment is created and linked to user, current user's payment details and payment policy same as set in payment details
    And Pending payment has payment system as o2Psms, type as FIRST and amount, currencyISO, duration as set in correspondent payment policy
    And User's payment details last status is changed to AWAITING
    When Payment is successfully confirmed by o2Psms
    Then User's payment details status is changed to SUCCESSFUL with made attempts as 1
    And New submitted payment is created and linked to user and current user's payment details
    And New submitted payment has payment system as o2Psms, type as FIRST, status as SUCCESSFUL and amount, currencyISO, duration of correspondent payment policy
    And User's NextSubPayment is prolonged from current date to correspondent duration from submitted payment
  Examples:
    |tariff| option          |
    |_3G   |AUDIO-1          |
    |_3G   |AUDIO-2          |
    |_3G   |AUDIO-3          |
    |_4G   |AUDIO-1          |
    |_4G   |AUDIO-2          |
    |_4G   |AUDIO-3          |
    |_4G   |VIDEO_AND_AUDIO-1|
    |_4G   |VIDEO_AND_AUDIO-2|
    |_4G   |VIDEO_AND_AUDIO-3|

  Scenario Outline: User on Preview mode after subscription subscribes via o2Psms
    #Preview mode after subscription:
    #tb_users.status = 11,
    #tb_users.nextSubPayment < current
    #tb_users.freeTrialExpiredMillis < current
    #tb_users.currentPaymentDetailsId = tb_paymentDetails.i
    #tb_users.last_successful_payment_details_id = tb_users.currentPaymentDetailsId
    #tb_users.last_subscribed_payment_system = o2Psms
    #tb_paymentDetails.lastPaymentStatus = SUCCESSFUL
    #tb_paymentDetails.activated = false
    Given Activated <tariff> O2 CONSUMER user on Preview mode with deactivated o2Psms <option> payment details with all devices using all formats for all supported versions and o2 community
    When User choose o2Psms <new_option> option on web portal and confirms subscription
    Then New active payment details are created with user's phone number and are linked to user
    And In database new payment details have paymentType as o2Psms, lastPaymentStatus as NONE, paymentPolicyId as id of payment policy for selected option, retriesOnError as set in application.properties
    And User account has current payment details as new payment details id
    When Payment job starts payment
    Then Pending payment is created and linked to user, current user's payment details and payment policy same as set in payment details
    And Pending payment has payment system as o2Psms, type as REGULAR and amount, currencyISO, duration as set in correspondent payment policy
    And User's payment details last status is changed to AWAITING
    When Payment is successfully confirmed by o2Psms
    Then User's payment details status is changed to SUCCESSFUL with made attempts as 1
    And New submitted payment is created and linked to user and current user's payment details
    And New submitted payment has payment system as o2Psms, type as REGULAR, status as SUCCESSFUL and amount, currencyISO, duration of correspondent payment policy
    And User's NextSubPayment is prolonged from current date to correspondent duration from submitted payment
  Examples:
    |tariff| option          |new_option       |
    |_3G   |AUDIO-1          |AUDIO-2          |
    |_3G   |AUDIO-2          |AUDIO-3          |
    |_3G   |AUDIO-3          |AUDIO-1          |
    |_4G   |AUDIO-1          |AUDIO-2          |
    |_4G   |AUDIO-2          |AUDIO-3          |
    |_4G   |AUDIO-3          |VIDEO_AND_AUDIO-1|
    |_4G   |VIDEO_AND_AUDIO-1|VIDEO_AND_AUDIO-2|
    |_4G   |VIDEO_AND_AUDIO-2|VIDEO_AND_AUDIO-3|
    |_4G   |VIDEO_AND_AUDIO-3|AUDIO-1          |

  Scenario Outline:
    Given Activated <tariff> O2 CONSUMER user on <user_status> with failed advanced o2Psms <option> payment details with all devices using all formats for all supported versions and o2 community
    When Current date becomes as user's next payment time
    Then Pending payment is created and linked to user, current user's payment details and payment policy same as set in payment details
    And Pending payment has payment system as o2Psms, type as REGULAR and amount, currencyISO, duration as set in correspondent payment policy
    And User's payment details last status is changed to AWAITING
    When Payment is successfully confirmed by o2Psms
    Then User's payment details status is changed to SUCCESSFUL with made attempts as 1
    And New submitted payment is created and linked to user and current user's payment details
    And New submitted payment has payment system as o2Psms, type as REGULAR, status as SUCCESSFUL and amount, currencyISO, duration of correspondent payment policy
    And User's NextSubPayment is prolonged from current date to correspondent duration from submitted payment
  Examples:
    |tariff| option          |user_status|
    |_3G   |AUDIO-1          |Free Trial|
    |_3G   |AUDIO-2          |Free Trial|
    |_3G   |AUDIO-3          |Free Trial|
    |_4G   |AUDIO-1          |Free Trial|
    |_4G   |AUDIO-2          |Free Trial|
    |_4G   |AUDIO-3          |Free Trial|
    |_4G   |VIDEO_AND_AUDIO-1|Free Trial|
    |_4G   |VIDEO_AND_AUDIO-2|Free Trial|
    |_4G   |VIDEO_AND_AUDIO-3|Free Trial|
    |_3G   |AUDIO-1          |Subscribed|
    |_3G   |AUDIO-2          |Subscribed|
    |_3G   |AUDIO-3          |Subscribed|
    |_4G   |AUDIO-1          |Subscribed|
    |_4G   |AUDIO-2          |Subscribed|
    |_4G   |AUDIO-3          |Subscribed|
    |_4G   |VIDEO_AND_AUDIO-1|Subscribed|
    |_4G   |VIDEO_AND_AUDIO-2|Subscribed|
    |_4G   |VIDEO_AND_AUDIO-3|Subscribed|