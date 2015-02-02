
package uk.co.o2.soa.subscriberdata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.Contract;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import uk.co.o2.soa.coredata.PaymentCategoryType;
import uk.co.o2.soa.coredata.SegmentType;

import static mobi.nowtechnologies.server.shared.enums.Contract.*;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.*;
import static uk.co.o2.soa.coredata.PaymentCategoryType.*;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subscriberProfileType", propOrder = {
    "subscriberID",
    "operator",
    "paymentCategory",
    "segment",
    "channel",
    "serviceProviderID",
    "status",
    "puk"
})
public class SubscriberProfileType {

    @XmlElement(required = true)
    protected String subscriberID;
    protected String operator;
    protected PaymentCategoryType paymentCategory;
    protected SegmentType segment;
    protected String channel;
    protected Long serviceProviderID;
    @XmlElement(required = true)
    protected String status;
    protected String puk;

    public String getSubscriberID() {
        return subscriberID;
    }

    public void setSubscriberID(String value) {
        this.subscriberID = value;
    }

    public String getOperator() {
        return operator;
    }


    public void setOperator(String value) {
        this.operator = value;
    }

    public PaymentCategoryType getPaymentCategory() {
        return paymentCategory;
    }

    public void setPaymentCategory(PaymentCategoryType value) {
        this.paymentCategory = value;
    }

    public SegmentType getSegment() {
        return segment;
    }

    public void setSegment(SegmentType value) {
        this.segment = value;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String value) {
        this.channel = value;
    }

    public Long getServiceProviderID() {
        return serviceProviderID;
    }

    public void setServiceProviderID(Long value) {
        this.serviceProviderID = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    public String getPuk() {
        return puk;
    }

    public void setPuk(String value) {
        this.puk = value;
    }

    public mobi.nowtechnologies.server.shared.enums.SegmentType getSegmentType() {
        return SegmentType.CONSUMER.equals(getSegment()) ?
                mobi.nowtechnologies.server.shared.enums.SegmentType.CONSUMER :
                mobi.nowtechnologies.server.shared.enums.SegmentType.BUSINESS;
    }

    public Contract getCotract() {
        return POSTPAY.equals(getPaymentCategory()) ?
                PAYM : PAYG;
    }
    
    public ProviderType getProvider(){
    	return "O2".equals(getOperator()) ?
                O2 : NON_O2;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("subscriberID", subscriberID)
                .append("operator", operator)
                .append("paymentCategory", paymentCategory)
                .append("segment", segment)
                .append("channel", channel)
                .toString();
    }
}
