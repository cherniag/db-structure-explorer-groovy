package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Titov Mykhaylo (titov)
 */
public interface PendingPaymentRepository extends JpaRepository<PendingPayment, Long> {

    @Query(value = "select pendingPayment from PendingPayment pendingPayment" +
                   " left join FETCH pendingPayment.user user" +
                   " where" +
                   " pendingPayment.userId=?1")
    List<PendingPayment> findByUserId(int userId);

    @Query("select pendingPayment from PendingPayment pendingPayment where pendingPayment.expireTimeMillis < :timestamp")
    List<PendingPayment> findExpiredPayments(@Param("timestamp") long timestamp);


    @Query("select pendingPayment from PendingPayment pendingPayment where pendingPayment.internalTxId = :internalTxId")
    PendingPayment findByExternalTransactionId(@Param("internalTxId") String internalTxId);

}