@Ready
Feature: get context
  Scenario: get context and check all info is available
    Given First time user with all devices using JSON format with all versions to get context and get context supported communities available
    And freemium info is set
    When user invokes get context command
    Then response has 200 http response code
    And value by [context.referrals.required] path in response is same as set
    And value by [context.referrals.activated] path in response is equal to 0
    And value by [context.playlists.behaviorTemplates.SHUFFLED.offline] path in response is equal to false
    And value by [context.playlists.behaviorTemplates.NORMAL.offline] path in response is equal to true
    And value by [context.playlists.behaviorTemplates.PREVIEW.offline] path in response is equal to false
    And value by [context.playlists.behaviorTemplates.PREVIEW.playTime] path in response is equal to 30
    And value by [context.serverTime] path is equal to value by [context.favorites.instructions.0.validFrom] path
    And value by [context.serverTime] path is equal to value by [context.ads.instructions.0.validFrom] path

  Scenario: get context for different matched referral cases
    Given First time user with all devices using JSON format with all versions to get context and get context supported communities available
    When user invokes get context command
    And referrals info is created

    And user is in LIMITED state

    And the case is the following [----(Rm)--------(NOW)-----(Re)-------]
    And chart 84 configured FREE_TRIAL:NORMAL,locked:<NULL> and LIMITED:PREVIEW,locked:action1
    When referral data matches current case
    When user invokes get context command
    Then chart 84 contains [{now};SHUFFLED;null, {exp};PREVIEW;{community}://content/playlist?id=84&action=action1]

    And the case is the following [----(Rm)--------(NOW)----------------]
    And chart 85 configured FREE_TRIAL:NORMAL,locked:<NULL> and LIMITED:PREVIEW,locked:action2
    When referral data matches current case
    When user invokes get context command
    Then chart 85 contains [{now};SHUFFLED;null]

  Scenario: get context for different matched referral cases
    Given First time user with all devices using JSON format with all versions to get context and get context supported communities available
    When user invokes get context command
    And referrals info is created

    And user is in FREE_TRIAL state

    And the case is the following [----(Rm)-----(NOW)-------(FT)--(Re)--]
    And chart 86 configured FREE_TRIAL:NORMAL,locked:<NULL> and LIMITED:PREVIEW,locked:action3
    When referral data matches current case
    When user invokes get context command
    Then chart 86 contains [{now};NORMAL;null, {free_trial_exp};SHUFFLED;null, {exp};PREVIEW;{community}://content/playlist?id=86&action=action3]

    And the case is the following [----(Rm)-----(NOW)-------(FT)--------]
    And chart 87 configured FREE_TRIAL:NORMAL,locked:<NULL> and LIMITED:PREVIEW,locked:action4
    When referral data matches current case
    When user invokes get context command
    Then chart 87 contains [{now};NORMAL;null, {free_trial_exp};SHUFFLED;null]

    And the case is the following [----(Rm)-----(NOW)-(Re)--(FT)--------]
    And chart 87 configured FREE_TRIAL:NORMAL,locked:<NULL> and LIMITED:PREVIEW,locked:action5
    When referral data matches current case
    When user invokes get context command
    Then chart 87 contains [{now};NORMAL;null, {exp};NORMAL;null, {free_trial_exp};PREVIEW;{community}://content/playlist?id=87&action=action5]

