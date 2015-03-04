package mobi.nowtechnologies.server.shared.dto.web;


/**
 * @author Titov Mykhaylo (titov)
 */
public class OfferPaymentPolicyDto {

    public static final String OFFER_PAYMENT_POLICY_DTO = "offerPaymentPolicyDto";
    public static final String OFFER_PAYMENT_POLICY_DTO_LIST = "offerPaymentPolicyDtoList";

    private String paymentType;

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    @Override
    public String toString() {
        return "OfferPaymentPolicyDto [paymentType=" + paymentType + "]";
    }

}
