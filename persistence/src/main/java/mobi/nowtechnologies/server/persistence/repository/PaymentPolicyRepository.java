package mobi.nowtechnologies.server.persistence.repository;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;

import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface PaymentPolicyRepository extends JpaRepository<PaymentPolicy, Short>{
	
	@Query(value="select paymentPolicy.appStoreProductId from PaymentPolicy paymentPolicy " +
			"where paymentPolicy.community=?1 " +
			"and paymentPolicy.appStoreProductId is not NULL")
	List<String> findAppStoreProductIdsByCommunityAndAppStoreProductIdIsNotNull(Community community);
	
	@Query(value="select paymentPolicy from PaymentPolicy paymentPolicy " +
			"where paymentPolicy.community=?1 " +
			"and paymentPolicy.appStoreProductId=?2")
	PaymentPolicy findByCommunityAndAppStoreProductId(Community community, String appStoreProductId);

	@Query(value="select paymentPolicy from PaymentPolicy paymentPolicy "+
			"where paymentPolicy.community=?1 " +
			"and paymentPolicy.paymentType=?2")
	List<PaymentPolicy> getPaymentPoliciesByPaymentType(Community community, String paymentType);

    @Query(value="select p from PaymentPolicy p "+
            "where p.community=?1 and p.segment is null " +
            " group by p.paymentType ")
    List<PaymentPolicy> getPaymentPoliciesWithOutSegment(Community community);

	@Query(value="select paymentPolicy from PaymentPolicy paymentPolicy "+
			"where paymentPolicy.community=?1 " +
			"and paymentPolicy.paymentType<>?2 ")
	List<PaymentPolicy> getPaymentPoliciesWithoutSelectedPaymentType(Community community, String paymentType);

    @Query(value="select p from PaymentPolicy p "+
            " where p.community=?1  and p.segment = ?2 " +
            " group by p.paymentType")
    List<PaymentPolicy> getPaymentPolicies(Community community, SegmentType segment);
}
