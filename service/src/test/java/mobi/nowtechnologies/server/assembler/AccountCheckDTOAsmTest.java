package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.AutoOptInExemptPhoneNumber;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.repository.AutoOptInExemptPhoneNumberRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService;
import mobi.nowtechnologies.server.user.rules.RuleResult;
import mobi.nowtechnologies.server.user.rules.RuleServiceSupport;
import static mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService.AutoOptInTriggerType.ALL;

import org.junit.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class AccountCheckDTOAsmTest {

    @Mock
    private AutoOptInExemptPhoneNumberRepository autoOptInExemptPhoneNumberRepository;
    @Mock
    private RuleServiceSupport ruleServiceSupport;
    @Mock
    private UserDetailsDtoAsm userDetailsDtoAsm;

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
    private UserStatus userStatus;
    @Mock
    private DeviceType deviceType;
    private String mobile = "+447111111111";
    private String uuid = Utils.getRandomUUID();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(user.getUserGroup()).thenReturn(userGroup);
        when(user.getStatus()).thenReturn(userStatus);
        when(user.getDeviceType()).thenReturn(deviceType);
        when(user.getMobile()).thenReturn(mobile);
        when(user.getUuid()).thenReturn(uuid);
        when(userGroup.getChart()).thenReturn(chart);

        autoOptInRuleService = new AutoOptInRuleService() {
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

        AccountCheckDTO dto = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, false, false, false, false);

        assertFalse(dto.subjectToAutoOptIn);
        verify(ruleServiceSupport, times(0)).fireRules(eq(ALL), any(User.class));
    }

    @Test
    public void testToAccCheckDTOForNotExemptAndNotCampaignUser() throws Exception {
        userIsNotEligibleForPromo();

        final boolean isSubjectToAutoOptIn = true;
        when(user.isSubjectToAutoOptIn()).thenReturn(isSubjectToAutoOptIn);

        AccountCheckDTO dto = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, false, false, false, false);

        assertEquals(isSubjectToAutoOptIn, dto.subjectToAutoOptIn);
        verify(user, times(1)).isSubjectToAutoOptIn();
        verify(ruleServiceSupport, times(1)).fireRules(eq(ALL), any(User.class));
    }

    @Test
    public void testToAccCheckDTOForNotExemptAndInCampaignUser() throws Exception {
        when(autoOptInExemptPhoneNumberRepository.findOne(mobile)).thenReturn(null);
        when(ruleServiceSupport.fireRules(eq(ALL), any(User.class))).thenReturn(new RuleResult(true, true));

        final boolean isSubjectToAutoOptIn = false;
        when(user.isSubjectToAutoOptIn()).thenReturn(isSubjectToAutoOptIn);

        AccountCheckDTO dto = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, false, false, false, false);

        assertEquals(true, dto.subjectToAutoOptIn);
        verify(user, times(0)).isSubjectToAutoOptIn();
        verify(ruleServiceSupport, times(1)).fireRules(eq(ALL), any(User.class));
    }

    @Test
    public void testToAccCheckDTOWithDetails() throws Exception {
        userIsNotEligibleForPromo();

        accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, true, false, false, false);

        verify(userDetailsDtoAsm, times(1)).toUserDetailsDto(user);
    }

    @Test
    public void testToAccCheckDTOWithUuid() throws Exception {
        userIsNotEligibleForPromo();

        final boolean withUuid = true;
        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, true, false, withUuid, false);

        assertEquals(uuid, accountCheckDTO.uuid);
        verify(user, times(1)).getUuid();
    }

    @Test
    public void testToAccCheckDTOWithoutUuid() throws Exception {
        userIsNotEligibleForPromo();

        final boolean withUuid = false;
        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, true, false, withUuid, false);

        assertNull(accountCheckDTO.uuid);
        verify(user, never()).getUuid();
    }

    @Test
    public void testToAccCheckDTOWithoutOneTimePayment() throws Exception {
        userIsNotEligibleForPromo();

        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, true, false, false, false);

        assertNull(accountCheckDTO.oneTimePayment);
        verify(user, times(0)).hasOneTimeSubscription();
    }

    @Test
     public void toAccCheckDTOPaymentEnabledForO2AndDeactivatedDetails() throws Exception {
        userIsNotEligibleForPromo();

        when(user.hasActivePaymentDetails()).thenReturn(false);
        when(user.isSubscribedByITunes()).thenReturn(false);
        when(user.isO2CommunityUser()).thenReturn(true);

        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, true, false, false, false);

        assertFalse(accountCheckDTO.paymentEnabled);
    }

    @Test
    public void toAccCheckDTOPaymentEnabledForO2AndActivatedDetailsWithSuccessStatus() throws Exception {
        userIsNotEligibleForPromo();

        when(user.hasActivePaymentDetails()).thenReturn(true);
        when(user.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.SUCCESSFUL);
        when(user.isSubscribedByITunes()).thenReturn(false);
        when(user.isO2CommunityUser()).thenReturn(true);

        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, true, false, false, false);

        assertTrue(accountCheckDTO.paymentEnabled);
    }

    @Test
    public void toAccCheckDTOPaymentEnabledForO2AndActivatedDetailsWithErrorStatus() throws Exception {
        userIsNotEligibleForPromo();

        when(user.hasActivePaymentDetails()).thenReturn(true);
        when(user.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.ERROR);
        when(user.isSubscribedByITunes()).thenReturn(false);
        when(user.isO2CommunityUser()).thenReturn(true);

        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, true, false, false, false);

        assertFalse(accountCheckDTO.paymentEnabled);
    }

    @Test
    public void toAccCheckDTOPaymentEnabledForNewCommunityAndActivatedDetailsWithSuccessStatus() throws Exception {
        userIsNotEligibleForPromo();

        when(user.hasActivePaymentDetails()).thenReturn(true);
        when(user.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.SUCCESSFUL);
        when(user.isSubscribedByITunes()).thenReturn(false);
        when(user.isO2CommunityUser()).thenReturn(false);
        when(user.isVFNZCommunityUser()).thenReturn(false);

        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, true, false, false, false);

        assertTrue(accountCheckDTO.paymentEnabled);
    }

    @Test
    public void toAccCheckDTOPaymentEnabledForNewCommunityAndActivatedDetailsWithErrorStatus() throws Exception {
        userIsNotEligibleForPromo();

        when(user.hasActivePaymentDetails()).thenReturn(true);
        when(user.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.ERROR);
        when(user.isSubscribedByITunes()).thenReturn(false);
        when(user.isO2CommunityUser()).thenReturn(false);
        when(user.isVFNZCommunityUser()).thenReturn(false);

        AccountCheckDTO accountCheckDTO = accountCheckDTOAsm.toAccountCheckDTO(user, "any-remember-me-token", null, false, true, false, false, false);

        assertTrue(accountCheckDTO.paymentEnabled);
    }

    private void userIsNotEligibleForPromo() {
        when(autoOptInExemptPhoneNumberRepository.findOne(mobile)).thenReturn(null);
        when(ruleServiceSupport.fireRules(eq(ALL), any(User.class))).thenReturn(RuleResult.FAIL_RESULT);
    }

}
