package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;
import mobi.nowtechnologies.server.persistence.repository.AutoOptInExemptPhoneNumberRepository;
import mobi.nowtechnologies.server.persistence.repository.social.GooglePlusUserInfoRepository;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.social.GooglePlusUserDetailsDto;
import mobi.nowtechnologies.server.shared.dto.social.SocialInfoType;
import mobi.nowtechnologies.server.shared.enums.Gender;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService;
import mobi.nowtechnologies.server.user.rules.RuleResult;
import mobi.nowtechnologies.server.user.rules.RuleServiceSupport;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService.AutoOptInTriggerType.ALL;
import static org.mockito.Mockito.*;

public class AccountCheckDTOAsmTest {
    @Mock
    private AutoOptInExemptPhoneNumberRepository autoOptInExemptPhoneNumberRepository;
    @Mock
    private RuleServiceSupport ruleServiceSupport;
    @Mock
    private GooglePlusUserInfoRepository googlePlusUserInfoRepository;

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

    @Test
    public void testToAccCheckDTOForGooglePlusUser() throws Exception {
        when(user.getProvider()).thenReturn(ProviderType.GOOGLE_PLUS);
        GooglePlusUserInfo googlePlusUserInfo = mock(GooglePlusUserInfo.class);
        when(googlePlusUserInfoRepository.findByUser(user)).thenReturn(googlePlusUserInfo);
        when(googlePlusUserInfo.getEmail()).thenReturn("a@bc.com");
        when(googlePlusUserInfo.getDisplayName()).thenReturn("Display Name");
        when(googlePlusUserInfo.getPicture()).thenReturn("http://gp.com/1/1.jpg");
        when(googlePlusUserInfo.getGooglePlusId()).thenReturn("000001");
        when(googlePlusUserInfo.getGivenName()).thenReturn("Name");
        when(googlePlusUserInfo.getFamilyName()).thenReturn("Surname");
        when(googlePlusUserInfo.getGender()).thenReturn(Gender.FEMALE);
        when(googlePlusUserInfo.getLocation()).thenReturn("London");
        when(googlePlusUserInfo.getBirthday()).thenReturn(new SimpleDateFormat("MM/dd/yyyy").parse("07/12/1980"));

        when(autoOptInExemptPhoneNumberRepository.findOne(mobile)).thenReturn(null);
        when(ruleServiceSupport.fireRules(eq(ALL), eq(user))).thenReturn(RuleResult.FAIL_RESULT);

        AccountCheckDTO dto = accountCheckDTOAsm.toAccountCheckDTO(user, "remember-me-token", null, false, true, false);

        //check user details
        GooglePlusUserDetailsDto dtoUserDetails = (GooglePlusUserDetailsDto) dto.getUserDetails();
        assertEquals("a@bc.com", dtoUserDetails.getEmail());
        assertEquals("Display Name", dtoUserDetails.getUserName());
        assertEquals("http://gp.com/1/1.jpg", dtoUserDetails.getProfileUrl());
        assertEquals("000001", dtoUserDetails.getGooglePlusId());
        assertEquals("Name", dtoUserDetails.getFirstName());
        assertEquals("Surname", dtoUserDetails.getSurname());
        assertEquals(Gender.FEMALE, dtoUserDetails.getGender());
        assertEquals("London", dtoUserDetails.getLocation());
        assertEquals("07/12/1980", dtoUserDetails.getBirthDay());
        assertEquals(SocialInfoType.GooglePlus, dtoUserDetails.getSocialInfoType());
    }
}
