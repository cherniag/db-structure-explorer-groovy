package mobi.nowtechnologies.applicationtests.features.payments

import cucumber.api.Transform
import cucumber.api.java.After
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
import mobi.nowtechnologies.server.persistence.domain.payment.ITunesPaymentDetails
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository
import mobi.nowtechnologies.server.persistence.repository.UserRepository
import mobi.nowtechnologies.server.shared.enums.MediaType
import mobi.nowtechnologies.server.shared.enums.Tariff
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
    @Resource
    UserRepository userRepository

    Community community
    PaymentPolicy iTunesPaymentPolicy
    PaymentPolicy newITunesPaymentPolicy
    String appStoreReceipt
    String newAppStoreReceipt
    int nextSubPaymentSeconds;
    Runner runner;
    List<UserDeviceData> userDeviceDatas
    ConcurrentHashMap<DeviceUserData, User> userByDeviceUserData = [:]
    ConcurrentHashMap<DeviceUserData, ResponseWrapper<AccountCheckDto>> responses = [:]

    //
    // Scenario: Activated user sends valid Apple Store receipt and payment verification occurs immediately
    //
    @Given('^Activated user with (.+) device using (.+) for (.+) bellow (.+) and (.+) community$')
    def given0(String deviceType,
               @Transform(DictionaryTransformer.class) Word formats,
               @Transform(DictionaryTransformer.class) Word versions,
               String bellow,
               String communityUrl) {
        def versionsBellow = ApiVersions.from(versions.list()).bellow(bellow)
        userDeviceDatas = userDeviceDataService.table(versionsBellow, communityUrl, [deviceType], formats.set(RequestFormat));

        runner = runnerService.create(userDeviceDatas)
        runner.parallel {
            deviceSet.singup(it)
            deviceSet.loginUsingFacebook(it)
        }

        community = communityRepository.findByRewriteUrlParameter(communityUrl)
    }

    @And('^User is on LIMITED state$')
    def and0() {
        runner.parallel {
            User user = findUserInDatabase(it, deviceSet.getPhoneState(it))
            logger.info("Limit access for user {}", user.userName)
            subscriptionService.limitAccess(user, new Date())
        }
    }

    @When('^Subscribes in iTunes on product id of existing recurrent payment policy$')
    def when() {
        iTunesPaymentPolicy = getITunesPaymentPolicy({ it.paymentType == 'iTunesSubscription' && it.paymentPolicyType == PaymentPolicyType.RECURRENT })
        logger.info("Recurrent payment policy found: {}", iTunesPaymentPolicy)

        nextSubPaymentSeconds = iTunesPaymentPolicy.period.toNextSubPaymentSeconds((new Date().getTime() / 1000).intValue())
        appStoreReceipt = "renewable:200:0:${iTunesPaymentPolicy.appStoreProductId}:0123456789:${nextSubPaymentSeconds}000".toString()
        logger.info("Actual receip: {}", appStoreReceipt)
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
    def and8(String headerName) {
        userDeviceDatas.each {
            String headerValue = responses[(it)].headers[headerName].get(0)
            long expires = Date.parse(headerValue)
            logger.info("Header {} is {}", headerName, headerValue)
            Assert.assertTrue("Header value: " + headerValue, new Date().before(new Date(expires)))
        }
    }

    @And('^Client should have \'(.+)\' status$')
    def and0(String status) {
        userDeviceDatas.each {
            PhoneState state = deviceSet.getPhoneState(it)
            Assert.assertEquals(status, state.lastAccountCheckResponse.status)
        }
    }

    @And('^Next sub payment should be the same as expiration date of receipt$')
    def and2() {
        userDeviceDatas.each {
            PhoneState state = deviceSet.getPhoneState(it)
            Assert.assertEquals(nextSubPaymentSeconds, state.lastAccountCheckResponse.nextSubPaymentSeconds)
        }
    }

    @And('^Payment type in response should be \'(.+)\'$')
    def and3(String paymentType) {
        userDeviceDatas.each {
            PhoneState state = deviceSet.getPhoneState(it)
            Assert.assertEquals(paymentType, state.lastAccountCheckResponse.paymentType)
        }
    }

    @And('^Current payment details should not exist$')
    def and4() {
        runner.parallel {
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
               String communityUrl) {
        def versionsAbove = ApiVersions.from(versions.list()).above(above)
        userDeviceDatas = userDeviceDataService.table(versionsAbove, communityUrl, [deviceType], formats.set(RequestFormat));

        runner = runnerService.create(userDeviceDatas)
        runner.parallel {
            deviceSet.singup(it)
            deviceSet.loginUsingFacebook(it)
        }

        community = communityRepository.findByRewriteUrlParameter(communityUrl)
    }

    //
    // Scenario: Activated user sends valid Apple Store receipt and payment details are created
    //
    @Then('^Next sub payment should be in the past$')
    def and5() {
        userDeviceDatas.each {
            PhoneState state = deviceSet.getPhoneState(it)
            Assert.assertTrue((new Date().getTime() / 1000).intValue() > state.lastAccountCheckResponse.nextSubPaymentSeconds)
        }
    }

    @And('^User should have active current payment details$')
    def and6() {
        runner.parallel {
            User user = findUserInDatabase(it, deviceSet.getPhoneState(it))
            userByDeviceUserData[(it)] = user

            logger.info("Current payment details: {}", user.getCurrentPaymentDetails())
            Assert.assertNotNull(user.getCurrentPaymentDetails())
            Assert.assertTrue(user.getCurrentPaymentDetails().activated)
        }
    }

    @And('^Payment type of current payment details should be \'(.+)\'$')
    def and7(String paymentType) {
        userDeviceDatas.each {
            User user = userByDeviceUserData[(it)]
            Assert.assertEquals(paymentType, user.getCurrentPaymentDetails().paymentType)
        }
    }

    @And('^Payment policy of current payment details should be the same as subscribed$')
    def and8() {
        userDeviceDatas.each {
            User updatedUser = userByDeviceUserData[(it)]
            def actualCurrentPaymentDetails = updatedUser.getCurrentPaymentDetails()

            logger.info("Actual payment details : {}", actualCurrentPaymentDetails)
            Assert.assertEquals(iTunesPaymentPolicy.id, actualCurrentPaymentDetails.paymentPolicy.id)
            Assert.assertEquals(iTunesPaymentPolicy.appStoreProductId, actualCurrentPaymentDetails.paymentPolicy.appStoreProductId)
            Assert.assertEquals(iTunesPaymentPolicy.period.duration, actualCurrentPaymentDetails.paymentPolicy.period.duration)
            Assert.assertEquals(iTunesPaymentPolicy.period.durationUnit, actualCurrentPaymentDetails.paymentPolicy.period.durationUnit)
            Assert.assertTrue(iTunesPaymentPolicy.subcost.equals(actualCurrentPaymentDetails.paymentPolicy.subcost))
            Assert.assertEquals(iTunesPaymentPolicy.paymentType, actualCurrentPaymentDetails.paymentPolicy.paymentType)
        }
    }

    //
    // Scenario: Activated user with stored Apple Store receipt doesn't send it in ACC_CHEC but iTunes payment details are created
    //
    @And('^User is subscribed on iTunes via existing recurrent payment policy without payment details$')
    def and9() {
        runner.parallel {
            User user = findUserInDatabase(it, deviceSet.getPhoneState(it))

            iTunesPaymentPolicy = getITunesPaymentPolicy({ it.paymentType == 'iTunesSubscription' && it.paymentPolicyType == PaymentPolicyType.RECURRENT })
            nextSubPaymentSeconds = iTunesPaymentPolicy.period.toNextSubPaymentSeconds((new Date().getTime() / 1000).intValue())
            appStoreReceipt = "renewable:200:0:${iTunesPaymentPolicy.appStoreProductId}:0123456789:${nextSubPaymentSeconds}000".toString()

            subscriptionService.subscribeOnITunesWithoutPaymentDetails(user, nextSubPaymentSeconds, appStoreReceipt)
        }
    }

    @When('^Sends ACC_CHECK request without Apple Store receipt$')
    def when3() {
        runner.parallel {
            responses[(it)] = deviceSet.accountCheck(it)
        }
    }

    @And('^Response header \'(.+)\' should be \'(.+)\'$')
    def and10(String headerName, String expectedHeaderValue) {
        userDeviceDatas.each {
            String actualHeaderValue = responses[(it)].headers[headerName].get(0)
            Assert.assertEquals("Header $headerName: expected=$expectedHeaderValue, actual=$actualHeaderValue", expectedHeaderValue, actualHeaderValue)
        }
    }

    //
    // Scenario: Activated user with active ITunes payment details sends new Apple Store receipt with the same product id
    //
    @When('^User sends in ACC_CHECK request new valid Apple Store receipt with the same product id$')
    def when4() {
        def newTransactionId = "111110000"
        newAppStoreReceipt = "renewable:200:0:${iTunesPaymentPolicy.appStoreProductId}:$newTransactionId:${nextSubPaymentSeconds}000".toString()

        responses.clear()

        logger.info("New App Store receipt: {}", newAppStoreReceipt)
        runner.parallel {
            responses[(it)] = deviceSet.accountCheckFromIOS(it, newAppStoreReceipt)
        }
    }

    @And('^User is subscribed on iTunes via existing recurrent payment policy with payment details$')
    def and14() {
        iTunesPaymentPolicy = getITunesPaymentPolicy({ it.paymentType == 'iTunesSubscription' && it.paymentPolicyType == PaymentPolicyType.RECURRENT })
        nextSubPaymentSeconds = iTunesPaymentPolicy.period.toNextSubPaymentSeconds((new Date().getTime() / 1000).intValue())
        appStoreReceipt = "renewable:200:0:${iTunesPaymentPolicy.appStoreProductId}:0123456789:${nextSubPaymentSeconds}000".toString()

        runner.parallel {
            User user = findUserInDatabase(it, deviceSet.getPhoneState(it))
            logger.info("Subscribe user {} on payment policy id {} , product id {}", user, iTunesPaymentPolicy.id, iTunesPaymentPolicy.appStoreProductId)
            subscriptionService.subscribeOnITunesWithPaymentDetails(user, iTunesPaymentPolicy, nextSubPaymentSeconds, appStoreReceipt)
            userByDeviceUserData[(it)] = user
        }
    }


    @And('^User should have the same active current payment details$')
    def and11() {
        runner.parallel {
            def originalUser = userByDeviceUserData[(it)]
            User updatedUser = findUserInDatabase(it, deviceSet.getPhoneState(it))

            Assert.assertEquals(originalUser.getCurrentPaymentDetails().i, updatedUser.getCurrentPaymentDetails().i)
        }
    }

    @And('^Apple Store receipt in current payment details should be updated with the last one$')
    def and12() {
        runner.parallel {
            User updatedUser = findUserInDatabase(it, deviceSet.getPhoneState(it))
            ITunesPaymentDetails details = updatedUser.getCurrentPaymentDetails()

            Assert.assertEquals(newAppStoreReceipt, details.appStoreReceipt)
        }
    }

    //
    // Scenario: Activated user with active ITunes payment details sends new Apple Store receipt with new product id
    //
    @When('^User sends in ACC_CHECK request new valid Apple Store receipt with new product id$')
    def when5() {
        newITunesPaymentPolicy = getITunesPaymentPolicy({ it.paymentType == 'iTunesSubscription' && it.id != iTunesPaymentPolicy.id})
        newAppStoreReceipt = "renewable:200:0:${newITunesPaymentPolicy.appStoreProductId}:0123456789:${nextSubPaymentSeconds}000".toString()

        responses.clear()
        logger.info("New payment policy: {}", newITunesPaymentPolicy)
        logger.info("New App store receipt: {}", newAppStoreReceipt)
        runner.parallel {
            responses[(it)] = deviceSet.accountCheckFromIOS(it, newAppStoreReceipt)
        }
    }

    @And('^User should have new active current payment details with new Apple Store receipt and new product id$')
    def and13() {
        runner.parallel {
            def originalUser = userByDeviceUserData[(it)]
            User updatedUser = findUserInDatabase(it, deviceSet.getPhoneState(it))

            ITunesPaymentDetails actualITunesPaymentDetails = updatedUser.getCurrentPaymentDetails()
            Assert.assertNotEquals(originalUser.getCurrentPaymentDetails().i, actualITunesPaymentDetails.i)
            Assert.assertEquals(newAppStoreReceipt, actualITunesPaymentDetails.appStoreReceipt)
            Assert.assertEquals(newITunesPaymentPolicy.appStoreProductId, actualITunesPaymentDetails.paymentPolicy.appStoreProductId)
        }
    }

    @After
    def tearDown() {
        responses.clear()
        userDeviceDatas.clear()
        userByDeviceUserData.clear()
        deviceSet.cleanup()
    }

    private User findUserInDatabase(UserDeviceData userDeviceData, PhoneState phoneState) {
        return userDbService.findUser(phoneState, userDeviceData);
    }

    private PaymentPolicy getITunesPaymentPolicy(def filter) {
        List<PaymentPolicy> policies = paymentPolicyRepository.findPaymentPolicies(community, null, null, null, Tariff._3G, [MediaType.AUDIO])
        policies.find filter
    }
}
