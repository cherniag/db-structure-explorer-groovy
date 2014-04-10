package mobi.nowtechnologies.server.user.criteria;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.SubscriptionCampaignRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/8/2014
 */
@RunWith(MockitoJUnitRunner.class)
public class UserInCampaignTableMatcherTest {

    @Mock
    private SubscriptionCampaignRepository subscriptionCampaignRepository;

    @InjectMocks
    private IsInCampaignTableUserMatcher isInCampaignTableUserMatcher;

    @Before
    public void setUp() throws Exception {
        when(subscriptionCampaignRepository.getCountForMobile("+44123456789")).thenReturn(1L);
        when(subscriptionCampaignRepository.getCountForMobile("+44000000000")).thenReturn(0L);
    }

    @Test
    public void testMatchContainingUser() throws Exception {
        User user = new User();
        user.setMobile("+44123456789");
        boolean match = isInCampaignTableUserMatcher.match(user);
        assertThat(match, is(true));
    }

    @Test
    public void testMatchNotContainingUser() throws Exception {
        User user = new User();
        user.setMobile("+44000000000");
        boolean match = isInCampaignTableUserMatcher.match(user);
        assertThat(match, is(false));
    }

    @Test
    public void testMatchUserWithNullMobile() throws Exception {
        User user = new User();
        user.setMobile(null);
        boolean match = isInCampaignTableUserMatcher.match(user);
        assertThat(match, is(false));
    }
}
