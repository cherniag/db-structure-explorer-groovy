Feature: Successful Recurrent PayPal payment creation/re-creation and execution

  Scenario Outline: Free Trial user subscribes with Recurrent PayPal payment details
    #Free Trial user:
    #tb_users.status = 10,
    #(tb_users.nextSubPayment = tb_users.freeTrialExpiredMillis) > current date
    #tb_users.currentPaymentDetailsId = NULL
    Given Activated <provider> user on Free Trial with all devices using all formats for all supported versions and <community> community
    When User choose Premium access option on web portal and confirms subscription
    Then New active payment details are created and are linked to user
    And In database new payment details have paymentType as payPal, lastPaymentStatus as NONE, paymentPolicyId as id of payment policy for selected option, retriesOnError as set in application.properties
    And User account has current payment details as new payment details id
    When Current date becomes as user's next payment time
    Then Pending payment is created and linked to user, current user's payment details and payment policy same as set in payment details
    And Pending payment has payment system as payPal, type as FIRST and amount, currencyISO, duration, duration_unit as set in correspondent payment policy
    And User's payment details last status is changed to AWAITING
    When Payment is successfully confirmed by payPal
    Then User's payment details status is changed to SUCCESSFUL with made attempts as 0
    And New submitted payment is created and linked to user, current user's payment details and correspondent payment policy
    And New submitted payment has payment system as payPal, type as FIRST, status as SUCCESSFUL and amount, currencyISO, duration, duration_unit of correspondent payment policy
    And User's NextSubPayment is prolonged from current date to correspondent duration from submitted payment
  Examples:
    |community|provider   |
    |mtv1     |GOOGLE_PLUS|
    |         |FACEBOOK   |

  Scenario Outline: Free Trial user with active Recurrent PayPal payment details unsubscribes
    #Free Trial user with active payment details:
    #tb_users.status = 10,
    #(tb_users.nextSubPayment = tb_users.freeTrialExpiredMillis) > current date
    #tb_users.currentPaymentDetailsId = tb_paymentDetails.i
    #tb_users.last_successful_payment_details_id = NULL
    #tb_users.last_subscribed_payment_system = NULL
    #tb_paymentDetails.lastPaymentStatus = NONE
    #tb_paymentDetails.activated = TRUE
    Given Activated <provider> user on Free Trial with active Recurrent payPal payment details with all devices using all formats for all supported versions and <community> community
    When User choose to unsubscribe on web portal and confirms unsubscribing
    Then Current payment details becomes deactivated with description "Unsubscribed by user manually via web portal"
    When Current date becomes as user's next payment time
    Then User is moved to Preview mode
    #tb_users.status=11, tb_users.nextSubPayment = tb_users.freeTrialExpiredMillis < current date
  Examples:
    |community|provider   |
    |mtv1     |GOOGLE_PLUS|
    |         |FACEBOOK   |

  Scenario Outline: User on active Recurrent PayPal payment details unsubscribes
    #Acrive subscription:
    #tb_users.status = 10,
    #tb_users.nextSubPayment > current date
    #tb_users.freeTrialExpiredMillis < current date
    #tb_users.currentPaymentDetailsId = tb_paymentDetails.i
    #tb_users.last_successful_payment_details_id = tb_users.currentPaymentDetailsId
    #tb_users.last_subscribed_payment_system = payPal
    #tb_paymentDetails.lastPaymentStatus = SUCCESSFUL
    #tb_paymentDetails.activated = TRUE
    Given Activated <provider> user on subscription with active Recurrent payPal payment details with all devices using all formats for all supported versions and <community> community
    When User choose to unsubscribe on web portal and confirms unscubscribing
    Then Current payment details becomes deactivated with description "Unsubscribed by user manually via web portal"
    When Current date becomes as user's NextSubPayment
    Then User is moved to Preview mode
  Examples:
    |community|provider   |
    |mtv1     |GOOGLE_PLUS|
    |         |FACEBOOK   |

  Scenario Outline: User on Preview mode after Free Trial subscribes via Recurrent PayPal payment
      #Preview mode after Free Trial:
      #tb_users.status = 11,
      #(tb_users.nextSubPayment = tb_users.freeTrialExpiredMillis) < current date
      #tb_users.currentPaymentDetailsId = NULL
    Given Activated <provider> user on Preview mode without payment details with all devices using all formats for all supported versions and <community> community
    When User choose Premium access option on web portal and confirms subscription
    Then New active payment details are created and are linked to user
    And In database new payment details have paymentType as payPal, lastPaymentStatus as NONE, paymentPolicyId as id of payment policy for selected option, retriesOnError as set in application.properties
    And User account has current payment details as new payment details id
    When Payment job starts payment
    Then Pending payment is created and linked to user, current user's payment details and payment policy same as set in payment details
    And Pending payment has payment system as payPal, type as FIRST and amount, currencyISO, duration, duration_unit as set in correspondent payment policy
    And User's payment details last status is changed to AWAITING
    When Payment is successfully confirmed by payPal
    Then User's payment details status is changed to SUCCESSFUL with made attempts as 0
    And New submitted payment is created and linked to user, current user's payment details and correspondent payment policy
    And New submitted payment has payment system as payPal, type as FIRST, status as SUCCESSFUL and amount, currencyISO, duration, duration_unit of correspondent payment policy
    And User's NextSubPayment is prolonged from current date to correspondent duration from submitted payment
  Examples:
    |community|provider   |
    |mtv1     |GOOGLE_PLUS|
    |         |FACEBOOK   |

  Scenario Outline: User on Preview mode after subscription subscribes via Recurrent PayPal payment
    #Preview mode after subscription:
    #tb_users.status = 11,
    #tb_users.nextSubPayment < current
    #tb_users.freeTrialExpiredMillis < current
    #tb_users.currentPaymentDetailsId = tb_paymentDetails.i
    #tb_users.last_successful_payment_details_id = tb_users.currentPaymentDetailsId
    #tb_users.last_subscribed_payment_system = payPal
    #tb_paymentDetails.lastPaymentStatus = SUCCESSFUL
    #tb_paymentDetails.activated = false
    Given Activated <provider> user on Preview mode with deactivated Recurrent PayPal payment details with all devices using all formats for all supported versions and <community> community
    When User choose Premium access option on web portal and confirms subscription
    Then New active payment details are created and are linked to user
    And In database new payment details have paymentType as payPal, lastPaymentStatus as NONE, paymentPolicyId as id of payment policy for selected option, retriesOnError as set in application.properties
    And User account has current payment details as new payment details id
    When Payment job starts payment
    Then Pending payment is created and linked to user, current user's payment details and payment policy same as set in payment details
    And Pending payment has payment system as payPal, type as REGULAR and amount, currencyISO, duration, duration_unit as set in correspondent payment policy
    And User's payment details last status is changed to AWAITING
    When Payment is successfully confirmed by payPal
    Then User's payment details status is changed to SUCCESSFUL with made attempts as 0
    And New submitted payment is created and linked to user, current user's payment details and correspondent payment policy
    And New submitted payment has payment system as payPal, type as REGULAR, status as SUCCESSFUL and amount, currencyISO, duration, duration_unit of correspondent payment policy
    And User's NextSubPayment is prolonged from current date to correspondent duration from submitted payment
  Examples:
    |community|provider   |
    |mtv1     |GOOGLE_PLUS|
    |         |FACEBOOK   |

  Scenario Outline: User on active Recurrent PayPal payment details makes recurrent payment
    #Acrive subscription:
    #tb_users.status = 10,
    #tb_users.nextSubPayment > current date
    #tb_users.freeTrialExpiredMillis < current date
    #tb_users.currentPaymentDetailsId = tb_paymentDetails.i
    #tb_users.last_successful_payment_details_id = tb_users.currentPaymentDetailsId
    #tb_users.last_subscribed_payment_system = payPal
    #tb_paymentDetails.lastPaymentStatus = SUCCESSFUL
    #tb_paymentDetails.activated = TRUE
    Given Activated Subscribed <provider> user with active Recurrent payPal payment details with all devices using all formats for all supported versions and <community> community
    When Current date becomes as user's next payment time
    Then Pending payment is created and linked to user, current user's payment details and payment policy same as set in payment details
    And Pending payment has payment system as payPal, type as REGULAR and amount, currencyISO, duration, duration_unit as set in correspondent payment policy
    And User's payment details last status is changed to AWAITING
    When Payment is successfully confirmed by payPal
    Then User's payment details status is changed to SUCCESSFUL with made attempts as 0
    And New submitted payment is created and linked to user, current user's payment details and correspondent payment policy
    And New submitted payment has payment system as payPal, type as REGULAR, status as SUCCESSFUL and amount, currencyISO, duration, duration_unit of correspondent payment policy
    And User's NextSubPayment is prolonged from current date to correspondent duration from submitted payment
  Examples:
    |community|provider   |
    |mtv1     |GOOGLE_PLUS|
    |         |FACEBOOK   |