package mobi.nowtechnologies.server.shared.dto.web.payment;

public enum CreditCardType {
	VISA("pay.cc.form.card.type.VISA"), MC("pay.cc.form.card.type.MC"), DELTA("pay.cc.form.card.type.DELTA"), MAESTRO("pay.cc.form.card.type.MAESTRO"), UKE(
			"pay.cc.form.card.type.UKE"), JCB("pay.cc.form.card.type.JCB");

	private String code;

	CreditCardType(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

}