package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderService;
import mobi.nowtechnologies.server.service.payment.response.O2Response;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.shared.Utils;

/**
 * @author Titov Mykhaylo (titov)
 */
public class O2PaymentServiceImpl extends BasicPSMSPaymentServiceImpl<O2PSMSPaymentDetails> {
    private O2ProviderService o2ClientService;

    public void setO2ClientService(O2ProviderService o2ClientService) {
        this.o2ClientService = o2ClientService;
    }

    @Override
    public PaymentSystemResponse getExpiredResponse() {
        return O2Response.failO2Response("O2 pending payment has been expired");
    }

    @Override
    protected PaymentSystemResponse makePayment(PendingPayment pendingPayment, String message) {
        final User user = pendingPayment.getUser();
        final O2PSMSPaymentDetails paymentDetails = (O2PSMSPaymentDetails) pendingPayment.getPaymentDetails();
        final PaymentPolicy paymentPolicy = paymentDetails.getPaymentPolicy();

        String internalTxId = Utils.getBigRandomInt().toString();
        O2Response response = o2ClientService
            .makePremiumSMSRequest(user.getId(), internalTxId, pendingPayment.getAmount(), paymentDetails.getPhoneNumber(), message, paymentPolicy.getContentCategory(), paymentPolicy.getContentType(),
                                   paymentPolicy.getContentDescription(), paymentPolicy.getSubMerchantId(), message != null);

        pendingPayment.setInternalTxId(internalTxId);

        final String externalTxId = response.getExternalTxId();
        if (externalTxId != null) {
            pendingPayment.setExternalTxId(externalTxId);
        } else {
            pendingPayment.setExternalTxId("");
        }

        getPendingPaymentRepository().save(pendingPayment);

        return response;
    }

}