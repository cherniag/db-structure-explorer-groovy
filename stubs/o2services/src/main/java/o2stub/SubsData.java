package o2stub;

public class SubsData {

	private String phoneNumberWithCode;//"+447722323"
	private boolean o2;
	private boolean business;
	private boolean payAsYouGo = true;
	private boolean tariff4G;
	private boolean directChannel4G;

	@Override
	public String toString() {
		return "SubsData [phoneNumberWithCode=" + phoneNumberWithCode + ", o2=" + o2
				+ ", business=" + business + ", payAsYouGo=" + payAsYouGo + ", tariff4G=" + tariff4G
				+ ", directChannel4G=" + directChannel4G + "]";
	}

	public String getPhoneNumberWithCode() {
		return phoneNumberWithCode;
	}

	public void setPhoneNumberWithCode(String phoneNumberWithCode) {
		this.phoneNumberWithCode = phoneNumberWithCode;
	}

	public boolean isO2() {
		return o2;
	}

	public void setO2(boolean o2) {
		this.o2 = o2;
	}

	public boolean isBusiness() {
		return business;
	}

	public void setBusiness(boolean business) {
		this.business = business;
	}

	public boolean isPayAsYouGo() {
		return payAsYouGo;
	}

	public void setPayAsYouGo(boolean payAsYouGo) {
		this.payAsYouGo = payAsYouGo;
	}

	public boolean isTariff4G() {
		return tariff4G;
	}

	public void setTariff4G(boolean tariff4g) {
		tariff4G = tariff4g;
	}

	public boolean isDirectChannel4G() {
		return directChannel4G;
	}

	public void setDirectChannel4G(boolean directChannel4G) {
		this.directChannel4G = directChannel4G;
	}

}
