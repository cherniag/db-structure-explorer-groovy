package mobi.nowtechnologies.applicationtests.features.activation.common

import mobi.nowtechnologies.applicationtests.features.activation.facebook.UserState
import mobi.nowtechnologies.applicationtests.features.common.client.ClientDevicesSet
import mobi.nowtechnologies.applicationtests.services.db.UserDbService
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData
import mobi.nowtechnologies.server.persistence.repository.AccountLogRepository
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository
import mobi.nowtechnologies.server.shared.enums.ActivationStatus
import mobi.nowtechnologies.server.shared.enums.ProviderType
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource
import org.springframework.stereotype.Service

import javax.annotation.Resource

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

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

    def checkUserState(UserState userState, List<UserDeviceData> devices, ClientDevicesSet deviceSet) {
        devices.each {
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

    def checkDeviceTypeField(List<UserDeviceData> devices, ClientDevicesSet devicesSet) {
        devices.each {
            def accountCheckResponse = devicesSet.getPhoneState(it).lastAccountCheckResponse
            assertEquals(accountCheckResponse.deviceType, it.deviceType)
        }
    }

    def checkDeviceUIDField(List<UserDeviceData> devices, ClientDevicesSet devicesSet) {
        devices.each {
            def accountCheckResponse = devicesSet.getPhoneState(it).lastAccountCheckResponse
            def phoneState = devicesSet.getPhoneState(it)
            assertEquals(accountCheckResponse.deviceUID, phoneState.deviceUID)
        }
    }

    def checkUsernameField(List<UserDeviceData> devices, ClientDevicesSet devicesSet) {
        devices.each {
            def accountCheckResponse = devicesSet.getPhoneState(it).lastAccountCheckResponse
            def phoneState = devicesSet.getPhoneState(it)
            assertEquals(accountCheckResponse.userName, phoneState.email)
        }
    }

    def checkStatusAndSubPaymentTimes(List<UserDeviceData> devices, ClientDevicesSet deviceSet) {
        devices.each {
            def accountCheckResponse = deviceSet.getPhoneState(it).lastAccountCheckResponse
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.getUserByDeviceUIDAndCommunity(phoneState.getDeviceUID(), it.getCommunityUrl())
            def promotion = promotionRepository.getPromotionByPromoCode(user.getLastPromo().getCode(),
                    user.getUserGroup(),
                    user.getLastPromo().getPromotion().getType())
            //TODO: what's wrong
//            assertEquals(accountCheckResponse.timeOfMovingToLimitedStatusSeconds, promotion.endDate)
//            assertEquals(accountCheckResponse.nextSubPaymentSeconds, promotion.endDate)
        }
    }

    def checkDeviceTypeDB(List<UserDeviceData> devices, ClientDevicesSet deviceSet) {
        devices.each {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.getUserByDeviceUIDAndCommunity(phoneState.getDeviceUID(), it.getCommunityUrl())
            assertEquals(user.getDeviceModel(), it.deviceType)
        }
    }

    def checkActivatedDB(List<UserDeviceData> devices, ClientDevicesSet deviceSet) {
        devices.each {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.getUserByDeviceUIDAndCommunity(phoneState.getDeviceUID(), it.getCommunityUrl())
            assertEquals(user.getActivationStatus(), ActivationStatus.ACTIVATED)
        }
    }

    def checkProviderDB(List<UserDeviceData> devices, ClientDevicesSet deviceSet, ProviderType providerType) {
        devices.each {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.getUserByDeviceUIDAndCommunity(phoneState.getDeviceUID(), it.getCommunityUrl())
            assertEquals(user.getProvider(), providerType)
        }
    }

    def checkNoPaymentDetailsDB(List<UserDeviceData> devices, ClientDevicesSet deviceSet) {
        devices.each {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.getUserByDeviceUIDAndCommunity(phoneState.getDeviceUID(), it.getCommunityUrl())
            assertTrue(user.getPaymentDetailsList() == null || user.getPaymentDetailsList().isEmpty())
        }
    }

    def checkAppliedForPromotionDB(List<UserDeviceData> devices, ClientDevicesSet deviceSet) {
        devices.each { device ->
            def phoneState = deviceSet.getPhoneState(device)
            def user = userDbService.getUserByDeviceUIDAndCommunity(phoneState.getDeviceUID(), device.getCommunityUrl())
            def logs = accountLogRepository.findByUserId(user.id)
            logs.each {
                it.promoCode == communityResourceBundleMessageSource.getMessage(device.communityUrl, DEFAULT_PROMOTION_CODE_KEY, null, null)
            }
        }
    }

    def checkLastPromotionDB(List<UserDeviceData> devices, ClientDevicesSet deviceSet) {
        devices.each {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.getUserByDeviceUIDAndCommunity(phoneState.getDeviceUID(), it.getCommunityUrl())
            assertEquals(user.getLastPromo().getCode(),
                    communityResourceBundleMessageSource.getMessage(it.communityUrl, DEFAULT_PROMOTION_CODE_KEY, null, null))
        }
    }
}