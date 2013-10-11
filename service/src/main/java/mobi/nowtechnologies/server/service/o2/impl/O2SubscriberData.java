package mobi.nowtechnologies.server.service.o2.impl;

import mobi.nowtechnologies.server.service.data.SubscriberData;

/** Represents O2 mobile network subscriber information */
public class O2SubscriberData extends SubscriberData {

	private boolean providerO2;

	private boolean businessOrConsumerSegment;

	private boolean contractPostPayOrPrePay;

	private boolean tariff4G;

	/**
	 * true if customer got 4G through the O2 directly, false if he got 4G
	 * through third party (CarPhoneWarehouse/etc)
	 */
	private boolean directOrIndirect4GChannel = true;

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

	@Override
	public String toString() {
		return "O2SubscriberData [providerO2=" + providerO2 + ", businessOrConsumerSegment="
				+ businessOrConsumerSegment + ", contractPostPayOrPrePay=" + contractPostPayOrPrePay + ", tariff4G="
				+ tariff4G + ", directOrIndirect4GChannel=" + directOrIndirect4GChannel + "]";
	}

	public void copyFrom(O2SubscriberData other) {
		this.providerO2 = other.providerO2;
		this.businessOrConsumerSegment = other.businessOrConsumerSegment;
		this.contractPostPayOrPrePay = other.contractPostPayOrPrePay;
		this.tariff4G = other.tariff4G;
		this.directOrIndirect4GChannel = other.directOrIndirect4GChannel;
	}

	public O2SubscriberData(O2SubscriberData other) {
		super();
		copyFrom(other);
	}

	// **************** getters/setters**********************************

	public O2SubscriberData() {
		super();
	}

	public boolean isProviderO2() {
		return providerO2;
	}

	public void setProviderO2(boolean providerO2) {
		this.providerO2 = providerO2;
	}

	public boolean isContractPostPayOrPrePay() {
		return contractPostPayOrPrePay;
	}

	public void setContractPostPayOrPrePay(boolean contractPostPayOrPrePay) {
		this.contractPostPayOrPrePay = contractPostPayOrPrePay;
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

	public boolean isBusinessOrConsumerSegment() {
		return businessOrConsumerSegment;
	}

	public void setBusinessOrConsumerSegment(boolean businessOrConsumerSegment) {
		this.businessOrConsumerSegment = businessOrConsumerSegment;
	}

    public O2SubscriberData withTariff4G(boolean tariff4g){
        setTariff4G(tariff4g);
        return this;
    }
}
