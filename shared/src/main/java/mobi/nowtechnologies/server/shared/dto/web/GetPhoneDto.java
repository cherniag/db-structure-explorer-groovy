package mobi.nowtechnologies.server.shared.dto.web;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

public class GetPhoneDto {
	
	public static final String NAME = "getPhone";
	
	@NotEmpty
	@Pattern(regexp="^(07(\\d ?){9})$")
	private String phone;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}