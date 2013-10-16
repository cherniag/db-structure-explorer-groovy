package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.admin.AccountLogDto;
import mobi.nowtechnologies.server.shared.enums.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class AccountLogAsm {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountLogAsm.class);

	public static List<AccountLogDto> toAccountLogDtos(Collection<AccountLog> accountLogs) {
		LOGGER.debug("input parameters accountLogs: [{}]", accountLogs);

		final List<AccountLogDto> accountLogDtos;
		if (accountLogs.isEmpty()) {
			accountLogDtos = Collections.<AccountLogDto>emptyList();
		} else {
			accountLogDtos = new ArrayList<AccountLogDto>(accountLogs.size());
			for (AccountLog accountLog : accountLogs) {
				accountLogDtos.add(toAccountLogDto(accountLog));
			}
		}

		LOGGER.info("Output parameter accountLogDtos=[{}]", accountLogDtos);
		return accountLogDtos;
	}

	public static AccountLogDto toAccountLogDto(AccountLog accountLog) {
		LOGGER.debug("input parameters accountLog: [{}]", accountLog);

		final Media media = accountLog.getMedia();
		final SubmittedPayment submittedPayment = accountLog.getSubmittedPayment();

		//final int subWeeks;
		final String gateway;
		final String internalTxId;
		if (submittedPayment != null) {
			gateway = submittedPayment.getPaymentSystem();
			internalTxId=submittedPayment.getInternalTxId();
			//subWeeks = submittedPayment.getSubweeks();
		} else {
			gateway = null;
			internalTxId=null;
//			if (media != null){
//				subWeeks = media.getPrice().intValue();
//			} else
//				throw new ServiceException("Couldn't find amount for accountLog with [" + accountLog.getId() + "]");
		}
		
		final Integer amount;
		final String amountCurrency;
		if (accountLog.getTransactionType().equals(TransactionType.TRACK_PURCHASE)
				|| accountLog.getTransactionType().equals(TransactionType.SUBSCRIPTION_CHARGE)) {
			amount = 1;
			amountCurrency="WEEKS";
		} else if(accountLog.getTransactionType().equals(TransactionType.CARD_TOP_UP)
				|| accountLog.getTransactionType().equals(TransactionType.REFUND)){
			if (submittedPayment != null) {
				amount = submittedPayment.getAmount().intValue();
				amountCurrency = submittedPayment.getCurrencyISO();
			}else{
				amount = null;
				amountCurrency=null;
			}
		} else {
			amount = null;
			amountCurrency=null;
		}

		AccountLogDto accountLogDto = new AccountLogDto();

		accountLogDto.setAmount(amount);
		accountLogDto.setAmountCurrency(amountCurrency);
		accountLogDto.setBalanceAfter(accountLog.getBalanceAfter());
		accountLogDto.setGateway(gateway);
		accountLogDto.setId(accountLog.getId());
		accountLogDto.setLogTimestamp(Utils.getDateFromInt(accountLog.getLogTimestamp()));
		accountLogDto.setPromoCode(accountLog.getPromoCode());
		accountLogDto.setRelatedMediaId(accountLog.getRelatedMediaUID());

		if (media != null)
			accountLogDto.setRelatedMediaIsrc(media.getIsrc());

		accountLogDto.setRelatedPaymentUID(accountLog.getRelatedPaymentUID());
		accountLogDto.setTransactionType(accountLog.getTransactionType());
		accountLogDto.setUserId(accountLog.getUserId());
		accountLogDto.setInternalTxId(internalTxId);

		LOGGER.debug("Output parameter accountLogDto=[{}]", accountLogDto);
		return accountLogDto;
	}

}
