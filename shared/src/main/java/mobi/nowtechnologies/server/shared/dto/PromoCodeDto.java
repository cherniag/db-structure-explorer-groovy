package mobi.nowtechnologies.server.shared.dto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name="promoCode")
public class PromoCodeDto {
	
	private String promoCode;

	public PromoCodeDto() {
	}
	
	@XmlValue
	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}
}