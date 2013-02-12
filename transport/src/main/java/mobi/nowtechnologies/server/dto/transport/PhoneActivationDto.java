package mobi.nowtechnologies.server.dto.transport;

import javax.xml.bind.annotation.XmlRootElement;

import mobi.nowtechnologies.server.shared.enums.ActivationStatus;

@XmlRootElement(name="phoneActivation")
public class PhoneActivationDto {
	private ActivationStatus activation;
	private String phoneNumber;
	private String redeemServerUrl;
	
	public PhoneActivationDto(){
		
	}
	
	public PhoneActivationDto(ActivationStatus activation, String phone, String redeemServerUrl){
		this(activation, phone);
		
		this.redeemServerUrl = redeemServerUrl;
	}
	
	public PhoneActivationDto(ActivationStatus activation, String phone){
		this.phoneNumber = phone;
		this.activation = activation;
	}
	
	public ActivationStatus getActivation() {
		return activation;
	}

	public String getRedeemServerUrl() {
		return redeemServerUrl;
	}

	public void setRedeemServerUrl(String redeemServerUrl) {
		this.redeemServerUrl = redeemServerUrl;
	}

	public void setActivation(ActivationStatus activation) {
		this.activation = activation;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String toString() {
		return "PhoneActivationDto [activation=" + activation + ", phone=" + phoneNumber + "]";
	}
}