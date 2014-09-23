Feature: Reactivation request flow for o2 community
 Scenario: Eligible for reactivation request
   Given Existing user has mobile application for 6.0 and above
   When user mobile makes account check request if it is unsubscribed
   Then server sends reactivation required response back if user info is in reactivation info tables
   When user mobile reactivates
   Then auto-opt-in is always sent when its mobile is in campaign info
   And reactivation request info is changed after activation
   And campaign promo is applied
   And next account check will not require reactivation


