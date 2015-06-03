package mobi.nowtechnologies.server.transport.referrals;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.referral.Referral;
import mobi.nowtechnologies.server.persistence.domain.referral.ReferralState;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.ReferralRepository;
import mobi.nowtechnologies.server.security.bind.annotation.AuthenticatedUser;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.transport.controller.AbstractControllerTestIT;
import static mobi.nowtechnologies.server.shared.Utils.createTimestampToken;

import javax.annotation.Resource;

import java.util.Date;

import org.springframework.http.MediaType;

import org.junit.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ReferralControllerIT extends AbstractControllerTestIT {

    @Resource
    private ReferralRepository referralRepository;

    @Resource
    private CommunityRepository communityRepository;

    @Test
    public void checkSaveReferrals_LatestVersion() throws Exception {
        String apiVersion = LATEST_SERVER_API_VERSION;

        String communityUrl = "hl_uk";
        String userName = "test@ukr.net";
        String timestamp = "" + new Date().getTime();
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = createTimestampToken(storedToken, timestamp);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/REFERRALS").contentType(MediaType.APPLICATION_JSON).param(AuthenticatedUser.USER_NAME, userName)
                                                                                  .param(AuthenticatedUser.USER_TOKEN, userToken).param(AuthenticatedUser.TIMESTAMP, timestamp).content("[{" +
                                                                                                                                                                                        "   \"source\" : \"FACEBOOK\", \"id\" : \"facebook-12345\"" +
                                                                                                                                                                                        " }, {" +
                                                                                                                                                                                        "   \"source\" : \"EMAIL\", \"id\" : \"jon.smith@email.com\"" +
                                                                                                                                                                                        "}]"))
               .andExpect(status().isOk());

        User user = userRepository.findByUserNameAndCommunityUrl(userName, communityUrl);
        Community community = communityRepository.findByRewriteUrlParameter(communityUrl);

        Referral referral1 = referralRepository.findByContactAndCommunityId("facebook-12345", community.getId());
        assertEquals(ProviderType.FACEBOOK, referral1.getProviderType());
        assertEquals(user.getId(), referral1.getUserId());
        assertEquals(ReferralState.PENDING, referral1.getState());

        Referral referral2 = referralRepository.findByContactAndCommunityId("jon.smith@email.com", community.getId());
        assertEquals(ProviderType.EMAIL, referral2.getProviderType());
        assertEquals(user.getId(), referral2.getUserId());
        assertEquals(ReferralState.PENDING, referral2.getState());
    }

}