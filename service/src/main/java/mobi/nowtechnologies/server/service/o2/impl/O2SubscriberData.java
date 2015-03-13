package mobi.nowtechnologies.server.service.o2.impl;

import mobi.nowtechnologies.server.service.data.SubscriberData;

import org.apache.commons.lang3.builder.ToStringBuilder;

/** Represents O2 mobile network subscriber information */
public class O2SubscriberData extends SubscriberData {

    private boolean providerO2;

    private boolean businessOrConsumerSegment;

    private boolean contractPostPayOrPrePay;

    private boolean tariff4G;

    /**
     * true if customer got 4G through the O2 directly, false if he got 4G through third party (CarPhoneWarehouse/etc)
     */
    private boolean directOrIndirect4GChannel = true;

    public O2SubscriberData(O2SubscriberData other) {
        super();
        copyFrom(other);
    }

    public O2SubscriberData() {
        super();
    }

    public boolean isConsumerSegment() {
        return !isBusinessOrConsumerSegment();
    }

    public boolean isContractPostPay() {
        return contractPostPayOrPrePay;
    }

    public boolean isContractPrePay() {
        return !contractPostPayOrPrePay;
    }

    public boolean isDirect4GChannel() {
        return directOrIndirect4GChannel;
    }

    public boolean isIndirect4GChannel() {
        return !directOrIndirect4GChannel;
    }

    // **************** getters/setters**********************************

    public void copyFrom(O2SubscriberData other) {
        this.providerO2 = other.providerO2;
        this.businessOrConsumerSegment = other.businessOrConsumerSegment;
        this.contractPostPayOrPrePay = other.contractPostPayOrPrePay;
        this.tariff4G = other.tariff4G;
        this.directOrIndirect4GChannel = other.directOrIndirect4GChannel;
    }

    public boolean isProviderO2() {
        return providerO2;
    }

    public void setProviderO2(boolean providerO2) {
        this.providerO2 = providerO2;
    }

    public O2SubscriberData withProviderO2(boolean providerO2) {
        setProviderO2(providerO2);

        return this;
    }

    public boolean isContractPostPayOrPrePay() {
        return contractPostPayOrPrePay;
    }

    public void setContractPostPayOrPrePay(boolean contractPostPayOrPrePay) {
        this.contractPostPayOrPrePay = contractPostPayOrPrePay;
    }

    public O2SubscriberData withContractPostPayOrPrePay(boolean contractPostPayOrPrePay) {
        setContractPostPayOrPrePay(contractPostPayOrPrePay);

        return this;
    }

    public boolean isTariff4G() {
        return tariff4G;
    }

    public void setTariff4G(boolean tariff4g) {
        tariff4G = tariff4g;
    }

    public boolean isDirectOrIndirect4GChannel() {
        return directOrIndirect4GChannel;
    }

    public void setDirectOrIndirect4GChannel(boolean directOrIndirect4GChannel) {
        this.directOrIndirect4GChannel = directOrIndirect4GChannel;
    }

    public O2SubscriberData withDirectOrIndirect4GChannel(boolean directOrIndirect4GChannel) {
        setDirectOrIndirect4GChannel(directOrIndirect4GChannel);

        return this;
    }

    public boolean isBusinessOrConsumerSegment() {
        return businessOrConsumerSegment;
    }

    public void setBusinessOrConsumerSegment(boolean businessOrConsumerSegment) {
        this.businessOrConsumerSegment = businessOrConsumerSegment;
    }

    public O2SubscriberData withBusinessOrConsumerSegment(boolean businessOrConsumerSegment) {
        setBusinessOrConsumerSegment(businessOrConsumerSegment);

        return this;
    }

    public O2SubscriberData withTariff4G(boolean tariff4g) {
        setTariff4G(tariff4g);
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("providerO2", providerO2).append("businessOrConsumerSegment", businessOrConsumerSegment)
                                        .append("contractPostPayOrPrePay", contractPostPayOrPrePay).append("tariff4G", tariff4G).append("directOrIndirect4GChannel", directOrIndirect4GChannel)
                                        .toString();
    }
}
