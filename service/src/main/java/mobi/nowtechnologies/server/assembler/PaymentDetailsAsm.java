package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.shared.dto.admin.PaymentDetailsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class PaymentDetailsAsm {

	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentDetailsAsm.class);

	@SuppressWarnings("unchecked")
	public static List<PaymentDetailsDto> toPaymentDetailsDtos(Collection<PaymentDetails> paymentDetailsList) {
		LOGGER.debug("input parameters paymentDetailsList: [{}]", paymentDetailsList);

		final List<PaymentDetailsDto> paymentDetailsDtos;
		if (paymentDetailsList.isEmpty()) {
			paymentDetailsDtos = Collections.EMPTY_LIST;
		} else {
			paymentDetailsDtos = new ArrayList<PaymentDetailsDto>(paymentDetailsList.size());

			for (PaymentDetails paymentDetails : paymentDetailsList) {
				paymentDetailsDtos.add(toPaymentDetailsDto(paymentDetails));
			}
		}

		LOGGER.debug("Output parameter paymentDetailsDtos=[{}]", paymentDetailsDtos);
		return paymentDetailsDtos;
	}

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
		// paymentDetailsDto.setPaymentPolicy(paymentPolicy);
		// paymentDetailsDto.setPromotionPaymentPolicyDto(promotionPaymentPolicyDto);
		paymentDetailsDto.setRetriesOnError(paymentDetails.getRetriesOnError());
		// paymentDetailsDto.setUserDto(userDto);

		LOGGER.info("Output parameter paymentDetailsDto=[{}]", paymentDetailsDto);
		return paymentDetailsDto;
	}

}
