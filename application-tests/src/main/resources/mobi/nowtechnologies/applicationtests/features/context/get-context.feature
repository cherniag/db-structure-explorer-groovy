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
    And chart chart_ID configured FREE_TRIAL:NORMAL,locked:<NULL> and LIMITED:PREVIEW,locked:action1
    When referral data matches current case
    When user invokes get context command
    Then chart chart_ID contains [{now};PREVIEW;{community}://content/playlist?id={chart_ID}&action=action1, {exp};PREVIEW;{community}://content/playlist?id={chart_ID}&action=action1]

    And the case is the following [----(Rm)--------(NOW)-----(Re)-------]
    And chart chart_ID configured FREE_TRIAL:NORMAL,locked:<NULL> and LIMITED:PREVIEW,locked:refer_a_friend
    When referral data matches current case
    When user invokes get context command
    Then chart chart_ID contains [{now};SHUFFLED;null, {exp};PREVIEW;{community}://content/playlist?id={chart_ID}&action=refer_a_friend]

    And the case is the following [----(Rm)--------(NOW)----------------]
    And chart chart_ID configured FREE_TRIAL:NORMAL,locked:<NULL> and LIMITED:PREVIEW,locked:action2
    When referral data matches current case
    When user invokes get context command
    Then chart chart_ID contains [{now};PREVIEW;{community}://content/playlist?id={chart_ID}&action=action2]

    And the case is the following [----(Rm)--------(NOW)----------------]
    And chart chart_ID configured FREE_TRIAL:NORMAL,locked:<NULL> and LIMITED:PREVIEW,locked:refer_a_friend
    When referral data matches current case
    When user invokes get context command
    Then chart chart_ID contains [{now};SHUFFLED;null]

  Scenario: get context for different matched referral cases
    Given First time user with all devices using JSON format with all versions to get context and get context supported communities available
    When user invokes get context command
    And referrals info is created

    And user is in FREE_TRIAL state

    And the case is the following [----(Rm)-----(NOW)-------(FT)--(Re)--]
    And chart chart_ID configured FREE_TRIAL:NORMAL,locked:<NULL> and LIMITED:PREVIEW,locked:action3
    When referral data matches current case
    When user invokes get context command
    Then chart chart_ID contains [{now};NORMAL;null, {free_trial_exp};PREVIEW;{community}://content/playlist?id={chart_ID}&action=action3, {exp};PREVIEW;{community}://content/playlist?id={chart_ID}&action=action3]

    And the case is the following [----(Rm)-----(NOW)-------(FT)--(Re)--]
    And chart chart_ID configured FREE_TRIAL:NORMAL,locked:<NULL> and LIMITED:PREVIEW,locked:refer_a_friend
    When referral data matches current case
    When user invokes get context command
    Then chart chart_ID contains [{now};NORMAL;null, {free_trial_exp};SHUFFLED;null, {exp};PREVIEW;{community}://content/playlist?id={chart_ID}&action=refer_a_friend]

    And the case is the following [----(Rm)-----(NOW)-------(FT)--------]
    And chart chart_ID configured FREE_TRIAL:NORMAL,locked:<NULL> and LIMITED:PREVIEW,locked:action4
    When referral data matches current case
    When user invokes get context command
    Then chart chart_ID contains [{now};NORMAL;null, {free_trial_exp};PREVIEW;{community}://content/playlist?id={chart_ID}&action=action4]

    And the case is the following [----(Rm)-----(NOW)-------(FT)--------]
    And chart chart_ID configured FREE_TRIAL:NORMAL,locked:<NULL> and LIMITED:PREVIEW,locked:refer_a_friend
    When referral data matches current case
    When user invokes get context command
    Then chart chart_ID contains [{now};NORMAL;null, {free_trial_exp};SHUFFLED;null]

    And the case is the following [----(Rm)-----(NOW)-(Re)--(FT)--------]
    And chart chart_ID configured FREE_TRIAL:NORMAL,locked:<NULL> and LIMITED:PREVIEW,locked:action5
    When referral data matches current case
    When user invokes get context command
    Then chart chart_ID contains [{now};NORMAL;null, {exp};NORMAL;null, {free_trial_exp};PREVIEW;{community}://content/playlist?id={chart_ID}&action=action5]

  Scenario: get context for user with AWAITING payment details
    Given First time user with all devices using JSON format with all versions to get context and get context supported communities available
    And user has AWAITING payment details
    And chart chart_ID configured FREE_TRIAL:NORMAL,locked:<NULL> and LIMITED:PREVIEW,locked:<NULL>
    When user invokes get context command
    Then response has 200 http response code
    Then chart chart_ID contains [{now};PREVIEW;null]

  Scenario: get context for user with pending ERROR payment details
    Given First time user with all devices using JSON format with all versions to get context and get context supported communities available
    And user has ERROR payment details
    And chart chart_ID configured FREE_TRIAL:NORMAL,locked:<NULL> and LIMITED:PREVIEW,locked:<NULL>
    When user invokes get context command
    Then response has 200 http response code
    Then chart chart_ID contains [{now};PREVIEW;null]
