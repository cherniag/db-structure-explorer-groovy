package mobi.nowtechnologies.applicationtests.features.activation.google_plus

import cucumber.api.DataTable
import cucumber.api.Transform
import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import mobi.nowtechnologies.applicationtests.features.activation.common.UserState
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word
import mobi.nowtechnologies.applicationtests.services.CommonAssertionsService
import mobi.nowtechnologies.applicationtests.services.RequestFormat
import mobi.nowtechnologies.applicationtests.services.db.UserDbService
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData
import mobi.nowtechnologies.applicationtests.services.runner.Runner
import mobi.nowtechnologies.applicationtests.services.runner.RunnerService
import mobi.nowtechnologies.server.shared.enums.ProviderType
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfoRepository
import mobi.nowtechnologies.server.social.domain.SocialNetworkType
import mobi.nowtechnologies.server.social.service.googleplus.impl.mock.AppTestGooglePlusTokenService
import org.springframework.stereotype.Component

import javax.annotation.Resource
import java.text.SimpleDateFormat

import static org.junit.Assert.assertEquals

/**
 * Created by kots on 8/29/2014.
 */
@Component
class GooglePlusSuccessFeature {
    @Resource
    UserDeviceDataService userDeviceDataService

    @Resource
    MQAppClientDeviceSet deviceSet

    @Resource
    CommunityResourceBundleMessageSource communityResourceBundleMessageSource

    @Resource
    UserDbService userDbService

    @Resource
    SocialNetworkInfoRepository socialNetworkInfoRepository

    @Resource
    CommonAssertionsService commonAssertionsService

    @Resource
    AppTestGooglePlusTokenService appTestGooglePlusTokenService

    List<UserDeviceData> currentUserDevices

    @Resource
    RunnerService runnerService;
    Runner runner;

    @Given('^Registered user with (.+) using (.+) format for (.+) and (.+)$')
    def "Registered user with given devices using given format for given versions and given communities"(
            @Transform(DictionaryTransformer.class) Word devices,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            @Transform(DictionaryTransformer.class) Word communities) {
        currentUserDevices = userDeviceDataService.table(versions.list(), communities.set(), devices.set(), formats.set(RequestFormat))
        runner = runnerService.create(currentUserDevices)
        runner.parallel {
            deviceSet.singup(it)
        }
    }

    @When('^Registered user enters Google Plus credentials$')
    def "Registered user enters Google Plus credentials"() {
        runner.parallel { deviceSet.loginUsingGooglePlus(it) }
    }

    @Then('^Default promo set in services properties is applied$')
    def "Default promo set in services properties is applied"() {
        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            assertEquals(user.getLastPromo().getCode(),
                    communityResourceBundleMessageSource.getMessage(it.communityUrl, CommonAssertionsService.DEFAULT_PROMOTION_CODE_KEY, null, null))
        }
    }

    @And('^User receives following in the SIGN_IN_GOOGLE_PLUS response:$')
    def "User receives following in the SIGN_IN_GOOGLE_PLUS response"(DataTable userStateTable) {
        def userState = userStateTable.asList(UserState)[0];
        runner.parallel {
            def lastGooglePlusInfo = deviceSet.getPhoneState(it).lastGooglePlusInfo
            assertEquals(userState.activation.name(), lastGooglePlusInfo.activation)
            assertEquals(userState.freeTrial, lastGooglePlusInfo.freeTrial)
            assertEquals(userState.fullyRegistred, lastGooglePlusInfo.fullyRegistred)
            assertEquals(userState.hasAllDetails, lastGooglePlusInfo.hasAllDetails)
            assertEquals(userState.paymentType, lastGooglePlusInfo.paymentType)
            assertEquals(userState.provider, lastGooglePlusInfo.provider)
            assertEquals(userState.status, lastGooglePlusInfo.status)
        }
    }

    @And('^\'deviceType\' field is the same as sent during registration$')
    def "deviceType field is the same as sent during registration"() {
        runner.parallel {
            commonAssertionsService.checkDeviceTypeField(it, deviceSet)
        }
    }

    @And('^\'deviceUID\' field is the same as sent during registration$')
    def "deviceUID field is the same as sent during registration"() {
        commonAssertionsService.checkDeviceUIDField(currentUserDevices, deviceSet)
    }

    @And('^\'username\' field is the same as entered Google Plus email$')
    def "username field is the same as entered Google Plus email"() {
        commonAssertionsService.checkUsernameField(currentUserDevices, deviceSet)
    }

    @And('^\'timeOfMovingToLimitedStatusSeconds\' and \'nextSubPaymentSeconds\' fields are the end date of given promotion$')
    def "timeOfMovingToLimitedStatusSeconds and nextSubPaymentSeconds fields are the end date of given promotion"() {
        commonAssertionsService.checkStatusAndSubPaymentTimes(currentUserDevices, deviceSet)
    }

    @And('^\'userDetails\' filed contains all specified Google Plus details$')
    def "userDetails filed contains all specified Google Plus details"() {
        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def googlePlusInfo = phoneState.lastGooglePlusInfo.userDetails
            def person = appTestGooglePlusTokenService.parseToken(phoneState.googlePlusToken)

            assertEquals(googlePlusInfo.googlePlusId, person.id)
            assertEquals(googlePlusInfo.birthDay, dateFormat.format(person.birthday))
            assertEquals(googlePlusInfo.email, person.accountEmail)
            assertEquals(googlePlusInfo.gender.toLowerCase(), person.gender)
            assertEquals(googlePlusInfo.firstName, person.givenName)
            assertEquals(googlePlusInfo.surname, person.familyName)
            assertEquals(googlePlusInfo.userName, person.displayName)
        }
    }

    @And('^In database user has username as entered Google Plus email$')
    def "In database user has username as entered Google Plus email"() {
        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)

            def user = userDbService.findUser(phoneState, it)
            def googlePlusUserInfo = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.GOOGLE)

            assertEquals(googlePlusUserInfo.getEmail(), phoneState.getEmail())
        }
    }

    @And('^In database user has deviceType according to device on which registration is done$')
    def "In database user has deviceType according to device on which registration is done"() {
        commonAssertionsService.checkDeviceTypeDB(currentUserDevices, deviceSet)
    }

    @And('^In database user has ACTIVATED activation status$')
    def "In database user has ACTIVATED activation status"() {
        commonAssertionsService.checkDeviceTypeDB(currentUserDevices, deviceSet)
    }

    @And('^In database user has GOOGLE_PLUS provider\$')
    def 'In database user has GOOGLE PLUS provider'() {
        commonAssertionsService.checkProviderDB(currentUserDevices, deviceSet, ProviderType.GOOGLE_PLUS)
    }

    @And('^In database user does not have payment details$')
    def "In database user does not have payment details"() {
        commonAssertionsService.checkNoPaymentDetailsDB(currentUserDevices, deviceSet)
    }

    @And('^In database user has log for applied promotion$')
    def "In database user has log for applied promotion"() {
        commonAssertionsService.checkAppliedForPromotionDB(currentUserDevices, deviceSet)
    }

    @And('^In database user has Google Plus details the same as specified in Google Plus account$')
    def "In database user has Google Plus details the same as specified in Google Plus account"() {
        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            def googlePlusUserInfo = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.GOOGLE)
            def googlePlusProfile = appTestGooglePlusTokenService.parseToken(phoneState.googlePlusToken)
            assertEquals(googlePlusUserInfo.getEmail(), phoneState.getEmail())
            assertEquals(googlePlusUserInfo.getUserName(), googlePlusProfile.getDisplayName())
            assertEquals(googlePlusUserInfo.getLastName(), googlePlusProfile.getFamilyName())
            assertEquals(googlePlusUserInfo.getFirstName(), googlePlusProfile.getGivenName())
            assertEquals(googlePlusUserInfo.getSocialNetworkId(), googlePlusProfile.getId())
            assertEquals(googlePlusUserInfo.getCity(), googlePlusProfile.getPlacesLived().keySet().iterator().next())
        }
    }

    @And('^In database user has last promotion according to promotion settings$')
    def "In database user has last promotion according to promotion settings"() {
        commonAssertionsService.checkLastPromotionDB(currentUserDevices, deviceSet)
    }

    private SimpleDateFormat getDateFormat() {
        new SimpleDateFormat("MM/dd/yyyy")
    }
}
