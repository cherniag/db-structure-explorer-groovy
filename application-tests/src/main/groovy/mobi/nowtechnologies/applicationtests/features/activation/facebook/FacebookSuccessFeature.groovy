package mobi.nowtechnologies.applicationtests.features.activation.facebook

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
import mobi.nowtechnologies.applicationtests.services.device.domain.ApiVersions
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData
import mobi.nowtechnologies.applicationtests.services.http.facebook.FacebookUserInfoGenerator
import mobi.nowtechnologies.applicationtests.services.runner.Runner
import mobi.nowtechnologies.applicationtests.services.runner.RunnerService
import mobi.nowtechnologies.server.persistence.repository.AccountLogRepository
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository
import mobi.nowtechnologies.server.shared.enums.ProviderType
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfoRepository
import mobi.nowtechnologies.server.social.domain.SocialNetworkType
import mobi.nowtechnologies.server.social.service.facebook.impl.mock.AppTestFacebookOperationsAdaptor
import mobi.nowtechnologies.server.social.service.facebook.impl.mock.AppTestFacebookTokenService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource
import java.text.SimpleDateFormat

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse

/**
 * @author kots
 * @since 8/14/2014.
 */
@Component
class FacebookSuccessFeature {
    @Resource
    UserDbService userDbService

    @Resource
    UserDeviceDataService userDeviceDataService

    @Resource
    SocialNetworkInfoRepository socialNetworkInfoRepository

    @Resource
    AccountLogRepository accountLogRepository

    @Resource
    MQAppClientDeviceSet deviceSet

    @Resource
    PromotionRepository promotionRepository

    @Resource
    AppTestFacebookTokenService composer

    @Resource
    CommunityResourceBundleMessageSource communityResourceBundleMessageSource

    @Resource
    CommonAssertionsService commonAssertionsService

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
        runner.parallel { deviceSet.singup(it) }
    }

    @When('^Registered user enters Facebook credentials$')
    def "Registered_user_enters_Facebook_credentials"() {
        runner.parallel { deviceSet.loginUsingFacebook(it) }
    }

    @Then('^Default promo set in services properties is applied$')
    def "Default_promo_set_in_services_properties_is_applied"() {
        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)

            def user = userDbService.findUser(phoneState, it)
            def facebookUserInfo = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.FACEBOOK)

            assertEquals(facebookUserInfo.getEmail(), phoneState.getEmail())
            assertEquals(user.getLastPromo().getCode(),
                    communityResourceBundleMessageSource.getMessage(it.communityUrl, CommonAssertionsService.DEFAULT_PROMOTION_CODE_KEY, null, null))
        }
    }

    @And('^User receives following in the SIGN_IN_FACEBOOK response:$')
    def "User receives following in the SIGN_IN_FACEBOOK_response"(DataTable userStateTable) {
        def userState = userStateTable.asList(UserState)[0];
        runner.parallel {
            def lastFacebookInfo = deviceSet.getPhoneState(it).lastFacebookInfo
            assertEquals(userState.activation.name(), lastFacebookInfo.activation)
            assertEquals(userState.freeTrial, lastFacebookInfo.freeTrial)
            assertEquals(userState.fullyRegistred, lastFacebookInfo.fullyRegistred)
            assertEquals(userState.hasAllDetails, lastFacebookInfo.hasAllDetails)
            assertEquals(userState.paymentType, lastFacebookInfo.paymentType)
            assertEquals(userState.provider, lastFacebookInfo.provider)
            assertEquals(userState.status, lastFacebookInfo.status)
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

    @And('^\'username\' field is the same as entered Facebook email$')
    def "username field is the same as entered Facebook email"() {
        commonAssertionsService.checkUsernameField(currentUserDevices, deviceSet)
    }

    @And('^\'timeOfMovingToLimitedStatusSeconds\' and \'nextSubPaymentSeconds\' fields are the end date of given promotion$')
    def "timeOfMovingToLimitedStatusSeconds and nextSubPaymentSeconds fields are the end date of given promotion"() {
        commonAssertionsService.checkStatusAndSubPaymentTimes(currentUserDevices, deviceSet)
    }

    @And('^\'userDetails\' filed contains correct facebook details$')
    def "userDetails filed contains correct facebook details"() {
        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def expected = composer.parseToken(phoneState.facebookAccessToken)
            assertEquals(phoneState.facebookUserId, expected.id)

            def actual = phoneState.lastFacebookInfo.userDetails

            assertEquals(expected.id, actual.userName)
            assertEquals(expected.id, actual.facebookId)
            assertEquals(dateFormat.parse(expected.getBirthday()), dateFormat.parse(actual.birthDay))
            assertEquals(expected.firstName, actual.firstName)
            assertEquals(expected.lastName, actual.surname)
            assertEquals(expected.gender, actual.gender.toLowerCase())
            assertEquals(FacebookUserInfoGenerator.CITY, actual.location)
            assertEquals(phoneState.email, actual.email)
        }
    }

    @And('^In database user has username as entered Facebook email$')
    def "In database user has username as entered Facebook email"() {
        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            assertEquals(user.getUserName(), phoneState.email)
        }
    }

    @And('^In database user has deviceType according to device on which registration is done$')
    def "In database user has deviceType according to device on which registration is done"() {
        commonAssertionsService.checkDeviceTypeDB(currentUserDevices, deviceSet)
    }

    @And('^In database user has ACTIVATED activation status$')
    def "In database user has ACTIVATED activation status"() {
        commonAssertionsService.checkActivatedDB(currentUserDevices, deviceSet)
    }

    @And('^In database user has FACEBOOK provider$')
    def "In database user has FACEBOOK provider"() {
        commonAssertionsService.checkProviderDB(currentUserDevices, deviceSet, ProviderType.FACEBOOK)
    }

    @And('^In database user has last promotion according to promotion settings$')
    def "In database user has last promotion according to promotion settings"() {
        commonAssertionsService.checkLastPromotionDB(currentUserDevices, deviceSet)
    }

    @And('^In database user does not have payment details$')
    def "In database user does not have payment details"() {
        commonAssertionsService.checkNoPaymentDetailsDB(currentUserDevices, deviceSet)
    }

    @And('^In database user has log for applied promotion$')
    @Transactional("applicationTestsTransactionManager")
    def "In database user has log for applied promotion"() {
        commonAssertionsService.checkAppliedForPromotionDB(currentUserDevices, deviceSet)
    }

    @And('^In database user has facebook details the same as specified in facebook account$')
    def "In database user has facebook details the same as specified in facebook account"() {
        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def expected = composer.parseToken(phoneState.facebookAccessToken)
            assertEquals(phoneState.facebookUserId, expected.id)

            def user = userDbService.findUser(phoneState, it)
            def actual = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.FACEBOOK)

            assertEquals(expected.id, actual.getSocialNetworkId())
            assertEquals(expected.id, actual.getUserName())

            assertEquals(phoneState.getEmail(), actual.getEmail())
            assertEquals(FacebookUserInfoGenerator.FIRST_NAME, actual.getFirstName())
            assertEquals(dateFormat.parse(expected.getBirthday()).getTime(), actual.getBirthday().getTime())
            assertEquals(FacebookUserInfoGenerator.SURNAME, actual.getLastName())
            assertEquals(FacebookUserInfoGenerator.CITY, actual.getCity())
            assertEquals(FacebookUserInfoGenerator.COUNTRY, actual.getCountry())
        }
    }

    @Given('^Registered user with (.+) using (.+) format for (.+) above (.+) and (.+)$')
    def "Registered user with given devices using given format for given versions above version and given communities"(
            @Transform(DictionaryTransformer.class) Word devices,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            String aboveVersion,
            @Transform(DictionaryTransformer.class) Word communities) {

        def above = ApiVersions.from(versions.list()).above(aboveVersion)
        currentUserDevices = userDeviceDataService.table(above, communities.set(), devices.set(), formats.set(RequestFormat))

        runner = runnerService.create(currentUserDevices)
        runner.parallel { deviceSet.singup(it) }
    }

    @Then('^User receives additional facebook profile image url in the SIGN_IN_FACEBOOK response$')
    def "User receives additional facebook profile image url in the SIGN_IN_FACEBOOK response"() {
        runner.parallel {
            def lastFacebookInfo = deviceSet.getPhoneState(it).lastFacebookInfo
            assertEquals(AppTestFacebookOperationsAdaptor.TEST_PROFILE_IMAGE_URL, lastFacebookInfo.userDetails.facebookProfileImageUrl)
            assertFalse(lastFacebookInfo.userDetails.facebookProfileImageSilhouette)
        }
    }

    private SimpleDateFormat getDateFormat() {
        new SimpleDateFormat("MM/dd/yyyy")
    }
}
