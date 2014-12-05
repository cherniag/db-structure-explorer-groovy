package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.security.bind.annotation.AuthenticatedUser;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.Date;

import static mobi.nowtechnologies.server.shared.Utils.createTimestampToken;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserContextControllerIT extends AbstractControllerTestIT {

    @Test
    public void checkGetContext_LatestVersion() throws Exception {
        String apiVersion = "6.7";

        String communityUrl = "hl_uk";
        String userName = "test@ukr.net";
        String timestamp = "" + new Date().getTime();
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = createTimestampToken(storedToken, timestamp);

        mockMvc.perform(
                get("/" + communityUrl + "/" + apiVersion + "/CONTEXT").
                        accept(MediaType.APPLICATION_JSON).
                        param(AuthenticatedUser.USER_NAME, userName).
                        param(AuthenticatedUser.USER_TOKEN, userToken).
                        param(AuthenticatedUser.TIMESTAMP, timestamp)).
                andExpect(status().isOk()).
                andExpect(content().contentType(MediaType.APPLICATION_JSON)).
                andExpect(jsonPath("$.context").exists()).
                andExpect(jsonPath("$.context.referrals").exists()).
                andExpect(jsonPath("$.context.referrals.required").value(5)).
                andExpect(jsonPath("$.context.referrals.activated").value(0));
    }

    /**
     * This is to test re-factoring introduced by
     * {@link mobi.nowtechnologies.server.security.bind.annotation.AuthenticatedUser}
     * @throws Exception
     */
    @Test
    public void checkHttp400_OnMissingRequiredParam() throws Exception {
        String apiVersion = "6.7";

        String communityUrl = "hl_uk";
        String timestamp = "" + new Date().getTime();
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = createTimestampToken(storedToken, timestamp);

        mockMvc.perform(
                get("/" + communityUrl + "/" + apiVersion + "/CONTEXT").
                        accept(MediaType.APPLICATION_JSON).
                        param(AuthenticatedUser.USER_NAME, "").
                        param(AuthenticatedUser.USER_TOKEN, userToken).
                        param(AuthenticatedUser.TIMESTAMP, timestamp)).
                andExpect(status().isBadRequest());
    }

}