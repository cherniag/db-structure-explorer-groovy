@Ready1
Feature: Server returns json data about application upgrade 'call-to-action'

  Scenario: device sends User-Agent header not according to application upgrade format
    Given Mobile client makes Service Config call using JSON and XML formats for all devices and all communities and all versions
    When User-Agent header is in old format "Android Http Client"
    Then response has 400 http response code
    And error message is 'A required HTTP header was not specified.'

  Scenario: device sends User-Agent header according to application upgrade format for SUGGESTED_UPDATE case
    Given Mobile client makes Service Config call using JSON and XML formats for all devices and all communities and all versions
    When service config data is set to 'SUGGESTED_UPDATE' for version '1.3.3', 'musicqubed-{random}' application, 'service.config.some.message' message, 'image_suggested.jpg' image and 'http://example.com' link
    And User-agent header is in new format "musicqubed-{random}/1.3.3 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'SUGGESTED_UPDATE'
    And json field has 'message' set to 'Service Config Some Message'
    And json field has 'link' set to 'http://example.com'
    And json field has 'imageFileName' set to 'image_suggested.jpg'

  Scenario: device sends User-Agent header according to application upgrade format for SUGGESTED_UPDATE qualifier exact match case
    Given Mobile client makes Service Config call using JSON and XML formats for all devices and all communities and all versions
    When service config data is set to 'SUGGESTED_UPDATE' for version '1.9.9-RELEASE', 'musicqubed-{random}' application, 'service.config.some.message' message, 'image_suggested.jpg' image and 'http://example.com' link
    And User-agent header is in new format "musicqubed-{random}/1.9.9-RELEASE ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'SUGGESTED_UPDATE'
    And json field has 'message' set to 'Service Config Some Message'
    And json field has 'link' set to 'http://example.com'
    And json field has 'imageFileName' set to 'image_suggested.jpg'

  Scenario: device sends User-Agent header according to application upgrade format for SUGGESTED_UPDATE qualifier not match case
    Given Mobile client makes Service Config call using JSON and XML formats for all devices and all communities and all versions
    When service config data is set to 'SUGGESTED_UPDATE' for version '1.9.9-SNAPSHOT', 'musicqubed-{random}' application, 'service.config.some.message' message, 'image_suggested.jpg' image and 'http://example.com' link
    And User-agent header is in new format "musicqubed-{random}/1.9.9-RELEASE ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'CURRENT'
    And json field has 'message' set to '<null>'
    And json field has 'link' set to '<null>'
    And json field has 'imageFileName' set to '<null>'

    When service config data is set to 'FORCED_UPDATE' for version '1.3.4', 'musicqubed-{random}' application, 'service.config.some.message' message, 'image_forced.jpg' image and 'http://example.com' link
    And User-agent header is in new format "musicqubed-{random}/1.3.4 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'FORCED_UPDATE'
    And json field has 'message' set to 'Service Config Some Message'
    And json field has 'link' set to 'http://example.com'
    And json field has 'imageFileName' set to 'image_forced.jpg'

    When service config data is set to 'FORCED_UPDATE' for version '1.3', 'musicqubed-{random}' application, 'service.config.some.message' message, 'image_forced.jpg' image and 'http://example.com' link
    And User-agent header is in new format "musicqubed-{random}/1.3 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'FORCED_UPDATE'
    And json field has 'message' set to 'Service Config Some Message'
    And json field has 'link' set to 'http://example.com'
    And json field has 'imageFileName' set to 'image_forced.jpg'

  Scenario: device sends User-Agent header according to application upgrade format for REVOKED case
    Given Mobile client makes Service Config call using JSON and XML formats for all devices and all communities and all versions
    When service config data is set to 'REVOKED' for version '1.3.5', 'musicqubed-{random}' application, 'service.config.some.message' message, 'image_revoked.jpg' image and 'http://example.com' link
    And User-agent header is in new format "musicqubed-{random}/1.3.5 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'REVOKED'
    And json field has 'message' set to 'Service Config Some Message'
    And json field has 'link' set to 'http://example.com'
    And json field has 'imageFileName' set to 'image_revoked.jpg'

  Scenario: device sends User-Agent header according to application upgrade format for REVOKED case and the case when message is absent for <some.not.found.in.message.bundle> message key
    Given Mobile client makes Service Config call using JSON and XML formats for all devices and all communities and all versions
    When service config data is set to 'REVOKED' for version '1.3.5', 'musicqubed-{random}' application, 'some.not.found.in.message.bundle' message, 'image_revoked.jpg' image and 'http://example.com' link
    And User-agent header is in new format "musicqubed-{random}/1.3.5 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'REVOKED'
    And json field has 'message' set to '<null>'
    And json field has 'link' set to 'http://example.com'
    And json field has 'imageFileName' set to 'image_revoked.jpg'

  Scenario: device sends User-Agent header according to application upgrade format for REVOKED case but get CURRENT value
    Given Mobile client makes Service Config call using JSON and XML formats for all devices and all communities and all versions
    When service config data is set to 'REVOKED' for version '1.3.5', 'musicqubed-{random}' application, 'service.config.some.message' message, 'image_revoked.jpg' image and 'http://example.com' link
    And User-agent header is in new format "musicqubed-{random}/1.4.0 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'CURRENT'
    And json field has 'message' set to '<null>'
    And json field has 'link' set to '<null>'
    And json field has 'imageFileName' set to '<null>'

  Scenario: device sends User-Agent header according to application upgrade format for MIGRATED case
    Given Mobile client makes Service Config call using JSON and XML formats for all devices and all communities and all versions
    When service config data is set to 'MIGRATED' for version '1.3.5', 'musicqubed-{random}' application, 'service.config.some.message' message, 'image_migrated.jpg' image and 'http://example.com' link
    And User-agent header is in new format "musicqubed-{random}/1.3.5 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'MIGRATED'
    And json field has 'message' set to 'Service Config Some Message'
    And json field has 'link' set to 'http://example.com'
    And json field has 'imageFileName' set to 'image_migrated.jpg'

  Scenario: device sends User-Agent header according to application upgrade format for MIGRATED case but get CURRENT value
    Given Mobile client makes Service Config call using JSON and XML formats for all devices and all communities and all versions
    When service config data is set to 'MIGRATED' for version '1.3.5', 'musicqubed-{random}' application, 'service.config.some.message' message, 'image_migrated.jpg' image and 'http://example.com' link
    And User-agent header is in new format "musicqubed-{random}/1.4.0 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'CURRENT'
    And json field has 'message' set to '<null>'
    And json field has 'link' set to '<null>'
    And json field has 'imageFileName' set to '<null>'
  @Ready1
  Scenario: device sends User-Agent header according to application upgrade format for different statuses with different messages
    Given Mobile client makes Service Config call using JSON and XML formats for all devices and all communities and all versions

    When service config data is set to 'REVOKED' for version '1.0', 'musicqubed-{random}' application, 'service.config.some.message' message, 'image_revoked1.jpg' image and 'http://1.0/example.com' link
    When service config data is set to 'REVOKED' for version '2.0', 'musicqubed-{random}' application, 'service.config.some.message' message, 'image_revoked2.jpg' image and 'http://2.0/example.com' link
    When service config data is set to 'REVOKED' for version '3.0', 'musicqubed-{random}' application, 'service.config.some.message' message, 'image_revoked3.jpg' image and 'http://3.0/example.com' link
    When service config data is set to 'FORCED_UPDATE' for version '4.0', 'musicqubed-{random}' application, 'service.config.some.message' message, 'image_forced4.jpg' image and 'http://4.0/example.com' link
    When service config data is set to 'FORCED_UPDATE' for version '5.0', 'musicqubed-{random}' application, 'service.config.some.message' message, 'image_forced5.jpg' image and 'http://5.0/example.com' link
    When service config data is set to 'FORCED_UPDATE' for version '6.0', 'musicqubed-{random}' application, 'service.config.some.message' message, 'image_forced6.jpg' image and 'http://6.0/example.com' link
    When service config data is set to 'SUGGESTED_UPDATE' for version '7.0', 'musicqubed-{random}' application, 'service.config.some.message' message, 'image_suggested7.jpg' image and 'http://7.0/example.com' link
    When service config data is set to 'SUGGESTED_UPDATE' for version '8.0', 'musicqubed-{random}' application, 'service.config.some.message' message, 'image_suggested8.jpg' image and 'http://8.0/example.com' link
    When service config data is set to 'SUGGESTED_UPDATE' for version '9.0', 'musicqubed-{random}' application, 'service.config.some.message' message, 'image_suggested9.jpg' image and 'http://9.0/example.com' link
    When service config data is set to 'MIGRATED' for version '10.0', 'musicqubed-{random}' application, 'service.config.some.message' message, 'image_migrated10.jpg' image and 'http://10.0/example.com' link


    And User-Agent header is in new format "musicqubed-{random}/1.0 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'REVOKED'
    And json field has 'message' set to 'Service Config Some Message'
    And json field has 'link' set to 'http://1.0/example.com'
    And json field has 'imageFileName' set to 'image_revoked1.jpg'

    And User-Agent header is in new format "musicqubed-{random}/2.0 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'REVOKED'
    And json field has 'message' set to 'Service Config Some Message'
    And json field has 'link' set to 'http://2.0/example.com'
    And json field has 'imageFileName' set to 'image_revoked2.jpg'

    And User-Agent header is in new format "musicqubed-{random}/3.0 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'REVOKED'
    And json field has 'message' set to 'Service Config Some Message'
    And json field has 'link' set to 'http://3.0/example.com'
    And json field has 'imageFileName' set to 'image_revoked3.jpg'

    And User-Agent header is in new format "musicqubed-{random}/4.0 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'FORCED_UPDATE'
    And json field has 'message' set to 'Service Config Some Message'
    And json field has 'link' set to 'http://4.0/example.com'
    And json field has 'imageFileName' set to 'image_forced4.jpg'

    And User-Agent header is in new format "musicqubed-{random}/5.0 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'FORCED_UPDATE'
    And json field has 'message' set to 'Service Config Some Message'
    And json field has 'link' set to 'http://5.0/example.com'
    And json field has 'imageFileName' set to 'image_forced5.jpg'

    And User-Agent header is in new format "musicqubed-{random}/6.0 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'FORCED_UPDATE'
    And json field has 'message' set to 'Service Config Some Message'
    And json field has 'link' set to 'http://6.0/example.com'
    And json field has 'imageFileName' set to 'image_forced6.jpg'

    And User-Agent header is in new format "musicqubed-{random}/7.0 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'SUGGESTED_UPDATE'
    And json field has 'message' set to 'Service Config Some Message'
    And json field has 'link' set to 'http://7.0/example.com'
    And json field has 'imageFileName' set to 'image_suggested7.jpg'

    And User-Agent header is in new format "musicqubed-{random}/8.0 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'SUGGESTED_UPDATE'
    And json field has 'message' set to 'Service Config Some Message'
    And json field has 'link' set to 'http://8.0/example.com'
    And json field has 'imageFileName' set to 'image_suggested8.jpg'

    And User-Agent header is in new format "musicqubed-{random}/9.0 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'SUGGESTED_UPDATE'
    And json field has 'message' set to 'Service Config Some Message'
    And json field has 'link' set to 'http://9.0/example.com'
    And json field has 'imageFileName' set to 'image_suggested9.jpg'

    And User-Agent header is in new format "musicqubed-{random}/10.0 ({platform}; {community})"
    Then response has 200 http response code
    And json data is 'versionCheck'
    And json field has 'status' set to 'MIGRATED'
    And json field has 'message' set to 'Service Config Some Message'
    And json field has 'link' set to 'http://10.0/example.com'
    And json field has 'imageFileName' set to 'image_migrated10.jpg'
