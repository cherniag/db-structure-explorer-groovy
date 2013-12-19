package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import org.junit.Test;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.xpath;

public class CommonControllerTestIT extends AbstractControllerTestIT{

    @Test
    public void shouldThrowActivationStatusException_onAccountCheck() throws Exception {
    	String userName = "+447111111114";
		String apiVersion = "3.9";
		String communityName = "o2";
		String communityUrl = "o2";
		String timestamp = "2011_12_26_07_04_23";
		String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
		String userToken = Utils.createTimestampToken(storedToken, timestamp);
		
		User user = userService.findByNameAndCommunity(userName, communityName);
		user.setActivationStatus(ActivationStatus.REGISTERED);
		userService.updateUser(user);
		
		mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/ACC_CHECK")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isForbidden()).andDo(print()).andExpect(xpath("/response/errorMessage/errorCode").number(604d));
    }

    @Test
    public void shouldInvalidPhoneNumberGivenVersionMore5() throws Exception {
        String userName = "b88106713409e92622461a876abcd74a444";
        String phone = "+44711111xxxxx";
        String apiVersion = "5.1";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(
                post("/some_key/"+communityUrl+"/"+apiVersion+"/PHONE_NUMBER")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("PHONE", phone)
        ).andExpect(status().isBadRequest())
                .andExpect(xpath("/response/errorMessage/errorCode").number(601d));
    }

    @Test
    public void shouldInvalidPhoneNumberGivenVersionLessOrEqual5() throws Exception {
        String userName = "b88106713409e92622461a876abcd74a444";
        String phone = "+44711111xxxxx";
        String apiVersion = "4.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(
                post("/some_key/"+communityUrl+"/"+apiVersion+"/PHONE_NUMBER")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("PHONE", phone)
        ).andExpect(status().isOk())
                .andExpect(xpath("/response/errorMessage/errorCode").number(601d));
    }
}