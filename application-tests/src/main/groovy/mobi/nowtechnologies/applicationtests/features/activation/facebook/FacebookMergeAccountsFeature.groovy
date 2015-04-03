package mobi.nowtechnologies.applicationtests.features.activation.facebook

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
import mobi.nowtechnologies.server.social.service.facebook.impl.mock.AppTestFacebookTokenService
import org.springframework.stereotype.Component

import javax.annotation.Resource
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by kots on 9/23/2014.
 */
@Component
class FacebookMergeAccountsFeature {

    @Resource
    UserDeviceDataService userDeviceDataService

    @Resource
    MQAppClientDeviceSet deviceSet

    @Resource
    UserDbService userDbService

    @Resource
    UserRepository userRepository

    @Resource
    AppTestFacebookTokenService appTestFacebookTokenService

    @Resource
    UserDataCreator userDataCreator

    @Resource
    CommonAssertionsService commonStepsService

    List<UserDeviceData> currentUserDevices
    List<UserDeviceData> secondUserDevices
    List<List<UserDeviceData>> zippedUserDevices

    Map<UserDeviceData, Integer> oldUserIdMap = new ConcurrentHashMap<>()
    Map<UserDeviceData, Integer> tempUserIdMap = new ConcurrentHashMap<>()
    Map<UserDeviceData, Integer> secondUserIdMap = new ConcurrentHashMap<>()

    @Before
    def cleanupBeforeScenario() {
        deviceSet.cleanup()
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

    @Given('^Activated via Facebook user with (.+) using (.+) format for (.+) and (.+) with a second activated user$')
    def "Activated via Facebook user with all devices using JSON and XML format for all google plus supported versions and google plus supported communities with a second activated user"(
            @Transform(DictionaryTransformer.class) Word devices,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            @Transform(DictionaryTransformer.class) Word communities) {
        currentUserDevices = userDeviceDataService.table(versions.list(), communities.set(), devices.set(), formats.set(RequestFormat))
        secondUserDevices = userDeviceDataService.table(versions.list(), communities.set(), devices.set(), formats.set(RequestFormat), "secondary")
        currentUserDevices.each {
            deviceSet.singup(it)
            deviceSet.loginUsingFacebook(it)
            def phoneState = deviceSet.getPhoneState(it)
            oldUserIdMap.put(it, userDbService.findUser(phoneState, it).id)
        }
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

    @When('^Registered user enters the same Facebook credentials with updated details$')
    def "Registered user enters the same Facebook credentials with updated details"() {
        currentUserDevices.each {
            deviceSet.loginUsingFacebook(it)
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

    @And('^In database user has updated facebook details the same as specified in facebook account$')
    def "In database user has updated facebook details the same as specified in facebook account"() {
        commonStepsService.checkFacebookUserDetails(currentUserDevices, deviceSet)
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

    @When('^Registered user enters the same Facebook credentials$')
    def "Registered user enters the same Facebook credentials"() {
        currentUserDevices.each {
            deviceSet.loginUsingFacebook(it)
        }
    }

    @And('^First account is updated with new device uid$')
    def "First account is updated with new device uid"() {
        commonStepsService.assertFirstAccountIsUpdated(currentUserDevices, deviceSet, oldUserIdMap)
    }

    @When('^Registered user enters new Facebook credentials$')
    def "Registered user enters new Facebook credentials"() {
        currentUserDevices.each {
            deviceSet.loginUsingFacebookWithDefinedEmail(it, userDataCreator.generateEmail())
        }
    }

    @And('^First account remains deactivated$')
    def "First account remains deactivated"() {
        commonStepsService.assertAccountDeactivated(currentUserDevices, oldUserIdMap)
    }

    @And('^In database new user has facebook details the same as specified in new facebook account$')
    def "In database new user has facebook details the same as specified in new facebook account"() {
        commonStepsService.checkFacebookUserDetails(currentUserDevices, deviceSet)
    }

    @Then('^New user is successfully activated$')
    def "New user is successfully activated"() {
        commonStepsService.assertNewUserActivated(currentUserDevices, tempUserIdMap)
    }

    @When('^Registered user enters first Facebook credentials$')
    def "Registered user enters first Facebook credentials"() {
        zippedUserDevices.each {
            deviceSet.loginUsingFacebookWithOtherDevice(it[0], it[1])
        }
    }

    @When('^User registers using second activated device$')
    def "User registers using second activated device"() {
        registerUsingFacebook(secondUserDevices, deviceSet, secondUserIdMap)
        zippedUserDevices = [currentUserDevices, secondUserDevices].transpose()
        zippedUserDevices.each {
            deviceSet.singupWithOtherDevice(it[0], it[1])
        }
    }

    @And('^Second account becomes deactivated$')
    def "Second account becomes deactivated"() {
        commonStepsService.assertAccountDeactivated(secondUserDevices, secondUserIdMap)
    }

    @And('^Second account remains deactivated$')
    def "Second account remains deactivated"() {
        commonStepsService.assertAccountDeactivated(secondUserDevices, secondUserIdMap)
    }

    @And('^First account is updated with second device uid$')
    def "First account is updated with second device uid"() {
        commonStepsService.assertFirstAccountIsUpdatedWithSecondDeviceUID(zippedUserDevices, deviceSet, oldUserIdMap)
    }

    def registerUsingFacebook(List<UserDeviceData> devices, MQAppClientDeviceSet deviceSet, Map<UserDeviceData, Integer> userIdMap) {
        devices.each {
            deviceSet.singup(it)
            deviceSet.loginUsingFacebook(it)
            def phoneState = deviceSet.getPhoneState(it)
            userIdMap.put(it, userDbService.findUser(phoneState, it).id)
        }
    }
}
