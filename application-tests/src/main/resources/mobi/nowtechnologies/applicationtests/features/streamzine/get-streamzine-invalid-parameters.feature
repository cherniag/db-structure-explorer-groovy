@Ready
Feature: Transport API call for the GET_STREAMZINE command

  Scenario Outline: device sends GET_STREAMZINE command with wrong parameters
    Given First time user with device using JSON and XML format for all streamzine supported versions and streamzine supported communities and for all devices available
    When user invokes get streamzine for the <WIDTHXHEIGHT>, <TIMESTAMP>, <USER_NAME>, <USER_TOKEN> parameters
    Then user gets <http error code> code in response and <error code>, <message> also <display message> in the message body
  Examples:
    | WIDTHXHEIGHT | TIMESTAMP | USER_NAME | USER_TOKEN | http error code | error code  | message                                                                            | display message                                                                    |
    | <empty>      | Valid     | Valid     | Valid      | 400             | 0           | error                                                                              | error                                                                              |
    | 200x400      | NotValid  | Valid     | Valid      | 401             | 12          | user login/pass check failed for [{username}] username and community [{community}] | user login/pass check failed for [{username}] username and community [{community}] |
    | 200x400      | Valid     | NotValid  | Valid      | 401             | 13          | user login/pass check failed for [{username}] username and community [{community}] | user login/pass check failed for [{username}] username and community [{community}] |
    | 200x400      | Valid     | Valid     | NotValid   | 401             | 12          | user login/pass check failed for [{username}] username and community [{community}] | user login/pass check failed for [{username}] username and community [{community}] |
