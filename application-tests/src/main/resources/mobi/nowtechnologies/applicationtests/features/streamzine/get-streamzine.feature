@Ready
Feature: Transport API call for the GET_STREAMZINE command
  Scenario: device sends GET_STREAMZINE command with correct parameters and gets correct response
    Given First time user with device using JSON format for all streamzine supported versions and streamzine supported communities and for all devices available

    When update is prepared

    And NARROW block included with VIP access policy, with '<null>' title and 'subtitle-0' subtitle which contains PROMOTIONAL, EXTERNAL_AD opened 'url' in BROWSER
    And NARROW block included with VIP access policy, with '<null>' title and 'subtitle-1' subtitle which contains PROMOTIONAL, EXTERNAL_AD opened 'url' in IN_APP
    And WIDE block not included with no access policy, with 'title-2' title and 'subtitle-2' subtitle which contains PROMOTIONAL, EXTERNAL_AD opened 'not-included' in IN_APP
    And WIDE block included with HiddenForSubscribed access policy, with 'title-3' title and 'subtitle-3' subtitle which contains PROMOTIONAL, INTERNAL_AD with 'subscription_page0' page and '<null>' action
    And SLIM_BANNER block included with no           access policy, with '<null>' title and '<null>' subtitle which contains PROMOTIONAL, INTERNAL_AD with 'subscription_page1' page and 'subscribe' action
    And WIDE block included with no access policy, with 'title-5' title and 'subtitle-5' subtitle which contains MUSIC, TRACK with 'GB0000000001' isrc and 1 id
    And WIDE block included with no access policy, with 'title-6' title and 'subtitle-6' subtitle which contains MUSIC, PLAYLIST with 10 ids
    And WIDE block included with no access policy, with 'title-7' title and 'subtitle-7' subtitle which contains MUSIC, MANUAL_COMPILATION with 2, 3, 4 ids

    And user invokes get streamzine command

    And block on 1 position is NARROW, DEEPLINK, [mtv1://web/dXJs?open=externally] with RESTRICTED permission granted to LIMITED, FREETRIAL
    And block on 2 position is NARROW, DEEPLINK, [mtv1://web/dXJs?open=internally] with RESTRICTED permission granted to LIMITED, FREETRIAL
    And block on 3 position is WIDE, DEEPLINK, [mtv1://page/subscription_page0] with HIDDEN permission granted to SUBSCRIBED
    And block on 4 position is SLIM_BANNER, DEEPLINK, [mtv1://page/subscription_page1?action=subscribe] with no permissions
    And block on 5 position is WIDE, DEEPLINK, [mtv1://content/track?id=GB0000000001_1, mtv1://content/track?player=mini&id=GB0000000001_1] with no permissions
    And block on 6 position is WIDE, DEEPLINK, [mtv1://content/playlist?id=10, mtv1://content/playlist?player=mini&id=10] with no permissions
    And block on 7 position is WIDE, ID_LIST, [2, 3, 4] with no permissions

    And block on 3 has title equal to title-3
    And block on 3 has subtitle equal to subtitle-3
    And block on 5 has title equal to title-5
    And block on 5 has subtitle equal to subtitle-5
    And block on 6 has title equal to title-6
    And block on 6 has subtitle equal to subtitle-6
    And block on 7 has title equal to title-7
    And block on 7 has subtitle equal to subtitle-7
