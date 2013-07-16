package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.DataToDoRefund;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.DataToDoRefundRepository;
import mobi.nowtechnologies.server.shared.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: Titov Mykhaylo (titov)
 * 15.07.13 16:17
 */
public interface DataToDoRefundService {

    DataToDoRefund logOnTariffMigration(User user);
}
