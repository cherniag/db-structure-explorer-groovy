/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Operator;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface OperatorRepository extends JpaRepository<Operator, Integer> {

    @Query("select paymentPolicy.operator from PaymentPolicy paymentPolicy " +
           "where paymentPolicy.communityId = :comunityId " +
           "and paymentPolicy.paymentType = :paymentType")
    List<Operator> findOperators(@Param("comunityId") Integer communityId, @Param("paymentType") String paymentType);

    @Query("select o from Operator o where o.id = (select min(op.id) from Operator op)")
    Operator findFirst();
}
