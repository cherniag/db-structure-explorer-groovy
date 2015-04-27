package mobi.nowtechnologies.applicationtests.features.payments

import cucumber.api.Transform
import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word
import mobi.nowtechnologies.applicationtests.services.RequestFormat
import mobi.nowtechnologies.applicationtests.services.db.UserDbService
import mobi.nowtechnologies.applicationtests.services.device.PhoneState
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService
import mobi.nowtechnologies.applicationtests.services.device.domain.ApiVersions
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData
import mobi.nowtechnologies.applicationtests.services.http.ResponseWrapper
import mobi.nowtechnologies.applicationtests.services.runner.Runner
import mobi.nowtechnologies.applicationtests.services.runner.RunnerService
import mobi.nowtechnologies.applicationtests.services.subscribe.SubscriptionService
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto
import mobi.nowtechnologies.server.persistence.domain.Community
import mobi.nowtechnologies.server.persistence.domain.DeviceUserData
import mobi.nowtechnologies.server.persistence.domain.User
import mobi.nowtechnologies.server.persistence.domain.enums.PaymentPolicyType
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository
import mobi.nowtechnologies.server.shared.enums.MediaType
import org.junit.Assert
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource
import java.util.concurrent.ConcurrentHashMap

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/24/2015
 */
@Component
class ITunesPaymentFeature {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    UserDeviceDataService userDeviceDataService
    @Resource
    MQAppClientDeviceSet deviceSet
    @Resource
    SubscriptionService subscriptionService
    @Resource
    UserDbService userDbService
    @Resource
    RunnerService runnerService;
    @Resource
    PaymentPolicyRepository paymentPolicyRepository;
    @Resource
    CommunityRepository communityRepository

    Community community
    PaymentPolicy iTunesPaymentPolicy
    String appStoreReceipt
    int nextSubPaymentSeconds;
    Runner runner;
    List<UserDeviceData> userDeviceDatas
    Map<DeviceUserData, User> userByDeviceUserData = [:]
    ConcurrentHashMap<DeviceUserData, ResponseWrapper<AccountCheckDto>> responses = [:]

    //
    // Scenario: Activated user sends valid Apple Store receipt and payment verification occurs immediately
    //
    @Given('^Activated user with (.+) device using (.+) for (.+) bellow (.+) and (.+) community$')
    def given0(String deviceType,
               @Transform(DictionaryTransformer.class) Word formats,
               @Transform(DictionaryTransformer.class) Word versions,
               String bellow,
               String communityUrl){
        def versionsBellow = ApiVersions.from(versions.list()).bellow(bellow)
        userDeviceDatas = userDeviceDataService.table(versionsBellow, communityUrl, [deviceType], formats.set(RequestFormat));

        runner = runnerService.create(userDeviceDatas)
        runner.parallel {
            deviceSet.singup(it)
            deviceSet.loginUsingFacebook(it)
        }

        community = communityRepository.findByName(communityUrl)
    }

    @When('^Client is on Preview state$')
    def when0() {
        userDeviceDatas.each {
            User user = findUserInDatabase(it, deviceSet.getPhoneState(it))
            subscriptionService.limitAccess(user, new Date())
        }
    }

    @And('^Subscribes via iTunes using existing recurrent payment policy$')
    def and0() {
        List<PaymentPolicy> policies = paymentPolicyRepository.findPaymentPolicies(community, [MediaType.AUDIO])
        iTunesPaymentPolicy = policies.find { it.paymentType == 'iTunesSubscription' && it.paymentPolicyType == PaymentPolicyType.RECURRENT }

        nextSubPaymentSeconds = iTunesPaymentPolicy.period.toNextSubPaymentSeconds((new Date().getTime() / 1000).intValue())
        appStoreReceipt = "renewable:200:0:${iTunesPaymentPolicy.appStoreProductId}:0123456789:${nextSubPaymentSeconds}000".toString()
    }

    @And('^Sends ACC_CHECK request with provided valid Apple Store receipt$')
    def and1() {
        runner.parallel {
            responses[(it)] = deviceSet.accountCheckFromIOS(it, appStoreReceipt)
        }
    }

    @Then('^Response should have (.+) http status$')
    def then0(int status) {
        userDeviceDatas.each {
            Assert.assertEquals(status, responses[(it)].httpStatus)
        }
    }

    @And('^Response header \'(.+)\' should be in the future$')
    def and8(String status){
        userDeviceDatas.each {
            String headerValue = responses[(it)].headers[status].get(0)
            long expires = Date.parse(headerValue)
            Assert.assertTrue(new Date().before(new Date(expires)))
        }
    }

    @And('^Client should have \'(.+)\' status$')
    def and0(String status){
        userDeviceDatas.each {
            PhoneState state = deviceSet.getPhoneState(it)
            Assert.assertEquals(status, state.lastAccountCheckResponse.status)
        }
    }

    @And('^Next sub payment should be the same as expiration date of receipt$')
    def and2(){
        userDeviceDatas.each {
            PhoneState state = deviceSet.getPhoneState(it)
            Assert.assertEquals(nextSubPaymentSeconds, state.lastAccountCheckResponse.nextSubPaymentSeconds)
        }
    }

    @And('^Payment type should be \'(.+)\'$')
    def and3(String paymentType){
        userDeviceDatas.each {
            PhoneState state = deviceSet.getPhoneState(it)
            Assert.assertEquals(paymentType, state.lastAccountCheckResponse.paymentType)
        }
    }

    @And('^Current payment details should not exist$')
    def and4(){
        userDeviceDatas.each {
            User user = findUserInDatabase(it, deviceSet.getPhoneState(it))
            Assert.assertNull(user.getCurrentPaymentDetails())
        }
    }

    //
    // Scenario: Activated user sends valid Apple Store receipt and payment details are created
    //
    @Given('^Activated user with (.+) device using (.+) for (.+) above (.+) and (.+) community$')
    def given1(String deviceType,
               @Transform(DictionaryTransformer.class) Word formats,
               @Transform(DictionaryTransformer.class) Word versions,
               String above,
               String communityUrl){
        def versionsAbove = ApiVersions.from(versions.list()).above(above)
        userDeviceDatas = userDeviceDataService.table(versionsAbove, communityUrl, [deviceType], formats.set(RequestFormat));

        runner = runnerService.create(userDeviceDatas)
        runner.parallel {
            deviceSet.singup(it)
            deviceSet.loginUsingFacebook(it)
        }

        community = communityRepository.findByName(communityUrl)
    }

    @Then('^Next sub payment should be in the past$')
    def and5(){
        userDeviceDatas.each {
            PhoneState state = deviceSet.getPhoneState(it)
            Assert.assertTrue((new Date().getTime() / 1000).intValue() > state.lastAccountCheckResponse.nextSubPaymentSeconds)
        }
    }

    @And('^User should have active current payment details$')
    def and6(){
        userDeviceDatas.each {
            User user = findUserInDatabase(it, deviceSet.getPhoneState(it))
            userByDeviceUserData[(it)] = user

            Assert.assertNotNull(user.getCurrentPaymentDetails())
            Assert.assertTrue(user.getCurrentPaymentDetails().activated)
        }
    }

    @And('^Payment type of current payment details should be \'(.+)\'$')
    def and7(String paymentType){
        userDeviceDatas.each {
            User user = userByDeviceUserData[(it)]
            Assert.assertEquals(paymentType, user.getCurrentPaymentDetails().paymentType)
        }
    }

    @And('^Payment policy of current payment details should be the same as subscribed$')
    def and8(){
        userDeviceDatas.each {
            User user = userByDeviceUserData[(it)]
            def currentPaymentDetails = user.getCurrentPaymentDetails()

            Assert.assertEquals(iTunesPaymentPolicy.id, currentPaymentDetails.paymentPolicy.id)
            Assert.assertEquals(iTunesPaymentPolicy.appStoreProductId, currentPaymentDetails.paymentPolicy.appStoreProductId)
            Assert.assertEquals(iTunesPaymentPolicy.period.duration, currentPaymentDetails.paymentPolicy.period.duration)
            Assert.assertEquals(iTunesPaymentPolicy.period.durationUnit, currentPaymentDetails.paymentPolicy.period.durationUnit)
            Assert.assertEquals(iTunesPaymentPolicy.subcost, currentPaymentDetails.paymentPolicy.subcost)
            Assert.assertEquals(iTunesPaymentPolicy.paymentType, currentPaymentDetails.paymentPolicy.paymentType)
        }
    }

    private User findUserInDatabase(UserDeviceData userDeviceData, PhoneState phoneState) {
        return userDbService.findUser(phoneState, userDeviceData);
    }
}
