package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.dto.admin.PendingPaymentDto;
import mobi.nowtechnologies.server.shared.enums.CurrencyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class PendingPaymentAsm {

	private static final Logger LOGGER = LoggerFactory.getLogger(PendingPaymentAsm.class);

	public static List<PendingPaymentDto> toPendingPaymentDtos(Collection<PendingPayment> pendingPayments) {
		LOGGER.debug("input parameters pendingPayments: [{}]", pendingPayments);

		final List<PendingPaymentDto> pendingPaymentDtos;
		if (pendingPayments.isEmpty()) {
			pendingPaymentDtos = Collections.<PendingPaymentDto> emptyList();
		} else {
			pendingPaymentDtos = new ArrayList<PendingPaymentDto>(pendingPayments.size());
			for (PendingPayment pendingPayment : pendingPayments) {
				pendingPaymentDtos.add(toPendingPaymentDto(pendingPayment));
			}
		}

		LOGGER.info("Output parameter pendingPaymentDtos=[{}]", pendingPaymentDtos);
		return pendingPaymentDtos;
	}

	public static PendingPaymentDto toPendingPaymentDto(PendingPayment pendingPayment) {
		LOGGER.debug("input parameters pendingPayment: [{}]", pendingPayment);

		PendingPaymentDto pendingPaymentDto = new PendingPaymentDto();

		pendingPaymentDto.setAmount(pendingPayment.getAmount());
		pendingPaymentDto.setCurrencyCode(CurrencyCode.valueOf(pendingPayment.getCurrencyISO()));
		pendingPaymentDto.setDate(new Date(pendingPayment.getTimestamp()));
		pendingPaymentDto.setGateway(pendingPayment.getPaymentSystem());
		pendingPaymentDto.setId(pendingPayment.getI());
		pendingPaymentDto.setInternalTxId(pendingPayment.getInternalTxId());

		final User user = pendingPayment.getUser();
		if (user != null) {
			pendingPaymentDto.setNumPsmsRetries(user.getNumPsmsRetries());
		}

		LOGGER.info("Output parameter pendingPaymentDto=[{}]", pendingPaymentDto);
		return pendingPaymentDto;
	}

}
