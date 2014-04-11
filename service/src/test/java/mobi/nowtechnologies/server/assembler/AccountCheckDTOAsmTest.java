package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.AutoOptInExemptPhoneNumberRepository;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.user.rules.AutoOptInRuleService;
import mobi.nowtechnologies.server.user.rules.RuleResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static mobi.nowtechnologies.server.user.rules.AutoOptInRuleService.AutoOptInTriggerType.ACC_CHECK;
import static org.mockito.Mockito.*;

public class AccountCheckDTOAsmTest {
    @Mock
    private AutoOptInExemptPhoneNumberRepository autoOptInExemptPhoneNumberRepository;
    @Mock
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
    }

    @Test
    public void testToAccountCheckDTOWhenUserIsInDatabase() throws Exception {
        when(autoOptInExemptPhoneNumberRepository.findOne(mobile)).thenReturn(autoOptInExemptPhoneNumber);

        AccountCheckDTO dto = accountCheckDTOAsm.toAccountCheckDTO(user, "remember-me-token", null, false, false);

        assertFalse(dto.subjectToAutoOptIn);
    }

    @Test
    public void testToAccountCheckDTOWhenUserIsInNotDatabase() throws Exception {
        when(autoOptInExemptPhoneNumberRepository.findOne(mobile)).thenReturn(null);
        when(autoOptInRuleService.fireRules(ACC_CHECK, user)).thenReturn(RuleResult.FAIL_RESULT);

        boolean isSubjectToAutoOptIn = true;
        when(user.isSubjectToAutoOptIn()).thenReturn(isSubjectToAutoOptIn);

        AccountCheckDTO dto = accountCheckDTOAsm.toAccountCheckDTO(user, "remember-me-token", null, false, false);

        verify(user, times(1)).isSubjectToAutoOptIn();

        assertEquals(isSubjectToAutoOptIn, dto.subjectToAutoOptIn);
    }
}
