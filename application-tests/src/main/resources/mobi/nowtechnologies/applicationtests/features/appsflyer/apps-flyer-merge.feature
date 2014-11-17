@Ready
Feature: AppsFlyer uid update during user merge

  Scenario Outline: Merge registered user with appsflyer uid to existing record with own appsflyer uid
    Given Activated via <Provider> user with all devices using all formats for all providers supported versions above 6.6 and providers supported communities which has appsflyer data
    When User activates on same device with the same provider profile which has own appsflyer data
    Then Temporary user's appsflyer data should be removed
    And Preserved appsflyer data in db should point to old user
    And Preserved appsflyer data in db should have new user's appsflyer uid
  Examples:
    | Provider   |
    | Facebook   |
    | GooglePlus |


  Scenario Outline: Merge registered user with appsflyer uid to existing record without appsflyer uid
    Given Activated via <Provider> user with all devices using all formats for all providers supported versions above 6.6 and providers supported communities which doesn't have appsflyer data
    When User activates on same device with the same provider profile which has own appsflyer data
    Then Temporary user's appsflyer data should be removed
    And Preserved appsflyer data in db should point to old user
    And Preserved appsflyer data in db should have new user's appsflyer uid
  Examples:
    | Provider   |
    | Facebook   |
    | GooglePlus |


  Scenario Outline: Merge registered user without appsflyer uid to existing record with appsflyer uid
    Given Activated via <Provider> user with all devices using all formats for all providers supported versions above 6.6 and providers supported communities which has appsflyer data
    When User activates on same device with the same provider profile which doesn't have appsflyer data
    Then Old user's appsflyer data should exist
    And Appsflyer data in db should have old user's appsflyer uid
  Examples:
    | Provider   |
    | Facebook   |
    | GooglePlus |


  Scenario Outline: Merge registered user without appsflyer uid to existing record without appsflyer uid
    Given Activated via <Provider> user with all devices using all formats for all providers supported versions above 6.6 and providers supported communities which doesn't have appsflyer data
    When User activates on same device with the same provider profile which doesn't have appsflyer data
    Then No appsflyer data should exist in db for both users
  Examples:
    | Provider   |
    | Facebook   |
    | GooglePlus |


  Scenario Outline: Merge registered user with appsflyer uid to existing record with own appsflyer uid
    Given Activated via <Provider> user with all devices using all formats for all providers supported versions above 6.6 and providers supported communities which doesn't have appsflyer data
    When User activates on another device with the same provider profile which has own appsflyer data
    Then Temporary user's appsflyer data should be removed
    And Preserved appsflyer data in db should point to old user
    And Preserved appsflyer data in db should have new user's appsflyer uid
  Examples:
    | Provider   |
    | Facebook   |
    | GooglePlus |


  Scenario Outline: Merge registered user with appsflyer uid to existing record with own appsflyer uid
    Given Two activated via <Provider> users with all devices using all formats for all providers supported versions above 6.6 and providers supported communities which have appsflyer data
    When First user activates on same device that has appsflyer data with provider profile of second user which has own appsflyer data
    Then Temporary user's appsflyer data should be removed
    And Second user's appsflyer data in db should point to second user
    And Second user's appsflyer data in db should have new user's appsflyer uid
  Examples:
    | Provider   |
    | Facebook   |
    | GooglePlus |