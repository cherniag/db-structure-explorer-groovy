package mobi.nowtechnologies.applicationtests.features.appsflyer
import cucumber.api.Transform
import cucumber.api.java.After
import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import mobi.nowtechnologies.applicationtests.features.common.client.PartnerDeviceSet
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word
import mobi.nowtechnologies.applicationtests.services.RequestFormat
import mobi.nowtechnologies.applicationtests.services.db.UserDbService
import mobi.nowtechnologies.applicationtests.services.device.PhoneState
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService
import mobi.nowtechnologies.applicationtests.services.device.domain.ApiVersions
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData
import mobi.nowtechnologies.server.persistence.domain.User
import mobi.nowtechnologies.server.persistence.repository.AppsFlyerDataRepository
import mobi.nowtechnologies.server.shared.enums.ActivationStatus
import org.springframework.stereotype.Component

import javax.annotation.Resource

import static org.junit.Assert.*
/**
 * Author: Gennadii Cherniaiev
 * Date: 11/12/2014
 */
@Component
class AppsFlyerOnSignUpDeviceFeature {

    @Resource
    PartnerDeviceSet partnerDeviceSet
    @Resource
    UserDeviceDataService userDeviceDataService
    @Resource
    UserDbService userDbService
    @Resource
    AppsFlyerDataRepository appsFlyerDataRepository

    List<UserDeviceData> userDeviceDatas
    def appsFlyerUids = [:]
    def newAppsFlyerUids = [:]

    @Given('^First time user with (.+) using (.+) formats with (.+) bellow (.+) and (.+)$')
    def "First time user with all devices using JSON and XML formats with all versions bellow 6.6 and all communities"(
              @Transform(DictionaryTransformer.class) Word deviceTypes,
              @Transform(DictionaryTransformer.class) Word formats,
              @Transform(DictionaryTransformer.class) Word versions,
              String bellowVersion,
              @Transform(DictionaryTransformer.class) Word communities) {
        def bellow = ApiVersions.from(versions.set()).bellow(bellowVersion)
        userDeviceDatas = userDeviceDataService.table(bellow, communities.set(), deviceTypes.set(), formats.set(RequestFormat));
    }

    @When('^User registers using device with appsflyer uid$')
    def "User registers using device with appsflyer uid"() {
        userDeviceDatas.each {
            def appsFlyerUid = UUID.randomUUID().toString();
            appsFlyerUids.put(it, appsFlyerUid)
            partnerDeviceSet.singupWithAppsFlyer(it, appsFlyerUid);
        }
    }

    @Then('^User should have (.+) activation status in database$')
    def "User should have REGISTERED activation status in database"(ActivationStatus activationStatus) {
        userDeviceDatas.each {
            def phoneState = partnerDeviceSet.getPhoneState(it);
            def user = findUserInDatabase(it, phoneState);
            assertEquals("Expected status $activationStatus but was $user.activationStatus for [$it]-[$user.id]", activationStatus, user.activationStatus);
        }
    }

    @And('^appsflyer data should not be created$')
    def "appsflyer data should not be created"() {
        userDeviceDatas.each {
            def phoneState = partnerDeviceSet.getPhoneState(it);
            def user = findUserInDatabase(it, phoneState);
            def found = appsFlyerDataRepository.findDataByUserId(user.id)
            assertNull("For [$it]-[$user.id] no appsflyer data expected but was [$found]", found);
        }
    }

    @Given('^First time user with (.+) using (.+) formats with (.+) above (.+) and (.+)$')
    def "First time user with all devices using JSON and XML formats with all versions above 6.6 and all communities"(
               @Transform(DictionaryTransformer.class) Word deviceTypes,
               @Transform(DictionaryTransformer.class) Word formats,
               @Transform(DictionaryTransformer.class) Word versions,
               String aboveVersion,
               @Transform(DictionaryTransformer.class) Word communities) {
        def above = ApiVersions.from(versions.set()).above(aboveVersion);
        userDeviceDatas = userDeviceDataService.table(above, communities.set(), deviceTypes.set(), formats.set(RequestFormat));
    }

    @And('^appsflyer data should be created$')
    def "appsflyer data should be created"() {
        userDeviceDatas.each {
            def phoneState = partnerDeviceSet.getPhoneState(it);
            def user = findUserInDatabase(it, phoneState);
            def found = appsFlyerDataRepository.findDataByUserId(user.id)
            assertNotNull("For [$it]-[$user.id] appsflyer data is expected", found);
        }
    }

    @And('^appsflyer uid in db should be the same as sent during sign up$')
    def "appsflyer uid in db should be the same as sent during sign up"() {
        userDeviceDatas.each {
            def phoneState = partnerDeviceSet.getPhoneState(it);
            def user = findUserInDatabase(it, phoneState);
            def found = appsFlyerDataRepository.findDataByUserId(user.id)
            assertEquals("Expected appsflyer uid [${appsFlyerUids.get(it)}] but was [$found.appsFlyerUid] for [$it]-[$user.id]", appsFlyerUids.get(it), found.appsFlyerUid)
        }
    }

    @When('^User registers using device without appsflyer uid$')
    def "User registers using device without appsflyer uid"() {
        userDeviceDatas.each {
            partnerDeviceSet.singupWithAppsFlyer(it, null);
        }
    }

    @Given('^Registered user with (.+) using (.+) formats with (.+) above (.+) and (.+)$')
    def givenRegisteredUser(
            @Transform(DictionaryTransformer.class) Word deviceTypes,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            String aboveVersion,
            @Transform(DictionaryTransformer.class) Word communities) {
        def above = ApiVersions.from(versions.set()).above(aboveVersion)
        userDeviceDatas = userDeviceDataService.table(above, communities.set(), deviceTypes.set(), formats.set(RequestFormat));
        userDeviceDatas.each {
            def appsFlyerUid = UUID.randomUUID().toString();
            appsFlyerUids.put(it, appsFlyerUid)
            partnerDeviceSet.singupWithAppsFlyer(it, appsFlyerUid);
        }
    }

    @When('^User registers again using device with new appsflyer uid$')
    def "User registers again using device with new appsflyer uid"(){
        userDeviceDatas.each {
            def phoneState = partnerDeviceSet.getPhoneState(it);
            def appsFlyerUid = UUID.randomUUID().toString();
            appsFlyerUids.put(it, appsFlyerUid)
            partnerDeviceSet.singup(it, null, appsFlyerUid, false, phoneState.deviceUID);
        }
    }

    @And('appsflyer uid in db should be the same as sent during last sign up')
    def "appsflyer uid in db should be the same as sent during last sign up"(){
        userDeviceDatas.each {
            def phoneState = partnerDeviceSet.getPhoneState(it);
            def user = findUserInDatabase(it, phoneState);
            def found = appsFlyerDataRepository.findDataByUserId(user.id)
            assertEquals("Expected appsflyer uid [${appsFlyerUids.get(it)}] but was [$found.appsFlyerUid] for [$it]-[$user.id]", appsFlyerUids.get(it), found.appsFlyerUid)
        }
    }

    @And('^appsflyer data should be re-created$')
    def "appsflyer data should be re-created"() {
        userDeviceDatas.each {
            def phoneState = partnerDeviceSet.getPhoneState(it);
            def user = findUserInDatabase(it, phoneState);
            def found = appsFlyerDataRepository.findDataByUserId(user.id)
            assertNotNull("For [$it]-[$user.id] appsflyer data is expected", found);
        }
    }

    @And('appsflyer uid in db should be as old user\'s appsflyer uid')
    def "appsflyer uid in db should be as old user's appsflyer uid"(){
        userDeviceDatas.each {
            def phoneState = partnerDeviceSet.getPhoneState(it);
            def user = findUserInDatabase(it, phoneState);
            def found = appsFlyerDataRepository.findDataByUserId(user.id)
            assertEquals("Expected appsflyer uid [${appsFlyerUids.get(it)}] but was [$found.appsFlyerUid] for [$it]-[$user.id]", appsFlyerUids.get(it), found.appsFlyerUid)
        }
    }

    @When('^User registers again using device without appsflyer uid$')
    def "User registers again using device without appsflyer uid"(){
        userDeviceDatas.each {
            def phoneState = partnerDeviceSet.getPhoneState(it);
            partnerDeviceSet.singup(it, null, null, false, phoneState.deviceUID);
        }
    }

    @After
    def cleanDevicesSet() {
        appsFlyerUids.clear()
        partnerDeviceSet.cleanup();
    }

    private User findUserInDatabase(UserDeviceData userDeviceData, PhoneState phoneState) {
        return userDbService.findUser(phoneState, userDeviceData);
    }
}
