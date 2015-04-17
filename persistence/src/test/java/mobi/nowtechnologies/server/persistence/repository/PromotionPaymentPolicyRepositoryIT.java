/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.domain.payment.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;


public class PromotionPaymentPolicyRepositoryIT extends AbstractRepositoryIT {

    @Resource
    PromotionPaymentPolicyRepository promotionPaymentPolicyRepository;

    @Resource
    PromotionRepository promotionRepository;

    @Resource
    PaymentPolicyRepository paymentPolicyRepository;

    @Test
    public void testFindOperators(){
        PaymentPolicy paymentPolicy = paymentPolicyRepository.findOne(4);
        Promotion promotion = promotionRepository.findOne(1);

        PromotionPaymentPolicy promotionPaymentPolicy = new PromotionPaymentPolicy();
        promotionPaymentPolicy.setPromotion(promotion);
        promotionPaymentPolicy.setPaymentPolicies(Arrays.asList(paymentPolicy, paymentPolicyRepository.findOne(5)));
        promotionPaymentPolicy.setPeriod(new Period(DurationUnit.WEEKS, 4));
        promotionPaymentPolicy.setSubcost(new BigDecimal("12.00"));
        promotionPaymentPolicyRepository.save(promotionPaymentPolicy);

        PromotionPaymentPolicy foundPromotionPaymentPolicy = promotionPaymentPolicyRepository.findPromotionPaymentPolicy(promotion, paymentPolicy);

        assertEquals(promotionPaymentPolicy.getId(), foundPromotionPaymentPolicy.getId());
    }

    @After
    public void afterTest(){
        promotionPaymentPolicyRepository.deleteAll();
    }
}