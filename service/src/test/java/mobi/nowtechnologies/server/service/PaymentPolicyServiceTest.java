package mobi.nowtechnologies.server.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.Operator;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
public class PaymentPolicyServiceTest {
	
	private PaymentPolicyService fixturePaymentPolicyService;
	private PaymentPolicyRepository mockPaymentPolicyRepository;
	
	@Before
	public void before() {
		mockPaymentPolicyRepository = Mockito.mock(PaymentPolicyRepository.class);

		fixturePaymentPolicyService = new PaymentPolicyService();
		fixturePaymentPolicyService.setPaymentPolicyRepository(mockPaymentPolicyRepository);
	}
	
	@Test
	public void mergePaymentPolicyWithPromotion() {
		PaymentPolicy paymentPolicy = createPaymentPolicy();
			
		PromotionPaymentPolicy promotion = createPromotionPaymentPolicy();
		
		PaymentPolicyDto dto = fixturePaymentPolicyService.getPaymentPolicy(paymentPolicy, promotion);
		
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
		PaymentPolicyDto dto = fixturePaymentPolicyService.getPaymentPolicy(paymentPolicy, null);
		
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
		PaymentPolicyDto dto = fixturePaymentPolicyService.getPaymentPolicy(null, null);
		assertNull(dto);
	}
	
	@Test
	public void mergePaymentPolicyWithNullOperator() {
		PaymentPolicy paymentPolicy = createPaymentPolicy();
		paymentPolicy.setOperator(null);
		
		PaymentPolicyDto dto = fixturePaymentPolicyService.getPaymentPolicy(paymentPolicy, null);
		
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
	public void testGetPaymentPoliciesWithouSelectedPaymentTypeGroupdeByPaymentType_Success() {
		
		Community community = CommunityFactory.createCommunity();
		String paymentType ="paymentType";
		
		List<PaymentPolicy> paymentPolicies = Collections.<PaymentPolicy>emptyList();
				
		Mockito.when(mockPaymentPolicyRepository.getPaymentPoliciesWithouSelectedPaymentTypeGroupdeByPaymentType(community, paymentType)).thenReturn(paymentPolicies);
		
		List<PaymentPolicy> actualPaymentPolicies = fixturePaymentPolicyService.getPaymentPoliciesWithouSelectedPaymentTypeGroupdeByPaymentType(community, paymentType);
		
		assertNotNull(actualPaymentPolicies);
		assertEquals(paymentPolicies, actualPaymentPolicies);
	}

}