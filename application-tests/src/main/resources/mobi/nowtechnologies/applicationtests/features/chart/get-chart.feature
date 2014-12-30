@Ready
Feature: Get chart command processing
  Scenario: Get Chart GET success
    Given Activated user with all devices using all formats for all versions above 6.6 and mtv1 community
    And chart update for 'HOT_TRACKS - MTV1' with medias '1,2,3' and publish time in the past exists in db
    And chart update for 'FIFTH_CHART - MTV1' with medias '4,5' and publish time in the past exists in db
    And chart update for 'HL_UK_PLAYLIST_1 - MTV1' with medias '1,2,3' and publish time in the past exists in db
    And chart update for 'HL_UK_PLAYLIST_2 - MTV1' with medias '4,5' and publish time in the past exists in db
    And chart update for 'HL_UK_PLAYLIST_3 - MTV1' with medias '1,2,3' and publish time in the past exists in db
    And chart update for 'OTHER_CHART - MTV1' with medias '4,5' and publish time in the past exists in db

    When the client requests GET /GET_CHART with resolution '400x800'
    Then the response code should be 200
    And count of playlist and tracks returned should be as in the database
    And playlist content should be as in the database
    And tracks content should be as in the database

