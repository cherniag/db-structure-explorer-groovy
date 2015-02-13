package mobi.nowtechnologies.server.service.nz;

/**
 * @author Anton Zemliankin
 */

public class NZSubscriberResult {

    private String payIndicator;
    private String providerName;
    private String billingAccountNumber;
    private String billingAccountName;

    public NZSubscriberResult(String payIndicator, String providerName, String billingAccountNumber, String billingAccountName){
        this.payIndicator = payIndicator;
        this.providerName = providerName;
        this.billingAccountNumber = billingAccountNumber;
        this.billingAccountName = billingAccountName;
    }

    public String getPayIndicator() {
        return payIndicator;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getBillingAccountNumber() {
        return billingAccountNumber;
    }

    public String getBillingAccountName() {
        return billingAccountName;
    }
}
