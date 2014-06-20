package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.log4j.InMemoryEventAppender;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.service.exception.ActivationStatusException;
import mobi.nowtechnologies.server.service.exception.InvalidPhoneNumberException;
import mobi.nowtechnologies.server.service.exception.LimitPhoneNumberValidationException;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static mobi.nowtechnologies.server.persistence.domain.Community.HL_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.persistence.domain.Community.O2_COMMUNITY_REWRITE_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

/**
 * Created by Oleg Artomov on 6/20/2014.
 */
public class ExceptionHandlingControllerIT extends AbstractControllerTestIT {
    @Resource
    private CommunityRepository communityRepository;
    @Resource
    private UserGroupRepository userGroupRepository;

    private InMemoryEventAppender inMemoryEventAppender = new InMemoryEventAppender();

    @After
    public void onComplete() {
        Logger.getRootLogger().removeAppender(inMemoryEventAppender);
    }

    @Before
    public void onStart() throws Exception {
        Logger.getRootLogger().addAppender(inMemoryEventAppender);
    }

    @Test
    @Transactional
    public void testValidateInvalidNumberException() throws Exception {
        String userName = "b88106713409e92622461a876abcd74b444";
        String apiVersion = "4.0";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        resetMobile(userName);

        mockMvc.perform(
                post("/somekey/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk()).andExpect(xpath("/response/errorMessage/errorCode").string("601"));
        validateLoggingForClass(PhoneNumberController.class, InvalidPhoneNumberException.class, 0, 1, 1);
    }

    @Test
    public void testGetChartWhenUserInInvalidState() throws Exception {
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "5.2";
        String communityUrl = "hl_uk";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        Community hlCommunity = communityRepository.findByRewriteUrlParameter(HL_COMMUNITY_REWRITE_URL);
        Community o2Community = communityRepository.findByRewriteUrlParameter(O2_COMMUNITY_REWRITE_URL);
        UserGroup hlUserGroup = userGroupRepository.findByCommunity(hlCommunity);
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, o2Community);
        user.setUserGroup(hlUserGroup);
        user.setActivationStatus(ActivationStatus.PENDING_ACTIVATION);
        userRepository.saveAndFlush(user);
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
                        .param("APP_VERSION", apiVersion)
                        .param("API_VERSION", apiVersion)
                        .param("COMMUNITY_NAME", apiVersion)
        );
        validateLoggingForClass(GetChartController.class, ActivationStatusException.class, 0, 1, 1);
    }

    @Test
    public void testValidateLogsForLimitPhoneException() throws Exception {
        String userName = "b88106713409e92622461a876abcd74a444";
        String phone = "+447111111113";
        String apiVersion = "4.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        o2ProviderServiceSpy.setLimitValidatePhoneNumber(-1);
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/PHONE_NUMBER.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("PHONE", phone)
        );
        validateLoggingForClass(PhoneNumberController.class, LimitPhoneNumberValidationException.class, 0, 1, 1);
    }

    private void validateLoggingForClass(Class loggerClass, Class throwableClass, int expectedForCritical, int expectedForWarn, int totalCountWithStackTrace) {
        assertEquals(expectedForCritical, inMemoryEventAppender.countOfErrorsWithStackTraceForLogger(loggerClass));
        assertEquals(expectedForWarn, inMemoryEventAppender.countOfWarnWithStackTraceForLogger(loggerClass));
        assertEquals(totalCountWithStackTrace, inMemoryEventAppender.totalCountOfMessagesWithStackTraceForException(throwableClass));
    }

    private void resetMobile(String userName) {
        User user = userService.findByName(userName);
        user.setMobile(null);

        userService.updateUser(user);
        user = userService.findByName(userName);
        assertTrue(StringUtils.isEmpty(user.getMobile()));
    }

}
