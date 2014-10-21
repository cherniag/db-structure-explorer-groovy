package mobi.nowtechnologies.server.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.domain.payment.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;

import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import static mobi.nowtechnologies.server.shared.enums.PeriodUnit.WEEKS;
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
			
		PromotionPaymentPolicy promotionPaymentPolicy = createPromotionPaymentPolicy();
		
		PaymentPolicyDto paymentPolicyDto = paymentPolicyServiceFixture.getPaymentPolicy(paymentPolicy, promotionPaymentPolicy);
		
		assertNotNull(paymentPolicyDto);
		assertEquals(Integer.valueOf(paymentPolicy.getOperator().getId()), paymentPolicyDto.getOperator());
		assertEquals(paymentPolicy.getOperator().getName(), paymentPolicyDto.getOperatorName());
		assertEquals(paymentPolicy.getPaymentType(), paymentPolicyDto.getPaymentType());
		assertEquals(paymentPolicy.getShortCode(), paymentPolicyDto.getShortCode());
		assertEquals(paymentPolicy.getCurrencyISO(), paymentPolicyDto.getCurrencyISO());
		
		assertEquals(paymentPolicy.getSubcost(), paymentPolicyDto.getOldSubcost());
		Period period = paymentPolicy.getPeriod();
		assertEquals(Long.valueOf(period.getDuration()), paymentPolicyDto.getOldPeriod());
		assertEquals(period.getPeriodUnit(), paymentPolicyDto.getOldPeriodUnit());
		assertEquals(promotionPaymentPolicy.getPeriod().getDuration(), paymentPolicyDto.getPeriod());
		assertEquals(promotionPaymentPolicy.getPeriod().getPeriodUnit(), paymentPolicyDto.getPeriodUnit());
		assertEquals(promotionPaymentPolicy.getSubcost(), paymentPolicyDto.getSubcost());
	}
	
	@Test
	public void mergePaymentPolicyWithNullPromotion() {
		PaymentPolicy paymentPolicy = createPaymentPolicy();
		PaymentPolicyDto paymentPolicyDto = paymentPolicyServiceFixture.getPaymentPolicy(paymentPolicy, null);
		
		assertNotNull(paymentPolicyDto);
		assertEquals(Integer.valueOf(paymentPolicy.getOperator().getId()), paymentPolicyDto.getOperator());
		assertEquals(paymentPolicy.getOperator().getName(), paymentPolicyDto.getOperatorName());
		assertEquals(paymentPolicy.getPaymentType(), paymentPolicyDto.getPaymentType());
		assertEquals(paymentPolicy.getShortCode(), paymentPolicyDto.getShortCode());
		assertEquals(paymentPolicy.getCurrencyISO(), paymentPolicyDto.getCurrencyISO());
		
		assertNull(paymentPolicyDto.getOldSubcost());
		assertNull(paymentPolicyDto.getOldPeriod());
		assertNull(paymentPolicyDto.getOldPeriodUnit());
		assertEquals(paymentPolicy.getSubcost(), paymentPolicyDto.getSubcost());
		assertEquals(paymentPolicy.getPeriod().getDuration(), paymentPolicyDto.getPeriod());
		assertEquals(paymentPolicy.getPeriod().getPeriodUnit(), paymentPolicyDto.getPeriodUnit());
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

		assertNull(dto.getOldSubcost());
		assertNull(dto.getOldPeriod());
		assertNull(dto.getOldPeriodUnit());
		assertEquals(paymentPolicy.getSubcost(), dto.getSubcost());
		assertEquals(paymentPolicy.getPeriod().getDuration(), dto.getPeriod());
		assertEquals(paymentPolicy.getPeriod().getPeriodUnit(), dto.getPeriodUnit());
	}
	
	private PromotionPaymentPolicy createPromotionPaymentPolicy() {
		PromotionPaymentPolicy promotion = new PromotionPaymentPolicy();
			promotion.setSubcost(new BigDecimal(3));
			promotion.setPeriod(new Period().withDuration(7).withPeriodUnit(WEEKS));
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
			paymentPolicy.setPeriod(new Period().withDuration(5).withPeriodUnit(WEEKS));
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