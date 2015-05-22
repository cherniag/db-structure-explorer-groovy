package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.device.domain.DeviceTypeFactory;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicyFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserGroupFactory;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import java.util.Locale;

import org.junit.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


public class O2OAndVFNZMessageNotificationServiceImplTest {

    @InjectMocks
    O2OAndVFNZMessageNotificationServiceImpl userNotificationImplSpy;

    @Mock
    CommunityResourceBundleMessageSource communityResourceBundleMessageSourceMock;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetMessageCode_ProviderIsNotNullSegmentContractDeviceTypeAreNull_Success() throws Exception {
        final String rewriteUrlParameter = "o2";

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(ProviderType.O2);
        user.setSegment(null);
        user.setContract(null);
        user.setDeviceType(null);

        String msgCodeBase = "msgCodeBase";

        String expectedMsg = "expectedMsg";
        final String expectedMsgCode = msgCodeBase + ".for." + user.getProvider().getKey();

        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter),
                                                                 AdditionalMatchers.not(eq(expectedMsgCode)),
                                                                 any(Object[].class),
                                                                 eq(""),
                                                                 eq((Locale) null))).thenReturn(null);
        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

        String result = userNotificationImplSpy.getMessage(user, msgCodeBase, new String[0]);

        assertNotNull(result);
        assertEquals(expectedMsg, result);

        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null));
        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
    }

    @Test
    public void testGetMessageCode_ContractIsNotNullProvicerSegmentDeviceTypeAreNull_Success() throws Exception {
        final String rewriteUrlParameter = "o2";

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(null);
        user.setSegment(null);
        user.setContract(Contract.PAYG);
        user.setDeviceType(null);

        String msgCodeBase = "msgCodeBase";

        String expectedMsg = "expectedMsg";
        final String expectedMsgCode = msgCodeBase + ".for." + user.getContract();

        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter),
                                                                 AdditionalMatchers.not(eq(expectedMsgCode)),
                                                                 any(Object[].class),
                                                                 eq(""),
                                                                 eq((Locale) null))).thenReturn(null);
        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

        String result = userNotificationImplSpy.getMessage(user, msgCodeBase, new String[0]);

        assertNotNull(result);
        assertEquals(expectedMsg, result);

        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null));
        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
    }

    @Test
    public void testGetMessageCode_SegmentIsNotNullContractProvicerDeviceTypeAreNull_Success() throws Exception {
        final String rewriteUrlParameter = "o2";

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(null);
        user.setSegment(SegmentType.BUSINESS);
        user.setContract(null);
        user.setDeviceType(null);

        String msgCodeBase = "msgCodeBase";

        String expectedMsg = "expectedMsg";
        final String expectedMsgCode = msgCodeBase + ".for." + user.getSegment();

        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter),
                                                                 AdditionalMatchers.not(eq(expectedMsgCode)),
                                                                 any(Object[].class),
                                                                 eq(""),
                                                                 eq((Locale) null))).thenReturn(null);
        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

        String result = userNotificationImplSpy.getMessage(user, msgCodeBase, new String[0]);

        assertNotNull(result);
        assertEquals(expectedMsg, result);

        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null));
        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
    }

    @Test
    public void testGetMessageCode_DeviceTypeIsNotNullSegmentContractProvicerAreNull_Success() throws Exception {
        final String rewriteUrlParameter = "o2";

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(null);
        user.setSegment(null);
        user.setContract(null);
        user.setDeviceType(deviceType);

        String msgCodeBase = "msgCodeBase";

        String expectedMsg = "expectedMsg";
        final String expectedMsgCode = msgCodeBase + ".for." + deviceType.getName();

        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter),
                                                                 AdditionalMatchers.not(eq(expectedMsgCode)),
                                                                 any(Object[].class),
                                                                 eq(""),
                                                                 eq((Locale) null))).thenReturn(null);
        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

        String result = userNotificationImplSpy.getMessage(user, msgCodeBase, new String[0]);

        assertNotNull(result);
        assertEquals(expectedMsg, result);

        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null));
        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
    }

    @Test
    public void testGetMessageCode_DeviceTypeSegmentContractProviderAreNotNull_Success() throws Exception {
        final String rewriteUrlParameter = "o2";

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(ProviderType.NON_O2);
        user.setSegment(SegmentType.BUSINESS);
        user.setContract(Contract.PAYG);
        user.setDeviceType(deviceType);

        String msgCodeBase = "msgCodeBase";

        String expectedMsg = "expectedMsg";
        final String expectedMsgCode = msgCodeBase + ".for." + user.getProvider().getKey() + "." + user.getSegment() + "." + user.getContract() + "." + deviceType.getName();

        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter),
                                                                 AdditionalMatchers.not(eq(expectedMsgCode)),
                                                                 any(Object[].class),
                                                                 eq(""),
                                                                 eq((Locale) null))).thenReturn(null);
        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

        String result = userNotificationImplSpy.getMessage(user, msgCodeBase, new String[0]);

        assertNotNull(result);
        assertEquals(expectedMsg, result);

        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null));
        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
    }

    @Test
    public void testGetMessageCode_ProviderIsNullDeviceTypeSegmentContractAreNotNull_Success() throws Exception {
        final String rewriteUrlParameter = "o2";

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(null);
        user.setSegment(SegmentType.BUSINESS);
        user.setContract(Contract.PAYG);
        user.setDeviceType(deviceType);

        String msgCodeBase = "msgCodeBase";

        String expectedMsg = "expectedMsg";
        final String expectedMsgCode = msgCodeBase + ".for." + user.getSegment() + "." + user.getContract() + "." + deviceType.getName();

        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter),
                                                                 AdditionalMatchers.not(eq(expectedMsgCode)),
                                                                 any(Object[].class),
                                                                 eq(""),
                                                                 eq((Locale) null))).thenReturn(null);
        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

        String result = userNotificationImplSpy.getMessage(user, msgCodeBase, new String[0]);

        assertNotNull(result);
        assertEquals(expectedMsg, result);

        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null));
        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
    }

    @Test
    public void testGetMessageCode_ProvicerContractAreNullDeviceTypeSegmentAreNotNull_Success() throws Exception {
        final String rewriteUrlParameter = "o2";

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(null);
        user.setSegment(SegmentType.BUSINESS);
        user.setContract(null);
        user.setDeviceType(deviceType);

        String msgCodeBase = "msgCodeBase";

        String expectedMsg = "expectedMsg";
        final String expectedMsgCode = msgCodeBase + ".for." + user.getSegment() + "." + deviceType.getName();

        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter),
                                                                 AdditionalMatchers.not(eq(expectedMsgCode)),
                                                                 any(Object[].class),
                                                                 eq(""),
                                                                 eq((Locale) null))).thenReturn(null);
        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

        String result = userNotificationImplSpy.getMessage(user, msgCodeBase, new String[0]);

        assertNotNull(result);
        assertEquals(expectedMsg, result);

        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null));
        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
    }

    @Test
    public void testGetMessageCode_SegmentContractAreNullDeviceTypeProviderPaymentTypeAreNotNull_Success() throws Exception {
        final String rewriteUrlParameter = "o2";

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
        paymentPolicy.setProvider(ProviderType.VF);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(ProviderType.VF);
        user.setDeviceType(deviceType);
        user.setSegment(null);
        user.setContract(null);
        user.setCurrentPaymentDetails(new PaymentDetails() {
            @Override
            public String getPaymentType() {
                return PaymentDetails.VF_PSMS_TYPE;
            }
        });
        user.getCurrentPaymentDetails().setPaymentPolicy(paymentPolicy);

        String msgCodeBase = "msgCodeBase";

        String expectedMsg = "expectedMsg";
        final String expectedMsgCode = msgCodeBase + ".for." + user.getProvider().getKey() + "." + deviceType.getName() + "." + user.getCurrentPaymentDetails().getPaymentType();

        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter),
                                                                 AdditionalMatchers.not(eq(expectedMsgCode)),
                                                                 any(Object[].class),
                                                                 eq(""),
                                                                 eq((Locale) null))).thenReturn(null);
        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

        String result = userNotificationImplSpy.getMessage(user, msgCodeBase, new String[0]);

        assertNotNull(result);
        assertEquals(expectedMsg, result);

        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null));
        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
    }

    @Test
    public void testGetMessageCode_ChangedProvider_Success() throws Exception {
        final String rewriteUrlParameter = "o2";

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");

        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
        paymentPolicy.setProvider(ProviderType.NON_VF);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(ProviderType.VF);
        user.setDeviceType(deviceType);
        user.setSegment(null);
        user.setContract(null);
        user.setCurrentPaymentDetails(new PaymentDetails() {
            @Override
            public String getPaymentType() {
                return PaymentDetails.VF_PSMS_TYPE;
            }
        });
        user.getCurrentPaymentDetails().setPaymentPolicy(paymentPolicy);

        String msgCodeBase = "msgCodeBase";

        String expectedMsg = "expectedMsg";
        final String expectedMsgCode = msgCodeBase + ".for." + user.getProvider().getKey() + "." + deviceType.getName() + "." + user.getCurrentPaymentDetails().getPaymentType() + ".before." +
                                       paymentPolicy.getProvider().getKey();

        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter),
                                                                 AdditionalMatchers.not(eq(expectedMsgCode)),
                                                                 any(Object[].class),
                                                                 eq(""),
                                                                 eq((Locale) null))).thenReturn(null);
        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

        String result = userNotificationImplSpy.getMessage(user, msgCodeBase, new String[0]);

        assertNotNull(result);
        assertEquals(expectedMsg, result);

        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null));
        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
    }

    @Test
    public void testGetMessageCode_ChangedSegment_Success() throws Exception {
        final String rewriteUrlParameter = "o2";

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");

        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
        paymentPolicy.setProvider(ProviderType.VF);
        paymentPolicy.setSegment(SegmentType.CONSUMER);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(ProviderType.VF);
        user.setDeviceType(deviceType);
        user.setSegment(SegmentType.BUSINESS);
        user.setContract(null);
        user.setCurrentPaymentDetails(new PaymentDetails() {
            @Override
            public String getPaymentType() {
                return PaymentDetails.VF_PSMS_TYPE;
            }
        });
        user.getCurrentPaymentDetails().setPaymentPolicy(paymentPolicy);

        String msgCodeBase = "msgCodeBase";

        String expectedMsg = "expectedMsg";
        final String expectedMsgCode =
            msgCodeBase + ".for." + user.getProvider().getKey() + "." + user.getSegment() + "." + deviceType.getName() + "." + user.getCurrentPaymentDetails().getPaymentType() + ".before." +
            paymentPolicy.getProvider().getKey() + "." + paymentPolicy.getSegment();

        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter),
                                                                 AdditionalMatchers.not(eq(expectedMsgCode)),
                                                                 any(Object[].class),
                                                                 eq(""),
                                                                 eq((Locale) null))).thenReturn(null);
        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

        String result = userNotificationImplSpy.getMessage(user, msgCodeBase, new String[0]);

        assertNotNull(result);
        assertEquals(expectedMsg, result);

        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null));
        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
    }

    @Test
    public void testGetMessageCode_ChangedContract_Success() throws Exception {
        final String rewriteUrlParameter = "o2";

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        DeviceType deviceType = DeviceTypeFactory.createDeviceType("deviceTypeName");

        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
        paymentPolicy.setProvider(ProviderType.VF);
        paymentPolicy.setSegment(SegmentType.CONSUMER);
        paymentPolicy.setContract(Contract.PAYM);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(ProviderType.VF);
        user.setDeviceType(deviceType);
        user.setSegment(SegmentType.CONSUMER);
        user.setContract(Contract.PAYG);
        user.setCurrentPaymentDetails(new PaymentDetails() {
            @Override
            public String getPaymentType() {
                return PaymentDetails.VF_PSMS_TYPE;
            }
        });
        user.getCurrentPaymentDetails().setPaymentPolicy(paymentPolicy);

        String msgCodeBase = "msgCodeBase";

        String expectedMsg = "expectedMsg";
        final String expectedMsgCode = msgCodeBase + ".for." + user.getProvider().getKey() + "." + user.getSegment() + "." + user.getContract() + "." + deviceType.getName() + "." +
                                       user.getCurrentPaymentDetails().getPaymentType() + ".before." + paymentPolicy.getProvider().getKey() + "." + paymentPolicy.getSegment() + "." +
                                       paymentPolicy.getContract();

        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter),
                                                                 AdditionalMatchers.not(eq(expectedMsgCode)),
                                                                 any(Object[].class),
                                                                 eq(""),
                                                                 eq((Locale) null))).thenReturn(null);
        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

        String result = userNotificationImplSpy.getMessage(user, msgCodeBase, new String[0]);

        assertNotNull(result);
        assertEquals(expectedMsg, result);

        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null));
        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
    }

    @Test
    public void testGetMessageCode_ProviderSegmentContractDeviceTypeAreNull_Success() throws Exception {
        final String rewriteUrlParameter = "o2";

        Community o2Community = CommunityFactory.createCommunity();
        o2Community.setRewriteUrlParameter(rewriteUrlParameter);

        UserGroup o2UserGroup = UserGroupFactory.createUserGroup(o2Community);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserGroup(o2UserGroup);
        user.setProvider(null);
        user.setSegment(null);
        user.setContract(null);
        user.setDeviceType(null);

        String msgCodeBase = "msgCodeBase";

        final String expectedMsgCode = msgCodeBase;
        String expectedMsg = "";

        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter),
                                                                 AdditionalMatchers.not(eq(expectedMsgCode)),
                                                                 any(Object[].class),
                                                                 eq(""),
                                                                 eq((Locale) null))).thenReturn(null);
        when(communityResourceBundleMessageSourceMock.getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null))).thenReturn(expectedMsg);

        String result = userNotificationImplSpy.getMessage(user, msgCodeBase, new String[0]);

        assertNotNull(result);
        assertEquals(expectedMsg, result);

        verify(communityResourceBundleMessageSourceMock, times(0)).getMessage(eq(rewriteUrlParameter), AdditionalMatchers.not(eq(expectedMsgCode)), any(Object[].class), eq(""), eq((Locale) null));
        verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(eq(rewriteUrlParameter), eq(expectedMsgCode), any(Object[].class), eq(""), eq((Locale) null));
    }
}