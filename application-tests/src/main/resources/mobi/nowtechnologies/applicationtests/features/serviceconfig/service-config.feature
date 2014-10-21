@Ready
Feature: Server returns json data about application upgrade 'call-to-action'

  Scenario: device sends User-Agent header not according to application upgrade format
    Given Mobile client makes Service Config call using JSON format for all devices and all communities and all versions
    When User-Agent header is in old format "Android Http Client"
    Then response has 400 http response code
    And error message is 'A required HTTP header was not specified.'


  Scenario Outline: device sends User-Agent header according to application upgrade format
    Given Mobile client makes Service Config call using JSON format for all devices and all communities and all versions bellow 6.3
    When service config data is set to '<db_status>' for version '<db_app_version>', 'musicqubed-{random}' application, '<db_message_key>' message, '<db_image>' image and '<db_url>' link
    And User-agent header is in new format "musicqubed-{random}/<req_app_version> ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to '<response_status>'
    And json field has 'message' set to '<response_message>'
    And json field has 'link' set to '<response_link>'
    And json field has 'image' set to '<null>'

  Examples:
      | db_status        | db_app_version | db_image     | db_url       | db_message_key              | req_app_version | response_status  | response_message            | response_link |
      | SUGGESTED_UPDATE | 1.2.5          | suggest.jpg  | http://e.com | service.config.some.message | 1.2.5           | SUGGESTED_UPDATE | Service Config Some Message | http://e.com  |
      | SUGGESTED_UPDATE | 1.9.9-RELEASE  | suggest.jpg  | http://e.com | service.config.some.message | 1.9.9-RELEASE   | SUGGESTED_UPDATE | Service Config Some Message | http://e.com  |
      | FORCED_UPDATE    | 1.3            | forced.jpg   | http://e.com | service.config.some.message | 1.3             | FORCED_UPDATE    | Service Config Some Message | http://e.com  |
      | FORCED_UPDATE    | 1.3.5          | forced.jpg   | http://e.com | service.config.some.message | 1.4.0           | CURRENT          | <null>                      | <null>        |
      | FORCED_UPDATE    | 1.9.9-SNAPSHOT | forced.jpg   | http://e.com | service.config.some.message | 1.9.9-RELEASE   | CURRENT          | <null>                      | <null>        |
      | REVOKED          | 1.3.5          | revoked.jpg  | http://e.com | service.config.some.message | 1.3.5           | REVOKED          | Service Config Some Message | http://e.com  |
      | REVOKED          | 1.3.5          | revoked.jpg  | http://e.com | not.found.message.key       | 1.3.5           | REVOKED          | <null>                      | http://e.com  |
      | REVOKED          | 1.3.5          | revoked.jpg  | http://e.com | service.config.some.message | 1.4.0           | CURRENT          | <null>                      | <null>        |
      | MIGRATED         | 1.3.5          | migrated.jpg | http://e.com | service.config.some.message | 1.3.5           | CURRENT          | <null>                      | <null>        |
      | MIGRATED         | 1.3.5          | migrated.jpg | http://e.com | service.config.some.message | 1.4.0           | CURRENT          | <null>                      | <null>        |



  Scenario Outline: device sends User-Agent header according to application upgrade format
    Given Mobile client makes Service Config call using JSON format for all devices and all communities and all versions above 6.3
    When service config data is set to '<db_status>' for version '<db_app_version>', 'musicqubed-{random}' application, '<db_message_key>' message, '<db_image>' image and '<db_url>' link
    And User-agent header is in new format "musicqubed-{random}/<req_app_version> ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to '<response_status>'
    And json field has 'message' set to '<response_message>'
    And json field has 'link' set to '<response_link>'
    And json field has 'image' set to '<response_image>'

  Examples:
      | db_status        | db_app_version | db_image     | db_url       | db_message_key              | req_app_version | response_status  | response_message            | response_link | response_image |
      | SUGGESTED_UPDATE | 1.2.5          | suggest.jpg  | http://e.com | service.config.some.message | 1.2.5           | SUGGESTED_UPDATE | Service Config Some Message | http://e.com  | suggest.jpg    |
      | SUGGESTED_UPDATE | 1.9.9-RELEASE  | suggest.jpg  | http://e.com | service.config.some.message | 1.9.9-RELEASE   | SUGGESTED_UPDATE | Service Config Some Message | http://e.com  | suggest.jpg    |
      | FORCED_UPDATE    | 1.3            | forced.jpg   | http://e.com | service.config.some.message | 1.3             | FORCED_UPDATE    | Service Config Some Message | http://e.com  | forced.jpg     |
      | FORCED_UPDATE    | 1.3.5          | forced.jpg   | http://e.com | service.config.some.message | 1.4.0           | CURRENT          | <null>                      | <null>        | <null>         |
      | FORCED_UPDATE    | 1.9.9-SNAPSHOT | forced.jpg   | http://e.com | service.config.some.message | 1.9.9-RELEASE   | CURRENT          | <null>                      | <null>        | <null>         |
      | REVOKED          | 1.3.5          | revoked.jpg  | http://e.com | service.config.some.message | 1.3.5           | REVOKED          | Service Config Some Message | http://e.com  | revoked.jpg    |
      | REVOKED          | 1.3.5          | revoked.jpg  | http://e.com | not.found.message.key       | 1.3.5           | REVOKED          | <null>                      | http://e.com  | revoked.jpg    |
      | REVOKED          | 1.3.5          | revoked.jpg  | http://e.com | service.config.some.message | 1.4.0           | CURRENT          | <null>                      | <null>        | <null>         |
      | MIGRATED         | 1.3.5          | migrated.jpg | http://e.com | service.config.some.message | 1.3.5           | MIGRATED         | Service Config Some Message | http://e.com  | migrated.jpg   |




  Scenario Outline: device sends User-Agent header according to application upgrade format for different statuses with different messages
    Given Mobile client makes Service Config call using JSON format for all devices and all communities and all versions bellow 6.3
    When client version info exist:

      | status           | appVersion | applicationName     | image         | url        | messageKey                  |
      | REVOKED          | 1.0        | musicqubed-{random} | revoke_1.jpg  | http://1.0 | service.config.some.message |
      | REVOKED          | 1.2        | musicqubed-{random} | revoke_1.jpg  | http://1.0 | service.config.some.message |
      | MIGRATED         | 2.0        | musicqubed-{random} | migrate_2.jpg | http://2.0 | service.config.some.message |
      | MIGRATED         | 2.2        | musicqubed-{random} | migrate_2.jpg | http://2.0 | service.config.some.message |
      | FORCED_UPDATE    | 3.0        | musicqubed-{random} | force_3.jpg   | http://3.0 | service.config.some.message |
      | FORCED_UPDATE    | 3.2        | musicqubed-{random} | force_3.jpg   | http://3.0 | service.config.some.message |
      | SUGGESTED_UPDATE | 4.0        | musicqubed-{random} | suggest_4.jpg | http://4.0 | service.config.some.message |
      | SUGGESTED_UPDATE | 4.2        | musicqubed-{random} | suggest_4.jpg | http://4.0 | service.config.some.message |

    And User-Agent header is in new format "musicqubed-{random}/<req_app_version> ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to '<response_status>'
    And json field has 'message' set to '<response_message>'
    And json field has 'link' set to '<response_link>'
    And json field has 'image' set to '<null>'

  Examples:
      | req_app_version | response_status  | response_message            | response_link |
      | 0.5             | REVOKED          | Service Config Some Message | http://1.0    |
      | 1.0             | REVOKED          | Service Config Some Message | http://1.0    |
      | 1.5             | FORCED_UPDATE    | Service Config Some Message | http://3.0    |
      | 2.0             | FORCED_UPDATE    | Service Config Some Message | http://3.0    |
      | 2.5             | FORCED_UPDATE    | Service Config Some Message | http://3.0    |
      | 3.0             | FORCED_UPDATE    | Service Config Some Message | http://3.0    |
      | 3.5             | SUGGESTED_UPDATE | Service Config Some Message | http://4.0    |
      | 4.0             | SUGGESTED_UPDATE | Service Config Some Message | http://4.0    |
      | 4.5             | CURRENT          | <null>                      | <null>        |



  Scenario Outline: device sends User-Agent header according to application upgrade format for different statuses with different messages
    Given Mobile client makes Service Config call using JSON format for all devices and all communities and all versions above 6.3
    When client version info exist:

      | status           | appVersion | applicationName     | image         | url        | messageKey                  |
      | REVOKED          | 1.0        | musicqubed-{random} | revoke_1.jpg  | http://1.0 | service.config.some.message |
      | MIGRATED         | 2.0        | musicqubed-{random} | migrate_2.jpg | http://2.0 | service.config.some.message |
      | FORCED_UPDATE    | 3.0        | musicqubed-{random} | force_3.jpg   | http://3.0 | service.config.some.message |
      | SUGGESTED_UPDATE | 4.0        | musicqubed-{random} | suggest_4.jpg | http://4.0 | service.config.some.message |

    And User-Agent header is in new format "musicqubed-{random}/<req_app_version> ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to '<response_status>'
    And json field has 'message' set to '<response_message>'
    And json field has 'link' set to '<response_link>'
    And json field has 'image' set to '<response_image>'

  Examples:
     | req_app_version | response_status  | response_message            | response_link | response_image |
     | 0.5             | REVOKED          | Service Config Some Message | http://1.0    | revoke_1.jpg   |
     | 1.0             | REVOKED          | Service Config Some Message | http://1.0    | revoke_1.jpg   |
     | 1.5             | MIGRATED         | Service Config Some Message | http://2.0    | migrate_2.jpg  |
     | 2.0             | MIGRATED         | Service Config Some Message | http://2.0    | migrate_2.jpg  |
     | 2.5             | FORCED_UPDATE    | Service Config Some Message | http://3.0    | force_3.jpg    |
     | 3.0             | FORCED_UPDATE    | Service Config Some Message | http://3.0    | force_3.jpg    |
     | 3.5             | SUGGESTED_UPDATE | Service Config Some Message | http://4.0    | suggest_4.jpg  |
     | 4.0             | SUGGESTED_UPDATE | Service Config Some Message | http://4.0    | suggest_4.jpg  |
     | 4.5             | CURRENT          | <null>                      | <null>        | <null>         |