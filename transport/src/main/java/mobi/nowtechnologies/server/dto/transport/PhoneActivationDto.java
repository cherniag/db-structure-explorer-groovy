package mobi.nowtechnologies.server.dto.transport;

import mobi.nowtechnologies.server.shared.enums.ActivationStatus;

public class PhoneActivationDto {
	private ActivationStatus activation;
	private String phoneNumber;
	
	public PhoneActivationDto(){
		
	}
	
	public PhoneActivationDto(ActivationStatus activation, String phone){
		this.phoneNumber = phone;
		this.activation = activation;
	}
	
	public ActivationStatus getActivation() {
		return activation;
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