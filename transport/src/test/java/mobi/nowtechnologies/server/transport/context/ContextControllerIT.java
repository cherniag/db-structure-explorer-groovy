package mobi.nowtechnologies.server.transport.context;

import mobi.nowtechnologies.server.security.bind.annotation.AuthenticatedUser;
import mobi.nowtechnologies.server.transport.controller.AbstractControllerTestIT;
import static mobi.nowtechnologies.server.shared.Utils.createTimestampToken;

import java.util.Date;

import org.springframework.http.MediaType;

import org.junit.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ContextControllerIT extends AbstractControllerTestIT {

    @Test
    public void checkGetContext_LatestVersion() throws Exception {
        String apiVersion = LATEST_SERVER_API_VERSION;

        String communityUrl = "hl_uk";
        String userName = "test@ukr.net";
        String timestamp = "" + new Date().getTime();
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = createTimestampToken(storedToken, timestamp);

        mockMvc.perform(get("/" + communityUrl + "/" + apiVersion + "/CONTEXT").
                                                                                   accept(MediaType.APPLICATION_JSON).
                                                                                   param(AuthenticatedUser.USER_NAME, userName).
                                                                                   param(AuthenticatedUser.USER_TOKEN, userToken).
                                                                                   param(AuthenticatedUser.TIMESTAMP, timestamp)).
                   andDo(print()).
                   andExpect(status().isOk()).
                   andExpect(content().contentType(MediaType.APPLICATION_JSON)).
                   andExpect(jsonPath("$.context").exists()).
                   andExpect(jsonPath("$.context.referrals").exists());
    }

    @Test
    public void checkGetContext_67_referralsOff() throws Exception {
        String apiVersion = "6.7";

        String communityUrl = "hl_uk";
        String userName = "test@ukr.net";
        String timestamp = "" + new Date().getTime();
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = createTimestampToken(storedToken, timestamp);

        mockMvc.perform(get("/" + communityUrl + "/" + apiVersion + "/CONTEXT").
                                                                                   accept(MediaType.APPLICATION_JSON).
                                                                                   param(AuthenticatedUser.USER_NAME, userName).
                                                                                   param(AuthenticatedUser.USER_TOKEN, userToken).
                                                                                   param(AuthenticatedUser.TIMESTAMP, timestamp)).
                   andDo(print()).
                   andExpect(status().isOk()).
                   andExpect(content().contentType(MediaType.APPLICATION_JSON)).
                   andExpect(jsonPath("$.context").exists()).
                   andExpect(jsonPath("$.context.referrals").exists()).
                   andExpect(jsonPath("$.context.referrals.required").value(-1)).
                   andExpect(jsonPath("$.context.referrals.activated").value(-1)).
                   andExpect(jsonPath("$.context.playlists").doesNotExist());
    }


    @Test
    public void checkGetContext_67_referralsOn() throws Exception {
        String apiVersion = "6.7";

        String communityUrl = "hl_uk";
        String userName = "zam@ukr.net";
        String timestamp = "" + new Date().getTime();
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = createTimestampToken(storedToken, timestamp);

        mockMvc.perform(get("/" + communityUrl + "/" + apiVersion + "/CONTEXT").
                                                                                   accept(MediaType.APPLICATION_JSON).
                                                                                   param(AuthenticatedUser.USER_NAME, userName).
                                                                                   param(AuthenticatedUser.USER_TOKEN, userToken).
                                                                                   param(AuthenticatedUser.TIMESTAMP, timestamp)).
                   andDo(print()).
                   andExpect(status().isOk()).
                   andExpect(content().contentType(MediaType.APPLICATION_JSON)).
                   andExpect(jsonPath("$.context").exists()).
                   andExpect(jsonPath("$.context.referrals").exists()).
                   andExpect(jsonPath("$.context.referrals.required").value(5)).
                   andExpect(jsonPath("$.context.referrals.activated").value(0)).
                   andExpect(jsonPath("$.context.playlists").exists());
    }

    @Test
    public void checkGetContext_Latest() throws Exception {
        String apiVersion = "6.8";

        String communityUrl = "hl_uk";
        String userName = "test@ukr.net";
        String timestamp = "" + new Date().getTime();
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = createTimestampToken(storedToken, timestamp);

        mockMvc.perform(get("/" + communityUrl + "/" + apiVersion + "/CONTEXT").
                                                                                   accept(MediaType.APPLICATION_JSON).
                                                                                   param(AuthenticatedUser.USER_NAME, userName).
                                                                                   param(AuthenticatedUser.USER_TOKEN, userToken).
                                                                                   param(AuthenticatedUser.TIMESTAMP, timestamp)).
                   andDo(print()).
                   andExpect(status().isOk()).
                   andExpect(content().contentType(MediaType.APPLICATION_JSON)).
                   andExpect(jsonPath("$.context").exists()).
                   andExpect(jsonPath("$.context.referrals").exists()).
                   andExpect(jsonPath("$.context.referrals.required").value(5)).
                   andExpect(jsonPath("$.context.referrals.activated").value(0)).
                   andExpect(jsonPath("$.context.playlists").exists());
    }


    /**
     * This is to test re-factoring introduced by {@link mobi.nowtechnologies.server.security.bind.annotation.AuthenticatedUser}
     *
     * @throws Exception
     */
    @Test
    public void checkHttpBadRequest_OnMissingRequiredParam() throws Exception {
        String apiVersion = "6.7";

        String communityUrl = "hl_uk";
        String timestamp = "" + new Date().getTime();
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = createTimestampToken(storedToken, timestamp);

        mockMvc.perform(get("/" + communityUrl + "/" + apiVersion + "/CONTEXT").
                                                                                   accept(MediaType.APPLICATION_JSON).
                                                                                   param(AuthenticatedUser.USER_NAME, "").
                                                                                   param(AuthenticatedUser.USER_TOKEN, userToken).
                                                                                   param(AuthenticatedUser.TIMESTAMP, timestamp)).
                   andDo(print()).
                   andExpect(status().isBadRequest());
    }

}