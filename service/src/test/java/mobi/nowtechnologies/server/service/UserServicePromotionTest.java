package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.user.rules.AutoOptInRuleService;
import mobi.nowtechnologies.server.user.rules.RuleResult;
import mobi.nowtechnologies.server.user.rules.RuleServiceSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static mobi.nowtechnologies.server.user.rules.AutoOptInRuleService.AutoOptInTriggerType.EMPTY;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/15/2014
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServicePromotionTest {

    @Mock
    private PromotionService promotionServiceMock;
    @Mock
    private PaymentDetailsService paymentDetailsServiceMock;
    @Mock
    private EntityService entityServiceMock;
    @InjectMocks
    private UserService userService;

    @Mock
    private RuleServiceSupport ruleServiceSupportMock;

    @Before
    public void setUp() throws Exception {
        AutoOptInRuleService autoOptInRuleService = new AutoOptInRuleService();
        autoOptInRuleService.setRuleServiceSupport(ruleServiceSupportMock);
        userService.setAutoOptInRuleService(autoOptInRuleService);
    }

    @Test
    public void testActivateVideoAudioFreeTrialAndAutoOptIn() throws Exception {
        User user = mock(User.class);
        when(user.isSubjectToAutoOptIn()).thenReturn(true);
        when(entityServiceMock.findById(eq(User.class), anyInt())).thenReturn(user);
        when(ruleServiceSupportMock.fireRules(eq(EMPTY), any(User.class))).thenReturn(RuleResult.FAIL_RESULT);

        userService.activateVideoAudioFreeTrialAndAutoOptIn(user);

        verify(ruleServiceSupportMock, times(1)).fireRules(eq(EMPTY), any(User.class));
        verify(user, times(1)).isSubjectToAutoOptIn();
        verify(paymentDetailsServiceMock, times(1)).createDefaultO2PsmsPaymentDetails(any(User.class));
    }

}
