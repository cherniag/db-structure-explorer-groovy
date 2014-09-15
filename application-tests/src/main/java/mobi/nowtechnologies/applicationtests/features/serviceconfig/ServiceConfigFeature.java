package mobi.nowtechnologies.applicationtests.features.serviceconfig;

import cucumber.api.Transform;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mobi.nowtechnologies.applicationtests.features.common.dictionary.DictionaryTransformer;
import mobi.nowtechnologies.applicationtests.features.common.dictionary.Word;
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

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

    private List<UserDeviceData> userDeviceDatas;
    private Map<UserDeviceData, String> randoms = new HashMap<UserDeviceData, String>();

    @Given("^Mobile client makes Service Config call using JSON and XML formats for (.+) and (.+) and (.+)$")
    public void givenOldFormat(@Transform(DictionaryTransformer.class) Word devices,
                               @Transform(DictionaryTransformer.class) Word communities,
                               @Transform(DictionaryTransformer.class) Word versions) {
        userDeviceDatas = userDeviceDataService.table(versions.list(), communities.set(), devices.set());
    }

    @When("^(.+) header is in old format \"(.+)\"$")
    public void whenUserAgent(String headerName, String headerValue) {
        serviceConfigHttpService.setHeader(headerName, headerValue);
    }

    @Then("^response has (\\d+) http response code$")
    public void thenResponse400(final int responseExpected) {
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            ResponseEntity<String> stringResponseEntity = serviceConfigHttpService.serviceConfig(userDeviceData);
            int responseFromServer = stringResponseEntity.getStatusCode().value();

            assertEquals(
                    "Code from server: " + responseFromServer + " differs from expected: " + responseExpected + " for " + userDeviceData,
                    responseExpected,
                    responseFromServer
            );
        }
    }
    // When service config data is set to 'SUGGESTED_UPDATE' for version '1.3.3', 'musicqubed-{random}' application, 'service.config.some.message' message, 'http://example.com' link
    @When("^service config data is set to '(.+)' for version '(.+)', '(.+)' application, '(.+)' message, '(.+)' link$")
    public void whenServiceConfig(VersionCheckStatus status,
                                  @Transform(ClientVersionTransformer.class) ClientVersion clientVersion,
                                  String application,
                                  String code,
                                  String link) {
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            randoms.put(userDeviceData, UUID.randomUUID().toString());

            String newApplicationName = application.replace("{random}", randoms.get(userDeviceData));
            VersionMessage versionMessage = versionMessageRepository.saveAndFlush(new VersionMessage(code, link));
            Community c = communityRepository.findByRewriteUrlParameter(userDeviceData.getCommunityUrl());
            DeviceType deviceType = getDeviceType(userDeviceData);

            versionCheckRepository.saveAndFlush(new VersionCheck(deviceType, c, versionMessage, status, newApplicationName, clientVersion));
        }
    }

    @And("^(.+) header is in new format \"(.+)\"$")
    public void whenUserAgentIsNotDefault(String headerName, String headerValue) {
        for (Map.Entry<UserDeviceData, String> entry : randoms.entrySet()) {
            final String random = entry.getValue();

            UserDeviceData userDeviceData = entry.getKey();


            Community c = communityRepository.findByRewriteUrlParameter(userDeviceData.getCommunityUrl());
            String interpolatedHeaderValue = headerValue
                    .replace("{random}", random)
                    .replace("{platform}", getDeviceType(userDeviceData).getName())
                    .replace("{community}", c.getName());

            getLogger().info("Sending " + headerName + ":" + interpolatedHeaderValue);

            serviceConfigHttpService.setHeader(headerName, interpolatedHeaderValue);
            ResponseEntity<String> stringResponseEntity = serviceConfigHttpService.serviceConfig(userDeviceData);
        }
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    private DeviceType getDeviceType(UserDeviceData userDeviceData) {
        return DeviceTypeDao.getDeviceTypeMapNameAsKeyAndDeviceTypeValue().get(userDeviceData.getDeviceType());
    }


}
