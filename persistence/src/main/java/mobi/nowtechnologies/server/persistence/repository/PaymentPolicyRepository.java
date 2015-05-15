package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Tariff;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Titov Mykhaylo (titov)
 */
public interface PaymentPolicyRepository extends JpaRepository<PaymentPolicy, Integer> {

    //TODO should be replaced by findPaymentPolicy()
    @Query(value = "select paymentPolicy.appStoreProductId " +
                   "from PaymentPolicy paymentPolicy " +
                   "where " +
                   "paymentPolicy.community=?1 " +
                   "and paymentPolicy.appStoreProductId is not NULL " +
                   "and paymentPolicy.online is true " +
                   "and (CURRENT_TIMESTAMP between paymentPolicy.startDateTime and paymentPolicy.endDateTime)")
    List<String> findAppStoreProductIdsByCommunityAndAppStoreProductIdIsNotNull(Community community);

    //TODO should be replaced by findPaymentPolicy()
    @Query(value = "select paymentPolicy " +
                   "from PaymentPolicy paymentPolicy " +
                   "where paymentPolicy.community=?1 " +
                   "and paymentPolicy.appStoreProductId=?2")
    PaymentPolicy findByCommunityAndAppStoreProductId(Community community, String appStoreProductId);

    @Query(value = "select paymentPolicy " +
                   "from PaymentPolicy paymentPolicy " +
                   "where " +
                   "paymentPolicy.community=?1 " +
                   "and paymentPolicy.paymentType= '" + O2PSMSPaymentDetails.O2_PSMS_TYPE + "' " +
                   "and (paymentPolicy.provider=?2 or paymentPolicy.provider is null)" +
                   "and (paymentPolicy.segment=?3 or paymentPolicy.segment is null)" +
                   "and (paymentPolicy.contract=?4 or paymentPolicy.contract is null)" +
                   "and paymentPolicy.tariff=?5 " +
                   "and paymentPolicy.isDefault is true " +
                   "and paymentPolicy.online is true " +
                   "and (CURRENT_TIMESTAMP between paymentPolicy.startDateTime and paymentPolicy.endDateTime)")
    PaymentPolicy findDefaultO2PsmsPaymentPolicy(Community community, ProviderType provider, SegmentType segment, Contract contract, Tariff tariff);

    @Query(value = "select paymentPolicy " +
                   "from PaymentPolicy paymentPolicy " +
                   "where " +
                   "paymentPolicy.community=?1 " +
                   "and (paymentPolicy.provider=?2 or paymentPolicy.provider is null)" +
                   "and (paymentPolicy.segment=?3 or paymentPolicy.segment is null)" +
                   "and (paymentPolicy.contract=?4 or paymentPolicy.contract is null)" +
                   "and paymentPolicy.tariff=?5 " +
                   "and paymentPolicy.mediaType in ?6 " +
                   "and paymentPolicy.online is true " +
                   "and (CURRENT_TIMESTAMP between paymentPolicy.startDateTime and paymentPolicy.endDateTime)")
    List<PaymentPolicy> findPaymentPolicies(Community community, ProviderType provider, SegmentType segment, Contract contract, Tariff tariff, List<MediaType> mediaTypes);


    @Query(value = "select paymentPolicy " +
                   "from PaymentPolicy paymentPolicy " +
                   "where paymentPolicy.community=?1 " +
                   "and paymentPolicy.provider=?2 " +
                   "and paymentPolicy.paymentType=?3 " +
                   "and paymentPolicy.online is true " +
                   "and (CURRENT_TIMESTAMP between paymentPolicy.startDateTime and paymentPolicy.endDateTime)")
    PaymentPolicy findPaymentPolicy(Community community, ProviderType providerType, String paymentType);

}
