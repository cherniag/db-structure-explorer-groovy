package mobi.nowtechnologies.applicationtests.features.serviceconfig;

import mobi.nowtechnologies.applicationtests.features.common.client.PartnerDeviceSet;
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer;
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word;
import mobi.nowtechnologies.applicationtests.features.common.transformers.util.NullableString;
import mobi.nowtechnologies.applicationtests.features.common.transformers.util.NullableStringTransformer;
import mobi.nowtechnologies.applicationtests.features.serviceconfig.helpers.ClientVersionTransformer;
import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService;
import mobi.nowtechnologies.applicationtests.services.device.domain.ApiVersions;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.JsonHelper;
import mobi.nowtechnologies.applicationtests.services.runner.Invoker;
import mobi.nowtechnologies.applicationtests.services.runner.Runner;
import mobi.nowtechnologies.applicationtests.services.runner.RunnerService;
import mobi.nowtechnologies.applicationtests.services.util.SimpleInterpolator;
import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.device.domain.DeviceTypeCache;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.ErrorMessage;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.versioncheck.domain.ClientVersion;
import mobi.nowtechnologies.server.versioncheck.domain.VersionCheck;
import mobi.nowtechnologies.server.versioncheck.domain.VersionCheckRepository;
import mobi.nowtechnologies.server.versioncheck.domain.VersionCheckStatus;
import mobi.nowtechnologies.server.versioncheck.domain.VersionMessage;
import mobi.nowtechnologies.server.versioncheck.domain.VersionMessageRepository;

import javax.annotation.Resource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Sets;
import cucumber.api.Transform;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static org.junit.Assert.*;

@Component
public class ServiceConfigFeature {

    @Resource
    UserDeviceDataService userDeviceDataService;
    @Resource
    VersionCheckRepository versionCheckRepository;
    @Resource
    VersionMessageRepository versionMessageRepository;
    @Resource
    CommunityRepository communityRepository;
    @Resource
    SimpleInterpolator interpolator;
    @Resource
    JsonHelper jsonHelper;
    @Resource
    PartnerDeviceSet partnerDeviceSet;
    @Resource
    RunnerService runnerService;
    private Runner runner;

    private List<UserDeviceData> userDeviceDatas;
    private String applicationName;
    private Map<UserDeviceData, ResponseEntity<String>> successfulResponses = new ConcurrentHashMap<>();
    private Map<UserDeviceData, String> headers = new ConcurrentHashMap<>();

    private ApiVersions apiVersions;

    @Given("^Mobile client makes Service Config call using JSON format for (.+) and (.+) and (\\w+\\s\\w+) above (.+)$")
    public void givenVersionsAbove(@Transform(DictionaryTransformer.class) Word devices,
                                   @Transform(DictionaryTransformer.class) Word communities,
                                   @Transform(DictionaryTransformer.class) Word versions,
                                   String aboveVersion) {
        apiVersions = ApiVersions.from(versions.set());
        userDeviceDatas = userDeviceDataService.table(apiVersions.above(aboveVersion), communities.set(), devices.set(), Sets.newHashSet(RequestFormat.JSON));
        applicationName = UUID.randomUUID().toString();
        runner = runnerService.create(userDeviceDatas);
    }

    @Given("^Mobile client makes Service Config call using JSON format for (.+) and (.+) and (\\w+\\s\\w+) bellow (.+)$")
    public void givenVersionsBellow(@Transform(DictionaryTransformer.class) Word devices,
                                    @Transform(DictionaryTransformer.class) Word communities,
                                    @Transform(DictionaryTransformer.class) Word versions,
                                    String bellowVersion) {
        apiVersions = ApiVersions.from(versions.set());
        userDeviceDatas = userDeviceDataService.table(apiVersions.bellow(bellowVersion), communities.set(), devices.set(), Sets.newHashSet(RequestFormat.JSON));
        applicationName = UUID.randomUUID().toString();
        runner = runnerService.create(userDeviceDatas);
    }

    @Given("^Mobile client makes Service Config call using JSON format for (.+) and (.+) and (\\w+\\s\\w+)$")
    public void given(@Transform(DictionaryTransformer.class) Word devices, @Transform(DictionaryTransformer.class) Word communities, @Transform(DictionaryTransformer.class) Word versions) {
        apiVersions = ApiVersions.from(versions.set());
        userDeviceDatas = userDeviceDataService.table(versions.list(), communities.set(), devices.set(), Sets.newHashSet(RequestFormat.JSON));
        applicationName = UUID.randomUUID().toString();
        runner = runnerService.create(userDeviceDatas);
    }

    @When("^client version info exist:$")
    public void tableExist(List<ServiceConfigVersionRow> configVersionRows) {
        for (final ServiceConfigVersionRow configVersionRow : configVersionRows) {
            runner.parallel(new Invoker<UserDeviceData>() {
                @Override
                public void invoke(UserDeviceData userDeviceData) {
                    String application = configVersionRow.applicationName;
                    String code = configVersionRow.messageKey;
                    String link = configVersionRow.url;
                    String imageFileName = configVersionRow.image;
                    ClientVersion clientVersion = ClientVersion.from(configVersionRow.appVersion);
                    VersionCheckStatus status = VersionCheckStatus.valueOf(configVersionRow.status);

                    String newApplicationName = interpolator.interpolate(application, Collections.<String, Object>singletonMap("random", applicationName));
                    VersionMessage versionMessage = versionMessageRepository.saveAndFlush(new VersionMessage(code, link));
                    Community c = communityRepository.findByRewriteUrlParameter(userDeviceData.getCommunityUrl());
                    DeviceType deviceType = getDeviceType(userDeviceData);
                    versionCheckRepository.saveAndFlush(new VersionCheck(deviceType, c.getId(), versionMessage, status, newApplicationName, clientVersion, imageFileName));
                }
            });
        }
    }


    @When("^header is in old format \"(.+)\"$")
    public void whenUserAgent(String headerValue) {
        for (UserDeviceData data : userDeviceDatas) {
            headers.put(data, headerValue);
        }
    }

    @Then("^response has (\\d+) http response code$")
    public void thenResponse(final int responseExpected) {
        successfulResponses.clear();

        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData userDeviceData) {
                String header = headers.get(userDeviceData);
                ResponseEntity<String> stringResponseEntity = partnerDeviceSet.serviceConfig(userDeviceData, header, apiVersions);
                int responseFromServer = stringResponseEntity.getStatusCode().value();

                if (responseFromServer == 200) {
                    // check later
                    successfulResponses.put(userDeviceData, stringResponseEntity);
                }

                assertEquals("Code from server: " + responseFromServer + " differs from expected: " + responseExpected + " for " + userDeviceData, responseExpected, responseFromServer);
            }
        });

        headers.clear();
    }

    @And("^error message is '(.+)'$")
    public void errorMessageIs(final String message) {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData userDeviceData) {
                ResponseEntity<String> stringResponseEntity = partnerDeviceSet.serviceConfig(userDeviceData, null, apiVersions);
                ErrorMessage errorMessage = jsonHelper.extractObjectValueByPath(stringResponseEntity.getBody(), "$.response.data[0].errorMessage", ErrorMessage.class);

                assertEquals("Error message from server: " + message + " differs from expected: " + errorMessage.getMessage() + " for " + userDeviceData, message, errorMessage.getMessage());
            }
        });
    }

    @When("^service config data is set to '(.+)' for version '(.+)', '(.+)' application, '(.+)' message, '(.+)' image and '(.+)' link$")
    public void whenServiceConfig(final VersionCheckStatus status,
                                  @Transform(ClientVersionTransformer.class) final ClientVersion clientVersion,
                                  final String application,
                                  final String code,
                                  final String imageFileName,
                                  final String link) {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData userDeviceData) {
                String newApplicationName = interpolator.interpolate(application, Collections.<String, Object>singletonMap("random", applicationName));

                VersionMessage versionMessage = versionMessageRepository.saveAndFlush(new VersionMessage(code, link));

                Community c = communityRepository.findByRewriteUrlParameter(userDeviceData.getCommunityUrl());
                DeviceType deviceType = getDeviceType(userDeviceData);
                VersionCheck versionCheck = new VersionCheck(deviceType, c.getId(), versionMessage, status, newApplicationName, clientVersion, imageFileName);
                versionCheckRepository.saveAndFlush(versionCheck);
            }
        });
    }

    @And("^header is in new format \"(.+)\"$")
    public void whenUserAgentIsNotDefault(final String headerValue) {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData data) {
                Map<String, Object> model = new HashMap<>();
                model.put("random", applicationName);
                model.put("platform", getDeviceType(data).getName());
                model.put("community", communityRepository.findByRewriteUrlParameter(data.getCommunityUrl()).getName());

                String interpolatedHeaderValue = interpolator.interpolate(headerValue, model);

                getLogger().info("Sending User-Agent / X-User-Agent:" + interpolatedHeaderValue);

                headers.put(data, interpolatedHeaderValue);
            }
        });
    }

    @And("^json data is '(.+)'$")
    public void jsonData(String field) {
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            ResponseEntity<String> response = successfulResponses.get(userDeviceData);
            getLogger().info("Response: " + response.getBody());
            Map<String, Object> stringObjectMap = jsonHelper.extractObjectMapByPath(response.getBody(), "$.response.data[0]." + field);
            assertTrue("Empty map for: [" + userDeviceData + "]: " + stringObjectMap, stringObjectMap != null && !stringObjectMap.isEmpty());
        }
    }

    @And("^json field has '(.+)' set to '(.+)'$")
    public void jsonFieldHasValue(String field, @Transform(NullableStringTransformer.class) NullableString nullable) {
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            ResponseEntity<String> response = successfulResponses.get(userDeviceData);
            Map<String, Object> stringObjectMap = jsonHelper.extractObjectMapByPath(response.getBody(), "$.response.data[0].versionCheck");

            if (nullable.isNull()) {
                assertNull("Value by field: " + field + " is not null for " + userDeviceData, stringObjectMap.get(field));
            } else {
                assertEquals("Value by field: " + field + " differs from expected for " + userDeviceData, nullable.value(), stringObjectMap.get(field));
            }
        }
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    private DeviceType getDeviceType(UserDeviceData userDeviceData) {
        return DeviceTypeCache.getDeviceTypeMapNameAsKeyAndDeviceTypeValue().get(userDeviceData.getDeviceType());
    }
}
