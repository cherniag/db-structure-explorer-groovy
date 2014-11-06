package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.shared.dto.admin.PaymentDetailsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Titov Mykhaylo (titov)
 */
public class PaymentDetailsAsm {

	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentDetailsAsm.class);

	public static PaymentDetailsDto toPaymentDetailsDto(PaymentDetails paymentDetails) {
		LOGGER.debug("input parameters paymentDetails: [{}] ", paymentDetails);

		PaymentDetailsDto paymentDetailsDto = new PaymentDetailsDto();

		paymentDetailsDto.setActivated(paymentDetails.isActivated());
		paymentDetailsDto.setCreationTimestamp(new Date(paymentDetails.getCreationTimestampMillis()));
		paymentDetailsDto.setDescriptionError(paymentDetails.getDescriptionError());
		paymentDetailsDto.setDisableTimestamp(new Date(paymentDetails.getDisableTimestampMillis()));
		paymentDetailsDto.setId(paymentDetails.getI());
		paymentDetailsDto.setLastPaymentStatus(paymentDetails.getLastPaymentStatus());
		paymentDetailsDto.setMadeRetries(paymentDetails.getMadeRetries());
		paymentDetailsDto.setRetriesOnError(paymentDetails.getRetriesOnError());

		LOGGER.info("Output parameter paymentDetailsDto=[{}]", paymentDetailsDto);
		return paymentDetailsDto;
	}

}
