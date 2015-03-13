@Ready
Feature: Urban airship token is stored when accessing acc check api.

  Scenario:  Device sends not blank urban airship token and token is persisted in DB. Version of API should be greater than 6.9
    Given First time user with all devices using JSON and XML formats with all versions above 6.9 and all communities
    When User checks acc check api with urban airship token
    Then Urban airship token should be persisted

  Scenario: Device sends not blank urban airship token and token is updated in DB. Version of API should be greater than 6.9
    Given Existing user with all devices using JSON and XML formats with all versions above 6.9 and all communities
    When User checks acc check api with new urban airship token
    Then Urban airship token should be updated
