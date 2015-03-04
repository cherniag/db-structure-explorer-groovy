package mobi.nowtechnologies.applicationtests.features.urbanairship

import cucumber.api.Transform
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
import mobi.nowtechnologies.applicationtests.services.runner.Runner
import mobi.nowtechnologies.applicationtests.services.runner.RunnerService
import mobi.nowtechnologies.server.persistence.domain.UrbanAirshipToken
import mobi.nowtechnologies.server.persistence.domain.User
import mobi.nowtechnologies.server.persistence.repository.UrbanAirshipTokenRepository
import org.junit.Assert
import org.springframework.stereotype.Component

import javax.annotation.Resource

import static mobi.nowtechnologies.applicationtests.features.common.social.SocialActivationType.Facebook
import static mobi.nowtechnologies.applicationtests.features.common.social.SocialActivationType.GooglePlus

/**
 * Created by enes on 2/4/15.
 */
@Component
class UrbanairshipTokenMergeFeature {

    @Resource
    MQAppClientDeviceSet deviceSet
    @Resource
    MQAppClientDeviceSet otherDeviceSet
    @Resource
    UserDeviceDataService userDeviceDataService
    @Resource
    UserDbService userDbService
    @Resource
    UrbanAirshipTokenRepository urbanAirshipTokenRepository;

    @Resource
    RunnerService runnerService;
    Runner runner;

    def currentProvider

    public List<UserDeviceData> userDeviceDatas

    def urbanAirshipTokenOld = "urban-airshipToken"
    def urbanAirshipTokenNew = "urban-airshipToken-new"

    @Given('^User is signed up from (.+) using (.+) for (.+) above (.+) and (.+)$')
    def given(
            @Transform(DictionaryTransformer.class) Word deviceTypes,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            String aboveVersion,
            @Transform(DictionaryTransformer.class) Word communities
    ) {
        def above = ApiVersions.from(versions.list()).above(aboveVersion)
        userDeviceDatas = userDeviceDataService.table(above, communities.set(), deviceTypes.set(), formats.set(RequestFormat))

        runner = runnerService.create(userDeviceDatas)

        userDeviceDatas.each {
            deviceSet.singup(it)
        }
    }

    @And('^Urban airship token is sent via Account Check$')
    def "Urban airship token is sent via Account Check"() {
        userDeviceDatas.each {
            deviceSet.accountCheckWithUrbanAirshipToken(it, urbanAirshipTokenOld)
        }
    }

    @And('^User is activated via (.+)$')
    def "User is activated via <Provider>"(SocialActivationType socialActivationType) {
        currentProvider = socialActivationType

        userDeviceDatas.each {
            activate(it, currentProvider)

            def user = findUserInDatabase(it, deviceSet.getPhoneState(it))
            UrbanAirshipToken token = urbanAirshipTokenRepository.findDataByUserId(user.id)

            Assert.assertEquals(token.token, urbanAirshipTokenOld)
        }
    }

    @When('^User signs up again$')
    def "User signs up again"() {
        userDeviceDatas.each {
            def phoneState = deviceSet.getPhoneState(it)
            deviceSet.singup(it, null, null, false, phoneState.deviceUID)
        }
    }

    @And('^New urban airship token is sent via Account Check$')
    def "New urban airship token is sent via Account Check"() {
        userDeviceDatas.each {
            deviceSet.accountCheckWithUrbanAirshipToken(it, urbanAirshipTokenNew)
        }
    }

    @And('^User activated via the same social profile$')
    def "User activated via the same social profile"() {
        userDeviceDatas.each {
            activate(it, currentProvider)
        }
    }

    @Then('^Urban airship token for old user is updated with the new value$')
    def "Urban airship token for old user is updated with the new value"() {
        userDeviceDatas.each {
            def user = findUserInDatabase(it, deviceSet.getPhoneState(it))
            UrbanAirshipToken token = urbanAirshipTokenRepository.findDataByUserId(user.id)

            Assert.assertEquals(token.token, urbanAirshipTokenNew)
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

    private User findUserInDatabase(UserDeviceData userDeviceData, PhoneState phoneState) {
        return userDbService.findUser(phoneState, userDeviceData);
    }
}
