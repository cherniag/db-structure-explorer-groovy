package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.AutoOptInExemptPhoneNumberRepository;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService;
import mobi.nowtechnologies.server.user.rules.RuleResult;
import mobi.nowtechnologies.server.user.rules.RuleServiceSupport;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService.AutoOptInTriggerType.ALL;
import static org.mockito.Mockito.*;

public class AccountCheckDTOAsmTest {
    @Mock
    private AutoOptInExemptPhoneNumberRepository autoOptInExemptPhoneNumberRepository;
    @Mock
    private RuleServiceSupport ruleServiceSupport;

    private AutoOptInRuleService autoOptInRuleService;

    @InjectMocks
    private AccountCheckDTOAsm accountCheckDTOAsm;

    @Mock
    private AutoOptInExemptPhoneNumber autoOptInExemptPhoneNumber;
    @Mock
    private User user;
    @Mock
    private UserGroup userGroup;
    @Mock
    private Chart chart;
    @Mock
    private DrmPolicy drmPolicy;
    @Mock
    private DrmType drmType;
    @Mock
    private UserStatus userStatus;
    @Mock
    private DeviceType deviceType;
    private String mobile = "+447111111111";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(user.getUserGroup()).thenReturn(userGroup);
        when(user.getStatus()).thenReturn(userStatus);
        when(user.getDeviceType()).thenReturn(deviceType);
        when(user.getMobile()).thenReturn(mobile);
        when(userGroup.getChart()).thenReturn(chart);
        when(userGroup.getDrmPolicy()).thenReturn(drmPolicy);
        when(drmPolicy.getDrmType()).thenReturn(drmType);

        autoOptInRuleService = new AutoOptInRuleService(){
            @Override
            public RuleServiceSupport<AutoOptInTriggerType> getRuleServiceSupport() {
                return ruleServiceSupport;
            }
        };
        accountCheckDTOAsm.setAutoOptInRuleService(autoOptInRuleService);
    }

    @Test
    public void testToAccountCheckDTOWhenUserIsInDatabase() throws Exception {
        when(autoOptInExemptPhoneNumberRepository.findOne(mobile)).thenReturn(autoOptInExemptPhoneNumber);

        AccountCheckDTO dto = accountCheckDTOAsm.toAccountCheckDTO(user, "remember-me-token", null, false, false, false);
        verify(ruleServiceSupport, times(0)).fireRules(eq(ALL), any(User.class));

        assertFalse(dto.subjectToAutoOptIn);
    }

    @Test
    public void testToAccCheckDTOForNotExemptAndNotCampaignUser() throws Exception {
        when(autoOptInExemptPhoneNumberRepository.findOne(mobile)).thenReturn(null);
        when(ruleServiceSupport.fireRules(eq(ALL), any(User.class))).thenReturn(RuleResult.FAIL_RESULT);

        boolean isSubjectToAutoOptIn = true;
        when(user.isSubjectToAutoOptIn()).thenReturn(isSubjectToAutoOptIn);

        AccountCheckDTO dto = accountCheckDTOAsm.toAccountCheckDTO(user, "remember-me-token", null, false, false, false);

        verify(user, times(1)).isSubjectToAutoOptIn();
        verify(ruleServiceSupport, times(1)).fireRules(eq(ALL), any(User.class));

        assertEquals(isSubjectToAutoOptIn, dto.subjectToAutoOptIn);
    }

    @Test
    public void testToAccCheckDTOForNotExemptAndInCampaignUser() throws Exception {
        when(autoOptInExemptPhoneNumberRepository.findOne(mobile)).thenReturn(null);
        when(ruleServiceSupport.fireRules(eq(ALL), any(User.class))).thenReturn(new RuleResult(true, true));

        boolean isSubjectToAutoOptIn = false;
        when(user.isSubjectToAutoOptIn()).thenReturn(isSubjectToAutoOptIn);

        AccountCheckDTO dto = accountCheckDTOAsm.toAccountCheckDTO(user, "remember-me-token", null, false, false, false);

        verify(user, times(0)).isSubjectToAutoOptIn();
        verify(ruleServiceSupport, times(1)).fireRules(eq(ALL), any(User.class));

        assertEquals(true, dto.subjectToAutoOptIn);
    }
}
