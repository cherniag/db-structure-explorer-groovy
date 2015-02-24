package mobi.nowtechnologies.applicationtests.features.activation.facebook
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
import mobi.nowtechnologies.applicationtests.services.device.domain.ApiVersions
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
import static org.junit.Assert.assertTrue
/**
 * @author kots
 * @since 8/21/2014.
 */
@Component
class FacebookFailureFeature {

    @Resource
    UserDeviceDataService userDeviceDataService

    @Resource
    MQAppClientDeviceSet deviceSet

    @Resource
    UserDbService userDbService

    @Resource
    CommonAssertionsService assertionsService;

    List<UserDeviceData> currentUserDevices

    Map<UserDeviceData, User> users = new ConcurrentHashMap<>()

    @Resource
    RunnerService runnerService;
    Runner runner;

    @Transactional('applicationTestsTransactionManager')
    @Given('^Registered user with (.+) using (.+) format for (.+) and (.+)$')
    def 'Registered user with given devices using given format for given versions and given communities'(
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

    @When('^Registered user enters Facebook credentials and client does not pass required parameter$')
    def "Registered user enters Facebook credentials and client does not pass required parameter"() throws Throwable {
        runner.parallel {
            deviceSet.loginUsingFacebookWithoutAccessToken(it)
        }
    }

    @Then('^User gets (\\d+) http error code for api version (.+) and (\\d+) http error code for (.+) above (.+) with message regarding missing parameter$')
    def "User gets http error code for api version and http error code for api versions and above with message regarding missing parameter"(int httpErrorCode1,
                                                                                                                                            String version,
                                                                                                                                            int httpErrorCode2,
                                                                                                                                            @Transform(DictionaryTransformer.class) Word versions,
                                                                                                                                            String versionAbove) {
        List<String> versions2 = ApiVersions.from(versions.set()).above(versionAbove);

        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)
            if (version.equals(it.apiVersion)) {
                assertEquals(phoneState.lastFacebookErrorStatus.value(), httpErrorCode1)
            } else if (versions2.contains(it.apiVersion)) {
                assertEquals(phoneState.lastFacebookErrorStatus.value(), httpErrorCode2)
            }
        }
    }

    @Transactional('applicationTestsTransactionManager')
    @And('^In database user account remains unchanged$')
    def "In database user account remains unchanged"() throws Throwable {
        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)
            def user = userDbService.findUser(phoneState, it)
            def oldUser = users[it]

            assertionsService.checkUserWasNotChanged(oldUser, user);
        }
    }

    @When('^Registered user enters Facebook credentials and client passes wrong authentication parameter$')
    def "Registered user enters Facebook credentials and client passes wrong authentication parameter"() {
        runner.parallel {
            deviceSet.loginUsingFacebookBadUserName(it)
        }
    }

    @Then('^User gets (\\d+) http error code with message "(.+)" for api version (.+) and message "(.+)" for (.+) above (.+)$')
    def "User gets http error code with message about bad user credentials for api version and message login pass check failed for api and above"(int httpErrorCode,
                                                                                                                                                  String message1,
                                                                                                                                                  String version,
                                                                                                                                                  String message2,
                                                                                                                                                  @Transform(DictionaryTransformer.class) Word versions,
                                                                                                                                                  String versionAbove) {
        List<String> versions2 = ApiVersions.from(versions.set()).above(versionAbove);

        runner.parallel {
            def phoneState = deviceSet.getPhoneState(it)
            assertEquals(httpErrorCode, phoneState.lastFacebookErrorStatus.value())
            if (version.equals(it.apiVersion)) {
                assertEquals(message1, phoneState.lastFacebookErrorResponse.getMessage())
            } else if (versions2.contains(it.apiVersion)) {
                assertTrue(phoneState.lastFacebookErrorResponse.getMessage().contains(message2))
            }
        }
    }
}

