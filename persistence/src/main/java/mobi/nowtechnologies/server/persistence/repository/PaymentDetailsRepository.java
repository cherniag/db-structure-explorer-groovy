package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;


/**
 * @author Titov Mykhaylo (titov)
 */
public interface PaymentDetailsRepository extends PagingAndSortingRepository<PaymentDetails, Long> {

	@Query(value="select pd from PaymentDetails pd where pd.activated=true and (pd.migPhoneNumber=CONCAT(?1, '.', ?2) or (TYPE(pd) like CONCAT(LOWER(?1),'%') and ?2=TRIM('+' from pd.phoneNumber)))")
	List<PaymentDetails> findActivatedPaymentDetails(String operatorName, String phoneNumber);

    @Query(value =  "select pd from PaymentDetails pd " +
            "join FETCH pd.owner o " +
            "join FETCH o.userGroup uG "+
            "join FETCH ug.community c " +
            "where " +
            "pd.activated=false " +
            "and pd.lastPaymentStatus='ERROR' " +
            "and pd.madeRetries=pd.retriesOnError " +
            "and pd.lastFailedPaymentNotificationMillis is null " +
            "and c.rewriteUrlParameter = ?1")
    List<PaymentDetails> findFailedPaymentWithNoNotificationPaymentDetails(String communityUrl, Pageable pageable);

}