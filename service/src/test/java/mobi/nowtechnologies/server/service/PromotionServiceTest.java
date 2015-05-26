package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.builder.PromoParamsBuilder;
import mobi.nowtechnologies.server.device.domain.DeviceTypeCache;
import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.event.service.EventLoggerService;
import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.PromoCode;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserBanned;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.domain.payment.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository;
import mobi.nowtechnologies.server.persistence.repository.UserBannedRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.UserStatusRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.ContractChannel;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.util.EmailValidator;
import mobi.nowtechnologies.server.user.rules.RuleResult;
import mobi.nowtechnologies.server.user.rules.RuleServiceSupport;
import static mobi.nowtechnologies.server.builder.PromoParamsBuilder.PromoParams;
import static mobi.nowtechnologies.server.persistence.domain.Community.VF_NZ_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.service.PromotionService.PromotionTriggerType.AUTO_OPT_IN;
import static mobi.nowtechnologies.server.shared.Utils.WEEK_SECONDS;
import static mobi.nowtechnologies.server.shared.Utils.getEpochSeconds;
import static mobi.nowtechnologies.server.shared.enums.ActionReason.VIDEO_AUDIO_FREE_TRIAL_ACTIVATION;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYM;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.DIRECT;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.INDIRECT;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.MediaType.VIDEO_AND_AUDIO;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.VF;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;
import static mobi.nowtechnologies.server.shared.enums.Tariff._4G;

import java.util.Calendar;
import java.util.Locale;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.invocation.*;
import org.mockito.stubbing.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import static org.hamcrest.CoreMatchers.is;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * @author Titov Mykhaylo (titov)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({UserService.class,
                 Utils.class,
                 DeviceTypeCache.class,
                 AccountLog.class,
                 EmailValidator.class,
                 PromoParams.class,
                 PromotionService.class})
public class PromotionServiceTest {

    public static final String PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT = "promocode.for.o2.consumer.4g.payg.direct";
    public static final String PROMO_CODE_FOR_O2_CONSUMER_4G_PAYM_DIRECT = "promocode.for.o2.consumer.4g.paym.direct";
    public static final String PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_INDIRECT = "promocode.for.o2.consumer.4g.payg.indirect";
    public static final String PROMO_CODE_FOR_O2_CONSUMER_4G_PAYM_INDIRECT = "promocode.for.o2.consumer.4g.paym.indirect";
    public static final String O2_REWRITE_URL_PARAMETER = "o2";
    @Mock
    PromotionProvider.PromotionProxy promotionProxyMock;
    @Mock
    UserService userServiceMock;
    @Mock
    CommunityResourceBundleMessageSource messageSourceMock;
    @Mock
    PromotionRepository promotionRepositoryMock;
    @Mock
    UserBannedRepository userBannedRepositoryMock;
    @Mock
    EventLoggerService eventLoggerService;
    @Mock
    UserStatusRepository userStatusRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    PaymentDetailsRepository paymentDetailsRepository;
    @Mock
    DevicePromotionsService deviceServiceMock;
    @Mock
    RuleServiceSupport ruleServiceSupportMock;
    private PromotionService promotionServiceSpy;
    private String promoCode;
    private Promotion promotion;
    private User user;
    private UserGroup userGroup;
    private Community community;
    private boolean isPromotionForO24GConsumerApplied;
    private Answer userWithPromoAnswer;
    private Answer firstArgAnswer;

    @Before
    public void before() {
        promotionServiceSpy = spy(new PromotionService() {
            @Override
            public RuleServiceSupport<PromotionTriggerType> getRuleServiceSupport() {
                return ruleServiceSupportMock;
            }
        });
        userWithPromoAnswer = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return ((User) invocation.getArguments()[0]).withIsPromotionApplied(true);
            }
        };
        firstArgAnswer = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0];
            }
        };

        promotionServiceSpy.setMessageSource(messageSourceMock);
        promotionServiceSpy.setUserService(userServiceMock);
        promotionServiceSpy.setDeviceService(deviceServiceMock);
        promotionServiceSpy.setEventLoggerService(eventLoggerService);
        promotionServiceSpy.promotionRepository = promotionRepositoryMock;
        promotionServiceSpy.userBannedRepository = userBannedRepositoryMock;
        promotionServiceSpy.userStatusRepository = userStatusRepository;
        promotionServiceSpy.userRepository = userRepository;
        promotionServiceSpy.paymentDetailsRepository = paymentDetailsRepository;
        promotionServiceSpy.setEventLoggerService(eventLoggerService);
    }

    @Test
    public void applyPromotion() {
        User user = new User();
        Promotion potentialPromotion = new Promotion();
        user.setPotentialPromotion(potentialPromotion);
        PaymentDetails currentPaymentDetails = new SagePayCreditCardPaymentDetails();
        PromotionPaymentPolicy promotionPaymentPolicy = new PromotionPaymentPolicy();
        currentPaymentDetails.setPromotionPaymentPolicy(promotionPaymentPolicy);
        user.setCurrentPaymentDetails(currentPaymentDetails);
        when(userRepository.save(user)).thenReturn(user);

        User userAfterPromotion = promotionServiceSpy.applyPromotion(user);

        assertNotNull(userAfterPromotion);
        assertNull(userAfterPromotion.getPotentialPromotion());
        assertNull(user.getCurrentPaymentDetails().getPromotionPaymentPolicy());
    }

    @Test
    public void shouldApplyPromotionForO2Payg4GDirectConsumerOnFreeTrial() {

        given().userWithCommunity("o2").withTariff(_4G).withProvider(O2).withContract(PAYG).withSegment(CONSUMER).withContractChannel(DIRECT).and().promotion();
        user.withFreeTrialExpiredMillis(Long.MAX_VALUE);

        promotion = new Promotion();

        promoCode = "promoCode";
        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);

        doReturn(true).when(userServiceMock).canActivateVideoTrial(user);
        doReturn(promotion).when(promotionServiceSpy).setPotentialPromoByPromoCode(user, promoCode);
        doAnswer(userWithPromoAnswer).when(promotionServiceSpy).applyPromotionByPromoCode(user, promotion);
        doAnswer(firstArgAnswer).when(promotionServiceSpy).applyPotentialPromo(user, community);
        doReturn(user).when(userServiceMock).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        doReturn(user).when(userServiceMock).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        doReturn(user).when(userServiceMock).unsubscribeAndSkipFreeTrial(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        doReturn(user).when(userServiceMock).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());

        User actualUser = promotionServiceSpy.applyPotentialPromo(user);
        isPromotionForO24GConsumerApplied = actualUser.isPromotionApplied();

        then().validateAs(true);

        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);
        verify(promotionServiceSpy, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(promotionServiceSpy, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(promotionServiceSpy, times(0)).applyPotentialPromo(user, community);
        verify(userServiceMock, times(0)).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        verify(userServiceMock, times(1)).unsubscribeAndSkipFreeTrial(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        verify(userServiceMock, times(0)).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());
    }

    @Test
    public void shouldApplyPromotionForO2Payg4GDirectConsumerOnAudioBoughtPeriod() {

        given().userWithCommunity("o2").withTariff(_4G).withProvider(O2).withContract(PAYG).withSegment(CONSUMER).withContractChannel(DIRECT).and().promotion();
        user.withNextSubPayment(Integer.MAX_VALUE).withLastSuccessfulPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withMediaType(AUDIO)));

        promotion = new Promotion();

        promoCode = "promoCode";
        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);

        doReturn(true).when(userServiceMock).canActivateVideoTrial(user);
        doReturn(promotion).when(promotionServiceSpy).setPotentialPromoByPromoCode(user, promoCode);
        doAnswer(userWithPromoAnswer).when(promotionServiceSpy).applyPromotionByPromoCode(user, promotion);
        doAnswer(firstArgAnswer).when(promotionServiceSpy).applyPotentialPromo(user, community);
        doReturn(user).when(userServiceMock).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        doReturn(user).when(userServiceMock).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        doReturn(user).when(userServiceMock).unsubscribeAndSkipFreeTrial(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        doReturn(user).when(userServiceMock).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());

        User actualUser = promotionServiceSpy.applyPotentialPromo(user);
        isPromotionForO24GConsumerApplied = actualUser.isPromotionApplied();

        then().validateAs(true);

        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);
        verify(promotionServiceSpy, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(promotionServiceSpy, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(promotionServiceSpy, times(0)).applyPotentialPromo(user, community);
        verify(userServiceMock, times(1)).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        verify(userServiceMock, times(0)).unsubscribeAndSkipFreeTrial(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        verify(userServiceMock, times(0)).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());
    }

    @Test
    public void shouldApplyPromotionForO2Payg4GDirectConsumerWithActivePaymentDetailsAndNextSubPaymentInThePast() {

        given().userWithCommunity("o2").withTariff(_4G).withProvider(O2).withContract(PAYG).withSegment(CONSUMER).withContractChannel(DIRECT).and().promotion();
        user.withNextSubPayment(Integer.MIN_VALUE)
            .withCurrentPaymentDetails(new O2PSMSPaymentDetails().withActivated(true))
            .withLastSuccessfulPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withMediaType(AUDIO)));

        promotion = new Promotion();

        promoCode = "promoCode";
        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);

        doReturn(true).when(userServiceMock).canActivateVideoTrial(user);
        doReturn(promotion).when(promotionServiceSpy).setPotentialPromoByPromoCode(user, promoCode);
        doAnswer(userWithPromoAnswer).when(promotionServiceSpy).applyPromotionByPromoCode(user, promotion);
        doAnswer(firstArgAnswer).when(promotionServiceSpy).applyPotentialPromo(user, community);
        doReturn(user).when(userServiceMock).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        doReturn(user).when(userServiceMock).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        doReturn(user).when(userServiceMock).unsubscribeAndSkipFreeTrial(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        doReturn(user).when(userServiceMock).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());

        User actualUser = promotionServiceSpy.applyPotentialPromo(user);
        isPromotionForO24GConsumerApplied = actualUser.isPromotionApplied();

        then().validateAs(true);

        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);
        verify(promotionServiceSpy, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(promotionServiceSpy, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(promotionServiceSpy, times(0)).applyPotentialPromo(user, community);
        verify(userServiceMock, times(0)).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        verify(userServiceMock, times(0)).unsubscribeAndSkipFreeTrial(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        verify(userServiceMock, times(1)).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());
    }

    @Test
    public void shouldApplyPromotionForO2Payg4GIndirectConsumer() {

        given().userWithCommunity("o2").withTariff(_4G).withProvider(O2).withContract(PAYG).withSegment(CONSUMER).withContractChannel(INDIRECT).and().promotion();

        promoCode = "promoCode";
        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_INDIRECT, null, null, null);

        doReturn(true).when(userServiceMock).canActivateVideoTrial(user);
        doReturn(promotion).when(promotionServiceSpy).setPotentialPromoByPromoCode(user, promoCode);
        doAnswer(userWithPromoAnswer).when(promotionServiceSpy).applyPromotionByPromoCode(user, promotion);
        doAnswer(firstArgAnswer).when(promotionServiceSpy).applyPotentialPromo(user, community);

        User actualUser = promotionServiceSpy.applyPotentialPromo(user);
        isPromotionForO24GConsumerApplied = actualUser.isPromotionApplied();

        then().validateAs(true);

        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_INDIRECT, null, null, null);
        verify(promotionServiceSpy, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(promotionServiceSpy, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(promotionServiceSpy, times(0)).applyPotentialPromo(user, community);
    }

    private void promotion() {
        promotion = new Promotion();
    }

    @Test
    public void shouldApplyPromotionForO2Payg4GUnknownContractChanelConsumer() {

        given().userWithCommunity("o2").withTariff(_4G).withProvider(O2).withContract(PAYG).withSegment(CONSUMER).withContractChannel(null).and().promotion();

        promoCode = "promoCode";
        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);

        doReturn(true).when(userServiceMock).canActivateVideoTrial(user);
        doReturn(promotion).when(promotionServiceSpy).setPotentialPromoByPromoCode(user, promoCode);
        doAnswer(userWithPromoAnswer).when(promotionServiceSpy).applyPromotionByPromoCode(user, promotion);
        doAnswer(firstArgAnswer).when(promotionServiceSpy).applyPotentialPromo(user, community);

        User actualUser = promotionServiceSpy.applyPotentialPromo(user);
        isPromotionForO24GConsumerApplied = actualUser.isPromotionApplied();

        then().validateAs(true);

        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);
        verify(promotionServiceSpy, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(promotionServiceSpy, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(promotionServiceSpy, times(0)).applyPotentialPromo(user, community);
    }

    @Test
    public void shouldApplyPromotionForO2Paym4GDirectConsumer() {
        given().userWithCommunity("o2").withTariff(_4G).withProvider(O2).withContract(PAYM).withSegment(CONSUMER).withContractChannel(DIRECT).and().promotion();

        promotion = new Promotion();

        promoCode = "promoCode";
        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYM_DIRECT, null, null, null);

        doReturn(true).when(userServiceMock).canActivateVideoTrial(user);
        doReturn(promotion).when(promotionServiceSpy).setPotentialPromoByPromoCode(user, promoCode);
        doAnswer(userWithPromoAnswer).when(promotionServiceSpy).applyPromotionByPromoCode(user, promotion);
        doAnswer(firstArgAnswer).when(promotionServiceSpy).applyPotentialPromo(user, community);

        User actualUser = promotionServiceSpy.applyPotentialPromo(user);
        isPromotionForO24GConsumerApplied = actualUser.isPromotionApplied();

        then().validateAs(true);

        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYM_DIRECT, null, null, null);
        verify(promotionServiceSpy, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(promotionServiceSpy, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(promotionServiceSpy, times(0)).applyPotentialPromo(user, community);
    }

    @Test
    public void shouldApplyPromotionForO2Paym4GIndirectConsumer() {

        given().userWithCommunity(O2_REWRITE_URL_PARAMETER).withTariff(_4G).withProvider(O2).withContract(PAYM).withSegment(CONSUMER).withContractChannel(INDIRECT).and().promotion();

        promoCode = "promoCode";
        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYM_INDIRECT, null, null, null);

        doReturn(true).when(userServiceMock).canActivateVideoTrial(user);
        doReturn(promotion).when(promotionServiceSpy).setPotentialPromoByPromoCode(user, promoCode);
        doAnswer(userWithPromoAnswer).when(promotionServiceSpy).applyPromotionByPromoCode(user, promotion);
        doAnswer(firstArgAnswer).when(promotionServiceSpy).applyPotentialPromo(user, community);

        User actualUser = promotionServiceSpy.applyPotentialPromo(user);
        isPromotionForO24GConsumerApplied = actualUser.isPromotionApplied();

        then().validateAs(true);

        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYM_INDIRECT, null, null, null);
        verify(promotionServiceSpy, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(promotionServiceSpy, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(promotionServiceSpy, times(0)).applyPotentialPromo(user, community);
    }

    @Test
    public void shouldApplyPromotionForO2Paym4GUnknownContractChanelConsumer() {

        given().userWithCommunity(O2_REWRITE_URL_PARAMETER).withTariff(_4G).withProvider(O2).withContract(PAYM).withSegment(CONSUMER).withContractChannel(null).and().promotion();

        promoCode = "promoCode";
        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYM_DIRECT, null, null, null);

        doReturn(true).when(userServiceMock).canActivateVideoTrial(user);
        doReturn(promotion).when(promotionServiceSpy).setPotentialPromoByPromoCode(user, promoCode);
        doAnswer(userWithPromoAnswer).when(promotionServiceSpy).applyPromotionByPromoCode(user, promotion);
        doAnswer(firstArgAnswer).when(promotionServiceSpy).applyPotentialPromo(user, community);

        User actualUser = promotionServiceSpy.applyPotentialPromo(user);
        isPromotionForO24GConsumerApplied = actualUser.isPromotionApplied();

        then().validateAs(true);

        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYM_DIRECT, null, null, null);
        verify(promotionServiceSpy, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(promotionServiceSpy, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(promotionServiceSpy, times(0)).applyPotentialPromo(user, community);
    }

    @Test
    public void shouldDoNotApplyPromotionForNonO2Paym3GUnknownContractChanelConsumer() {

        given().userWithCommunity(O2_REWRITE_URL_PARAMETER).withTariff(_3G).withProvider(O2).withContract(PAYM).withSegment(CONSUMER).withContractChannel(null).and().promotion();

        promoCode = "promoCode";
        doReturn(promoCode).when(messageSourceMock).getMessage(any(String.class), any(String.class), any(Object[].class), any(String.class), any(Locale.class));

        doReturn(promotion).when(promotionServiceSpy).setPotentialPromoByPromoCode(user, promoCode);
        doAnswer(firstArgAnswer).when(promotionServiceSpy).applyPotentialPromo(user, community);

        User actualUser = promotionServiceSpy.applyPotentialPromo(user);
        isPromotionForO24GConsumerApplied = actualUser.isPromotionApplied();

        then().validateAs(false);

        verify(messageSourceMock, times(0)).getMessage(any(String.class), any(String.class), any(Object[].class), any(String.class), any(Locale.class));
        verify(promotionServiceSpy, times(0)).setPotentialPromoByPromoCode(user, promoCode);
        verify(promotionServiceSpy, times(0)).applyPromotionByPromoCode(user, promotion);
        verify(promotionServiceSpy, times(1)).applyPotentialPromo(user, community);
    }

    @Test
    public void shouldActivateVideoAudioFreeTrialForUserOnAudioBoughPeriod() {
        //given
        String userName = "userName";
        String userToken = "";
        String timestamp = "";
        String communityUri = "o2";
        String deviceUID = "deviceUid";

        user = new User().withUserName(userName)
                         .withDeviceUID(deviceUID)
                         .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(communityUri)))
                         .withTariff(_4G)
                         .withProvider(O2)
                         .withContract(PAYG)
                         .withSegment(CONSUMER)
                         .withNextSubPayment(Integer.MAX_VALUE)
                         .withLastSuccessfulPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withMediaType(AUDIO)));
        promotion = new Promotion();

        promoCode = "promoCode";

        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);
        when(userServiceMock.checkCredentials(userName, userToken, timestamp, communityUri, deviceUID)).thenReturn(user);
        when(userServiceMock.canActivateVideoTrial(user)).thenReturn(true);

        doReturn(promotion).when(promotionServiceSpy).setPotentialPromoByPromoCode(user, promoCode);
        doAnswer(userWithPromoAnswer).when(promotionServiceSpy).applyPromotionByPromoCode(user, promotion);
        doReturn(user).when(userServiceMock).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());
        doReturn(user).when(userServiceMock).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);

        //when
        User actualUser = promotionServiceSpy.activateVideoAudioFreeTrial(user);

        //then
        assertEquals(user, actualUser);

        verify(userServiceMock, times(0)).checkCredentials(userName, userToken, timestamp, communityUri, deviceUID);
        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);
        verify(promotionServiceSpy, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(promotionServiceSpy, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(userServiceMock, times(0)).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());
        verify(userServiceMock, times(1)).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
    }

    @Test
    public void shouldActivateVideoAudioFreeTrialForUserOnFreeTrial() {
        //given
        String userName = "userName";
        String userToken = "";
        String timestamp = "";
        String communityUri = "o2";
        String deviceUID = "deviceUid";

        user = new User().withUserName(userName)
                         .withDeviceUID(deviceUID)
                         .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(communityUri)))
                         .withTariff(_4G)
                         .withProvider(O2)
                         .withContract(PAYG)
                         .withSegment(CONSUMER)
                         .withFreeTrialExpiredMillis(Long.MAX_VALUE);
        promotion = new Promotion();

        promoCode = "promoCode";

        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);
        when(userServiceMock.checkCredentials(userName, userToken, timestamp, communityUri, deviceUID)).thenReturn(user);
        when(userServiceMock.canActivateVideoTrial(user)).thenReturn(true);

        doReturn(promotion).when(promotionServiceSpy).setPotentialPromoByPromoCode(user, promoCode);
        doAnswer(userWithPromoAnswer).when(promotionServiceSpy).applyPromotionByPromoCode(user, promotion);
        doReturn(user).when(userServiceMock).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());
        doReturn(user).when(userServiceMock).unsubscribeAndSkipFreeTrial(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);

        //when
        User actualUser = promotionServiceSpy.activateVideoAudioFreeTrial(user);

        //then
        assertEquals(user, actualUser);

        verify(userServiceMock, times(0)).checkCredentials(userName, userToken, timestamp, communityUri, deviceUID);
        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);
        verify(promotionServiceSpy, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(promotionServiceSpy, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(userServiceMock, times(0)).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());
        verify(userServiceMock, times(1)).unsubscribeAndSkipFreeTrial(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
    }

    @Test
    public void shouldActivateVideoAudioFreeTrialForUserWithActivePaymentDetails() {
        //given
        String userName = "userName";
        String userToken = "";
        String timestamp = "";
        String deviceUID = "deviceUid";

        user = new User().withUserName(userName)
                         .withDeviceUID(deviceUID)
                         .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(O2_REWRITE_URL_PARAMETER)))
                         .withTariff(_4G)
                         .withProvider(O2)
                         .withContract(PAYG)
                         .withSegment(CONSUMER)
                         .withNextSubPayment(Integer.MAX_VALUE)
                         .withCurrentPaymentDetails(new O2PSMSPaymentDetails().withActivated(true));
        promotion = new Promotion();

        promoCode = "promoCode";

        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, "promocode.for.o2.consumer.4g.payg.direct", null, null, null);
        when(userServiceMock.checkCredentials(userName, userToken, timestamp, O2_REWRITE_URL_PARAMETER, deviceUID)).thenReturn(user);
        when(userServiceMock.canActivateVideoTrial(user)).thenReturn(true);

        doReturn(promotion).when(promotionServiceSpy).setPotentialPromoByPromoCode(user, promoCode);
        doAnswer(userWithPromoAnswer).when(promotionServiceSpy).applyPromotionByPromoCode(user, promotion);
        doReturn(user).when(userServiceMock).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());
        doReturn(user).when(userServiceMock).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);

        //when
        User actualUser = promotionServiceSpy.activateVideoAudioFreeTrial(user);

        //then
        assertEquals(user, actualUser);

        verify(userServiceMock, times(0)).checkCredentials(userName, userToken, timestamp, O2_REWRITE_URL_PARAMETER, deviceUID);
        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);
        verify(promotionServiceSpy, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(promotionServiceSpy, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(userServiceMock, times(1)).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());
        verify(userServiceMock, times(0)).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
    }

    @Test(expected = ServiceException.class)
    public void shouldDoNotActivateVideoAudioFreeTrialWhenNoPromotion() {
        //given
        String userName = "userName";
        String userToken = "";
        String timestamp = "";
        String communityUri = "o2";
        String deviceUID = "deviceUid";

        user = new User().withUserName(userName)
                         .withDeviceUID(deviceUID)
                         .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(communityUri)))
                         .withTariff(_4G)
                         .withProvider(O2)
                         .withContract(PAYG)
                         .withSegment(CONSUMER);
        promotion = new Promotion();

        promoCode = "promoCode";

        doReturn(promoCode).when(messageSourceMock).getMessage("promocode.for.o2.consumer.4g.payg.direct", null, null, null);
        when(userServiceMock.checkCredentials(userName, userToken, timestamp, communityUri, deviceUID)).thenReturn(user);
        when(userServiceMock.canActivateVideoTrial(user)).thenReturn(true);

        doReturn(null).when(promotionServiceSpy).setPotentialPromoByPromoCode(user, promoCode);
        doAnswer(userWithPromoAnswer).when(promotionServiceSpy).applyPromotionByPromoCode(user, promotion);

        //when
        promotionServiceSpy.activateVideoAudioFreeTrial(user);
    }

    @Test(expected = ServiceException.class)
    public void shouldDoNotActivateVideoAudioFreeTrialWhenUserIsNotEligible() {
        //given
        String userName = "userName";
        String userToken = "";
        String timestamp = "";
        String communityUri = "o2";
        String deviceUID = "deviceUid";

        user = new User().withUserName(userName)
                         .withDeviceUID(deviceUID)
                         .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(communityUri)))
                         .withTariff(_4G)
                         .withProvider(O2)
                         .withContract(PAYG)
                         .withSegment(CONSUMER);
        promotion = new Promotion();

        promoCode = "promoCode";

        doReturn(promoCode).when(messageSourceMock).getMessage("promocode.for.o2.consumer.4g.payg.direct", null, null, null);
        when(userServiceMock.checkCredentials(userName, userToken, timestamp, communityUri, deviceUID)).thenReturn(user);
        when(userServiceMock.canActivateVideoTrial(user)).thenReturn(false);

        doReturn(promotion).when(promotionServiceSpy).setPotentialPromoByPromoCode(user, promoCode);
        doAnswer(userWithPromoAnswer).when(promotionServiceSpy).applyPromotionByPromoCode(user, promotion);

        //when
        promotionServiceSpy.activateVideoAudioFreeTrial(user);
    }

    @Test
    public void shouldUpdatePromotionNumUsers() {
        //given
        promotion = new Promotion();

        doReturn(1).when(promotionRepositoryMock).updatePromotionNumUsers(promotion);

        //when
        boolean isUpdated = promotionServiceSpy.updatePromotionNumUsers(promotion);

        //then
        assertThat(isUpdated, is(true));
    }

    @Test(expected = ServiceException.class)
    public void shouldDoNotUpdatePromotionNumUsers() {
        //given
        promotion = new Promotion();

        doReturn(0).when(promotionRepositoryMock).updatePromotionNumUsers(promotion);

        //when
        promotionServiceSpy.updatePromotionNumUsers(promotion);
    }

    @Test(expected = ServiceException.class)
    public void shouldDoNotApplyPromotionByPromoCode() {
        //given
        User user = new User().withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO).withPromotion(new Promotion()));

        Promotion promotion = new Promotion().withPromoCode(new PromoCode().withMediaType(VIDEO_AND_AUDIO));

        doReturn(null).when(userBannedRepositoryMock).findOne(user.getId());

        promotion.getPromoCode().withPromotion(promotion);

        //when
        promotionServiceSpy.applyPromotionByPromoCode(new PromoParamsBuilder().setUser(user).setPromotion(promotion).setFreeTrialStartedTimestampSeconds(0).createPromoParams());
    }

    @Test
    public void shouldApplyPromotionByPromoCode() {
        //given
        final User user = new User().withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO).withPromotion(new Promotion()));

        final Promotion promotion = new Promotion().withPromoCode(new PromoCode().withCode("code").withMediaType(AUDIO));
        promotion.setPeriod(new Period(DurationUnit.WEEKS, 3));
        promotion.getPromoCode().withPromotion(promotion);
        promotion.setI(1);

        int freeTrialStartedTimestampSeconds = 1;

        Mockito.doReturn(null).when(userBannedRepositoryMock).findOne(user.getId());

        mockStatic(Utils.class);

        final int currentTimeSeconds = Integer.MAX_VALUE;
        int expectedNextSubPaymentSeconds = freeTrialStartedTimestampSeconds + promotion.getPeriod().getDuration() * WEEK_SECONDS;
        PowerMockito.when(Utils.getEpochSeconds()).thenReturn(currentTimeSeconds);
        PowerMockito.when(Utils.secondsToMillis(expectedNextSubPaymentSeconds)).thenReturn(SECONDS.toMillis(expectedNextSubPaymentSeconds));
        PowerMockito.when(Utils.secondsToMillis(freeTrialStartedTimestampSeconds)).thenReturn(freeTrialStartedTimestampSeconds * 1000L);

        mobi.nowtechnologies.server.persistence.domain.UserStatus subscribedUserStatus = new mobi.nowtechnologies.server.persistence.domain.UserStatus();
        PowerMockito.when(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name())).thenReturn(subscribedUserStatus);

        doReturn(user).when(userRepository).save(user);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                promotion.setNumUsers(promotion.getNumUsers() + 1);
                return true;
            }
        }).when(promotionServiceSpy).updatePromotionNumUsers(promotion);

        //when
        User actualUser = promotionServiceSpy.applyPromotionByPromoCode(new PromoParamsBuilder().setUser(user)
                                                                                                .setPromotion(promotion)
                                                                                                .setFreeTrialStartedTimestampSeconds(freeTrialStartedTimestampSeconds)
                                                                                                .createPromoParams());
        boolean isPromotionApplied = actualUser.isPromotionApplied();

        //than
        assertThat(isPromotionApplied, is(true));
        assertThat(user.getLastPromo(), is(promotion.getPromoCode()));
        assertThat(user.getNextSubPayment(), is(expectedNextSubPaymentSeconds));
        assertThat(user.getFreeTrialExpiredMillis(), is(expectedNextSubPaymentSeconds * 1000L));
        assertNull(user.getPotentialPromoCodePromotion());
        assertThat(user.getStatus(), is(subscribedUserStatus));
        assertThat(user.getFreeTrialStartedTimestampMillis(), is(freeTrialStartedTimestampSeconds * 1000L));
        assertThat(user.isVideoFreeTrialHasBeenActivated(), is(false));

        assertThat(promotion.getNumUsers(), is(1));

        verify(userBannedRepositoryMock, times(1)).findOne(user.getId());
        verify(userRepository, times(1)).save(user);
        verify(promotionServiceSpy, times(1)).updatePromotionNumUsers(promotion);
        verify(eventLoggerService, times(1)).logPromotionByPromoCodeApplied(eq(user.getId()),
                                                                            eq(user.getUuid()),
                                                                            eq(promotion.getI()),
                                                                            eq(freeTrialStartedTimestampSeconds * 1000L),
                                                                            eq(promotion.getEndSeconds(freeTrialStartedTimestampSeconds) * 1000L));
    }

    @Test
    public void testApplyPromotionByPromoCode_ToSomeDate_Success() {
        ProviderUserDetails o2UserDetails = new ProviderUserDetails();
        o2UserDetails.operator = "o2";
        o2UserDetails.contract = "payg";

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.getUserGroup().getCommunity().setRewriteUrlParameter("o2");

        Calendar calendar = Calendar.getInstance();
        calendar.set(2013, Calendar.JANUARY, 1);
        PromoCode promoCode = new PromoCode();
        promoCode.setCode("staff");
        final Promotion promotion = new Promotion();
        promotion.setPromoCode(promoCode);
        promotion.setEndDate((int) (calendar.getTimeInMillis() / 1000));
        promotion.setI(1);

        Mockito.when(userBannedRepositoryMock.findOne(anyInt())).thenReturn(null);
        Mockito.when(userRepository.save(eq(user))).thenAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                User user = (User) invocation.getArguments()[0];
                if (user != null) {
                    assertEquals(promotion.getEndDate(), user.getNextSubPayment());
                }

                return user;
            }
        });
        doReturn(true).when(promotionServiceSpy).updatePromotionNumUsers(promotion);

        promotion.getPromoCode().withPromotion(promotion);

        promotionServiceSpy.applyPromotionByPromoCode(user, promotion);

        verify(userBannedRepositoryMock, times(1)).findOne(anyInt());
        verify(promotionServiceSpy, times(1)).updatePromotionNumUsers(promotion);
        verify(userRepository, times(1)).save(eq(user));
    }

    @Test
    public void testApplyPromotionByPromoCode_OnSomeWeeks_Success() {
        ProviderUserDetails o2UserDetails = new ProviderUserDetails();
        o2UserDetails.operator = "o2";
        o2UserDetails.contract = "payg";

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.getUserGroup().getCommunity().setRewriteUrlParameter("o2");

        Calendar calendar = Calendar.getInstance();
        calendar.set(2013, Calendar.JANUARY, 1);
        PromoCode promoCode = new PromoCode();
        promoCode.setCode("store");
        final Promotion promotion = new Promotion();
        promotion.setPromoCode(promoCode);
        promotion.setPeriod(new Period(DurationUnit.WEEKS, 52));
        promotion.setI(1);

        Mockito.when(userRepository.save(eq(user))).thenAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                User user = (User) invocation.getArguments()[0];
                if (user != null) {
                    assertEquals(getEpochSeconds() + 52 * WEEK_SECONDS, user.getNextSubPayment());
                }

                return user;
            }
        });
        Mockito.when(userBannedRepositoryMock.findOne(anyInt())).thenReturn(null);
        doReturn(true).when(promotionServiceSpy).updatePromotionNumUsers(promotion);
        promotion.getPromoCode().withPromotion(promotion);

        promotionServiceSpy.applyPromotionByPromoCode(user, promotion);

        verify(userBannedRepositoryMock, times(1)).findOne(anyInt());
        verify(promotionServiceSpy, times(1)).updatePromotionNumUsers(promotion);
        verify(userRepository, times(1)).save(eq(user));
    }

    @Test
    public void testApplyPromotionByPromoCode_BannedUserWithNoPromotion_Success() {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.getUserGroup().getCommunity().setRewriteUrlParameter("o2");
        UserBanned userBanned = new UserBanned(user);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2013, Calendar.JANUARY, 1);
        PromoCode promoCode = new PromoCode();
        promoCode.setCode("store");
        final Promotion promotion = new Promotion();
        promotion.setPromoCode(promoCode);
        promotion.setPeriod(new Period(DurationUnit.WEEKS, 52));

        Mockito.when(userRepository.save(eq(user))).thenAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                User user = (User) invocation.getArguments()[0];
                return user;
            }
        });
        Mockito.when(userBannedRepositoryMock.findOne(anyInt())).thenReturn(userBanned);
        Mockito.when(promotionRepositoryMock.save(eq(promotion))).thenReturn(promotion);

        promotionServiceSpy.applyPromotionByPromoCode(user, promotion);

        verify(userBannedRepositoryMock, times(1)).findOne(anyInt());
        verify(promotionRepositoryMock, times(0)).save(eq(promotion));
        verify(userRepository, times(1)).save(eq(user));
    }

    @Test
    public void shouldApplyPotentialPromo() throws Exception {
        //given
        User user = new User().withProvider(VF).withUserGroup(new UserGroup().withCommunity(new Community().withName(VF_NZ_COMMUNITY_REWRITE_URL).withRewriteUrl(VF_NZ_COMMUNITY_REWRITE_URL)));

        Promotion promotion = new Promotion();

        Mockito.when(messageSourceMock.getMessage(eq(user.getUserGroup().getCommunity().getRewriteUrlParameter()), eq("o2.staff.promotionCode"), any(Object[].class), any(Locale.class)))
               .thenReturn("staff");
        Mockito.when(messageSourceMock.getMessage(eq(user.getUserGroup().getCommunity().getRewriteUrlParameter()), eq("o2.store.promotionCode"), any(Object[].class), any(Locale.class)))
               .thenReturn("store");
        Mockito.when(deviceServiceMock.isPromotedDevicePhone(eq(user.getUserGroup().getCommunity()), anyString(), eq("staff"))).thenReturn(false);
        Mockito.when(deviceServiceMock.isPromotedDevicePhone(eq(user.getUserGroup().getCommunity()), anyString(), eq("store"))).thenReturn(false);
        doReturn(null).when(promotionServiceSpy).setPotentialPromoByMessageCode(eq(user), eq("staff"));
        doReturn(null).when(promotionServiceSpy).setPotentialPromoByMessageCode(eq(user), eq("store"));
        doReturn(promotion).when(promotionServiceSpy).setPotentialPromoByMessageCode(eq(user), eq("promotionCode"));
        doReturn(null).when(promotionServiceSpy).setPotentialPromoByMessageCode(eq(user), eq("defaultPromotionCode"));
        PowerMockito.mockStatic(Utils.class);
        int currentTimeSeconds = 0;
        PowerMockito.when(Utils.getEpochSeconds()).thenReturn(currentTimeSeconds);
        PromoParams promoParams = new PromoParamsBuilder().setUser(user).setPromotion(promotion).setFreeTrialStartedTimestampSeconds(currentTimeSeconds).createPromoParams();
        PromoParamsBuilder promoParamsBuilderMock = mock(PromoParamsBuilder.class);
        PowerMockito.whenNew(PromoParamsBuilder.class).withNoArguments().thenReturn(promoParamsBuilderMock);
        Mockito.when(promoParamsBuilderMock.setUser(user)).thenReturn(promoParamsBuilderMock);
        Mockito.when(promoParamsBuilderMock.setFreeTrialStartedTimestampSeconds(currentTimeSeconds)).thenReturn(promoParamsBuilderMock);
        Mockito.when(promoParamsBuilderMock.setPromotion(promotion)).thenReturn(promoParamsBuilderMock);
        Mockito.when(promoParamsBuilderMock.createPromoParams()).thenReturn(promoParams);
        PowerMockito.whenNew(PromoParams.class).withArguments(user, promotion, 0).thenReturn(promoParams);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return ((PromoParams) invocation.getArguments()[0]).user.withIsPromotionApplied(true);
            }
        }).when(promotionServiceSpy).applyPromotionByPromoCode(promoParams);

        //when
        User actualUser = promotionServiceSpy.applyPotentialPromo(user, user.getUserGroup().getCommunity());
        boolean result = actualUser.isPromotionApplied();

        //then
        assertEquals(true, result);

        verify(messageSourceMock, times(1)).getMessage(eq(user.getUserGroup().getCommunity().getRewriteUrlParameter()), eq("o2.staff.promotionCode"), any(Object[].class), any(Locale.class));
        verify(messageSourceMock, times(1)).getMessage(eq(user.getUserGroup().getCommunity().getRewriteUrlParameter()), eq("o2.store.promotionCode"), any(Object[].class), any(Locale.class));
        verify(deviceServiceMock, times(1)).isPromotedDevicePhone(eq(user.getUserGroup().getCommunity()), anyString(), eq("staff"));
        verify(deviceServiceMock, times(1)).isPromotedDevicePhone(eq(user.getUserGroup().getCommunity()), anyString(), eq("store"));
        verify(promotionServiceSpy, times(1)).setPotentialPromoByMessageCode(eq(user), eq("promotionCode"));
        verify(promotionServiceSpy, times(0)).setPotentialPromoByMessageCode(eq(user), eq("defaultPromotionCode"));
        verify(promotionServiceSpy, times(0)).setPotentialPromoByMessageCode(eq(user), eq("store"));
        verify(promotionServiceSpy, times(0)).setPotentialPromoByMessageCode(eq(user), eq("staff"));
        verify(promotionServiceSpy, times(1)).applyPromotionByPromoCode(promoParams);
    }

    @Test
    public void shouldReturnPromotionFromRuleForAutoOptIn() {
        //given
        User user = new User();

        Promotion promotionFromRuleForAutoOptIn = new Promotion();

        doReturn(promotionFromRuleForAutoOptIn).when(promotionProxyMock).getPromotion();
        doReturn(new RuleResult<PromotionProvider.PromotionProxy>(true, promotionProxyMock)).when(ruleServiceSupportMock).fireRules(AUTO_OPT_IN, user);

        //when
        Promotion promotion = promotionServiceSpy.getPromotionFromRuleForAutoOptIn(user);

        //then
        assertThat(promotion, is(promotionFromRuleForAutoOptIn));
    }


    private PromotionServiceTest validateAs(boolean b) {
        assertEquals(b, isPromotionForO24GConsumerApplied);
        return this;
    }

    PromotionServiceTest userWithCommunity(String rewriteUrlParameter) {
        community = new Community();
        community.setRewriteUrlParameter(rewriteUrlParameter);

        userGroup = new UserGroup();
        userGroup.setCommunity(community);

        user = new User();
        user.setUserGroup(userGroup);
        return this;
    }

    PromotionServiceTest withTariff(Tariff tariff) {
        user.setTariff(tariff);
        return this;
    }

    PromotionServiceTest withProvider(ProviderType provider) {
        user.setProvider(provider);
        return this;
    }

    PromotionServiceTest withContract(Contract contract) {
        user.setContract(contract);
        return this;
    }

    PromotionServiceTest withSegment(SegmentType segment) {
        user.setSegment(CONSUMER);
        return this;
    }

    PromotionServiceTest withContractChannel(ContractChannel contractChannel) {
        user.setContractChannel(contractChannel);
        return this;
    }

    private PromotionServiceTest given() {
        return this;
    }

    private PromotionServiceTest and() {
        return this;
    }

    private PromotionServiceTest then() {
        return this;
    }

}