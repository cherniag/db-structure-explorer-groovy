package mobi.nowtechnologies.server.assembler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.SubmittedPayment;
import mobi.nowtechnologies.server.shared.dto.admin.SubmittedPaymentDto;
import mobi.nowtechnologies.server.shared.enums.CurrencyCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class SubmittedPaymentAsm {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SubmittedPaymentAsm.class);
	
	public static List<SubmittedPaymentDto> toSubmittedPaymentDtos(Collection<SubmittedPayment> submittedPayments) {
		LOGGER.debug("input parameters submittedPayments: [{}]", submittedPayments);

		final List<SubmittedPaymentDto> submittedPaymentDtos;
		if (submittedPayments.isEmpty()) {
			submittedPaymentDtos = Collections.<SubmittedPaymentDto> emptyList();
		} else {
			submittedPaymentDtos = new ArrayList<SubmittedPaymentDto>(submittedPayments.size());
			for (SubmittedPayment submittedPayment : submittedPayments) {
				submittedPaymentDtos.add(toSubmittedPaymentDto(submittedPayment));
			}
		}

		LOGGER.info("Output parameter submittedPaymentDtos=[{}]", submittedPaymentDtos);
		return submittedPaymentDtos;
	}

	public static SubmittedPaymentDto toSubmittedPaymentDto(SubmittedPayment submittedPayment) {
		LOGGER.debug("input parameters submittedPayment: [{}]", submittedPayment);

		SubmittedPaymentDto submittedPaymentDto = new SubmittedPaymentDto();
		
		submittedPaymentDto.setAmount(submittedPayment.getAmount());
		submittedPaymentDto.setCurrencyCode(CurrencyCode.valueOf(submittedPayment.getCurrencyISO()));
		submittedPaymentDto.setDate(new Date(submittedPayment.getTimestamp()));
		submittedPaymentDto.setDescriptionError(submittedPayment.getDescriptionError());
		submittedPaymentDto.setGateway(submittedPayment.getPaymentSystem());
		submittedPaymentDto.setId(submittedPayment.getI());
		submittedPaymentDto.setInternalTxId(submittedPayment.getInternalTxId());

		LOGGER.info("Output parameter submittedPaymentDto=[{}]", submittedPaymentDto);
		return submittedPaymentDto;
	}


}
