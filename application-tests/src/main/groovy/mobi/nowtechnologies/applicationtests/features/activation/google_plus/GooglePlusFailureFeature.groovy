package mobi.nowtechnologies.applicationtests.features.activation.google_plus

import cucumber.api.Transform
import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word
import mobi.nowtechnologies.applicationtests.services.CommonAssertionsService
import mobi.nowtechnologies.applicationtests.services.RequestFormat
import mobi.nowtechnologies.applicationtests.services.db.UserDbService
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData
import mobi.nowtechnologies.applicationtests.services.runner.Runner
import mobi.nowtechnologies.applicationtests.services.runner.RunnerService
import mobi.nowtechnologies.server.persistence.domain.User
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.unitils.core.util.ObjectFormatter

import javax.annotation.Resource
import java.util.concurrent.ConcurrentHashMap

import static org.junit.Assert.assertEquals
/**
 * Created by kots on 9/9/2014.
 */
@Component
class GooglePlusFailureFeature {

    @Resource
    UserDeviceDataService userDeviceDataService

    @Resource
    MQAppClientDeviceSet deviceSet

    @Resource
    UserDbService userDbService

    @Resource
    CommonAssertionsService assertionsService

    List<UserDeviceData> currentUserDevices

    Map<UserDeviceData, User> users = new ConcurrentHashMap<>()

    @Resource
    RunnerService runnerService;
    Runner runner;

    @Transactional('applicationTestsTransactionManager')
    @Given('^Registered user with (.+) using (.+) format for (.+) and (.+)')
    def "Registered user with given devices using given format for given versions and given communities"(
            @Transform(DictionaryTransformer.class) Word devices,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            @Transform(DictionaryTransformer.class) Word communities) {
        currentUserDevices = userDeviceDataService.table(versions.list(), communities.set(), devices.set(), formats.set(RequestFormat))
        runner = runnerService.create(currentUserDevices)
        runner.parallel {
            deviceSet.singup(it)
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)

            //dirty hack to fetch all lazy deps without customizing hibernate queries of manually checking data
            new ObjectFormatter(Integer.MAX_VALUE).format(user)
            //end of dirty hack

            users.put(it, user)
        }
    }

    @When('^Registered user enters Google Plus credentials and client does not pass required parameter$')
    def "Registered user enters Google Plus credentials and client does not pass required parameter"() {
        runner.parallel {
            deviceSet.loginUsingGooglePlusWithoutAccessToken(it)
        }
    }

    @Then('^User gets (\\d+) http error code with message regarding missing parameter$')
    def "User gets http error code with message regarding missing parameter"(int httpCode) {
        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)
            assertEquals(httpCode, phoneState.lastGooglePlusErrorStatus.value())
        }
    }

    @Transactional("applicationTestsTransactionManager")
    @And('^In database user account remains unchanged$')
    def "In database user account remains unchanged"() {
        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            def oldUser = users[it]

            assertionsService.checkUserWasNotChanged(oldUser, user);
        }
    }

    @When('^Registered user enters Google Plus credentials and client passes wrong authentication parameter$')
    def "Registered user enters Google Plus credentials and client passes wrong authentication parameter"() {
        runner.parallel {
            deviceSet.loginUsingGooglePlusBadAuth(it)
        }
    }

    @Then('^User gets (\\d+) http error code with message login/pass check failed$')
    def "User gets http error code with message login pass check failed"(int httpCode) {
        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)
            assertEquals(httpCode, phoneState.lastGooglePlusErrorStatus.value())
        }
    }
}
