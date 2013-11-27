package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.payment.PaymentStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PatmentStatusDao
 * 
 * @author Titov Mykhaylo (titov)
 * 
 */
public class PaymentStatusDao {
	private static Map<Integer, PaymentStatus> PAYMENT_STATUS_MAP_ID_AS_KEY;
	private static PaymentStatus NULL;
	private static PaymentStatus OK;
	private static PaymentStatus AWAITING_PSMS;
	private static PaymentStatus PSMS_ERROR;
	private static PaymentStatus PIN_PENDING;
	private static PaymentStatus AWAITING_PAYMENT;
	private static PaymentStatus PAY_PAL_ERROR;
	private static PaymentStatus AWAITING_PAY_PAL;

	private static void setEntityDao(EntityDao entityDao) {
		List<PaymentStatus> paymentStatusList = entityDao
				.findAll(PaymentStatus.class);
		Map<Integer, PaymentStatus> paymentStatusMapIdAsKey = new HashMap<Integer, PaymentStatus>();
		for (PaymentStatus paymentStatus : paymentStatusList) {
			paymentStatusMapIdAsKey.put(paymentStatus.getId(), paymentStatus);
			String paymentStatusName = paymentStatus.getName();
			if (paymentStatusName
					.equals(PaymentStatus.NULL))
				NULL = paymentStatus;
			else if (paymentStatusName
					.equals(PaymentStatus.OK))
				OK = paymentStatus;
			else if (paymentStatusName
					.equals(PaymentStatus.AWAITING_PSMS))
				AWAITING_PSMS = paymentStatus;
			else if (paymentStatusName
					.equals(PaymentStatus.PSMS_ERROR))
				PSMS_ERROR = paymentStatus;
			else if (paymentStatusName
					.equals(PaymentStatus.PIN_PENDING))
				PIN_PENDING = paymentStatus;
			else if (paymentStatusName
					.equals(PaymentStatus.AWAITING_PAYMENT))
				AWAITING_PAYMENT = paymentStatus;
			else if (paymentStatusName
					.equals(PaymentStatus.AWAITING_PAY_PAL))
				AWAITING_PAY_PAL = paymentStatus;
			else if (paymentStatusName
					.equals(PaymentStatus.PAY_PAL_ERROR))
				PAY_PAL_ERROR = paymentStatus;
		}
		PAYMENT_STATUS_MAP_ID_AS_KEY = Collections
				.unmodifiableMap(paymentStatusMapIdAsKey);
 
		if (NULL == null)
			throw new PersistenceException("The parameter NULL is null");
		if (OK == null)
			throw new PersistenceException("The parameter OK is null");
		if (AWAITING_PSMS == null)
			throw new PersistenceException(
					"The parameter AWAITING_PSMS is null");
		if (PSMS_ERROR == null)
			throw new PersistenceException("The parameter PSMS_ERROR is null");
		if (PIN_PENDING == null)
			throw new PersistenceException("The parameter PIN_PENDING is null");
		if (AWAITING_PAYMENT == null)
			throw new PersistenceException("The parameter AWAITING_PAYMENT is null");
		if (AWAITING_PAY_PAL == null)
			throw new PersistenceException("The parameter AWAITING_PAY_PAL is null");
		if (PAY_PAL_ERROR == null)
			throw new PersistenceException("The parameter PAY_PAL_ERROR is null");

	}

	public static Map<Integer, PaymentStatus> getMapIdAsKey() {
		return PAYMENT_STATUS_MAP_ID_AS_KEY;
	}

	public static PaymentStatus getNULL() {
		return NULL;
	}

	public static PaymentStatus getOK() {
		return OK;
	}

	public static PaymentStatus getAWAITING_PSMS() {
		return AWAITING_PSMS;
	}

	public static PaymentStatus getPSMS_ERROR() {
		return PSMS_ERROR;
	}

	public static PaymentStatus getPIN_PENDING() {
		return PIN_PENDING;
	}
	
	public static PaymentStatus getAWAITING_PAYMENT() {
		return AWAITING_PAYMENT;
	}
	
	public static PaymentStatus getAWAITING_PAY_PAL() {
		return AWAITING_PAY_PAL;
	}
	
	public static PaymentStatus getPAY_PAL_ERROR() {
		return PAY_PAL_ERROR;
	}
	
}
