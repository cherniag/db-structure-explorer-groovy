package mobi.nowtechnologies.server.persistence.domain;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Anton Zemliankin
 */

@Entity
@Table(name = "nz_subscriber_info")
public class NZSubscriberInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "msisdn", nullable = false, unique = true)
    private String msisdn;

    @Column(name = "pay_indicator", nullable = false)
    private String payIndicator;

    @Column(name = "provider_name", nullable = false)
    private String providerName;

    @Column(name = "billing_account_number", nullable = false)
    private String billingAccountNumber;

    @Column(name = "billing_account_name")
    private String billingAccountName;

    @Column(name = "ws_call_millis")
    private long wsCallMillis;

    @Column(name = "ws_call_count")
    private int wsCallCount;

    @Column(name = "create_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTimestamp = new Date();

    @Column(name = "update_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTimestamp = new Date();

    protected NZSubscriberInfo() {
    }

    public NZSubscriberInfo(String msisdn) {
        this.msisdn = Preconditions.checkNotNull(msisdn);
    }

    public int getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getPayIndicator() {
        return payIndicator;
    }

    public void setPayIndicator(String payIndicator) {
        refreshUpdateTimestamp();
        this.payIndicator = payIndicator;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        refreshUpdateTimestamp();
        this.providerName = providerName;
    }

    public String getBillingAccountNumber() {
        return billingAccountNumber;
    }

    public void setBillingAccountNumber(String billingAccountNumber) {
        refreshUpdateTimestamp();
        this.billingAccountNumber = billingAccountNumber;
    }

    public String getBillingAccountName() {
        return billingAccountName;
    }

    public void setBillingAccountName(String billingAccountName) {
        refreshUpdateTimestamp();
        this.billingAccountName = billingAccountName;
    }

    public NZProviderType getProviderType() {
        return NZProviderType.of(getProviderName());
    }

    public void setWsCallMillis(long wsCallMillis) {
        this.wsCallMillis = wsCallMillis;
        wsCallCount++;
    }

    private void refreshUpdateTimestamp(){
        updateTimestamp = new Date();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("userId", userId)
                .append("MSISDN", msisdn)
                .append("payIndicator", payIndicator)
                .append("providerName", providerName)
                .append("billingAccountNumber", billingAccountNumber)
                .append("billingAccountName", billingAccountName)
                .append("wsCallCount", wsCallCount)
                .append("wsCallMillis", wsCallMillis)
                .append("updateTimestamp", updateTimestamp)
                .append("createTimestamp", createTimestamp)
                .toString();
    }
}
