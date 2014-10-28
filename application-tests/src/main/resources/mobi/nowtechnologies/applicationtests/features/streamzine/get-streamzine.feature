@Ready
Feature: Transport API call for the GET_STREAMZINE command
  Scenario Outline: device sends GET_STREAMZINE command with wrong parameters
    Given First time user with device using JSON format for all streamzine supported versions below 6.3 and streamzine supported communities and for all devices available
    When user invokes get streamzine for the <WIDTHXHEIGHT>, <TIMESTAMP>, <USER_NAME>, <USER_TOKEN> parameters
    Then user gets <http error code> code in response and <error code>, <message> also <display message> in the message body
  Examples:
    | WIDTHXHEIGHT | TIMESTAMP | USER_NAME | USER_TOKEN | http error code | error code  | message                                                                            | display message                                                                    |
    | <empty>      | Valid     | Valid     | Valid      | 400             | 0           | error                                                                              | error                                                                              |
    | 200x400      | NotValid  | Valid     | Valid      | 401             | 12          | user login/pass check failed for [{username}] username and community [{community}] | user login/pass check failed for [{username}] username and community [{community}] |
    | 200x400      | Valid     | NotValid  | Valid      | 401             | 13          | user login/pass check failed for [{username}] username and community [{community}] | user login/pass check failed for [{username}] username and community [{community}] |
    | 200x400      | Valid     | Valid     | NotValid   | 401             | 12          | user login/pass check failed for [{username}] username and community [{community}] | user login/pass check failed for [{username}] username and community [{community}] |

  Scenario: device sends GET_STREAMZINE command with correct parameters and gets correct response
    Given First time user with device using JSON format for all streamzine supported versions below 6.3 and streamzine supported communities and for all devices available

    When update is prepared

    And NARROW block included with VIP access policy, with '<null>' title and 'subtitle-0' subtitle which contains PROMOTIONAL, EXTERNAL_AD opened 'url' in BROWSER
    And NARROW block included with VIP access policy, with '<null>' title and 'subtitle-1' subtitle which contains PROMOTIONAL, EXTERNAL_AD opened 'url' in IN_APP
    And WIDE block not included with no access policy, with 'title-2' title and 'subtitle-2' subtitle which contains PROMOTIONAL, EXTERNAL_AD opened 'not-included' in IN_APP
    And WIDE block included with HiddenForSubscribed access policy, with 'title-3' title and 'subtitle-3' subtitle which contains PROMOTIONAL, INTERNAL_AD with 'subscription_page0' page and '<null>' action
    And SLIM_BANNER block included with no           access policy, with '<null>' title and '<null>' subtitle which contains PROMOTIONAL, INTERNAL_AD with 'subscription_page1' page and 'subscribe' action
    And WIDE block included with no access policy, with 'title-5' title and 'subtitle-5' subtitle which contains MUSIC, TRACK with 'SOME_ISRC' isrc and 1 id
    And WIDE block included with no access policy, with 'title-6' title and 'subtitle-6' subtitle which contains MUSIC, PLAYLIST with 10 ids
    And WIDE block included with no access policy, with 'title-7' title and 'subtitle-7' subtitle which contains MUSIC, MANUAL_COMPILATION with 2, 3, 4 ids

    And user invokes get streamzine command

    And block on 1 position is NARROW, DEEPLINK, [hl-uk://web/dXJs?open=externally] with RESTRICTED permission granted to LIMITED, FREETRIAL
    And block on 2 position is NARROW, DEEPLINK, [hl-uk://web/dXJs?open=internally] with RESTRICTED permission granted to LIMITED, FREETRIAL
    And block on 3 position is WIDE, DEEPLINK, [hl-uk://page/subscription_page0] with HIDDEN permission granted to SUBSCRIBED
    And block on 4 position is SLIM_BANNER, DEEPLINK, [hl-uk://page/subscription_page1?action=subscribe] with no permissions
    And block on 5 position is WIDE, DEEPLINK, [hl-uk://content/track?id=SOME_ISRC_1] with no permissions
    And block on 6 position is WIDE, DEEPLINK, [hl-uk://content/playlist?id=10] with no permissions
    And block on 7 position is WIDE, ID_LIST, [2, 3, 4] with no permissions

    And block on 3 has title equal to title-3
    And block on 3 has subtitle equal to subtitle-3
    And block on 5 has title equal to title-5
    And block on 5 has subtitle equal to subtitle-5
    And block on 6 has title equal to title-6
    And block on 6 has subtitle equal to subtitle-6
    And block on 7 has title equal to title-7
    And block on 7 has subtitle equal to subtitle-7


  Scenario: device sends GET_STREAMZINE command with incorrect (not supported) community
    Given First time user with device using JSON format for all streamzine supported versions below 6.3 and streamzine supported communities and for all devices available
    When user invokes get streamzine command with incorrect community
    Then user gets 404 code in response
