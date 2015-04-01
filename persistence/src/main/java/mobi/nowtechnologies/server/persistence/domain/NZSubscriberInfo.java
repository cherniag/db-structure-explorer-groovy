package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.util.Date;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.ToStringBuilder;

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

    @Column(name = "call_count")
    private int callCount;

    @Column(name = "create_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTimestamp = new Date();

    @Column(name = "update_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTimestamp = new Date(createTimestamp.getTime());

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

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void unassignUser() {
        this.userId = null;
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

    public void incCallCount() {
        callCount++;
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
                .append("providerName", providerName)
                .append("callCount", callCount)
                .append("billingAccountNumber", billingAccountNumber)
                .append("billingAccountName", billingAccountName)
                .append("payIndicator", payIndicator)
                .append("updateTimestamp", updateTimestamp)
                .append("createTimestamp", createTimestamp)
                .toString();
    }
}
