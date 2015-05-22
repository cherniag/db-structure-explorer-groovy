package mobi.nowtechnologies.applicationtests.features.activation.google_plus

import cucumber.api.Transform
import cucumber.api.java.Before
import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word
import mobi.nowtechnologies.applicationtests.services.CommonAssertionsService
import mobi.nowtechnologies.applicationtests.services.RequestFormat
import mobi.nowtechnologies.applicationtests.services.db.UserDbService
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator
import mobi.nowtechnologies.server.persistence.repository.UserRepository
import mobi.nowtechnologies.server.shared.enums.ProviderType
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource
import mobi.nowtechnologies.server.social.service.facebook.impl.mock.AppTestFacebookTokenService
import mobi.nowtechnologies.server.social.service.googleplus.impl.mock.AppTestGooglePlusTokenService
import org.springframework.stereotype.Component

import javax.annotation.Resource

import static org.junit.Assert.*

/**
 * Created by kots on 9/16/2014.
 */
@Component
class GooglePlusMergeAccountsFeature {

    @Resource
    UserDeviceDataService userDeviceDataService

    @Resource
    MQAppClientDeviceSet deviceSet

    @Resource
    UserDbService userDbService

    @Resource
    UserRepository userRepository

    @Resource
    UserDataCreator userDataCreator

    @Resource
    CommunityResourceBundleMessageSource communityResourceBundleMessageSource

    @Resource
    AppTestGooglePlusTokenService appTestGooglePlusTokenService

    @Resource
    AppTestFacebookTokenService appTestFacebookTokenService

    @Resource
    CommonAssertionsService commonStepsService

    List<UserDeviceData> currentUserDevices
    List<UserDeviceData> secondUserDevices
    List<List<UserDeviceData>> zippedUserDevices

    Map<UserDeviceData, Integer> oldUserIdMap = new HashMap<>()

    Map<UserDeviceData, Integer> tempUserIdMap = new HashMap<>()

    Map<UserDeviceData, Integer> secondUserIdMap = new HashMap<>()

    Map<UserDeviceData, Integer> tempSecondUserIdMap = new HashMap<>()

    @Before
    def cleanupBeforeScenario() {
        deviceSet.cleanup()
    }

    @Given('^Activated via Google Plus user with (.+) using (.+) format for (.+) and (.+)$')
    def "Activated via Google Plus user with given devices using given format for given versions and given communities"(
            @Transform(DictionaryTransformer.class) Word devices,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            @Transform(DictionaryTransformer.class) Word communities) {
        currentUserDevices = userDeviceDataService.table(versions.list(), communities.set(), devices.set(), formats.set(RequestFormat))
        registerUsingGooglePlus(currentUserDevices, deviceSet, oldUserIdMap)
    }

    @Given('^Activated via Google Plus user with (.+) using (.+) format for (.+) and (.+) and a second activated user$')
    def "Activated via Google Plus user with given devices using given format for given versions and given communities and a second activated user"(
            @Transform(DictionaryTransformer.class) Word devices,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            @Transform(DictionaryTransformer.class) Word communities) {
        currentUserDevices = userDeviceDataService.table(versions.list(), communities.set(), devices.set(), formats.set(RequestFormat))
        secondUserDevices = userDeviceDataService.table(versions.list(), communities.set(), devices.set(), formats.set(RequestFormat), "secondary")
        registerUsingGooglePlus(currentUserDevices, deviceSet, oldUserIdMap)
    }

    @When('^User registers using same device$')
    def "User registers using same device"() {
        currentUserDevices.each { deviceSet.singup(it) }
    }

    @Then('^Temporary account is created$')
    def "Temporary account is created"() {
        commonStepsService.assertTempAccountCreated(currentUserDevices, deviceSet, oldUserIdMap, tempUserIdMap)
    }

    @And('^First account becomes deactivated$')
    def "First account becomes deactivated"() {
        commonStepsService.assertAccountDeactivated(currentUserDevices, oldUserIdMap)
    }

    @When('^Registered user enters the same Google Plus credentials with updated details$')
    def "Registered user enters the same Google Plus credentials with updated details"() {
        currentUserDevices.each {
            deviceSet.loginUsingGooglePlusWithUpdatedDetails(it)
        }
    }

    @Then('^Temporary account is removed$')
    def "Temporary account is removed"() {
        commonStepsService.assertTemporaryAccountRemoved(currentUserDevices, tempUserIdMap)
    }

    @And('^First account becomes active again$')
    def "First account becomes active again"() {
        commonStepsService.assertFirstAccountReactivated(currentUserDevices, oldUserIdMap)
    }

    @And('^In database user has updated Google Plus details the same as specified in Google Plus account$')
    def "In database user has updated Google Plus details the same as specified in Google Plus account"() {
        commonStepsService.assertGooglePlusUserDetails(currentUserDevices, deviceSet)
    }

    @When('^User registers using new device$')
    def "User registers using new device"() {
        currentUserDevices.each {
            deviceSet.singupWithNewDevice(it)
        }
    }

    @And('^First account remains active$')
    def "First account remains active"() {
        commonStepsService.assertFirstAccountActive(currentUserDevices, oldUserIdMap)
    }

    @When('^Registered user enters the same Google Plus credentials$')
    def "Registered user enters the same Google Plus credentials"() {
        currentUserDevices.each {
            deviceSet.loginUsingGooglePlus(it)
        }
    }

    @And('^First account is updated with new device uid$')
    def "First account is updated with new device uid"() {
        commonStepsService.assertFirstAccountIsUpdated(currentUserDevices, deviceSet, oldUserIdMap)
    }

    @When('^Registered user enters new Google Plus credentials$')
    def "Registered user enters new Google Plus credentials"() {
        currentUserDevices.each {
            deviceSet.loginUsingGooglePlusWithExactEmail(it, userDataCreator.generateEmail())
        }
    }

    @Then('^New user is successfully activated$')
    def "New user is successfully activated"() {
        commonStepsService.assertNewUserActivated(currentUserDevices, tempUserIdMap)
    }

    @And('^Default promo is applied$')
    def "Default promo is applied"() {
        currentUserDevices.each {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            assertEquals(user.getLastPromo().getCode(),
                    communityResourceBundleMessageSource.getMessage(it.communityUrl, CommonAssertionsService.DEFAULT_PROMOTION_CODE_KEY, null, null))
        }
    }

    @And('^First account remains deactivated$')
    def "First account remains deactivated"() {
        commonStepsService.assertAccountDeactivated(currentUserDevices, oldUserIdMap)
    }

    @And('^In database new user has Google Plus details the same as specified in new Google Plus account$')
    def "In database new user has Google Plus details the same as specified in new Google Plus account"() {
        commonStepsService.assertGooglePlusUserDetails(currentUserDevices, deviceSet)
    }

    @When('^User registers using second activated device$')
    def "User registers using second activated device"() {
        registerUsingGooglePlus(secondUserDevices, deviceSet, secondUserIdMap)
        zippedUserDevices = [currentUserDevices, secondUserDevices].transpose()
        zippedUserDevices.each {
            deviceSet.singupWithOtherDevice(it[0], it[1])
        }
    }

    @Then('^Temporary account is created for the second device$')
    def "Temporary account is created for the second device"() {
        secondUserDevices.each {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            tempSecondUserIdMap.put(it, user.id)
            assertNotEquals(user.id, secondUserIdMap.get(it))
        }
    }

    @Then('^Temporary account is removed for the second device$')
    def "Temporary account is removed for the second device"() {
        secondUserDevices.each {
            def tempUser = userRepository.findOne(tempSecondUserIdMap.get(it))
            assertNull(tempUser)
        }
    }

    @And('^Second account becomes deactivated$')
    def "Second account becomes deactivated"() {
        commonStepsService.assertAccountDeactivated(secondUserDevices, secondUserIdMap)
    }

    @When('^Registered user enters first Google Plus credentials$')
    def "Registered user enters first Google Plus credentials"() {
        zippedUserDevices.each {
            deviceSet.loginUsingGooglePlusWithOtherDevice(it[0], it[1])
        }
    }

    @And('^Second account remains deactivated$')
    def "Second account remains deactivated"() {
        commonStepsService.assertAccountDeactivated(secondUserDevices, secondUserIdMap)
    }

    @And('^First account is updated with second device uid$')
    def "First account is updated with second device uid"() {
        commonStepsService.assertFirstAccountIsUpdatedWithSecondDeviceUID(zippedUserDevices, deviceSet, oldUserIdMap)
    }

    @Given('^Activated via Facebook user with (.+) using (.+) format for (.+) and (.+)$')
    def "Activated via Facebook user with all devices using JSON and XML format for all google plus supported versions and google plus supported communities"(
            @Transform(DictionaryTransformer.class) Word devices,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            @Transform(DictionaryTransformer.class) Word communities) {
        currentUserDevices = userDeviceDataService.table(versions.list(), communities.set(), devices.set(), formats.set(RequestFormat))
        registerUsingFacebook(currentUserDevices, deviceSet, oldUserIdMap)
    }

    @Given('^Activated via Facebook user with (.+) using (.+) format for (.+) and (.+) with a second user activated via Google Plus$')
    def "Activated via Facebook user with all devices using JSON and XML format for all google plus supported versions and google plus supported communities with a second user activated via Google Plus"(
            @Transform(DictionaryTransformer.class) Word devices,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            @Transform(DictionaryTransformer.class) Word communities) {
        currentUserDevices = userDeviceDataService.table(versions.list(), communities.set(), devices.set(), formats.set(RequestFormat))
        secondUserDevices = userDeviceDataService.table(versions.list(), communities.set(), devices.set(), formats.set(RequestFormat), "secondary")
        registerUsingGooglePlus(currentUserDevices, deviceSet, oldUserIdMap)
        registerUsingFacebook(secondUserDevices, deviceSet, secondUserIdMap)
        zippedUserDevices = [currentUserDevices, secondUserDevices].transpose()
    }

    @Given('^Activated via Google Plus user with (.+) using (.+) format for (.+) and (.+) with a second user activated via Facebook$')
    def "Activated via Google Plus user with all devices using JSON and XML format for all google plus supported versions and google plus supported communities with a second user activated via Facebook"(
            @Transform(DictionaryTransformer.class) Word devices,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            @Transform(DictionaryTransformer.class) Word communities) {
        currentUserDevices = userDeviceDataService.table(versions.list(), communities.set(), devices.set(), formats.set(RequestFormat))
        secondUserDevices = userDeviceDataService.table(versions.list(), communities.set(), devices.set(), formats.set(RequestFormat), "secondary")
        registerUsingGooglePlus(currentUserDevices, deviceSet, oldUserIdMap)
        registerUsingFacebook(secondUserDevices, deviceSet, secondUserIdMap)
        zippedUserDevices = [currentUserDevices, secondUserDevices].transpose()
    }

    @And('^In database new user has Google Plus details the same as specified in Google Plus account$')
    def "In database new user has Google Plus details the same as specified in Google Plus account"() {
        commonStepsService.assertGooglePlusUserDetails(currentUserDevices, deviceSet)
    }

    @When('^Registered user enters Google Plus credentials with different email$')
    def "Registered user enters Google Plus credentials with different email"() {
        currentUserDevices.each { deviceSet.loginUsingGooglePlusWithExactEmail(it, userDataCreator.generateEmail()) }
    }

    @When('^Registered user enters Facebook credentials with different email$')
    def "Registered user enters Facebook credentials with different email"() {
        currentUserDevices.each { deviceSet.loginUsingFacebookWithDefinedEmail(it, userDataCreator.generateEmail()) }
    }

    @And('^In database new user has Facebook details the same as specified in Facebook account$')
    def "In database new user has Facebook details the same as specified in Facebook account"() {
        commonStepsService.checkFacebookUserDetails(currentUserDevices, deviceSet)
    }

    @When('^Registered user enters Google Plus credentials with same email as first account has$')
    def "Registered user enters Google Plus credentials with same email as first account has"() {
        currentUserDevices.each { deviceSet.loginUsingGooglePlus(it) }
    }

    @And('^First account is updated with provider Google Plus$')
    def "First account is updated with provider Google Plus"() {
        currentUserDevices.each {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            assertEquals(user.provider, ProviderType.GOOGLE_PLUS)
        }
    }

    @And('^In database first user has details the same as specified in Google Plus account$')
    def "In database first user has details the same as specified in Google Plus account"() {
        commonStepsService.assertGooglePlusUserDetails(currentUserDevices, deviceSet)
    }

    @And('^First account is updated with provider Facebook$')
    def "First account is updated with provider Facebook"() {
        currentUserDevices.each {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            assertEquals(user.provider, ProviderType.FACEBOOK)
        }
    }

    @And('^In database first user has details the same as specified in Facebook account$')
    def "In database first user has details the same as specified in Facebook account"() {
        commonStepsService.checkFacebookUserDetails(currentUserDevices, deviceSet)
    }

    @When('^Registered user enters Facebook credentials with same email as first account has$')
    def "Registered user enters Facebook credentials with same email as first account has"() {
        currentUserDevices.each { deviceSet.loginUsingFacebook(it) }
    }

    @When('^Registered user enters Google Plus credentials of second activated user$')
    def "Registered user enters Google Plus credentials of second activated user"() {
        zippedUserDevices.each {
            def phoneState = deviceSet.getPhoneState(it[1])
            deviceSet.loginUsingGooglePlusWithExactEmail(it[0], phoneState.email)
        }
    }

    @When('^Registered user enters Facebook credentials of second activated user$')
    def "Registered user enters Facebook credentials of second activated user"() {
        zippedUserDevices.each {
            def phoneState = deviceSet.getPhoneState(it[1])
            deviceSet.loginUsingGooglePlusWithExactEmail(it[0], phoneState.email)
        }
    }

    @And('^Second account is updated with first device uid$')
    def "Second account is updated with first device uid"() {
        zippedUserDevices.each {
            def phoneState = deviceSet.getPhoneState(it[0])
            def user = userRepository.findOne(secondUserIdMap[it[1]])
            assertEquals(phoneState.deviceUID, user.deviceUID)
        }
    }

    def registerUsingFacebook(List<UserDeviceData> devices, MQAppClientDeviceSet deviceSet, Map<UserDeviceData, Integer> userIdMap) {
        devices.each {
            deviceSet.singup(it)
            deviceSet.loginUsingFacebook(it)
            def phoneState = deviceSet.getPhoneState(it)
            userIdMap.put(it, userDbService.findUser(phoneState, it).id)
        }
    }

    def registerUsingGooglePlus(List<UserDeviceData> devices, MQAppClientDeviceSet deviceSet, Map<UserDeviceData, Integer> userIdMap) {
        devices.each {
            deviceSet.singup(it)
            deviceSet.loginUsingGooglePlus(it)
            def phoneState = deviceSet.getPhoneState(it)
            userIdMap.put(it, userDbService.findUser(phoneState, it).id)
        }
    }
}
