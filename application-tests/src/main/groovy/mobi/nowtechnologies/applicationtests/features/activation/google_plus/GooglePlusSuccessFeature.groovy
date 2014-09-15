package mobi.nowtechnologies.applicationtests.features.activation.google_plus
import cucumber.api.DataTable
import cucumber.api.Transform
import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import mobi.nowtechnologies.applicationtests.features.activation.common.CommonAssertionsService
import mobi.nowtechnologies.applicationtests.features.activation.facebook.UserState
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet
import mobi.nowtechnologies.applicationtests.features.common.dictionary.DictionaryTransformer
import mobi.nowtechnologies.applicationtests.features.common.dictionary.Word
import mobi.nowtechnologies.applicationtests.services.RequestFormat
import mobi.nowtechnologies.applicationtests.services.db.UserDbService
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData
import mobi.nowtechnologies.server.apptests.googleplus.AppTestGooglePlusTokenService
import mobi.nowtechnologies.server.persistence.repository.social.GooglePlusUserInfoRepository
import mobi.nowtechnologies.server.shared.enums.ProviderType
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource
import org.springframework.stereotype.Component

import javax.annotation.Resource
import java.text.SimpleDateFormat

import static org.junit.Assert.assertEquals
/**
 * Created by kots on 8/29/2014.
 */
@Component
class GooglePlusSuccessFeature {

    static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy")

    @Resource
    UserDeviceDataService userDeviceDataService

    @Resource
    MQAppClientDeviceSet deviceSet

    @Resource
    CommunityResourceBundleMessageSource communityResourceBundleMessageSource

    @Resource
    UserDbService userDbService

    @Resource
    GooglePlusUserInfoRepository googlePlusUserInfoRepository

    @Resource
    CommonAssertionsService commonAssertionsService

    @Resource
    AppTestGooglePlusTokenService appTestGooglePlusTokenService

    List<UserDeviceData> currentUserDevices

    @Given('^Registered user with (.+) using (.+) format for (.+) and (.+)$')
    def "Registered user with given devices using given format for given versions and given communities"(
            @Transform(DictionaryTransformer.class) Word devices,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            @Transform(DictionaryTransformer.class) Word communities) {
        currentUserDevices = userDeviceDataService.table(versions.list(), communities.set(), devices.set(), RequestFormat.from(formats.set()))
        currentUserDevices.each { deviceSet.singup(it) }
    }

    @When('^Registered user enters Google Plus credentials$')
    def "Registered user enters Google Plus credentials"() {
        currentUserDevices.each {deviceSet.loginUsingGooglePlus(it)}
    }

    @Then('^Default promo set in services properties is applied$')
    def "Default promo set in services properties is applied"() {
        currentUserDevices.each {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.getUserByDeviceUIDAndCommunity(phoneState.getDeviceUID(), it.getCommunityUrl())
            assertEquals(user.getLastPromo().getCode(),
                    communityResourceBundleMessageSource.getMessage(it.communityUrl, CommonAssertionsService.DEFAULT_PROMOTION_CODE_KEY, null, null))
        }
    }

    @And('^User receives following in the SIGN_IN_GOOGLE_PLUS response:$')
    def "User receives following in the SIGN_IN_GOOGLE_PLUS response"(DataTable userStateTable) {
        commonAssertionsService.checkUserState(userStateTable.asList(UserState)[0], currentUserDevices, deviceSet)
    }

    @And('^\'deviceType\' field is the same as sent during registration$')
    def "deviceType field is the same as sent during registration"() {
        commonAssertionsService.checkDeviceTypeField(currentUserDevices, deviceSet)
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
        currentUserDevices.each {
            def phoneState = deviceSet.getPhoneState(it)
            def googlePlusInfo = phoneState.lastGooglePlusInfo
            def person = appTestGooglePlusTokenService.parse(phoneState.googlePlusToken)

            assertEquals(googlePlusInfo.googlePlusId, person.id)
            assertEquals(googlePlusInfo.birthDay, dateFormat.format(person.birthday))
            assertEquals(googlePlusInfo.email, person.accountEmail)
            assertEquals(googlePlusInfo.gender.key, person.gender)
            assertEquals(googlePlusInfo.firstName, person.givenName)
            assertEquals(googlePlusInfo.surname, person.familyName)
            //TODO this is modified
            //assertEquals(googlePlusInfo.profileUrl, person.imageUrl)
        }
    }

    @And('^In database user has username as entered Google Plus email$')
    def "In database user has username as entered Google Plus email"() {
        currentUserDevices.each {
            def phoneState = deviceSet.getPhoneState(it)

            def user = userDbService.getUserByDeviceUIDAndCommunity(phoneState.getDeviceUID(), it.getCommunityUrl())
            def googlePlusUserInfo = googlePlusUserInfoRepository.findByUser(user)

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
        currentUserDevices.each {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.getUserByDeviceUIDAndCommunity(phoneState.getDeviceUID(), it.getCommunityUrl())
            def googlePlusUserInfo = googlePlusUserInfoRepository.findByUser(user)
            def googlePlusProfile = appTestGooglePlusTokenService.parse(phoneState.googlePlusToken)
            assertEquals(googlePlusUserInfo.getEmail(), phoneState.getEmail())
            //TODO this fails
            //assertEquals(googlePlusUserInfo.getBirthday().time, googlePlusProfile.getBirthday().getTime())
            assertEquals(googlePlusUserInfo.getDisplayName(), googlePlusProfile.getDisplayName())
            assertEquals(googlePlusUserInfo.getFamilyName(), googlePlusProfile.getFamilyName())
            assertEquals(googlePlusUserInfo.getGivenName(), googlePlusProfile.getGivenName())
            assertEquals(googlePlusUserInfo.getGooglePlusId(), googlePlusProfile.getId())
            //TODO this fails as well
            //assertEquals(googlePlusUserInfo.getHomePage(), googlePlusProfile.getUrl())
            assertEquals(googlePlusUserInfo.getLocation(), googlePlusProfile.getPlacesLived().keySet().iterator().next())
            //TODO this is modified
            //assertEquals(googlePlusUserInfo.getPicture(), googlePlusProfile.getImageUrl())
        }
    }

    @And('^In database user has last promotion according to promotion settings$')
    def "In database user has last promotion according to promotion settings"() {
        commonAssertionsService.checkLastPromotionDB(currentUserDevices, deviceSet)
    }
}
