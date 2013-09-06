package mobi.nowtechnologies.server.persistence.repository;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;

import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface PaymentPolicyRepository extends JpaRepository<PaymentPolicy, Integer>{
	
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
            " where p.community=?1  and p.segment = ?2 ")
    List<PaymentPolicy> getPaymentPolicies(Community community, SegmentType segment);

    @Query(value="select paymentPolicy from PaymentPolicy paymentPolicy "+
            "where " +
            "paymentPolicy.community=?1 " +
            "and paymentPolicy.paymentType= '" + O2PSMSPaymentDetails.O2_PSMS_TYPE +"' " +
            "and (paymentPolicy.provider=?2 or paymentPolicy.provider is null)" +
            "and (paymentPolicy.segment=?3 or paymentPolicy.segment is null)" +
            "and (paymentPolicy.contract=?4 or paymentPolicy.contract is null)" +
            "and paymentPolicy.tariff=?5 " +
            "and paymentPolicy.isDefault=true ")
    PaymentPolicy findDefaultO2PsmsPaymentPolicy(Community community, ProviderType provider, SegmentType segment, Contract contract, Tariff tariff);
}
