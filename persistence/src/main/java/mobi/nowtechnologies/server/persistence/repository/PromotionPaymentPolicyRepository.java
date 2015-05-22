/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PromotionPaymentPolicy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PromotionPaymentPolicyRepository extends JpaRepository<PromotionPaymentPolicy, Long> {

    @Query("select promo from PromotionPaymentPolicy promo " +
           "join promo.paymentPolicies pPolicy " +
           "where promo.promotion = :promotion " +
           "and pPolicy = :paymentPolicy")
    PromotionPaymentPolicy findPromotionPaymentPolicy(@Param("promotion") Promotion promotion, @Param("paymentPolicy") PaymentPolicy paymentPolicy);

}
