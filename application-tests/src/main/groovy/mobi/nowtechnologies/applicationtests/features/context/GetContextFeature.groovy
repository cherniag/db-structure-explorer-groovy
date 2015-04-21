package mobi.nowtechnologies.applicationtests.features.context
import com.google.common.collect.Sets
import cucumber.api.Transform
import cucumber.api.java.After
import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import groovy.json.JsonSlurper
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word
import mobi.nowtechnologies.applicationtests.features.common.transformers.list.ListValues
import mobi.nowtechnologies.applicationtests.features.common.transformers.list.ListValuesTransformer
import mobi.nowtechnologies.applicationtests.features.common.transformers.util.NullableString
import mobi.nowtechnologies.applicationtests.features.common.transformers.util.NullableStringTransformer
import mobi.nowtechnologies.applicationtests.services.RequestFormat
import mobi.nowtechnologies.applicationtests.services.db.UserDbService
import mobi.nowtechnologies.applicationtests.services.device.PhoneState
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData
import mobi.nowtechnologies.applicationtests.services.runner.Runner
import mobi.nowtechnologies.applicationtests.services.runner.RunnerService
import mobi.nowtechnologies.applicationtests.services.subscribe.SubscriptionService
import mobi.nowtechnologies.applicationtests.services.util.SimpleInterpolator
import mobi.nowtechnologies.server.persistence.domain.Duration
import mobi.nowtechnologies.server.persistence.domain.User
import mobi.nowtechnologies.server.persistence.domain.UserStatusType
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfig
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfigType
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehaviorType
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartUserStatusBehavior
import mobi.nowtechnologies.server.persistence.domain.referral.UserReferralsSnapshot
import mobi.nowtechnologies.server.persistence.repository.ChartRepository
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository
import mobi.nowtechnologies.server.persistence.repository.SubmittedPaymentRepository
import mobi.nowtechnologies.server.persistence.repository.UserReferralsSnapshotRepository
import mobi.nowtechnologies.server.persistence.repository.behavior.BehaviorConfigRepository
import mobi.nowtechnologies.server.persistence.repository.behavior.ChartUserStatusBehaviorRepository
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

import javax.annotation.Resource
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.ConcurrentHashMap

import static org.junit.Assert.*

@Component
public class GetContextFeature {
    @Resource
    UserDeviceDataService userDeviceDataService
    @Resource
    UserDbService userDbService
    @Resource
    MQAppClientDeviceSet appClientDeviceSet
    @Resource
    CommunityRepository communityRepository
    @Resource
    BehaviorConfigRepository behaviorConfigRepository
    @Resource
    ChartRepository chartRepository;
    @Resource
    SubscriptionService subscriptionService
    @Resource
    UserReferralsSnapshotRepository referralsSnapshotRepository
    @Resource
    ChartUserStatusBehaviorRepository chartUserStatusBehaviorRepository
    @Resource
    PendingPaymentRepository pendingPaymentRepository
    @Resource
    SubmittedPaymentRepository submittedPaymentRepository

    @Resource
    SimpleInterpolator simpleInterpolator
    @Resource
    RunnerService runnerService
    Runner runner


    Set<UserDeviceData> userDeviceDatas = new HashSet<>()
    Map<String, BehaviorConfig> freemiumSettings = new ConcurrentHashMap<>();
    Map<UserDeviceData, ResponseEntity<String>> responses = new ConcurrentHashMap<>()
    Set<String> allCommunities = new HashSet<>()
    Map<String, Integer> communitiesCharts = new HashMap<>();

    Date serverTime = new Date()
    ChartBehaviorCase currentCase;

    @Given('^First time user with (.+) using (.+) format with (.+) and (.+) available$')
    def "First time user with using format with and available"(
            @Transform(DictionaryTransformer.class) Word deviceTypes,
            RequestFormat format,
            @Transform(DictionaryTransformer.class) Word versions,
            @Transform(DictionaryTransformer.class) Word communities) {
        allCommunities.addAll(communities.set())
        userDeviceDatas.addAll(userDeviceDataService.table(versions.list(), allCommunities, deviceTypes.set(), Sets.newHashSet(format)))
        runner = runnerService.create(userDeviceDatas)

        allCommunities.each{ it ->
            List charts = chartRepository.findByCommunityName(it);
            if(charts == null || charts.empty){
                throw new IllegalStateException("Community $it has no charts.");
            }
            communitiesCharts[it] = charts[0].i;
        }

        for(def it : userDeviceDatas) {
            appClientDeviceSet.singup(it)
            appClientDeviceSet.loginUsingFacebook(it)
        }
    }

    @Given('^user invokes get context command$')
    def "user invokes get context command"() {
        runner.sequence {
            def context = appClientDeviceSet.context(it)
            responses[it] = context
        }
    }

    @Given('^chart chart_ID contains \\[(.+)\\]$')
    def "chart contains info"(@Transform(ListValuesTransformer.class) ListValues listValues) {
        runner.sequence {
            def chartId = communitiesCharts.get(it.community);
            def foundJson = extractFromJsonChartInfo(responses[it], chartId);

            UserReferralsSnapshot referralInfo = findUserReferralsSnapshot(it);
            def timeInResponse = extractValueFromResponse(responses[it], "context.serverTime")

            def expected = extractResult(it, referralInfo, listValues, timeInResponse, chartId);

            assertEquals("Did not match for " + it, expected, foundJson)
        }
    }

    @Then('^header (.+) contains value$')
    def "header Expires contains value"(String header) {
        runner.parallel {
            def time = responses[it].getHeaders().getFirstDate(header)
            assertTrue time > new Date(0).getTime()
        }
    }

    @Then('^response has (.+) http response code$')
    def "response has 200 http response code"(final int httpResponseCode) {
        runner.parallel {
            assertEquals httpResponseCode, responses[it].getStatusCode().value()
        }
    }

    @And('^freemium info is set$')
    def "freemium info is set"() {
        allCommunities.each {
            freemiumSettings[it] = findBehaviorConfig(it)
        }
    }

    @And('^referrals info is created$')
    def "referrals info is created"() {
        runner.parallel {
            def referralInfo = findUserReferralsSnapshot(it);
            assertNotNull("Referral info is not created for user data: " + it, referralInfo)
        }
    }

    @And('^value by \\[(.+)\\] path in response is same as set$')
    def "value by path in response is same as set"(String key) {
        runner.parallel {
            def inJson = extractValueFromResponse(responses[it], key) as int
            def asSet = freemiumSettings[it.communityUrl].requiredReferrals

            assertEquals inJson, asSet
        }
    }

    @And('^value by \\[(.+)\\] path in response is equal to (.+)$')
    def "value by path in response is equal to"(String key, String value) {
        runner.parallel {
            def inJson = extractValueFromResponse(responses[it], key) as String
            assertEquals inJson, value
        }
    }

    @And('^value by \\[(.+)\\] path is equal to value by \\[(.+)\\] path$')
    def "value by path1 is equal to value by path2"(String path1, String path2) {
        runner.parallel {
            def inJson1 = extractValueFromResponse(responses[it], path1) as String
            def inJson2 = extractValueFromResponse(responses[it], path2) as String
            assertEquals inJson1, inJson2
        }
    }

    @And('^user is in (.+) state$')
    def "user is in LIMITED state"(UserStatusType userStatusType) {
        runner.parallel {
            PhoneState phoneState = appClientDeviceSet.getPhoneState(it);
            User user = findUserInDatabase(it, phoneState);

            if(userStatusType == UserStatusType.LIMITED) {
                logger().info("Limit access for user: " + user.getId())
                subscriptionService.limitAccess(user, serverTime)
            }

            if(userStatusType == UserStatusType.FREE_TRIAL) {
                logger().info("Q: Is currently on free trial user: " + user.getId() + "? A:"+ user.isOnFreeTrial())
                assertTrue(user.isOnFreeTrial())
            }
        }
    }

    @And('^user has (.+) payment details$')
    def "user has AWAITING payment details"(PaymentDetailsStatus paymentDetailsStatus) {
        runner.parallel {
            PhoneState phoneState = appClientDeviceSet.getPhoneState(it);
            User user = findUserInDatabase(it, phoneState);
            subscriptionService.setCurrentPaymentDetailsStatus(user, paymentDetailsStatus);
        }
    }

    @And('^the case is the following \\[(.+)\\]$')
    def "the case is the following"(@Transform(ChartBehaviorCaseTransformer.class) ChartBehaviorCase chartBehaviorCase) {
        currentCase = chartBehaviorCase
    }

    @And('^chart chart_ID configured (.+):(.+),locked:(.+) and (.+):(.+),locked:(.+)$')
    def "chart is configured"(UserStatusType status1, ChartBehaviorType type1, @Transform(NullableStringTransformer.class) NullableString action1,
                              UserStatusType status2, ChartBehaviorType type2, @Transform(NullableStringTransformer.class) NullableString action2) {
        allCommunities.each {
            def chartId = communitiesCharts.get(it);

            ChartUserStatusBehavior b1 = find(it, chartId, status1);
            b1.@chartBehavior.@type=type1
            b1.@action=action1.value()

            chartUserStatusBehaviorRepository.saveAndFlush(b1)

            ChartUserStatusBehavior b2 = find(it, chartId, status2);
            b2.@chartBehavior.@type=type2
            b2.@action=action2.value()

            chartUserStatusBehaviorRepository.saveAndFlush(b2)
        }
    }

    @When('^referral data matches current case$')
    def "referral data matches current case"() {
        runner.parallel {
            PhoneState phoneState = appClientDeviceSet.getPhoneState(it);
            User user = findUserInDatabase(it, phoneState);

            def duration = calcDuration(currentCase, user, serverTime)

            def referralInfo = referralsSnapshotRepository.findByUserId(user.id)
            referralInfo.updateMatchesData(Integer.MAX_VALUE, serverTime)
            referralInfo.@referralsDuration = duration;

            referralsSnapshotRepository.saveAndFlush(referralInfo)
        }
    }

    @After
    def clean() {
        responses.clear()
        allCommunities.clear()
        freemiumSettings.clear()
        appClientDeviceSet.cleanup()
        userDeviceDatas.clear()

        pendingPaymentRepository.deleteAll()
        submittedPaymentRepository.deleteAll()
    }

    def extractResult(UserDeviceData data, UserReferralsSnapshot snapshot, ListValues listValues, String serverTime, Integer chartId) {
        List<ExpectedValue> expectedList = listValues.values(new ExpectedValueTransformer())
        List<Map<String, String>> result = []

        for(ExpectedValue expectedValue : expectedList) {
            expectedValue.expected['validFrom'] = simpleInterpolator.interpolate(expectedValue.expected['validFrom'], mapForDates(snapshot, serverTime, data))

            if(expectedValue.expected.containsKey('lockedAction')) {
                expectedValue.expected['lockedAction'] = simpleInterpolator.interpolate(expectedValue.expected['lockedAction'], mapForLockedAction(data, chartId))
            }

            result.add(expectedValue.expected)
        }
        return result
    }

    def mapForLockedAction(UserDeviceData data, Integer chartId) {
        def map = [:]
        map['community'] = data.getCommunityUrl()
        map['chart_ID'] = chartId
        return map
    }

    def mapForDates(UserReferralsSnapshot snapshot, String serverTime, UserDeviceData data) {
        PhoneState phoneState = appClientDeviceSet.getPhoneState(data);
        User user = findUserInDatabase(data, phoneState);

        def map = [:]
        map['now'] = serverTime
        map['free_trial_exp'] = createDateFormat().format(user.getFreeTrialExpiredAsDate())
        if(!snapshot.hasNoDuration()) {
            map['exp'] = createDateFormat().format(snapshot.getReferralsExpiresDate())
        }
        return map
    }

    def DateFormat createDateFormat() {
        def format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format
    }

    def extractValueFromResponse(ResponseEntity<String> response, String compositeKey) {
        def b = response.body
        def object = new JsonSlurper().parseText(b)
        findByKey(object, compositeKey);
    }

    def findByKey(def map, String compositeKey) {
        def v = map
        compositeKey.split('\\.').each() {
            if(v instanceof List) {
                v = v[it as int]
            } else {
                v = v[it]
            }
        }
        return v
    }

    def extractFromJsonChartInfo(ResponseEntity<String> response, int chartId) {
        List chartsInfoList = extractValueFromResponse(response, 'context.playlists.instructions');
        def chart = chartsInfoList.find {
            e -> (e['ID'] as int) == chartId
        }
        return chart['behavior']
    }

    User findUserInDatabase(UserDeviceData userDeviceData, PhoneState phoneState) {
        return userDbService.findUser(phoneState, userDeviceData);
    }

    Logger logger() {
        LoggerFactory.getLogger(getClass());
    }

    ChartUserStatusBehavior find(String communityUrl, int chartId, UserStatusType status) {
        def bc = findBehaviorConfig(communityUrl)
        chartUserStatusBehaviorRepository.findByChartIdBehaviorConfigAndStatus(chartId, bc, status)
    }

    BehaviorConfig findBehaviorConfig(String communityUrl) {
        def c = communityRepository.findByRewriteUrlParameter(communityUrl)
        behaviorConfigRepository.findByCommunityIdAndBehaviorConfigType(c.id, BehaviorConfigType.FREEMIUM)
    }

    Duration calcDuration(ChartBehaviorCase currentCase, User user, Date serverTime) {
        if(currentCase.hasReferralPeriod()) {
            return currentCase.getReferralPeriod(serverTime, user.getFreeTrialExpiredAsDate())
        } else {
            return Duration.noPeriod()
        }
    }

    UserReferralsSnapshot findUserReferralsSnapshot(UserDeviceData data) {
        PhoneState phoneState = appClientDeviceSet.getPhoneState(data);
        User user = findUserInDatabase(data, phoneState);
        return referralsSnapshotRepository.findByUserId(user.getId())
    }
}
