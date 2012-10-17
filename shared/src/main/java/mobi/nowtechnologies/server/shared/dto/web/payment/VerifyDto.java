package mobi.nowtechnologies.server.shared.dto.web.payment;

public class VerifyDto {
	public static final String NAME = "verifyDto";
	
	private String pin;
	
	public VerifyDto() {
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}
}