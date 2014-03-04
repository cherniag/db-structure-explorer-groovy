package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.payment.*;
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.enums.*;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Locale;

import static mobi.nowtechnologies.server.shared.enums.ActionReason.VIDEO_AUDIO_FREE_TRIAL_ACTIVATION;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYM;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.DIRECT;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.INDIRECT;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;
import static mobi.nowtechnologies.server.shared.enums.Tariff._4G;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Titov Mykhaylo (titov)
 */
@RunWith(PowerMockRunner.class)
public class PromotionServiceTest {

    public static final String PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT = "promocode.for.o2.consumer.4g.payg.direct";
    public static final String PROMO_CODE_FOR_O2_CONSUMER_4G_PAYM_DIRECT = "promocode.for.o2.consumer.4g.paym.direct";
    public static final String PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_INDIRECT = "promocode.for.o2.consumer.4g.payg.indirect";
    public static final String PROMO_CODE_FOR_O2_CONSUMER_4G_PAYM_INDIRECT = "promocode.for.o2.consumer.4g.paym.indirect";
    public static final String O2_REWRITE_URL_PARAMETER = "o2";

    private PromotionService promotionService;

    @Mock
    UserService userServiceMock;

    @Mock
    CommunityResourceBundleMessageSource messageSourceMock;

    @Mock
    PromotionRepository promotionRepositoryMock;

    private String promoCode;
    private Promotion promotion;
    private User user;
    private UserGroup userGroup;
    private Community community;
    private boolean isPromotionForO24GConsumerApplied;

    @Before
	public void before() {
		promotionService = new PromotionService();
			EntityService entityServiceMock = mock(EntityService.class);
			when(entityServiceMock.updateEntity(any(Object.class))).thenAnswer(new Answer<Object>() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    return invocation.getArguments()[0];
                }
            });

		promotionService.setEntityService(entityServiceMock);
        promotionService.setMessageSource(messageSourceMock);
        promotionService.setUserService(userServiceMock);
        promotionService.setPromotionRepository(promotionRepositoryMock);
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
		User userAfterPromotion = promotionService.applyPromotion(user);
		
		assertNotNull(userAfterPromotion);
		assertNull(userAfterPromotion.getPotentialPromotion());
		assertNull(user.getCurrentPaymentDetails().getPromotionPaymentPolicy());
	}

	@Test
	@Ignore
	public void testGetActivePromotion_Success()
		throws Exception {
		String promotionCode = "promo";
		String communityName = "Now Music";

		Promotion result = promotionService.getActivePromotion(promotionCode, communityName);

		assertNotNull(result);
	}

	@Test(expected = mobi.nowtechnologies.server.service.exception.ServiceException.class)
	@Ignore
	public void testGetActivePromotion_WhenPromotionCodeIsNull()
		throws Exception {
		String promotionCode = null;
		String communityName = "";

		Promotion result = promotionService.getActivePromotion(promotionCode, communityName);

		assertNotNull(result);
	}

	@Test(expected = mobi.nowtechnologies.server.service.exception.ServiceException.class)
	@Ignore
	public void testGetActivePromotion_WhenCommunityNameIsNull()
		throws Exception {
		String promotionCode = "";
		String communityName = null;

		Promotion result = promotionService.getActivePromotion(promotionCode, communityName);

		assertNotNull(result);
	}

    @Test
    public void shouldApplyPromotionForO2Payg4GDirectConsumerOnFreeTrial(){

        given().userWithCommunity("o2").withTariff(_4G).withProvider(O2).withContract(PAYG).withSegment(CONSUMER).withContractChannel(DIRECT).and().promotion();
        user.withFreeTrialExpiredMillis(Long.MAX_VALUE);

        promotion = new Promotion();

        promoCode = "promoCode";
        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);

        doReturn(true).when(userServiceMock).canActivateVideoTrial(user);
        doReturn(promotion).when(userServiceMock).setPotentialPromoByPromoCode(user, promoCode);
        doReturn(true).when(userServiceMock).applyPromotionByPromoCode(user, promotion);
        doReturn(true).when(userServiceMock).applyPotentialPromo(user.isO2User(), user, community);
        doReturn(user).when(userServiceMock).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        doReturn(user).when(userServiceMock).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        doReturn(user).when(userServiceMock).unsubscribeAndSkipFreeTrial(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        doReturn(user).when(userServiceMock).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());

        isPromotionForO24GConsumerApplied = promotionService.applyPotentialPromo(user, user.isO2User());

        then().validateAs(true);

        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);
        verify(userServiceMock, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(userServiceMock, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(userServiceMock, times(0)).applyPotentialPromo(user.isO2User(), user, community);
        verify(userServiceMock, times(0)).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        verify(userServiceMock, times(1)).unsubscribeAndSkipFreeTrial(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        verify(userServiceMock, times(0)).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());
    }

    @Test
    public void shouldApplyPromotionForO2Payg4GDirectConsumerOnAudioBoughtPeriod(){

        given().userWithCommunity("o2").withTariff(_4G).withProvider(O2).withContract(PAYG).withSegment(CONSUMER).withContractChannel(DIRECT).and().promotion();
        user.withNextSubPayment(Integer.MAX_VALUE).withLastSuccessfulPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withMediaType(AUDIO)));

        promotion = new Promotion();

        promoCode = "promoCode";
        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);

        doReturn(true).when(userServiceMock).canActivateVideoTrial(user);
        doReturn(promotion).when(userServiceMock).setPotentialPromoByPromoCode(user, promoCode);
        doReturn(true).when(userServiceMock).applyPromotionByPromoCode(user, promotion);
        doReturn(true).when(userServiceMock).applyPotentialPromo(user.isO2User(), user, community);
        doReturn(user).when(userServiceMock).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        doReturn(user).when(userServiceMock).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        doReturn(user).when(userServiceMock).unsubscribeAndSkipFreeTrial(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        doReturn(user).when(userServiceMock).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());

        isPromotionForO24GConsumerApplied = promotionService.applyPotentialPromo(user, user.isO2User());

        then().validateAs(true);

        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);
        verify(userServiceMock, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(userServiceMock, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(userServiceMock, times(0)).applyPotentialPromo(user.isO2User(), user, community);
        verify(userServiceMock, times(1)).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        verify(userServiceMock, times(0)).unsubscribeAndSkipFreeTrial(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        verify(userServiceMock, times(0)).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());
    }

    @Test
    public void shouldApplyPromotionForO2Payg4GDirectConsumerWithActivePaymentDetailsAndNextSubPaymentInThePast(){

        given().userWithCommunity("o2").withTariff(_4G).withProvider(O2).withContract(PAYG).withSegment(CONSUMER).withContractChannel(DIRECT).and().promotion();
        user.withNextSubPayment(Integer.MIN_VALUE).withCurrentPaymentDetails(new O2PSMSPaymentDetails().withActivated(true)).withLastSuccessfulPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withMediaType(AUDIO)));

        promotion = new Promotion();

        promoCode = "promoCode";
        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);

        doReturn(true).when(userServiceMock).canActivateVideoTrial(user);
        doReturn(promotion).when(userServiceMock).setPotentialPromoByPromoCode(user, promoCode);
        doReturn(true).when(userServiceMock).applyPromotionByPromoCode(user, promotion);
        doReturn(true).when(userServiceMock).applyPotentialPromo(user.isO2User(), user, community);
        doReturn(user).when(userServiceMock).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        doReturn(user).when(userServiceMock).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        doReturn(user).when(userServiceMock).unsubscribeAndSkipFreeTrial(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        doReturn(user).when(userServiceMock).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());

        isPromotionForO24GConsumerApplied = promotionService.applyPotentialPromo(user, user.isO2User());

        then().validateAs(true);

        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);
        verify(userServiceMock, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(userServiceMock, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(userServiceMock, times(0)).applyPotentialPromo(user.isO2User(), user, community);
        verify(userServiceMock, times(0)).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        verify(userServiceMock, times(0)).unsubscribeAndSkipFreeTrial(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
        verify(userServiceMock, times(1)).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());
    }

    @Test
    public void shouldApplyPromotionForO2Payg4GIndirectConsumer(){

        given().userWithCommunity("o2").withTariff(_4G).withProvider(O2).withContract(PAYG).withSegment(CONSUMER).withContractChannel(INDIRECT).and().promotion();

        promoCode = "promoCode";
        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_INDIRECT, null, null, null);

        doReturn(true).when(userServiceMock).canActivateVideoTrial(user);
        doReturn(promotion).when(userServiceMock).setPotentialPromoByPromoCode(user, promoCode);
        doReturn(true).when(userServiceMock).applyPromotionByPromoCode(user, promotion);
        doReturn(true).when(userServiceMock).applyPotentialPromo(user.isO2User(), user, community);

        isPromotionForO24GConsumerApplied = promotionService.applyPotentialPromo(user, user.isO2User());

        then().validateAs(true);

        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_INDIRECT, null, null, null);
        verify(userServiceMock, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(userServiceMock, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(userServiceMock, times(0)).applyPotentialPromo(user.isO2User(), user, community);
    }

    private void promotion() {
        promotion = new Promotion();
    }

    @Test
    public void shouldApplyPromotionForO2Payg4GUnknownContractChanelConsumer(){

        given().userWithCommunity("o2").withTariff(_4G).withProvider(O2).withContract(PAYG).withSegment(CONSUMER).withContractChannel(null).and().promotion();

        promoCode = "promoCode";
        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);

        doReturn(true).when(userServiceMock).canActivateVideoTrial(user);
        doReturn(promotion).when(userServiceMock).setPotentialPromoByPromoCode(user, promoCode);
        doReturn(true).when(userServiceMock).applyPromotionByPromoCode(user, promotion);
        doReturn(true).when(userServiceMock).applyPotentialPromo(user.isO2User(), user, community);

        isPromotionForO24GConsumerApplied = promotionService.applyPotentialPromo(user, user.isO2User());

        then().validateAs(true);

        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);
        verify(userServiceMock, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(userServiceMock, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(userServiceMock, times(0)).applyPotentialPromo(user.isO2User(), user, community);
    }

    @Test
    public void shouldApplyPromotionForO2Paym4GDirectConsumer(){
        given().userWithCommunity("o2").withTariff(_4G).withProvider(O2).withContract(PAYM).withSegment(CONSUMER).withContractChannel(DIRECT).and().promotion();

        promotion = new Promotion();

        promoCode = "promoCode";
        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYM_DIRECT, null, null, null);

        doReturn(true).when(userServiceMock).canActivateVideoTrial(user);
        doReturn(promotion).when(userServiceMock).setPotentialPromoByPromoCode(user, promoCode);
        doReturn(true).when(userServiceMock).applyPromotionByPromoCode(user, promotion);
        doReturn(true).when(userServiceMock).applyPotentialPromo(user.isO2User(), user, community);

        isPromotionForO24GConsumerApplied = promotionService.applyPotentialPromo(user, user.isO2User());

        then().validateAs(true);

        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYM_DIRECT, null, null, null);
        verify(userServiceMock, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(userServiceMock, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(userServiceMock, times(0)).applyPotentialPromo(user.isO2User(), user, community);
    }

    @Test
    public void shouldApplyPromotionForO2Paym4GIndirectConsumer(){

        given().userWithCommunity(O2_REWRITE_URL_PARAMETER).withTariff(_4G).withProvider(O2).withContract(PAYM).withSegment(CONSUMER).withContractChannel(INDIRECT).and().promotion();

        promoCode = "promoCode";
        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYM_INDIRECT, null, null, null);

        doReturn(true).when(userServiceMock).canActivateVideoTrial(user);
        doReturn(promotion).when(userServiceMock).setPotentialPromoByPromoCode(user, promoCode);
        doReturn(true).when(userServiceMock).applyPromotionByPromoCode(user, promotion);
        doReturn(true).when(userServiceMock).applyPotentialPromo(user.isO2User(), user, community);

        isPromotionForO24GConsumerApplied = promotionService.applyPotentialPromo(user, user.isO2User());

        then().validateAs(true);

        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYM_INDIRECT, null, null, null);
        verify(userServiceMock, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(userServiceMock, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(userServiceMock, times(0)).applyPotentialPromo(user.isO2User(), user, community);
    }

    @Test
    public void shouldApplyPromotionForO2Paym4GUnknownContractChanelConsumer(){

        given().userWithCommunity(O2_REWRITE_URL_PARAMETER).withTariff(_4G).withProvider(O2).withContract(PAYM).withSegment(CONSUMER).withContractChannel(null).and().promotion();

        promoCode = "promoCode";
        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYM_DIRECT, null, null, null);

        doReturn(true).when(userServiceMock).canActivateVideoTrial(user);
        doReturn(promotion).when(userServiceMock).setPotentialPromoByPromoCode(user, promoCode);
        doReturn(true).when(userServiceMock).applyPromotionByPromoCode(user, promotion);
        doReturn(true).when(userServiceMock).applyPotentialPromo(user.isO2User(), user, community);

        isPromotionForO24GConsumerApplied = promotionService.applyPotentialPromo(user, user.isO2User());

        then().validateAs(true);

        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYM_DIRECT, null, null, null);
        verify(userServiceMock, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(userServiceMock, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(userServiceMock, times(0)).applyPotentialPromo(user.isO2User(), user, community);
    }

    @Test
    public void shouldDoNotApplyPromotionForNonO2Paym3GUnknownContractChanelConsumer(){

        given().userWithCommunity(O2_REWRITE_URL_PARAMETER).withTariff(_3G).withProvider(O2).withContract(PAYM).withSegment(CONSUMER).withContractChannel(null).and().promotion();

        promoCode = "promoCode";
        doReturn(promoCode).when(messageSourceMock)
                .getMessage(any(String.class), any(String.class), any(Object[].class), any(String.class), any(Locale.class));

        doReturn(promotion).when(userServiceMock).setPotentialPromoByPromoCode(user, promoCode);
        doReturn(true).when(userServiceMock).applyPromotionByPromoCode(user, promotion);
        doReturn(true).when(userServiceMock).applyPotentialPromo(user.isO2User(), user, community);

        isPromotionForO24GConsumerApplied = promotionService.applyPotentialPromo(user, user.isO2User());

        then().validateAs(true);

        verify(messageSourceMock, times(0)).getMessage(any(String.class), any(String.class), any(Object[].class), any(String.class), any(Locale.class));
        verify(userServiceMock, times(0)).setPotentialPromoByPromoCode(user, promoCode);
        verify(userServiceMock, times(0)).applyPromotionByPromoCode(user, promotion);
        verify(userServiceMock, times(1)).applyPotentialPromo(user.isO2User(), user, community);
    }

    @Test
    public void shouldActivateVideoAudioFreeTrialForUserOnAudioBoughPeriod(){
        //given
        String userName = "userName";
        String userToken = "";
        String timestamp = "";
        String communityUri = "o2";
        String deviceUID = "deviceUid";

        user = new User().withUserName(userName).withDeviceUID(deviceUID).withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(communityUri))).withTariff(_4G).withProvider(O2).withContract(PAYG).withSegment(CONSUMER).withNextSubPayment(Integer.MAX_VALUE).withLastSuccessfulPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withMediaType(AUDIO)));
        promotion = new Promotion();

        promoCode = "promoCode";

        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);
        when(userServiceMock.checkCredentials(userName, userToken, timestamp, communityUri, deviceUID)).thenReturn(user);
        when(userServiceMock.canActivateVideoTrial(user)).thenReturn(true);

        doReturn(promotion).when(userServiceMock).setPotentialPromoByPromoCode(user, promoCode);
        doReturn(true).when(userServiceMock).applyPromotionByPromoCode(user, promotion);
        doReturn(user).when(userServiceMock).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());
        doReturn(user).when(userServiceMock).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);

        //when
        User actualUser = promotionService.activateVideoAudioFreeTrial(user);

       //then
        assertEquals(user, actualUser);

        verify(userServiceMock, times(0)).checkCredentials(userName, userToken, timestamp, communityUri, deviceUID);
        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);
        verify(userServiceMock, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(userServiceMock, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(userServiceMock, times(0)).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());
        verify(userServiceMock, times(1)).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
    }

    @Test
    public void shouldActivateVideoAudioFreeTrialForUserOnFreeTrial(){
        //given
        String userName = "userName";
        String userToken = "";
        String timestamp = "";
        String communityUri = "o2";
        String deviceUID = "deviceUid";

        user = new User().withUserName(userName).withDeviceUID(deviceUID).withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(communityUri))).withTariff(_4G).withProvider(O2).withContract(PAYG).withSegment(CONSUMER).withFreeTrialExpiredMillis(Long.MAX_VALUE);
        promotion = new Promotion();

        promoCode = "promoCode";

        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);
        when(userServiceMock.checkCredentials(userName, userToken, timestamp, communityUri, deviceUID)).thenReturn(user);
        when(userServiceMock.canActivateVideoTrial(user)).thenReturn(true);

        doReturn(promotion).when(userServiceMock).setPotentialPromoByPromoCode(user, promoCode);
        doReturn(true).when(userServiceMock).applyPromotionByPromoCode(user, promotion);
        doReturn(user).when(userServiceMock).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());
        doReturn(user).when(userServiceMock).unsubscribeAndSkipFreeTrial(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);

        //when
        User actualUser = promotionService.activateVideoAudioFreeTrial(user);

        //then
        assertEquals(user, actualUser);

        verify(userServiceMock, times(0)).checkCredentials(userName, userToken, timestamp, communityUri, deviceUID);
        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);
        verify(userServiceMock, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(userServiceMock, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(userServiceMock, times(0)).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());
        verify(userServiceMock, times(1)).unsubscribeAndSkipFreeTrial(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
    }

    @Test
    public void shouldActivateVideoAudioFreeTrialForUserWithActivePaymentDetails(){
        //given
        String userName = "userName";
        String userToken = "";
        String timestamp = "";
        String deviceUID = "deviceUid";

        user = new User().withUserName(userName).withDeviceUID(deviceUID).withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(O2_REWRITE_URL_PARAMETER))).withTariff(_4G).withProvider(O2).withContract(PAYG).withSegment(CONSUMER).withNextSubPayment(Integer.MAX_VALUE).withCurrentPaymentDetails(new O2PSMSPaymentDetails().withActivated(true));
        promotion = new Promotion();

        promoCode = "promoCode";

        doReturn(promoCode).when(messageSourceMock).getMessage(O2_REWRITE_URL_PARAMETER, "promocode.for.o2.consumer.4g.payg.direct", null, null, null);
        when(userServiceMock.checkCredentials(userName, userToken, timestamp, O2_REWRITE_URL_PARAMETER, deviceUID)).thenReturn(user);
        when(userServiceMock.canActivateVideoTrial(user)).thenReturn(true);

        doReturn(promotion).when(userServiceMock).setPotentialPromoByPromoCode(user, promoCode);
        doReturn(true).when(userServiceMock).applyPromotionByPromoCode(user, promotion);
        doReturn(user).when(userServiceMock).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());
        doReturn(user).when(userServiceMock).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);

        //when
        User actualUser = promotionService.activateVideoAudioFreeTrial(user);

        //then
        assertEquals(user, actualUser);

        verify(userServiceMock, times(0)).checkCredentials(userName, userToken, timestamp, O2_REWRITE_URL_PARAMETER, deviceUID);
        verify(messageSourceMock, times(1)).getMessage(O2_REWRITE_URL_PARAMETER, PROMO_CODE_FOR_O2_CONSUMER_4G_PAYG_DIRECT, null, null, null);
        verify(userServiceMock, times(1)).setPotentialPromoByPromoCode(user, promoCode);
        verify(userServiceMock, times(1)).applyPromotionByPromoCode(user, promotion);
        verify(userServiceMock, times(1)).unsubscribeUser(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.getDescription());
        verify(userServiceMock, times(0)).skipBoughtPeriodAndUnsubscribe(user, VIDEO_AUDIO_FREE_TRIAL_ACTIVATION);
    }

    @Test(expected = ServiceException.class)
    public void shouldDoNotActivateVideoAudioFreeTrialWhenNoPromotion(){
        //given
        String userName = "userName";
        String userToken = "";
        String timestamp = "";
        String communityUri = "o2";
        String deviceUID = "deviceUid";

        user = new User().withUserName(userName).withDeviceUID(deviceUID).withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(communityUri))).withTariff(_4G).withProvider(O2).withContract(PAYG).withSegment(CONSUMER);
        promotion = new Promotion();

        promoCode = "promoCode";

        doReturn(promoCode).when(messageSourceMock).getMessage("promocode.for.o2.consumer.4g.payg.direct", null, null, null);
        when(userServiceMock.checkCredentials(userName, userToken, timestamp, communityUri, deviceUID)).thenReturn(user);
        when(userServiceMock.canActivateVideoTrial(user)).thenReturn(true);

        doReturn(null).when(userServiceMock).setPotentialPromoByPromoCode(user, promoCode);
        doReturn(true).when(userServiceMock).applyPromotionByPromoCode(user, promotion);

        //when
        promotionService.activateVideoAudioFreeTrial(user);
    }

    @Test(expected = ServiceException.class)
    public void shouldDoNotActivateVideoAudioFreeTrialWhenUserIsNotEligible(){
        //given
        String userName = "userName";
        String userToken = "";
        String timestamp = "";
        String communityUri = "o2";
        String deviceUID = "deviceUid";

        user = new User().withUserName(userName).withDeviceUID(deviceUID).withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(communityUri))).withTariff(_4G).withProvider(O2).withContract(PAYG).withSegment(CONSUMER);
        promotion = new Promotion();

        promoCode = "promoCode";

        doReturn(promoCode).when(messageSourceMock).getMessage("promocode.for.o2.consumer.4g.payg.direct", null, null, null);
        when(userServiceMock.checkCredentials(userName, userToken, timestamp, communityUri, deviceUID)).thenReturn(user);
        when(userServiceMock.canActivateVideoTrial(user)).thenReturn(false);

        doReturn(promotion).when(userServiceMock).setPotentialPromoByPromoCode(user, promoCode);
        doReturn(true).when(userServiceMock).applyPromotionByPromoCode(user, promotion);

        //when
        promotionService.activateVideoAudioFreeTrial(user);
    }

    @Test
    public void shouldUpdatePromotionNumUsers(){
        //given
        promotion = new Promotion();

        doReturn(1).when(promotionRepositoryMock).updatePromotionNumUsers(promotion);

        //when
        boolean isUpdated = promotionService.updatePromotionNumUsers(promotion);

        //then
        assertThat(isUpdated, is(true));
    }

    @Test(expected = ServiceException.class)
    public void shouldDoNotUpdatePromotionNumUsers(){
        //given
        promotion = new Promotion();

        doReturn(0).when(promotionRepositoryMock).updatePromotionNumUsers(promotion);

        //when
        promotionService.updatePromotionNumUsers(promotion);
    }


    private PromotionServiceTest validateAs(boolean b) {
        assertEquals(b, isPromotionForO24GConsumerApplied);
        return this;
    }

    PromotionServiceTest userWithCommunity(String rewriteUrlParameter){
        community = new Community();
        community.setRewriteUrlParameter(rewriteUrlParameter);

        userGroup = new UserGroup();
        userGroup.setCommunity(community);

        user = new User();
        user.setUserGroup(userGroup);
        return this;
    }

    PromotionServiceTest withTariff(Tariff tariff){
        user.setTariff(tariff);
        return this;
    }

    PromotionServiceTest withProvider(ProviderType provider){
        user.setProvider(provider);
        return this;
    }

    PromotionServiceTest withContract(Contract contract){
        user.setContract(contract);
        return this;
    }

    PromotionServiceTest withSegment(SegmentType segment){
        user.setSegment(CONSUMER);
        return this;
    }

    PromotionServiceTest withContractChannel(ContractChannel contractChannel){
        user.setContractChannel(contractChannel);
        return this;
    }

    private PromotionServiceTest given(){
        return this;
    }

    private PromotionServiceTest and(){
        return this;
    }

    private PromotionServiceTest then(){
        return this;
    }

}