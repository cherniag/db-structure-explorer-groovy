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

    @Query("select paymentPolicy " +
           "from PaymentPolicy paymentPolicy " +
           "where paymentPolicy.community=?1 " +
           "and paymentPolicy.appStoreProductId=?2 " +
           "and paymentPolicy.online is true")
    PaymentPolicy findByCommunityAndAppStoreProductId(Community community, String appStoreProductId);

    @Query("select paymentPolicy " +
           "from PaymentPolicy paymentPolicy " +
           "where " +
           "paymentPolicy.community=?1 " +
           "and (paymentPolicy.provider=?2 or paymentPolicy.provider is null or ?2 is null) " +
           "and (paymentPolicy.segment=?3 or paymentPolicy.segment is null or ?3 is null) " +
           "and (paymentPolicy.contract=?4 or paymentPolicy.contract is null or ?4 is null) " +
           "and (paymentPolicy.tariff=?5 or ?5 is null) " +
           "and (paymentPolicy.mediaType in (?6) ) " +
           "and (paymentPolicy.paymentType in (?7) )  " +
           "and (paymentPolicy.isDefault=?8 or ?8 is null) " +
           "and paymentPolicy.online = true " +
           "and (CURRENT_TIMESTAMP between paymentPolicy.startDateTime and paymentPolicy.endDateTime)")
    List<PaymentPolicy> getPaymentPolicies(Community community, ProviderType provider, SegmentType segment, Contract contract, Tariff tariff, List<MediaType> mediaTypes, List<String> paymentTypes,
                                           Boolean isDefault);
}
