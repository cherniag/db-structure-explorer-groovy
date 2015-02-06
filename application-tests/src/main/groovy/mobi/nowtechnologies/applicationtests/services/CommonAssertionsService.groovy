package mobi.nowtechnologies.applicationtests.services
import mobi.nowtechnologies.applicationtests.features.activation.common.UserState
import mobi.nowtechnologies.applicationtests.features.common.client.ClientDevicesSet
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet
import mobi.nowtechnologies.applicationtests.services.db.UserDbService
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData
import mobi.nowtechnologies.applicationtests.services.http.facebook.FacebookUserInfoGenerator
import mobi.nowtechnologies.applicationtests.services.runner.Runner
import mobi.nowtechnologies.applicationtests.services.runner.RunnerService
import mobi.nowtechnologies.server.apptests.facebook.AppTestFacebookTokenService
import mobi.nowtechnologies.server.apptests.googleplus.AppTestGooglePlusTokenService
import mobi.nowtechnologies.server.persistence.domain.User
import mobi.nowtechnologies.server.persistence.repository.AccountLogRepository
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository
import mobi.nowtechnologies.server.persistence.repository.UserRepository
import mobi.nowtechnologies.server.persistence.repository.social.FacebookUserInfoRepository
import mobi.nowtechnologies.server.persistence.repository.social.GooglePlusUserInfoRepository
import mobi.nowtechnologies.server.shared.enums.ActivationStatus
import mobi.nowtechnologies.server.shared.enums.ProviderType
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource
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
    FacebookUserInfoRepository fbDetailsRepository
    @Resource
    AppTestFacebookTokenService appTestFacebookTokenService
    @Resource
    AppTestGooglePlusTokenService appTestGooglePlusTokenService
    @Resource
    GooglePlusUserInfoRepository googlePlusUserInfoRepository

    @Resource
    RunnerService runnerService;
    Runner runner;

    def forceGetFieldsToCheck(User which) {
        which.userName
        which.status.i
        which.provider
        which.contract
        which.segment
        which.tariff
        which.contractChannel
        which.token
        which.freeTrialStartedTimestampMillis
        which.lastPromo.id
        which.videoFreeTrialHasBeenActivated
        which.freeTrialExpiredMillis
        which.nextSubPayment
        which.deviceType.i
        which.deviceUID
        which.lastSubscribedPaymentSystem
        which.lastSuccessfulPaymentDetails
        which.activationStatus
        which.base64EncodedAppStoreReceipt
        which.appStoreOriginalTransactionId
        which.currentPaymentDetails
    }
    
    def checkUserWasNotChanged(User before, User after) {
        Assert.isTrue(before.id == after.id, "Trying to to check not the same " + User);

        assertEquals("The field userName changed for the user by id:" + before.id, before.userName, after.userName)
        assertEquals("The field status changed for the user by id:" + before.id, before.status.i, after.status.i)
        assertEquals("The field provider changed for the user by id:" + before.id, before.provider, after.provider)
        assertEquals("The field contract changed for the user by id:" + before.id, before.contract, after.contract)
        assertEquals("The field segment changed for the user by id:" + before.id, before.segment, after.segment)
        assertEquals("The field tariff changed for the user by id:" + before.id, before.tariff, after.tariff)
        assertEquals("The field contractChannel changed for the user by id:" + before.id, before.contractChannel, after.contractChannel)
        assertEquals("The field token changed for the user by id:" + before.id, before.token, after.token)
        assertEquals("The field freeTrialStartedTimestampMillis changed for the user by id:" + before.id, before.freeTrialStartedTimestampMillis, after.freeTrialStartedTimestampMillis)
        assertEquals("The field lastPromo changed for the user by id:" + before.id, before.lastPromo.id, after.lastPromo.id)
        assertEquals("The field videoFreeTrialHasBeenActivated changed for the user by id:" + before.id, before.videoFreeTrialHasBeenActivated, after.videoFreeTrialHasBeenActivated)
        assertEquals("The field freeTrialExpiredMillis changed for the user by id:" + before.id, before.freeTrialExpiredMillis, after.freeTrialExpiredMillis)
        assertEquals("The field nextSubPayment changed for the user by id:" + before.id, before.nextSubPayment, after.nextSubPayment)
        assertEquals("The field deviceType changed for the user by id:" + before.id, before.deviceType.i, after.deviceType.i)
        assertEquals("The field deviceUID changed for the user by id:" + before.id, before.deviceUID, after.deviceUID)
        assertEquals("The field lastSubscribedPaymentSystem changed for the user by id:" + before.id, before.lastSubscribedPaymentSystem, after.lastSubscribedPaymentSystem)

        if(before.lastSuccessfulPaymentDetails != null || after.lastSuccessfulPaymentDetails != null) {
            if(before.lastSuccessfulPaymentDetails == null || after.lastSuccessfulPaymentDetails == null) {
                assertNotNull("The field lastSuccessfulPaymentDetails changed and is null for the user by id:" + before.id, before.lastSuccessfulPaymentDetails.i)
                assertNotNull("The field lastSuccessfulPaymentDetails changed and is null for the user by id:" + before.id, after.lastSuccessfulPaymentDetails.i)
            } else {
                assertEquals("The field lastSuccessfulPaymentDetails changed for the user by id:" + before.id, before.lastSuccessfulPaymentDetails.i, after.lastSuccessfulPaymentDetails.i)
            }
        }
        assertEquals("The field activationStatus changed for the user by id:" + before.id, before.activationStatus, after.activationStatus)
        assertEquals("The field base64EncodedAppStoreReceipt changed for the user by id:" + before.id, before.base64EncodedAppStoreReceipt, after.base64EncodedAppStoreReceipt)
        assertEquals("The field appStoreOriginalTransactionId changed for the user by id:" + before.id, before.appStoreOriginalTransactionId, after.appStoreOriginalTransactionId)

        if(before.currentPaymentDetails != null || after.currentPaymentDetails != null) {
            if(before.currentPaymentDetails == null || after.currentPaymentDetails == null) {
                assertNotNull("The field currentPaymentDetails changed and is null for the user by id:" + before.id, before.lastSuccessfulPaymentDetails.i)
                assertNotNull("The field currentPaymentDetails changed and is null for the user by id:" + before.id, after.lastSuccessfulPaymentDetails.i)
            } else {
                assertEquals("The field currentPaymentDetails changed for the user by id:" + before.id, before.currentPaymentDetails.i, after.currentPaymentDetails.i)
                assertEquals("The field currentPaymentDetails.activated changed for the user by id:" + before.id, before.currentPaymentDetails.activated, after.currentPaymentDetails.activated)
                assertEquals("The field currentPaymentDetails.lastPaymentStatus changed for the user by id:" + before.id, before.currentPaymentDetails.lastPaymentStatus, after.currentPaymentDetails.lastPaymentStatus)
                assertEquals("The field currentPaymentDetails.descriptionError changed for the user by id:" + before.id, before.currentPaymentDetails.descriptionError, after.currentPaymentDetails.descriptionError)
            }
        }
    }

    def checkUserState(UserState userState, List<UserDeviceData> devices, ClientDevicesSet deviceSet) {
        runnerService.create(devices).parallel {
            def accountCheckResponse = deviceSet.getPhoneState(it).lastAccountCheckResponse
            assertEquals(userState.activation, accountCheckResponse.activation)
            assertEquals(userState.freeTrial, accountCheckResponse.freeTrial)
            assertEquals(userState.fullyRegistred, accountCheckResponse.fullyRegistred)
            assertEquals(userState.hasAllDetails, accountCheckResponse.hasAllDetails)
            assertEquals(userState.paymentType, accountCheckResponse.paymentType)
            assertEquals(userState.provider, accountCheckResponse.provider)
            assertEquals(userState.status, accountCheckResponse.status)
        }
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
            def promotion = promotionRepository.getPromotionByPromoCode(user.getLastPromo().getCode(),
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
            assertTrue(user.getPaymentDetailsList() == null || user.getPaymentDetailsList().isEmpty())
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

    def assertAccountDeactivated(List<UserDeviceData> devices,
                                      Map<UserDeviceData, Integer> userIdMap) {
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
            def facebookUserInfo = fbDetailsRepository.findByUser(user)
            def facebookProfile = appTestFacebookTokenService.parseToken(phoneState.facebookAccessToken)
            assertEquals(facebookUserInfo.getEmail(), phoneState.getEmail())
            assertEquals(facebookUserInfo.getFirstName(), FacebookUserInfoGenerator.FIRST_NAME)
            assertEquals(facebookUserInfo.getBirthday().getTime(), dateFormat.parse(facebookProfile.getBirthday()).getTime())
            assertEquals(facebookUserInfo.getSurname(), FacebookUserInfoGenerator.SURNAME)
            assertEquals(facebookUserInfo.getCity(), FacebookUserInfoGenerator.CITY)
            assertEquals(facebookUserInfo.getFacebookId(), phoneState.getFacebookUserId())
            assertEquals(facebookUserInfo.getUserName(), phoneState.getEmail())
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
            def googlePlusUserInfo = googlePlusUserInfoRepository.findByUser(user)
            def googlePlusProfile = appTestGooglePlusTokenService.parse(phoneState.googlePlusToken)
            assertEquals(googlePlusUserInfo.getEmail(), phoneState.getEmail())
            assertEquals(googlePlusUserInfo.getDisplayName(), googlePlusProfile.getDisplayName())
            assertEquals(googlePlusUserInfo.getFamilyName(), googlePlusProfile.getFamilyName())
            assertEquals(googlePlusUserInfo.getGivenName(), googlePlusProfile.getGivenName())
            assertEquals(googlePlusUserInfo.getGooglePlusId(), googlePlusProfile.getId())
            assertEquals(googlePlusUserInfo.getLocation(), googlePlusProfile.getPlacesLived().keySet().iterator().next())
        }
    }

    def getDateFormat() {
        new SimpleDateFormat("MM/dd/yyyy")
    }
}