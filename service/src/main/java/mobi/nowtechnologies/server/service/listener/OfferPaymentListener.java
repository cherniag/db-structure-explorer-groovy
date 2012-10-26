package mobi.nowtechnologies.server.service.listener;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.Offer;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.AccountLogService;
import mobi.nowtechnologies.server.service.DrmService;
import mobi.nowtechnologies.server.service.OfferService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.shared.enums.TransactionType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Alexander Kolpakov(akolpakov)
 *
 */
public class OfferPaymentListener implements ApplicationListener<PaymentEvent> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OfferPaymentListener.class);
		
	private OfferService offerService;
	private DrmService drmService;
	private AccountLogService accountLogService;
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void onApplicationEvent(PaymentEvent event) {
		SubmittedPayment payment = (SubmittedPayment) event.getPayment();
		
		if (payment.getType() == PaymentDetailsType.PAYMENT) {
			LOGGER.info("handle Offer payment event: [{}]", payment);
			
			User user = payment.getUser();
			Long paymentUID = payment.getI();
			byte balanceAfter = (byte) (user.getSubBalance());
			int offerId = payment.getOfferId();
			
			Offer offer = offerService.getOffer(offerId);
			List<Media> mediaList = offer.getMediaItems();
			
			drmService.processBuyTrackCommand(user, mediaList);
			
			accountLogService.logAccountEvent(user.getId(), balanceAfter, null, payment, TransactionType.OFFER_PURCHASE, offer);
		}
	}

	public void setDrmService(DrmService drmService) {
		this.drmService = drmService;
	}

	public void setOfferService(OfferService offerService) {
		this.offerService = offerService;
	}

	public void setAccountLogService(AccountLogService accountLogService) {
		this.accountLogService = accountLogService;
	}
}