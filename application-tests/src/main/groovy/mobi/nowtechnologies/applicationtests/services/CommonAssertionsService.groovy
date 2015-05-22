package mobi.nowtechnologies.applicationtests.services

import mobi.nowtechnologies.applicationtests.features.common.client.ClientDevicesSet
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet
import mobi.nowtechnologies.applicationtests.services.db.UserDbService
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData
import mobi.nowtechnologies.applicationtests.services.http.facebook.FacebookUserInfoGenerator
import mobi.nowtechnologies.applicationtests.services.runner.Runner
import mobi.nowtechnologies.applicationtests.services.runner.RunnerService
import mobi.nowtechnologies.server.persistence.domain.User
import mobi.nowtechnologies.server.persistence.repository.AccountLogRepository
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository
import mobi.nowtechnologies.server.persistence.repository.UserRepository
import mobi.nowtechnologies.server.shared.enums.ActivationStatus
import mobi.nowtechnologies.server.shared.enums.ProviderType
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfoRepository
import mobi.nowtechnologies.server.social.domain.SocialNetworkType
import mobi.nowtechnologies.server.social.service.facebook.impl.mock.AppTestFacebookTokenService
import mobi.nowtechnologies.server.social.service.googleplus.impl.mock.AppTestGooglePlusTokenService
import org.springframework.stereotype.Service
import org.springframework.util.Assert

import javax.annotation.Resource
import java.text.SimpleDateFormat

import static org.junit.Assert.*

/**
 * Created by kots on 9/5/2014.
 */
@Service
class CommonAssertionsService {

    static final String DEFAULT_PROMOTION_CODE_KEY = "defaultPromotionCode"

    @Resource
    UserDbService userDbService
    @Resource
    PromotionRepository promotionRepository
    @Resource
    AccountLogRepository accountLogRepository
    @Resource
    CommunityResourceBundleMessageSource communityResourceBundleMessageSource
    @Resource
    UserRepository userRepository
    @Resource
    AppTestFacebookTokenService appTestFacebookTokenService
    @Resource
    AppTestGooglePlusTokenService appTestGooglePlusTokenService

    @Resource
    SocialNetworkInfoRepository socialNetworkInfoRepository
    @Resource
    PaymentDetailsRepository paymentDetailsRepository;

    @Resource
    RunnerService runnerService;
    Runner runner;

    def checkUserWasNotChanged(User before, User after) {
        Assert.isTrue(before.id == after.id, "Trying to to check not the same " + User);
        assertEquals("The field userName changed for the user by id:" + before.id, before.userName, after.userName)
        assertEquals("The field nextSubPayment changed for the user by id:" + before.id, before.nextSubPayment, after.nextSubPayment)
        assertEquals("The field freeTrialStartedTimestampMillis changed for the user by id:" + before.id, before.freeTrialStartedTimestampMillis, after.freeTrialStartedTimestampMillis)
        assertEquals("The field freeTrialExpiredMillis changed for the user by id:" + before.id, before.freeTrialExpiredMillis, after.freeTrialExpiredMillis)
        assertEquals("The field activationStatus changed for the user by id:" + before.id, before.activationStatus, after.activationStatus)
        assertEquals("The field provider changed for the user by id:" + before.id, before.provider, after.provider)
        assertEquals("The field lastPromo changed for the user by id:" + before.id, before.lastPromo, after.lastPromo)
    }

    def checkFacebookUserWasNotChanged(User before, User after) {
        checkUserWasNotChanged(before, after);
        assertNull("New record is created", socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(after.getId(), SocialNetworkType.FACEBOOK));
    }

    def checkGooglePlusUserWasNotChanged(User before, User after) {
        checkUserWasNotChanged(before, after);
        assertNull("New record is created", socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(after.getId(), SocialNetworkType.GOOGLE));
    }

    def checkDeviceTypeField(UserDeviceData device, ClientDevicesSet devicesSet) {
        def accountCheckResponse = devicesSet.getPhoneState(device).lastAccountCheckResponse
        assertEquals(accountCheckResponse.deviceType, device.deviceType)
    }

    def checkDeviceUIDField(List<UserDeviceData> devices, ClientDevicesSet devicesSet) {
        runnerService.create(devices).parallel {
            def accountCheckResponse = devicesSet.getPhoneState(it).lastAccountCheckResponse
            def phoneState = devicesSet.getPhoneState(it)
            assertEquals(accountCheckResponse.deviceUID, phoneState.deviceUID)
        }
    }

    def checkUsernameField(List<UserDeviceData> devices, ClientDevicesSet devicesSet) {
        runnerService.create(devices).parallel {
            def accountCheckResponse = devicesSet.getPhoneState(it).lastAccountCheckResponse
            def phoneState = devicesSet.getPhoneState(it)
            assertEquals(accountCheckResponse.userName, phoneState.email)
        }
    }

    def checkStatusAndSubPaymentTimes(List<UserDeviceData> devices, ClientDevicesSet deviceSet) {
        runnerService.create(devices).parallel {
            def accountCheckResponse = deviceSet.getPhoneState(it).lastAccountCheckResponse
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            def promotion = promotionRepository.findPromotionByPromoCode(user.getLastPromo().getCode(),
                    user.getUserGroup(),
                    user.getLastPromo().getPromotion().getType())
            //TODO: what's wrong
//            assertEquals(accountCheckResponse.timeOfMovingToLimitedStatusSeconds, promotion.endDate)
//            assertEquals(accountCheckResponse.nextSubPaymentSeconds, promotion.endDate)
        }
    }

    def checkDeviceTypeDB(List<UserDeviceData> devices, ClientDevicesSet deviceSet) {
        runnerService.create(devices).parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            assertEquals(user.getDeviceModel(), it.deviceType)
        }
    }

    def checkActivatedDB(List<UserDeviceData> devices, ClientDevicesSet deviceSet) {
        runnerService.create(devices).parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            assertEquals(user.getActivationStatus(), ActivationStatus.ACTIVATED)
        }
    }

    def checkProviderDB(List<UserDeviceData> devices, ClientDevicesSet deviceSet, ProviderType providerType) {
        runnerService.create(devices).parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            assertEquals(user.getProvider(), providerType)
        }
    }

    def checkNoPaymentDetailsDB(List<UserDeviceData> devices, ClientDevicesSet deviceSet) {
        runnerService.create(devices).parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            def paymentDetailsByOwner = paymentDetailsRepository.findPaymentDetailsByOwner(user)
            assertTrue(paymentDetailsByOwner.isEmpty())
        }
    }

    def checkAppliedForPromotionDB(List<UserDeviceData> devices, ClientDevicesSet deviceSet) {
        runnerService.create(devices).parallel { device ->
            def phoneState = deviceSet.getPhoneState(device)
            def user = userDbService.findUser(phoneState, device)
            def logs = accountLogRepository.findByUserId(user.id)
            logs.each {
                it.promoCode == communityResourceBundleMessageSource.getMessage(device.communityUrl, DEFAULT_PROMOTION_CODE_KEY, null, null)
            }
        }
    }

    def checkLastPromotionDB(List<UserDeviceData> devices, ClientDevicesSet deviceSet) {
        runnerService.create(devices).parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            assertEquals(user.getLastPromo().getCode(),
                    communityResourceBundleMessageSource.getMessage(it.communityUrl, DEFAULT_PROMOTION_CODE_KEY, null, null))
        }
    }

    def assertTempAccountCreated(List<UserDeviceData> devices,
                                 MQAppClientDeviceSet deviceSet,
                                 Map<UserDeviceData, Integer> oldUserIdMap,
                                 Map<UserDeviceData, Integer> tempUserIdMap) {
        runnerService.create(devices).parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            tempUserIdMap.put(it, user.id)
            assertNotEquals(user.id, oldUserIdMap.get(it))
        }
    }

    def assertAccountDeactivated(List<UserDeviceData> devices, Map<UserDeviceData, Integer> userIdMap) {
        runnerService.create(devices).parallel {
            def oldUser = userRepository.findOne(userIdMap.get(it))
            assertTrue(oldUser.deviceUID.contains("_disabled_at_"))
        }
    }

    def assertTemporaryAccountRemoved(List<UserDeviceData> devices, Map<UserDeviceData, Integer> tempUserIdMap) {
        runnerService.create(devices).parallel {
            def tempUser = userRepository.findOne(tempUserIdMap.get(it))
            assertNull(tempUser)
        }
    }

    def assertFirstAccountReactivated(List<UserDeviceData> devices, Map<UserDeviceData, Integer> userIdMap) {
        runnerService.create(devices).parallel {
            def oldUser = userRepository.findOne(userIdMap.get(it))
            assertFalse(oldUser.deviceUID.contains("_disabled_at_"))
        }
    }

    def checkFacebookUserDetails(List<UserDeviceData> devices, MQAppClientDeviceSet deviceSet) {
        runnerService.create(devices).parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            def facebookUserInfo = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.FACEBOOK)
            def facebookProfile = appTestFacebookTokenService.parseToken(phoneState.facebookAccessToken)
            assertEquals(facebookUserInfo.getEmail(), phoneState.getEmail())
            assertEquals(facebookUserInfo.getFirstName(), FacebookUserInfoGenerator.FIRST_NAME)
            assertEquals(facebookUserInfo.getBirthday().getTime(), dateFormat.parse(facebookProfile.getBirthday()).getTime())
            assertEquals(facebookUserInfo.getLastName(), FacebookUserInfoGenerator.SURNAME)
            assertEquals(facebookUserInfo.getCity(), FacebookUserInfoGenerator.CITY)
            assertEquals(facebookUserInfo.getSocialNetworkId(), phoneState.getFacebookUserId())
            assertEquals(facebookUserInfo.getUserName(), phoneState.getFacebookUserId())
        }
    }

    def assertFirstAccountActive(List<UserDeviceData> devices, Map<UserDeviceData, Integer> userIdMap) {
        runnerService.create(devices).parallel {
            def oldUser = userRepository.findOne(userIdMap.get(it))
            assertFalse(oldUser.deviceUID.contains("_disabled_at_"))
        }
    }

    def assertFirstAccountIsUpdated(List<UserDeviceData> devices, MQAppClientDeviceSet deviceSet, Map<UserDeviceData, Integer> userIdMap) {
        runnerService.create(devices).parallel {
            def oldUser = userRepository.findOne(userIdMap.get(it))
            assertEquals(oldUser.deviceUID, deviceSet.getPhoneState(it).deviceUID)
        }
    }

    def assertNewUserActivated(List<UserDeviceData> devices, Map<UserDeviceData, Integer> newUserIdMap) {
        runnerService.create(devices).parallel {
            def tempUser = userRepository.findOne(newUserIdMap.get(it))
            assertEquals(ActivationStatus.ACTIVATED, tempUser.activationStatus)
        }
    }

    def assertFirstAccountIsUpdatedWithSecondDeviceUID(List<List<UserDeviceData>> zippedDevices,
                                                       MQAppClientDeviceSet deviceSet,
                                                       Map<UserDeviceData, Integer> userIdMap) {
        zippedDevices.each {
            def user = userRepository.findOne(userIdMap.get(it[0]))
            def otherState = deviceSet.getPhoneState(it[1])
            assertEquals(otherState.deviceUID, user.deviceUID)
        }
    }

    def assertGooglePlusUserDetails(List<UserDeviceData> devices, MQAppClientDeviceSet deviceSet) {
        runnerService.create(devices).parallel {
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

    def getDateFormat() {
        new SimpleDateFormat("MM/dd/yyyy")
    }
}