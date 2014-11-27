package mobi.nowtechnologies.applicationtests.features.registration

import cucumber.api.DataTable
import cucumber.api.Transform
import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import mobi.nowtechnologies.applicationtests.features.common.client.PartnerDeviceSet
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word
import mobi.nowtechnologies.applicationtests.services.CommonAssertionsService
import mobi.nowtechnologies.applicationtests.services.RequestFormat
import mobi.nowtechnologies.applicationtests.services.db.UserDbService
import mobi.nowtechnologies.applicationtests.services.device.PhoneState
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService
import mobi.nowtechnologies.applicationtests.services.device.domain.ApiVersions
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData
import mobi.nowtechnologies.server.persistence.domain.User
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO
import mobi.nowtechnologies.server.shared.enums.ActivationStatus
import org.springframework.stereotype.Component

import javax.annotation.Resource
import java.util.regex.Pattern

import static org.junit.Assert.*

/**
 * Author: Gennadii Cherniaiev
 * Date: 11/5/2014
 */
@Component
public class AccountRegistrationSuccessFeature {
    @Resource
    private PartnerDeviceSet partnerDeviceSet

    @Resource
    private UserDeviceDataService userDeviceDataService

    @Resource
    private UserDbService userDbService

    @Resource
    private CommonAssertionsService commonAssertionsService

    private List<UserDeviceData> userDeviceDatas

    private Map<UserDeviceData, User> inDbUsers = [:]

    def userFields = ['userName', 'deviceUID','userGroupId','nextSubPayment','currentPaymentDetailsId',
                      'facebookId','token','paymentStatus','paymentType','potentialPromoCodePromotionId','potentialPromotionId','operator','mobile',
                      'firstDeviceLoginMillis','device','deviceModel','ipAddress','deviceString','freeTrialStartedTimestampMillis','freeTrialExpiredMillis',
                      'activationStatus','segment','provider','tariff','contractChannel','lastPromoId','contract']


    @Given('^First time user with (.+) using (.+) formats for (.+) and (.+)$')
    public void given(
            @Transform(DictionaryTransformer.class) Word deviceTypes,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            @Transform(DictionaryTransformer.class) Word communities){
        userDeviceDatas = userDeviceDataService.table(versions.list(), communities.set(), deviceTypes.set(), RequestFormat.from(formats.set()));
    }

    @When('^User registers using device$')
    public void whenUserRegisters(){
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            partnerDeviceSet.singup(userDeviceData);
        }
    }

    @Then('^Temporary account is created$')
    public void tempAccountIsCreated() {
        userDeviceDatas.each {
            PhoneState phoneState = partnerDeviceSet.getPhoneState(it);
            User user = userDbService.getUserByUserNameAndCommunity(phoneState.lastAccountCheckResponse.userName, it.communityUrl)
            assertEquals(user.getDeviceUID(), user.getUserName());
        }
    }

    @And('^User receives following in SIGN_UP_DEVICE response:$')
    def "User receives following in SIGN_UP_DEVICE response:"(DataTable userStateTable) {
        def userState = userStateTable.asList(AccountCheckDTO)[0];
        userDeviceDatas.each {
            def signUpResponse = partnerDeviceSet.getPhoneState(it).lastAccountCheckResponse
            assertEquals(userState.activation, signUpResponse.activation)
            assertEquals(userState.freeTrial, signUpResponse.freeTrial)
            assertEquals(userState.fullyRegistred, signUpResponse.fullyRegistred)
            assertEquals(userState.hasAllDetails, signUpResponse.hasAllDetails)
            assertEquals(userState.paymentType, signUpResponse.paymentType)
            assertEquals(userState.provider, signUpResponse.provider)
            assertEquals(userState.status, signUpResponse.status)
        }
    }

    @And('^\'deviceType\' field is the same as sent during registration$')
    def "deviceType field is the same as sent during registration"() {
        commonAssertionsService.checkDeviceTypeField(userDeviceDatas, partnerDeviceSet)
    }

    @And('^\'deviceUID\' field is the same as sent during registration$')
    def "deviceUID field is the same as sent during registration"() {
        commonAssertionsService.checkDeviceUIDField(userDeviceDatas, partnerDeviceSet)
    }

    @And('^\'username\' field is the same as \'deviceUID\' sent during registration$')
    def "username field is the same as deviceUID sent during registration"() {
        userDeviceDatas.each {
            def phoneState = partnerDeviceSet.getPhoneState(it)
            assertEquals(phoneState.getDeviceUID(), phoneState.lastAccountCheckResponse.userName)
        }
    }

    @And('In database user has username and deviceUID as deviceUID sent during registration')
    def "In database user has username and deviceUID as deviceUID sent during registration"(){
        userDeviceDatas.each {
            def phoneState = partnerDeviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            assertEquals(user.getDeviceUID(), phoneState.deviceUID)
        }
    }

    @And('In database user has deviceType according to device on which registration is done')
    def "In database user has deviceType according to device on which registration is done"(){
        commonAssertionsService.checkDeviceTypeDB(userDeviceDatas, partnerDeviceSet)
    }

    @And('In database user has (\\w+) activation status')
    def "In database user has REGISTERED activation status"(ActivationStatus activationStatus){
        userDeviceDatas.each {
            def phoneState = partnerDeviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            assertEquals(activationStatus, user.activationStatus)
        }
    }

    @And('In database user does not have provider')
    def "In database user does not have provider"(){
        userDeviceDatas.each {
            def phoneState = partnerDeviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            assertNull(user.provider)
        }
    }

    @And('In database user does not have last promotions')
    def "In database user does not have last promotions"(){
        userDeviceDatas.each {
            def phoneState = partnerDeviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            assertNull(user.lastPromo)
        }
    }

    @And('In database user does not have payment details')
    def "In database user does not have payment details"(){
        userDeviceDatas.each {
            def phoneState = partnerDeviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            assertNull(user.currentPaymentDetails)
        }
    }

    @Given('^First time user with (.+) using (.+) formats with (.+) above (.+) and (.+)$')
    def "First time user with all devices using JSON and XML formats all versions above 6.5 and all communities"(
            @Transform(DictionaryTransformer.class) Word deviceTypes,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            String aboveVersion,
            @Transform(DictionaryTransformer.class) Word communities){
        userDeviceDatas = userDeviceDataService.table(ApiVersions.from(versions.list()).above(aboveVersion), communities.set(), deviceTypes.set(), RequestFormat.from(formats.set()));
    }

    @And('^\'uuid\' field is present in response and has UUID pattern$')
    def "'uuid' field is present in response and has UUID pattern"(){
        userDeviceDatas.each {
            def phoneState = partnerDeviceSet.getPhoneState(it)
            assertNotNull(phoneState.lastAccountCheckResponse.uuid)
            def pattern = Pattern.compile('\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}')
            def matcher = pattern.matcher(phoneState.lastAccountCheckResponse.uuid)
            assertTrue(matcher.matches())
        }
    }

    @Given('^Registered user with (.+) using (.+) format for (.+) and (.+)$')
    def "Registered user with given devices using given format for given versions and given communities"(
            @Transform(DictionaryTransformer.class) Word devices,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            @Transform(DictionaryTransformer.class) Word communities) {
        userDeviceDatas = userDeviceDataService.table(versions.list(), communities.set(), devices.set(), RequestFormat.from(formats.set()))
        userDeviceDatas.each {
            partnerDeviceSet.singup(it)
            def phoneState = partnerDeviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            inDbUsers.put(it, user);
        }
    }

    @When('^User registers using same device$')
    def "User registers using same device"() {
        userDeviceDatas.each {
            def phoneState = partnerDeviceSet.getPhoneState(it)
            partnerDeviceSet.singup(it, null, null, true, phoneState.deviceUID);
        }
    }

    @When('^In database new temporary account does not appear$')
    def "In database new temporary account does not appear"() {
        userDeviceDatas.each {
            def phoneState = partnerDeviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            assertEquals(inDbUsers[it].id, user.id)
        }
    }

    @And('^In database user account remains unchanged$')
    def "In database user account remains unchanged"() throws Throwable {
        userDeviceDatas.each {
            def phoneState = partnerDeviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            def oldUser = inDbUsers[it]

            userFields.each {fieldName ->
                assertEquals(oldUser[fieldName], user[fieldName])
            }
        }
    }
}


