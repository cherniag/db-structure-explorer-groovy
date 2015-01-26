package mobi.nowtechnologies.applicationtests.features.appsflyer
import cucumber.api.Transform
import cucumber.api.java.After
import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet
import mobi.nowtechnologies.applicationtests.features.common.social.SocialActivationType
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
import org.springframework.stereotype.Component

import javax.annotation.Resource

import static mobi.nowtechnologies.applicationtests.features.common.social.SocialActivationType.Facebook
import static mobi.nowtechnologies.applicationtests.features.common.social.SocialActivationType.GooglePlus
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull
/**
 * Author: Gennadii Cherniaiev
 * Date: 11/12/2014
 */
@Component
class AppsFlyerMergeFeature {

    @Resource
    MQAppClientDeviceSet deviceSet
    @Resource
    MQAppClientDeviceSet otherDeviceSet
    @Resource
    UserDeviceDataService userDeviceDataService
    @Resource
    UserDbService userDbService
    @Resource
    AppsFlyerDataRepository appsFlyerDataRepository


    public List<UserDeviceData> userDeviceDatas
    def newAppsFlyerUids = [:]
    def oldAppsFlyerUids = [:]
    def newUsers = [:]
    def currentProvider

    public List<UserDeviceData> otherUserDeviceDatas

    @Given('^Activated via (.+) user with (.+) using (.+) for (.+) above (.+) and (.+) which has appsflyer data$')
    def given(
              SocialActivationType socialActivationType,
              @Transform(DictionaryTransformer.class) Word deviceTypes,
              @Transform(DictionaryTransformer.class) Word formats,
              @Transform(DictionaryTransformer.class) Word versions,
              String aboveVersion,
              @Transform(DictionaryTransformer.class) Word communities) {
        currentProvider = socialActivationType

        def above = ApiVersions.from(versions.list()).above(aboveVersion)
        userDeviceDatas = userDeviceDataService.table(above, communities.set(), deviceTypes.set(), formats.set(RequestFormat))

        userDeviceDatas.each {
            def appsFlyerUid = UUID.randomUUID().toString();
            deviceSet.singupWithAppsFlyer(it, appsFlyerUid)

            activate(it, currentProvider)

            oldAppsFlyerUids.put(it, appsFlyerUid)

            def user = findUserInDatabase(it, deviceSet.getPhoneState(it))
            def found = appsFlyerDataRepository.findDataByUserId(user.id)
            assertNotNull("For [$it]-[$user.id] appsflyer data is expected", found);
        }
    }

    def activate(userDeviceData, socialActivationType){
        if(socialActivationType == Facebook){
            deviceSet.loginUsingFacebook(userDeviceData)
        } else if (socialActivationType == GooglePlus){
            deviceSet.loginUsingGooglePlus(userDeviceData)
        } else {
            throw new IllegalArgumentException("Not yet implemented for provider type $socialActivationType")
        }
    }

    @When('^User activates on same device with the same provider profile which has own appsflyer data$')
    def "User activates on same device with the same provider profile which has own appsflyer data"() {
        userDeviceDatas.each {
            def phoneState = deviceSet.getPhoneState(it)

            def appsFlyerUid = UUID.randomUUID().toString();
            newAppsFlyerUids.put(it, appsFlyerUid)

            deviceSet.singup(it, null, appsFlyerUid, false, phoneState.deviceUID);
            def user = findUserInDatabase(it, phoneState)
            def found = appsFlyerDataRepository.findDataByUserId(user.id)
            assertNotNull("For [$it]-[$user.id] appsflyer data is expected", found);
            newUsers.put(it, user)

            activate(it, currentProvider)
        }
    }

    @Then('^Temporary user\'s appsflyer data should be removed$')
    def "Temporary user's appsflyer data should be removed"() {
        userDeviceDatas.each {
            def user = newUsers[it]
            def found = appsFlyerDataRepository.findDataByUserId(user.id)
            assertNull("For [$it]-[$user.id] no appsflyer data expected but was [$found]", found);
        }
    }

    @And('^Preserved appsflyer data in db should point to old user$')
    def "Preserved appsflyer data in db should point to old user"(){
        userDeviceDatas.each {
            def phoneState = deviceSet.getPhoneState(it);
            def user = findUserInDatabase(it, phoneState);
            def found = appsFlyerDataRepository.findDataByUserId(user.id)
            assertNotNull("For [$it]-[$user.id] appsflyer data is expected but was null", found);
        }
    }

    @And('^Preserved appsflyer data in db should have new user\'s appsflyer uid$')
    def "Preserved appsflyer data in db should have new user's appsflyer uid"(){
        userDeviceDatas.each {
            def phoneState = deviceSet.getPhoneState(it);
            def user = findUserInDatabase(it, phoneState);
            def found = appsFlyerDataRepository.findDataByUserId(user.id)
            assertEquals("Expected appsflyer uid [${newAppsFlyerUids[it]}] but was [$found.appsFlyerUid] for [$it]-[$user.id]",
                    newAppsFlyerUids[it], found.appsFlyerUid)
        }
    }

    @Given('^Activated via (.+) user with (.+) using (.+) for (.+) above (.+) and (.+) which doesn\'t have appsflyer data$')
    def givenActivatedUserWithoutAppsFlyerData(
            SocialActivationType socialActivationType,
            @Transform(DictionaryTransformer.class) Word deviceTypes,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            String aboveVersion,
            @Transform(DictionaryTransformer.class) Word communities) {
        currentProvider = socialActivationType

        def above = ApiVersions.from(versions.list()).above(aboveVersion)
        userDeviceDatas = userDeviceDataService.table(above, communities.set(), deviceTypes.set(), formats.set(RequestFormat))

        userDeviceDatas.each {
            deviceSet.singupWithAppsFlyer(it, null)
            activate(it, currentProvider)
        }
    }

    @When('^User activates on same device with the same provider profile which doesn\'t have appsflyer data$')
    def "User activates on same device with the same provider profile which doesn't have appsflyer data"(){
        userDeviceDatas.each {
            def phoneState = deviceSet.getPhoneState(it)

            deviceSet.singup(it, null, null, false, phoneState.deviceUID);
            def user = findUserInDatabase(it, phoneState)
            def found = appsFlyerDataRepository.findDataByUserId(user.id)
            assertNull("For [$it]-[$user.id] no appsflyer data expected but was [$found]", found);
            newUsers.put(it, user)

            activate(it, currentProvider)
        }
    }

    @Then('Old user\'s appsflyer data should exist')
    def "Old user's appsflyer data should exist"(){
        userDeviceDatas.each {
            def phoneState = deviceSet.getPhoneState(it);
            def user = findUserInDatabase(it, phoneState);
            def found = appsFlyerDataRepository.findDataByUserId(user.id)
            assertNotNull("For [$it]-[$user.id] appsflyer data is expected but was null", found);
        }
    }

    @And('Appsflyer data in db should have old user\'s appsflyer uid')
    def "Appsflyer data in db should have old user's appsflyer uid"(){
        userDeviceDatas.each {
            def phoneState = deviceSet.getPhoneState(it);
            def user = findUserInDatabase(it, phoneState);
            def found = appsFlyerDataRepository.findDataByUserId(user.id)
            assertEquals("Expected appsflyer uid [${newAppsFlyerUids[it]}] but was [$found.appsFlyerUid] for [$it]-[$user.id]",
                    oldAppsFlyerUids[it], found.appsFlyerUid)
        }
    }

    @Then('No appsflyer data should exist in db for both users')
    def "No appsflyer data should exist in db for both users"(){
        userDeviceDatas.each {
            def phoneState = deviceSet.getPhoneState(it);
            def oldUser =  findUserInDatabase(it, phoneState);
            def oldData = appsFlyerDataRepository.findDataByUserId(oldUser.id)
            assertNull("For [$it]-[$oldUser.id] no appsflyer data expected but was [$oldData]", oldData);

            def newUser = newUsers[it]
            def newData = appsFlyerDataRepository.findDataByUserId(newUser.id)
            assertNull("For [$it]-[$newUser.id] no appsflyer data expected but was [$newData]", newData);
        }
    }

    @When('^User activates on another device with the same provider profile which has own appsflyer data$')
    def "User activates on another device with the same provider profile which has own appsflyer data"(){
        userDeviceDatas.each {
            def phoneState = deviceSet.getPhoneState(it)

            def appsFlyerUid = UUID.randomUUID().toString();
            newAppsFlyerUids.put(it, appsFlyerUid)

            deviceSet.singupWithAppsFlyer(it, appsFlyerUid)
            def user = findUserInDatabase(it, phoneState)
            def found = appsFlyerDataRepository.findDataByUserId(user.id)
            assertNotNull("For [$it]-[$user.id] appsflyer data is expected", found);
            newUsers.put(it, user)

            activate(it, currentProvider)
        }
    }


    @Given('^Two activated via (.+) users with (.+) using (.+) for (.+) above (.+) and (.+) which have appsflyer data$')
    def givenTwoActivatedUsers(
            SocialActivationType socialActivationType,
            @Transform(DictionaryTransformer.class) Word deviceTypes,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            String aboveVersion,
            @Transform(DictionaryTransformer.class) Word communities) {
        currentProvider = socialActivationType

        def above = ApiVersions.from(versions.list()).above(aboveVersion)
        userDeviceDatas = userDeviceDataService.table(above, communities.set(), deviceTypes.set(), formats.set(RequestFormat))
        otherUserDeviceDatas = new ArrayList<UserDeviceData>(userDeviceDatas)

        // First user
        userDeviceDatas.each {
            def appsFlyerUid = UUID.randomUUID().toString();
            deviceSet.singupWithAppsFlyer(it, appsFlyerUid)

            activate(it, currentProvider)

            def user = findUserInDatabase(it, deviceSet.getPhoneState(it))
            def found = appsFlyerDataRepository.findDataByUserId(user.id)
            assertNotNull("For [$it]-[$user.id] appsflyer data is expected", found);
        }

        // Second user
        otherUserDeviceDatas.each {
            def appsFlyerUid = UUID.randomUUID().toString();
            otherDeviceSet.singupWithAppsFlyer(it, appsFlyerUid)

            if(currentProvider == Facebook){
                otherDeviceSet.loginUsingFacebook(it)
            } else if (currentProvider == GooglePlus){
                otherDeviceSet.loginUsingGooglePlus(it)
            } else {
                throw new IllegalArgumentException("Not yet implemented for provider type $socialActivationType")
            }

            oldAppsFlyerUids.put(it, appsFlyerUid)

            def user = findUserInDatabase(it, otherDeviceSet.getPhoneState(it))
            def found = appsFlyerDataRepository.findDataByUserId(user.id)
            assertNotNull("For [$it]-[$user.id] appsflyer data is expected", found);
        }
    }

    @When('^First user activates on same device that has appsflyer data with provider profile of second user which has own appsflyer data$')
    def "First user activates on same device that has appsflyer data with provider profile of second user which has own appsflyer data"(){
        userDeviceDatas.each {
            def phoneState = deviceSet.getPhoneState(it)

            def appsFlyerUid = UUID.randomUUID().toString();
            newAppsFlyerUids.put(it, appsFlyerUid)

            deviceSet.singup(it, null, appsFlyerUid, false, phoneState.deviceUID);
            def user = findUserInDatabase(it, phoneState)
            def found = appsFlyerDataRepository.findDataByUserId(user.id)
            assertNotNull("For [$it]-[$user.id] appsflyer data is expected", found);
            newUsers.put(it, user)

            // First user uses profile of second
            if(currentProvider == Facebook){
                deviceSet.loginUsingFacebookWithProfile(it, otherDeviceSet.getPhoneState(it).facebookAccessToken, otherDeviceSet.getPhoneState(it).facebookUserId)
            } else if (currentProvider == GooglePlus){
                deviceSet.loginUsingGooglePlusWithProfile(it, otherDeviceSet.getPhoneState(it).googlePlusToken, otherDeviceSet.getPhoneState(it).googlePlusUserId)
            } else {
                throw new IllegalArgumentException("Not yet implemented for provider type $socialActivationType")
            }
        }
    }

    @And('^Second user\'s appsflyer data in db should point to second user$')
    def "Second user's appsflyer data in db should point to second user"(){
        userDeviceDatas.each {
            def phoneState = deviceSet.getPhoneState(it);
            def user = findUserInDatabase(it, phoneState);
            def found = appsFlyerDataRepository.findDataByUserId(user.id)
            assertNotNull("For [$it]-[$user.id] appsflyer data is expected but was null", found);
        }
    }


    @And('^Second user\'s appsflyer data in db should have new user\'s appsflyer uid$')
    def "Second user's appsflyer data in db should have new user's appsflyer uid"(){
        userDeviceDatas.each {
            def phoneState = deviceSet.getPhoneState(it);
            def user = findUserInDatabase(it, phoneState);
            def found = appsFlyerDataRepository.findDataByUserId(user.id)
            assertEquals("Expected appsflyer uid [${newAppsFlyerUids[it]}] but was [$found.appsFlyerUid] for [$it]-[$user.id]",
                    newAppsFlyerUids[it], found.appsFlyerUid)
        }
    }



    @After
    def cleanDevicesSet() {
        newAppsFlyerUids.clear()
        oldAppsFlyerUids.clear()
        deviceSet.cleanup();
    }

    private User findUserInDatabase(UserDeviceData userDeviceData, PhoneState phoneState) {
        return userDbService.findUser(phoneState, userDeviceData);
    }
}
