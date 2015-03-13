package mobi.nowtechnologies.applicationtests.features.urbanairship

import cucumber.api.Transform
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
import mobi.nowtechnologies.applicationtests.services.runner.Runner
import mobi.nowtechnologies.applicationtests.services.runner.RunnerService
import mobi.nowtechnologies.server.persistence.domain.User
import mobi.nowtechnologies.server.persistence.repository.UrbanAirshipTokenRepository
import org.springframework.stereotype.Component

import javax.annotation.Resource

import static org.junit.Assert.assertEquals

/**
 * Created by enes on 2/3/15.
 */
@Component
class UrbanairshipTokenOnAccCheckFeature {

    @Resource
    private MQAppClientDeviceSet deviceSet;

    @Resource
    UrbanAirshipTokenRepository urbanAirshipTokenRepository

    @Resource
    UserDeviceDataService userDeviceDataService

    @Resource
    UserDbService userDbService

    @Resource
    RunnerService runnerService

    def userDeviceDatas = []

    Runner runner;

    def urbanAirshipToken = "some-urban-airship-token"
    def urbanAirshipTokenUpdated = "some-urban-airship-token-updated"

    @Given('^First time user with (.+) using (.+) formats with (.+) above (.+) and (.+)$')
    def "First time user with all devices using JSON and XML formats with all versions above 6.9 and all communities"(
            @Transform(DictionaryTransformer.class) Word deviceTypes,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            String aboveVersion,
            @Transform(DictionaryTransformer.class) Word communities) {

        def above = ApiVersions.from(versions.set()).above(aboveVersion)
        userDeviceDatas = userDeviceDataService.table(above, communities.set(), deviceTypes.set(), formats.set(RequestFormat))
        runner = runnerService.create(userDeviceDatas)
    }

    @When('^User checks acc check api with urban airship token$')
    def "User checks acc check api with urban airship token"() {
        runner.parallel {
            deviceSet.singup(it)
            deviceSet.accountCheckWithUrbanAirshipToken(it, urbanAirshipToken)
        }
    }

    @Then('^Urban airship token should be persisted$')
    def "Urban airship token should be persisted"() {
        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def user = findUserInDatabase(it, phoneState)
            def urbanToken = urbanAirshipTokenRepository.findDataByUserId(user.id)
            assertEquals(urbanAirshipToken, urbanToken.token)
        }
    }

    @Given('^Existing user with (.+) using (.+) formats with (.+) above (.+) and (.+)$')
    def "Existing user with all devices using JSON and XML formats with all versions above 6.9 and all communities"(
            @Transform(DictionaryTransformer.class) Word deviceTypes,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            String aboveVersion,
            @Transform(DictionaryTransformer.class) Word communities) {

        def above = ApiVersions.from(versions.set()).above(aboveVersion)
        userDeviceDatas = userDeviceDataService.table(above, communities.set(), deviceTypes.set(), formats.set(RequestFormat))
        runner = runnerService.create(userDeviceDatas)
    }

    @When('^User checks acc check api with new urban airship token$')
    def "User checks acc check api with new urban airship token"() {
        runner.parallel {
            deviceSet.singup(it)
            deviceSet.accountCheckWithUrbanAirshipToken(it, urbanAirshipToken)

            deviceSet.accountCheckWithUrbanAirshipToken(it, urbanAirshipTokenUpdated)
        }
    }

    @Then('^Urban airship token should be updated')
    def "Urban airship token should be updated"() {
        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def user = findUserInDatabase(it, phoneState)
            def urbanToken = urbanAirshipTokenRepository.findDataByUserId(user.id)
            assertEquals(urbanAirshipTokenUpdated, urbanToken.token)
        }
    }

    private User findUserInDatabase(UserDeviceData userDeviceData, PhoneState phoneState) {
        return userDbService.findUser(phoneState, userDeviceData);
    }
}
