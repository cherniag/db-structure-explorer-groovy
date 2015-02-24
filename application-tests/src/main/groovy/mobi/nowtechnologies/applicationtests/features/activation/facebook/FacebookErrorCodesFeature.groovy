package mobi.nowtechnologies.applicationtests.features.activation.facebook
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
 * @author kots
 * @since 8/20/2014.
 */
@Component('activation.facebookErrorCodesFeature')
class FacebookErrorCodesFeature {

    @Resource
    UserDeviceDataService userDeviceDataService

    @Resource
    MQAppClientDeviceSet deviceSet

    @Resource
    UserDbService userDbService

    List<UserDeviceData> currentUserDevices

    Map<UserDeviceData, User> users = new ConcurrentHashMap<>();

    @Resource
    RunnerService runnerService;
    Runner runner;

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
            new ObjectFormatter().format(user)
            //end of dirty hack

            users.put(it, user)
        }
    }

    @When('^Registered user enters Facebook credentials$')
    def "Registered_user_enters_Facebook_credentials"() {
        runner.parallel { deviceSet.loginUsingFacebook(it) }
    }

    @Then('^User gets (\\d+) http error code and (\\d+) error code and (.*) message$')
    def "User gets given http error code and given error code and given message"(final int httpErrorCode, final int errorCode, final String errorBody) {
        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def lastFacebookError = phoneState.getLastFacebookErrorResponse()
            def status = phoneState.getLastFacebookErrorStatus()
            assertEquals(httpErrorCode, status.value())

            assertEquals(errorCode, Integer.valueOf(lastFacebookError.getErrorCode()).intValue())
            assertEquals(errorBody, lastFacebookError.getMessage())
        }
    }

    @Transactional("applicationTestsTransactionManager")
    @And('^In database user account remains unchanged$')
    def "In database user account remains unchanged"() {
        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            def oldUser = users[it]
            assertReflectionEquals(oldUser, user, ReflectionComparatorMode.LENIENT_ORDER)
        }
    }

    @When('^Registered user enters Facebook credentials and facebook returns invalid facebook id$')
    def "Registered user enters Facebook credentials and facebook returns invalid facebook id"() {
        runner.parallel {
            deviceSet.loginUsingFacebookWithInvalidFacebookId(it)
        }
    }

    @When('^Registered user enters Facebook credentials and facebook returns invalid access token$')
    def "Registered user enters Facebook credentials and facebook returns invalid access token"() {
        runner.parallel {
            deviceSet.loginUsingFacebookWithInvalidAccessToken(it)
        }
    }
}
