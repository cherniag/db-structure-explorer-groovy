package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.payment.ITunesPaymentLock;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author: Gennadii Cherniaiev Date: 12/10/2014
 */
public interface ITunesPaymentLockRepository extends JpaRepository<ITunesPaymentLock, Long> {}
