package mobi.nowtechnologies.server.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;

import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import static mobi.nowtechnologies.server.shared.enums.ProviderType.VF;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;

@RunWith(PowerMockRunner.class)
public class PaymentPolicyServiceTest {
	
	private PaymentPolicyService paymentPolicyServiceFixture;
	private PaymentPolicyRepository paymentPolicyRepositoryMock;
	
	@Before
	public void before() {
		paymentPolicyRepositoryMock = Mockito.mock(PaymentPolicyRepository.class);

		paymentPolicyServiceFixture = new PaymentPolicyService();
		paymentPolicyServiceFixture.setPaymentPolicyRepository(paymentPolicyRepositoryMock);
	}
	
	@Test
	public void mergePaymentPolicyWithPromotion() {
		PaymentPolicy paymentPolicy = createPaymentPolicy();
			
		PromotionPaymentPolicy promotion = createPromotionPaymentPolicy();
		
		PaymentPolicyDto dto = paymentPolicyServiceFixture.getPaymentPolicy(paymentPolicy, promotion);
		
		assertNotNull(dto);
		assertEquals(Integer.valueOf(paymentPolicy.getOperator().getId()), dto.getOperator());
		assertEquals(paymentPolicy.getOperator().getName(), dto.getOperatorName());
		assertEquals(paymentPolicy.getPaymentType(), dto.getPaymentType());
		assertEquals(paymentPolicy.getShortCode(), dto.getShortCode());
		assertEquals(paymentPolicy.getCurrencyISO(), dto.getCurrencyISO());
		
		assertEquals(paymentPolicy.getSubcost(), dto.getOldSubcost());
		assertEquals(Integer.valueOf(paymentPolicy.getSubweeks()), dto.getOldSubweeks());
		assertEquals(promotion.getSubcost(), dto.getSubcost());
		assertEquals(promotion.getSubweeks(), dto.getSubweeks());
	}
	
	@Test
	public void mergePaymentPolicyWithNullPromotion() {
		PaymentPolicy paymentPolicy = createPaymentPolicy();
		PaymentPolicyDto dto = paymentPolicyServiceFixture.getPaymentPolicy(paymentPolicy, null);
		
		assertNotNull(dto);
		assertEquals(Integer.valueOf(paymentPolicy.getOperator().getId()), dto.getOperator());
		assertEquals(paymentPolicy.getOperator().getName(), dto.getOperatorName());
		assertEquals(paymentPolicy.getPaymentType(), dto.getPaymentType());
		assertEquals(paymentPolicy.getShortCode(), dto.getShortCode());
		assertEquals(paymentPolicy.getCurrencyISO(), dto.getCurrencyISO());
		
		assertEquals(paymentPolicy.getSubcost(), dto.getOldSubcost());
		assertEquals(Integer.valueOf(paymentPolicy.getSubweeks()), dto.getOldSubweeks());
		assertEquals(paymentPolicy.getSubcost(), dto.getSubcost());
		assertEquals(Integer.valueOf(paymentPolicy.getSubweeks()), dto.getSubweeks());
	}
	
	@Test
	public void mergePaymentPolicyWithNulls() {
		PaymentPolicyDto dto = paymentPolicyServiceFixture.getPaymentPolicy(null, null);
		assertNull(dto);
	}
	
	@Test
	public void mergePaymentPolicyWithNullOperator() {
		PaymentPolicy paymentPolicy = createPaymentPolicy();
		paymentPolicy.setOperator(null);
		
		PaymentPolicyDto dto = paymentPolicyServiceFixture.getPaymentPolicy(paymentPolicy, null);
		
		assertNotNull(dto);		
		assertEquals(paymentPolicy.getPaymentType(), dto.getPaymentType());
		assertEquals(paymentPolicy.getShortCode(), dto.getShortCode());
		assertEquals(paymentPolicy.getCurrencyISO(), dto.getCurrencyISO());
		
		assertEquals(paymentPolicy.getSubcost(), dto.getOldSubcost());
		assertEquals(Integer.valueOf(paymentPolicy.getSubweeks()), dto.getOldSubweeks());
		assertEquals(paymentPolicy.getSubcost(), dto.getSubcost());
		assertEquals(Integer.valueOf(paymentPolicy.getSubweeks()), dto.getSubweeks());
	}
	
	private PromotionPaymentPolicy createPromotionPaymentPolicy() {
		PromotionPaymentPolicy promotion = new PromotionPaymentPolicy();
			promotion.setSubcost(new BigDecimal(3));
			promotion.setSubweeks(7);
		return promotion;
	}

	private PaymentPolicy createPaymentPolicy() {
		PaymentPolicy paymentPolicy = new PaymentPolicy();
			paymentPolicy.setCurrencyISO("GBP");
				Operator operator = new Operator();
					operator.setMigName("MIG01OU");
					operator.setName("Orange UK");
			paymentPolicy.setOperator(operator);
			paymentPolicy.setPaymentType(UserRegInfo.PaymentType.CREDIT_CARD);
			paymentPolicy.setShortCode("80988");
			paymentPolicy.setSubcost(new BigDecimal(10));
			paymentPolicy.setSubweeks((byte)5);
				Community community = new Community();
			paymentPolicy.setCommunity(community);
		return paymentPolicy;
	}
	
	@Test
	public void testFindAppStoreProductIdsByCommunityAndAppStoreProductIdIsNotNull_Success(){
		Community community = CommunityFactory.createCommunity();
		
		List<String> appStoreProductIds = Collections.<String>emptyList();
		
		Mockito.when(paymentPolicyRepositoryMock.findAppStoreProductIdsByCommunityAndAppStoreProductIdIsNotNull(community)).thenReturn(appStoreProductIds);
		
		List<String> actualAppStoreProductIds = paymentPolicyServiceFixture.findAppStoreProductIdsByCommunityAndAppStoreProductIdIsNotNull(community);
		
		assertNotNull(actualAppStoreProductIds);
		assertEquals(appStoreProductIds, actualAppStoreProductIds);
		
		Mockito.verify(paymentPolicyRepositoryMock, times(1)).findAppStoreProductIdsByCommunityAndAppStoreProductIdIsNotNull(community);
	}
	
	@Test
	public void testFindByCommunityAndAppStoreProductId_Success() {
		
		final Community community = CommunityFactory.createCommunity();
		final String appStoreProductId = "appStoreProductId";
		
		final PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy(); 
		
		Mockito.when(paymentPolicyRepositoryMock.findByCommunityAndAppStoreProductId(community, appStoreProductId)).thenReturn(paymentPolicy);
		
		final PaymentPolicy actualPaymentPolicy = paymentPolicyServiceFixture.findByCommunityAndAppStoreProductId(community, appStoreProductId);
		
		assertNotNull(actualPaymentPolicy);
		assertEquals(paymentPolicy, actualPaymentPolicy);
		
		Mockito.verify(paymentPolicyRepositoryMock, times(1)).findByCommunityAndAppStoreProductId(community, appStoreProductId);
		
	}

}