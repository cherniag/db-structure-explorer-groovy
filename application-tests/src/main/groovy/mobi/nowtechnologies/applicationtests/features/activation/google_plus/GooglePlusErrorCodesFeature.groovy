package mobi.nowtechnologies.applicationtests.features.activation.google_plus
import cucumber.api.Transform
import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word
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
import org.unitils.reflectionassert.ReflectionComparatorMode

import javax.annotation.Resource
import java.util.concurrent.ConcurrentHashMap

import static org.junit.Assert.assertEquals
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals
/**
 * Created by kots on 9/15/2014.
 */
@Component
class GooglePlusErrorCodesFeature {

    @Resource
    UserDeviceDataService userDeviceDataService

    @Resource
    MQAppClientDeviceSet deviceSet

    @Resource
    UserDbService userDbService

    @Resource
    RunnerService runnerService;
    Runner runner;

    List<UserDeviceData> currentUserDevices
    Map<UserDeviceData, User> users = new ConcurrentHashMap<>();

    @Transactional("applicationTestsTransactionManager")
    @Given('^Registered user with (.+) using (.+) format for (.+) and (.+)$')
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

    @When('^Registered user enters Google Plus credentials and Google Plus returns empty email$')
    def "Registered user enters Google Plus credentials and Google Plus returns empty email"() {
        runner.parallel { deviceSet.loginUsingGooglePlusWithEmptyEmail(it)}
    }

    @Then('^User gets (\\d+) http error code and (\\d+) error code and (.*) message$')
    def "User gets given http error code and given error code and given message"(final int httpErrorCode,
                                                                                 final int errorCode,
                                                                                 final String errorBody) {
        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def lastGooglePlusError = phoneState.getLastGooglePlusError();
            def status = phoneState.getLastGooglePlusErrorStatus()
            assertEquals(httpErrorCode, status.value())

            assertEquals(errorCode, Integer.valueOf(lastGooglePlusError.getErrorCode()).intValue())
            assertEquals(errorBody, lastGooglePlusError.getMessage())
        }
    }

    @Transactional('applicationTestsTransactionManager')
    @And('^In database user account remains unchanged$')
    def "In database user account remains unchanged"() {
        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            def oldUser = users[it]
            assertReflectionEquals(oldUser, user, ReflectionComparatorMode.LENIENT_ORDER)
        }
    }

    @When('^Registered user enters Google Plus credentials and Google Plus returns invalid Google Plus user id$')
    def "Registered user enters Google Plus credentials and Google Plus returns invalid Google Plus user id"() {
        runner.parallel { deviceSet.loginUsingGooglePlusWithInvalidGooglePlusId(it)}
    }

    @When('^Registered user enters Google Plus credentials and Google Plus returns invalid access token$')
    def "Registered user enters Google Plus credentials and Google Plus returns invalid access token"() {
        runner.parallel { deviceSet.loginUsingGooglePlusWithInvalidAuthToken(it)}
    }
}
