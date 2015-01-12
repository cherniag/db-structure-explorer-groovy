package mobi.nowtechnologies.applicationtests.features
import cucumber.api.Transform
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word
import mobi.nowtechnologies.applicationtests.services.RequestFormat
import mobi.nowtechnologies.applicationtests.services.db.UserDbService
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService
import mobi.nowtechnologies.applicationtests.services.device.domain.ApiVersions
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

import javax.annotation.Resource

import static org.junit.Assert.assertEquals

@Component
class GetContextFeature {
    @Resource
    UserDeviceDataService userDeviceDataService
    @Resource
    UserDbService userDbService
    @Resource
    MQAppClientDeviceSet appClientDeviceSet
    List<UserDeviceData> userDeviceDatas
    Map<UserDeviceData, ResponseEntity<String>> responses = [:]

    @Given('^First time user with (.+) using (.+) formats with (.+) above (.+) and (.+) available$')
    def "First time user with (.+) using (.+) formats with (.+) above (.+) and (.+) available"(
            @Transform(DictionaryTransformer.class) Word deviceTypes,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            String sinceVersion,
            @Transform(DictionaryTransformer.class) Word communities) {
        def bellow = ApiVersions.from(versions.set()).above(sinceVersion)
        userDeviceDatas = userDeviceDataService.table(bellow, communities.set(), deviceTypes.set(), formats.set(RequestFormat));

        for(def it : userDeviceDatas) {
            appClientDeviceSet.singup(it)
            appClientDeviceSet.loginUsingFacebook(it)
        }
    }

    @Given('^user invokes get context command$')
    def "user invokes get context command"() {
        userDeviceDatas.each {
            def context = appClientDeviceSet.context(it)
            responses[it] = context
        }
    }

    @Then('^response has (.+) http response code$')
    def "response has 200 http response code"(final int httpResponseCode) {
        userDeviceDatas.each {
            assertEquals httpResponseCode, responses[it].getStatusCode().value()
        }
    }
}
