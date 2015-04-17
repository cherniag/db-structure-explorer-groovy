package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 * @author Titov Mykhaylo (titov)
 */
public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails, Long> {

    @Query(value = "select pd from PaymentDetails pd where pd.activated=true and (pd.migPhoneNumber=CONCAT(?1, '.', ?2) or (TYPE(pd) like CONCAT(LOWER(?1),'%') and ?2=TRIM('+' from pd.phoneNumber)))")
    List<PaymentDetails> findActivatedPaymentDetails(String operatorName, String phoneNumber);

    @Query(value = "select pd from PaymentDetails pd " +
                   "join FETCH pd.owner o " +
                   "join FETCH o.userGroup uG " +
                   "join FETCH ug.community c " +
                   "where " +
                   "pd.activated=false " +
                   "and (pd.lastPaymentStatus='ERROR' or pd.lastPaymentStatus='EXTERNAL_ERROR') " +
                   "and pd.lastFailedPaymentNotificationMillis is null " +
                   "and c.rewriteUrlParameter = ?1")
    List<PaymentDetails> findFailedPaymentWithNoNotificationPaymentDetails(String communityUrl, Pageable pageable);

    @Query("select pd from PaymentDetails pd where pd.owner = :user order by pd.creationTimestampMillis")
    List<PaymentDetails> findPaymentDetailsByOwner(@Param("user") User user);

    @Query("select pd from PaymentDetails pd where pd.owner.id = :userId and pd.lastPaymentStatus = :lastPaymentStatus")
    List<PaymentDetails> findPaymentDetailsByOwnerIdAndLastPaymentStatus(@Param("userId") int userId, @Param("lastPaymentStatus") PaymentDetailsStatus status);


    @Query("select paymentDetails from PaymentDetails paymentDetails join paymentDetails.submittedPayments submittedPayments where paymentDetails.owner.id=:userId and submittedPayments.type=:paymentDetailsType order by paymentDetails.creationTimestampMillis desc")
    List<PaymentDetails> findByUserIdAndPaymentDetailsType(@Param("userId") int userId, @Param("paymentDetailsType") PaymentDetailsType paymentDetailsType);
}