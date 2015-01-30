package mobi.nowtechnologies.applicationtests.features.activation.facebook
import cucumber.api.DataTable
import cucumber.api.Transform
import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import mobi.nowtechnologies.applicationtests.services.CommonAssertionsService
import mobi.nowtechnologies.applicationtests.features.activation.common.UserState
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word
import mobi.nowtechnologies.applicationtests.services.RequestFormat
import mobi.nowtechnologies.applicationtests.services.db.UserDbService
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData
import mobi.nowtechnologies.applicationtests.services.http.facebook.FacebookUserInfoGenerator
import mobi.nowtechnologies.applicationtests.services.runner.Runner
import mobi.nowtechnologies.applicationtests.services.runner.RunnerService
import mobi.nowtechnologies.server.apptests.facebook.AppTestFacebookTokenService
import mobi.nowtechnologies.server.persistence.repository.AccountLogRepository
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository
import mobi.nowtechnologies.server.persistence.repository.social.FacebookUserInfoRepository
import mobi.nowtechnologies.server.shared.enums.ProviderType
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource
import java.text.SimpleDateFormat

import static org.junit.Assert.assertEquals
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
    FacebookUserInfoRepository fbDetailsRepository

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
        currentUserDevices.each { deviceSet.loginUsingFacebook(it) }
    }

    @Then('^Default promo set in services properties is applied$')
    def "Default_promo_set_in_services_properties_is_applied"() {
        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)

            def user = userDbService.findUser(phoneState, it)
            def facebookUserInfo = fbDetailsRepository.findByUser(user)

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
        commonAssertionsService.checkDeviceTypeField(currentUserDevices, deviceSet)
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
            def facebookInfo = phoneState.lastFacebookInfo.userDetails
            def facebookProfile = composer.parseToken(phoneState.facebookAccessToken)

            assertEquals(dateFormat.parse(facebookInfo.birthDay), dateFormat.parse(facebookProfile.getBirthday()))
            assertEquals(facebookInfo.email, phoneState.email)
            assertEquals(facebookInfo.userName, facebookProfile.username)
            assertEquals(facebookInfo.firstName, facebookProfile.firstName)
            assertEquals(facebookInfo.surname, facebookProfile.lastName)
            assertEquals(facebookInfo.gender.toLowerCase(), facebookProfile.gender)
            assertEquals(facebookInfo.location, FacebookUserInfoGenerator.CITY)
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
            def user = userDbService.findUser(phoneState, it)
            def facebookUserInfo = fbDetailsRepository.findByUser(user)
            def facebookProfile = composer.parseToken(phoneState.facebookAccessToken)
            assertEquals(facebookUserInfo.getEmail(), phoneState.getEmail())
            assertEquals(facebookUserInfo.getFirstName(), FacebookUserInfoGenerator.FIRST_NAME)
            assertEquals(facebookUserInfo.getBirthday().getTime(), dateFormat.parse(facebookProfile.getBirthday()).getTime())
            assertEquals(facebookUserInfo.getSurname(), FacebookUserInfoGenerator.SURNAME)
            assertEquals(facebookUserInfo.getCity(), FacebookUserInfoGenerator.CITY)
            assertEquals(facebookUserInfo.getCountry(), FacebookUserInfoGenerator.COUNTRY)
            assertEquals(facebookUserInfo.getFacebookId(), phoneState.getFacebookUserId())
            assertEquals(facebookUserInfo.getUserName(), phoneState.getEmail())
        }
    }

    private SimpleDateFormat getDateFormat() {
        new SimpleDateFormat("MM/dd/yyyy")
    }
}
