package mobi.nowtechnologies.applicationtests.features.cached;

import cucumber.api.Transform;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet;
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer;
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word;
import mobi.nowtechnologies.applicationtests.services.DbMediaService;
import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService;
import mobi.nowtechnologies.applicationtests.services.device.domain.ApiVersions;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.streamzine.GetStreamzineHttpService;
import mobi.nowtechnologies.applicationtests.services.streamzine.PositionGenerator;
import mobi.nowtechnologies.applicationtests.services.streamzine.StreamzineUpdateCreator;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.StreamzineUpdateRepository;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@Component
public class StreamzineClientCacheFeature {
    @Resource
    UserDataCreator userDataCreator;
    @Resource
    StreamzineUpdateCreator streamzineUpdateCreator;
    @Resource
    GetStreamzineHttpService getStreamzineHttpService;
    @Resource
    UserDeviceDataService userDeviceDataService;
    @Resource
    CommunityRepository communityRepository;
    @Resource
    DbMediaService dbMediaService;
    @Resource
    StreamzineUpdateRepository streamzineUpdateRepository;

    @Resource
    MQAppClientDeviceSet deviceSet;

    private List<UserDeviceData> currentUserDevices;

    private Map<UserDeviceData, ResponseEntity<String>> responses = new HashMap<UserDeviceData, ResponseEntity<String>>();

    private Map<String, Update> updates = new HashMap<String, Update>();
    private PositionGenerator positionGenerator = new PositionGenerator();

    private boolean sendLastModifiedSince;
    private boolean timestampIsBigger;
    private String corruptedHeader;
    private Set<String> communities;

    //
    // Given and After
    //
    @Given("^First time user with device using (.+) formats for (.+) starting from (.+) and (.+) and for (.+) available$")
    public void firstTimeUserUsingFormatAfter(@Transform(DictionaryTransformer.class) Word requestFormats,
                                        @Transform(DictionaryTransformer.class) Word versions,
                                        String version,
                                        @Transform(DictionaryTransformer.class) Word communities,
                                        @Transform(DictionaryTransformer.class) Word devices) throws Throwable {
        List<String> cacheVersions = ApiVersions.from(versions.list()).above(version);

        initUserDatas(requestFormats, communities, devices, cacheVersions);
    }

    @Given("^First time user with device using (.+) formats for (.+) starting before (.+) and (.+) and for (.+) available$")
    public void firstTimeUserUsingFormatBefore(@Transform(DictionaryTransformer.class) Word requestFormats,
                                         @Transform(DictionaryTransformer.class) Word versions,
                                         String version,
                                         @Transform(DictionaryTransformer.class) Word communities,
                                         @Transform(DictionaryTransformer.class) Word devices) throws Throwable {
        List<String> cacheVersions = ApiVersions.from(versions.list()).bellow(version);

        initUserDatas(requestFormats, communities, devices, cacheVersions);
    }

    private void initUserDatas(Word requestFormats, Word communities, Word devices, List<String> cacheVersions) {
        this.communities = communities.set();
        currentUserDevices = userDeviceDataService.table(cacheVersions, this.communities, devices.set(), requestFormats.set(RequestFormat.class));
        for (UserDeviceData data : currentUserDevices) {
            deviceSet.singup(data);
            deviceSet.loginUsingFacebook(data);
        }
        positionGenerator.init(currentUserDevices);
    }


    @After
    public void cleanDevicesSet() {
        responses.clear();
        deviceSet.cleanup();
        communities.clear();

        sendLastModifiedSince = false;
        timestampIsBigger = false;
        corruptedHeader = null;
    }

    //
    // Successful Scenario
    //
    @When("^update is prepared$")
    public void updateIsPrepared() {
        int shiftSeconds = 0;

        for (String community : communities) {
            Update saved = streamzineUpdateCreator.create(community, shiftSeconds++);
            // needed to get again to avoid potential issues with dates (milliseconds/seconds) cut
            Update getAfterSave = streamzineUpdateRepository.findOne(saved.getId());
            updates.put(community, getAfterSave);
        }
    }

    @And("^user does not send 'If-Modified-Since' header$")
    public void userDoesNotSend() {
        sendLastModifiedSince = false;
    }

    @And("^user sends 'If-Modified-Since' header and it is bigger than update timestamp$")
    public void userSendsAndIsBigger() {
        sendLastModifiedSince = true;
        timestampIsBigger = true;
    }

    @And("^user sends 'If-Modified-Since' header and it is less than update timestamp$")
    public void userSendsAndIsLess() {
        sendLastModifiedSince = true;
        timestampIsBigger = false;
    }


    @And("^user sends 'If-Modified-Since' header and it has '(.+)' value$")
    public void userSendsAndIsCorrupted(String corruptedHeader) {
        sendLastModifiedSince = true;
        this.corruptedHeader = corruptedHeader;
    }

    @When("^user invokes get streamzine command$")
    public void getStreamzineForVersion() {
        for (UserDeviceData data : currentUserDevices) {
            if(sendLastModifiedSince) {
                if(corruptedHeader != null) {
                    ResponseEntity<String> response = deviceSet.getStreamzineAnsSendIfModifiedSince(data, corruptedHeader);
                    responses.put(data, response);
                } else {
                    Update saved = updates.get(data.getCommunityUrl());
                    int delta = (timestampIsBigger) ? 1 : -1;
                    final long ifModifiedSince = DateUtils.addSeconds(saved.getDate(), delta * 10).getTime();
                    ResponseEntity<String> response = deviceSet.getStreamzineAnsSendIfModifiedSince(data, ifModifiedSince);
                    responses.put(data, response);
                }
            } else {
                ResponseEntity<String> response = deviceSet.getStreamzineAnsSendIfModifiedSince(data);
                responses.put(data, response);
            }
        }
    }

    @When("^user invokes get streamzine command for old clients$")
    public void getStreamzineForVersionOld() {
        for (UserDeviceData data : currentUserDevices) {
            if(sendLastModifiedSince) {
                Update saved = updates.get(data.getCommunityUrl());
                int delta = (timestampIsBigger) ? 1 : -1;
                final long ifModifiedSince = DateUtils.addSeconds(saved.getDate(), delta * 10).getTime();
                ResponseEntity<String> response = deviceSet.getStreamzineAnsSendIfModifiedSinceOld(data, ifModifiedSince);
                responses.put(data, response);
            } else {
                ResponseEntity<String> response = deviceSet.getStreamzineAnsSendIfModifiedSinceOld(data);
                responses.put(data, response);
            }
        }
    }

    @Then("^response has (\\d+) http response code$")
    public void thenResponse(final int httpCode) {
        for (UserDeviceData data : currentUserDevices) {
            assertEquals(
                    getErrorMessage(data),
                    httpCode,
                    responses.get(data).getStatusCode().value()
            );
        }
    }

    @Then("^update time is the same$")
    public void updateTime() {
        for (UserDeviceData data : currentUserDevices) {
/*            assertEquals(
                    getErrorMessage(data),
                    httpCode,
                    responses.get(data).getStatusCode().value()
            );*/
        }
    }

    //
    // Helpers
    //
    private String getErrorMessage(UserDeviceData data) {
        return "Failed to check for " + data;
    }

    Logger logger() {
        return LoggerFactory.getLogger(getClass());
    }

}
