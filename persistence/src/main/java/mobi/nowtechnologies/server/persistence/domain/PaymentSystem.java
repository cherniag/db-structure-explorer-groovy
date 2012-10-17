package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.dao.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum PaymentSystem {
	SagePay("SagePay"), Mig("Mig"), PayPal("PayPal");
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PaymentSystem.class);
	
	private String value;
	private PaymentSystem(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	// TODO remove it because there are might be more than one paymentSystem to paymentType
	public static PaymentSystem getPaymentSystem(String paymentType) {
		if (paymentType == null)
			throw new PersistenceException("The parameter paymentType is null");
		LOGGER.debug("input parameters paymentType: [{}]",
				new Object[] { paymentType });

		PaymentSystem paymentSystem;
		if (paymentType.equals(UserRegInfo.PaymentType.CREDIT_CARD)) {
			paymentSystem = SagePay;
		} else if (paymentType.equals(UserRegInfo.PaymentType.PREMIUM_USER)) {
			paymentSystem = Mig;
		} else if (paymentType.equals(UserRegInfo.PaymentType.PAY_PAL)) {
			paymentSystem = PayPal;
		} else
			throw new PersistenceException("Unknown payment type: ["
					+ paymentType + "]");

		LOGGER.debug("Output parameter paymentSystem=[{}]", paymentSystem);
		return paymentSystem;
	}
	
	@Override
	public String toString() {
		return name();
	}
}
