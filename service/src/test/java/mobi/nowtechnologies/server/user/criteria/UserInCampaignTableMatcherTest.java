package mobi.nowtechnologies.server.user.criteria;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.SubscriptionCampaignRepository;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.mockito.Mockito.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Gennadii Cherniaiev Date: 4/8/2014
 */
@RunWith(MockitoJUnitRunner.class)
public class UserInCampaignTableMatcherTest {

    @Mock
    private SubscriptionCampaignRepository subscriptionCampaignRepository;

    @InjectMocks
    private IsInCampaignTableUserMatcher isInCampaignTableUserMatcher = new IsInCampaignTableUserMatcher(subscriptionCampaignRepository, "campaignId");

    @Before
    public void setUp() throws Exception {
        when(subscriptionCampaignRepository.countForMobile("+44123456789", "campaignId")).thenReturn(1L);
        when(subscriptionCampaignRepository.countForMobile("+44000000000", "campaignId")).thenReturn(0L);
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
