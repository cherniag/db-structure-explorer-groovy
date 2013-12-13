package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.ResultActions;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

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
		
		ResultActions resultActions = mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/ACC_CHECK")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk());
		
		MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
		String resultXml = aHttpServletResponse.getContentAsString();
		
        assertTrue(resultXml.contains("<errorCode>604</errorCode>"));
    }
}