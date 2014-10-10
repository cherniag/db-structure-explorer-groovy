package mobi.nowtechnologies.applicationtests.features.serviceconfig;

import cucumber.api.Transform;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer;
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word;
import mobi.nowtechnologies.applicationtests.features.common.transformers.util.NullableString;
import mobi.nowtechnologies.applicationtests.features.common.transformers.util.NullableStringTransformer;
import mobi.nowtechnologies.applicationtests.features.serviceconfig.helpers.ClientVersionTransformer;
import mobi.nowtechnologies.applicationtests.features.serviceconfig.helpers.ServiceConfigHttpService;
import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.JsonHelper;
import mobi.nowtechnologies.applicationtests.services.util.SimpleInterpolator;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.ErrorMessage;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.ClientVersion;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheck;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheckStatus;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionMessage;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.VersionCheckRepository;
import mobi.nowtechnologies.server.persistence.repository.VersionMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

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
    ServiceConfigHttpService serviceConfigHttpService;
    @Resource
    SimpleInterpolator interpolator;
    @Resource
    JsonHelper jsonHelper;

    private List<UserDeviceData> userDeviceDatas;
    private String applicationName;
    private Map<UserDeviceData, ResponseEntity<String>> successfulResponses = new HashMap<UserDeviceData, ResponseEntity<String>>();
    private Map<UserDeviceData, String> headers = new HashMap<UserDeviceData, String>();


    @Given("^Mobile client makes Service Config call using (.+) format for (.+) and (.+) and (.+)$")
    public void given(RequestFormat requestFormat,
                      @Transform(DictionaryTransformer.class) Word devices,
                       @Transform(DictionaryTransformer.class) Word communities,
                       @Transform(DictionaryTransformer.class) Word versions) {
        userDeviceDatas = userDeviceDataService.table(versions.list(), communities.set(), devices.set(), Collections.singleton(requestFormat));
        applicationName = UUID.randomUUID().toString();
    }

    @When("^(.+) header is in old format \"(.+)\"$")
    public void whenUserAgent(String headerName, String headerValue) {
        serviceConfigHttpService.setHeader(headerName, headerValue);
    }

    @Then("^response has (\\d+) http response code$")
    public void thenResponse(final int responseExpected) {
        successfulResponses.clear();

        for (UserDeviceData userDeviceData : userDeviceDatas) {
            String header = headers.get(userDeviceData);
            serviceConfigHttpService.setHeader("User-Agent", header);
            ResponseEntity<String> stringResponseEntity = serviceConfigHttpService.serviceConfig(userDeviceData);
            int responseFromServer = stringResponseEntity.getStatusCode().value();

            if(responseFromServer == 200) {
                // check later
                successfulResponses.put(userDeviceData, stringResponseEntity);
            }

            assertEquals(
                    "Code from server: " + responseFromServer + " differs from expected: " + responseExpected + " for " + userDeviceData,
                    responseExpected,
                    responseFromServer
            );
        }

        headers.clear();
    }

    @And("^error message is '(.+)'$")
    public void errorMessageIs(String message) {
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            ResponseEntity<String> stringResponseEntity = serviceConfigHttpService.serviceConfig(userDeviceData);
            ErrorMessage errorMessage = jsonHelper.extractObjectValueByPath(stringResponseEntity.getBody(), "$.response.data[0].errorMessage", ErrorMessage.class);

            assertEquals(
                    "Error message from server: " + message + " differs from expected: " + errorMessage.getMessage() + " for " + userDeviceData,
                    message,
                    errorMessage.getMessage()
            );
        }
    }

    @When("^service config data is set to '(.+)' for version '(.+)', '(.+)' application, '(.+)' message, '(.+)' link$")
    public void whenServiceConfig(VersionCheckStatus status,
                                  @Transform(ClientVersionTransformer.class) ClientVersion clientVersion,
                                  String application,
                                  String code,
                                  String link) {
        for (UserDeviceData userDeviceData : userDeviceDatas) {

            // prepare data in database
            String newApplicationName = interpolator.interpolate(application, Collections.<String, Object>singletonMap("random", applicationName));
            VersionMessage versionMessage = versionMessageRepository.saveAndFlush(new VersionMessage(code, link));
            Community c = communityRepository.findByRewriteUrlParameter(userDeviceData.getCommunityUrl());
            DeviceType deviceType = getDeviceType(userDeviceData);
            versionCheckRepository.saveAndFlush(new VersionCheck(deviceType, c, versionMessage, status, newApplicationName, clientVersion));
        }
    }

    @And("^(.+) header is in new format \"(.+)\"$")
    public void whenUserAgentIsNotDefault(String headerName, String headerValue) {
        for (UserDeviceData data : userDeviceDatas) {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("random", applicationName);
            model.put("platform", getDeviceType(data).getName());
            model.put("community", communityRepository.findByRewriteUrlParameter(data.getCommunityUrl()).getName());

            String interpolatedHeaderValue = interpolator.interpolate(headerValue, model);

            getLogger().info("Sending " + headerName + ":" + interpolatedHeaderValue);

            headers.put(data, interpolatedHeaderValue);
        }
    }

    @And("^json data is '(.+)'$")
    public void jsonData(String field) {
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            ResponseEntity<String> response = successfulResponses.get(userDeviceData);
            Map<String, Object> stringObjectMap = jsonHelper.extractObjectMapByPath(response.getBody(), "$.response.data[0]." + field);
            assertTrue("Empty map for: [" + userDeviceData + "]: " + stringObjectMap, stringObjectMap != null && !stringObjectMap.isEmpty());
        }
    }

    @And("^json field has '(.+)' set to '(.+)'$")
    public void jsonFieldHasValue(String field, @Transform(NullableStringTransformer.class) NullableString nullable) {
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            ResponseEntity<String> response = successfulResponses.get(userDeviceData);
            Map<String, Object> stringObjectMap = jsonHelper.extractObjectMapByPath(response.getBody(), "$.response.data[0].versionCheck");

            if(nullable.isNull()) {
                assertNull("Value by field: " + field + " is not null", stringObjectMap.get(field));
            } else {
                assertEquals("Value by field: " + field + " differs from expected", nullable.value(), stringObjectMap.get(field));
            }
        }
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    private DeviceType getDeviceType(UserDeviceData userDeviceData) {
        return DeviceTypeDao.getDeviceTypeMapNameAsKeyAndDeviceTypeValue().get(userDeviceData.getDeviceType());
    }
}
