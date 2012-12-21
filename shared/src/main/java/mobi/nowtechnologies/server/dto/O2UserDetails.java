package mobi.nowtechnologies.server.dto;

public class O2UserDetails {
	
	private String operator;
	private String tariff;
	
	public O2UserDetails() {
	}

	public O2UserDetails(String operator, String tariff) {
		super();
		this.operator = operator;
		this.tariff = tariff;
	}
	
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getTariff() {
		return tariff;
	}

	public void setTariff(String tariff) {
		this.tariff = tariff;
	}
}