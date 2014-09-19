@InDevelopment1
Feature: Server returns json data about application upgrade 'call-to-action'

  Scenario: device sends User-Agent header not according to application upgrade format
    Given Mobile client makes Service Config call using JSON and XML formats for all devices and all communities and all versions
    When User-Agent header is in old format "Android Http Client"
    Then response has 400 http response code
    And error message is 'A required HTTP header was not specified.'

  Scenario: device sends User-Agent header according to application upgrade format for SUGGESTED_UPDATE case
    Given Mobile client makes Service Config call using JSON and XML formats for all devices and all communities and all versions
    When service config data is set to 'SUGGESTED_UPDATE' for version '1.3.3', 'musicqubed-{random}' application, 'service.config.some.message' message, 'http://example.com' link
    And User-agent header is in new format "musicqubed-{random}/1.3.3 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'SUGGESTED_UPDATE'
    And json field has 'message' set to 'service.config.some.message'
    And json field has 'link' set to 'http://example.com'

  Scenario: device sends User-Agent header according to application upgrade format for SUGGESTED_UPDATE qualifier exact match case
    Given Mobile client makes Service Config call using JSON and XML formats for all devices and all communities and all versions
    When service config data is set to 'SUGGESTED_UPDATE' for version '1.9.9-RELEASE', 'musicqubed-{random}' application, 'service.config.some.message' message, 'http://example.com' link
    And User-agent header is in new format "musicqubed-{random}/1.9.9-RELEASE ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'SUGGESTED_UPDATE'
    And json field has 'message' set to 'service.config.some.message'
    And json field has 'link' set to 'http://example.com'

  Scenario: device sends User-Agent header according to application upgrade format for SUGGESTED_UPDATE qualifier not match case
    Given Mobile client makes Service Config call using JSON and XML formats for all devices and all communities and all versions
    When service config data is set to 'SUGGESTED_UPDATE' for version '1.9.9-SNAPSHOT', 'musicqubed-{random}' application, 'service.config.some.message' message, 'http://example.com' link
    And User-agent header is in new format "musicqubed-{random}/1.9.9-RELEASE ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'CURRENT'
    And json field has 'message' set to '<null>'
    And json field has 'link' set to '<null>'

  Scenario: device sends User-Agent header according to application upgrade format for FORCED_UPDATE case
    Given Mobile client makes Service Config call using JSON and XML formats for all devices and all communities and all versions
    When service config data is set to 'FORCED_UPDATE' for version '1.3.4', 'musicqubed-{random}' application, 'service.config.some.message' message, 'http://example.com' link
    And User-agent header is in new format "musicqubed-{random}/1.3.4 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'FORCED_UPDATE'
    And json field has 'message' set to 'service.config.some.message'
    And json field has 'link' set to 'http://example.com'

  Scenario: device sends User-Agent header according to application upgrade format for REVOKED case
    Given Mobile client makes Service Config call using JSON and XML formats for all devices and all communities and all versions
    When service config data is set to 'REVOKED' for version '1.3.5', 'musicqubed-{random}' application, 'service.config.some.message' message, 'http://example.com' link
    And User-agent header is in new format "musicqubed-{random}/1.3.5 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'REVOKED'
    And json field has 'message' set to 'service.config.some.message'
    And json field has 'link' set to 'http://example.com'

  Scenario: device sends User-Agent header according to application upgrade format for REVOKED case but get CURRENT value
    Given Mobile client makes Service Config call using JSON and XML formats for all devices and all communities and all versions
    When service config data is set to 'REVOKED' for version '1.3.5', 'musicqubed-{random}' application, 'service.config.some.message' message, 'http://example.com' link
    And User-agent header is in new format "musicqubed-{random}/1.4.0 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'CURRENT'
    And json field has 'message' set to '<null>'
    And json field has 'link' set to '<null>'