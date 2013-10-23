package mobi.nowtechnologies.server.service.event;

import mobi.nowtechnologies.server.persistence.domain.payment.AbstractPayment;

import org.springframework.context.ApplicationEvent;

public class PaymentEvent extends ApplicationEvent {

	private static final long serialVersionUID = 8891214780997732297L;
	
	private AbstractPayment payment;
	
	public PaymentEvent(Object source) {
		super(source);
		payment = (AbstractPayment)source;
	}

	public AbstractPayment getPayment() {
		return payment;
	}
}