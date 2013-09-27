package mobi.nowtechnologies.server.dto;

public class O2UserDetails extends ProviderUserDetails{
	
	public O2UserDetails() {
	}

	public O2UserDetails(String operator, String tariff) {
		this.operator = operator;
		this.tariff = tariff;
	}

}