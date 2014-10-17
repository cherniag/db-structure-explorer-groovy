package mobi.nowtechnologies.applicationtests.features.news;

import cucumber.api.Transform;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mobi.nowtechnologies.applicationtests.features.common.client.PartnerDeviceSet;
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer;
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word;
import mobi.nowtechnologies.applicationtests.features.common.transformers.util.NullableString;
import mobi.nowtechnologies.applicationtests.features.common.transformers.util.NullableStringTransformer;
import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.db.UserDbService;
import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.MessageRepository;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.MessageType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Author: Gennadii Cherniaiev
 * Date: 10/10/2014
 */
@Component
public class GetNewsFeature {
    @Resource
    private PartnerDeviceSet partnerDeviceSet;

    @Resource
    private UserDeviceDataService userDeviceDataService;

    @Resource
    private UserDbService userDbService;

    @Resource
    private MessageRepository messageRepository;

    @Resource
    private CommunityRepository communityRepository;

    private List<UserDeviceData> userDeviceDatas = new ArrayList<UserDeviceData>();

    private Map<UserDeviceData, NewsDetailDto[]> newsResponses = new HashMap<UserDeviceData, NewsDetailDto[]>();
    private String community;
    private long publishTimeMillis = System.currentTimeMillis();


    @Given("^Activated via OTAC user with (.+) using (.+) format for (.+) and (\\w+) community$")
    public void given(@Transform(DictionaryTransformer.class) Word deviceTypes,
                      @Transform(DictionaryTransformer.class) Word formats,
                      @Transform(DictionaryTransformer.class) Word versions,
                      String community){
        // already activated if not empty
        if(!userDeviceDatas.isEmpty()) {
            return;
        }

        this.community = community;
        this.userDeviceDatas.addAll(userDeviceDataService.table(versions.list(), community, deviceTypes.list(), formats.set(RequestFormat.class)));
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            partnerDeviceSet.signUpAndActivate(userDeviceData);

            PhoneState phoneState = partnerDeviceSet.getPhoneState(userDeviceData);

            User user = findUserInDatabase(userDeviceData, phoneState);
            assertEquals("User [" + user.getUserName() + "] for device data " + userDeviceData + " should be activated", ActivationStatus.ACTIVATED, user.getActivationStatus());
        }
    }

    @After
    public void after(){
        newsResponses.clear();
        messageRepository.deleteAll();
    }

    @When("^News message with type '(.+)', title '(.+)' and text '(.+)' exists in database$")
    public void whenNewsMessageExists(MessageType messageType,
                                      @Transform(NullableStringTransformer.class) NullableString title,
                                      @Transform(NullableStringTransformer.class) NullableString body){
        Community community = communityRepository.findByRewriteUrlParameter(this.community);
        Message message = new Message();
        message.setPublishTimeMillis(publishTimeMillis);
        message.setActivated(true);
        message.setCommunity(community);
        message.setTitle(title.value());
        message.setBody(body.value());
        message.setMessageType(messageType);
        messageRepository.saveAndFlush(message);
    }

    @And("^User invokes get news command$")
    public void andUserMakesGetNewsCall(){
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            NewsDetailDto[] news = partnerDeviceSet.getNews(userDeviceData);
            newsResponses.put(userDeviceData, news);
        }
    }

    @Then("^response has (\\d+) http response code$")
    public void thenResponse(final int httpResponseCode) {

    }

    @And("^news response should contains (\\d+) news message$")
    public void andNewsResponseShouldContainsMessages(int messageCount){
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            NewsDetailDto[] news = newsResponses.get(userDeviceData);

            assertEquals(getErrorMessage(userDeviceData), messageCount, news.length);
        }
    }

    @And("^news message should have the same publish time$")
    public void andNewsMessagesShouldHaveTheSamePublishTime(){
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            NewsDetailDto[] news = newsResponses.get(userDeviceData);

            assertEquals(getErrorMessage(userDeviceData),
                    publishTimeMillis,
                    news[0].getTimestampMilis());
        }
    }

    @And("^news message should have message type '(.+)'$")
    public void andNewsMessagesShouldHaveMessageType(MessageType messageType){
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            NewsDetailDto[] news = newsResponses.get(userDeviceData);

            assertEquals(getErrorMessage(userDeviceData),
                    messageType,
                    news[0].getMessageType());
        }
    }

    @And("^news message should have detail '(.+)'$")
    public void andNewsMessagesShouldHaveBody(String detail){
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            NewsDetailDto[] news = newsResponses.get(userDeviceData);

            assertEquals(getErrorMessage(userDeviceData),
                    detail,
                    news[0].getDetail());
        }
    }

    @And("^news message should have body '(.+)'$")
    public void andNewsMessagesShouldHaveDetail(String body){
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            NewsDetailDto[] news = newsResponses.get(userDeviceData);

            assertEquals(getErrorMessage(userDeviceData),
                    body,
                    news[0].getBody());
        }
    }

    private String getErrorMessage(UserDeviceData userDeviceData) {
        return "Failed for " + userDeviceData;
    }


    private User findUserInDatabase(UserDeviceData userDeviceData, PhoneState phoneState) {
        return userDbService.findUser(phoneState, userDeviceData);
    }
}
