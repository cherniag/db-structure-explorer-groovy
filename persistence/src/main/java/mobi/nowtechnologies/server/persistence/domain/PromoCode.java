package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.shared.dto.PromoCodeDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="tb_promoCode")
public class PromoCode {
	
	public static enum Fields {
		id, code, promotionId
	}
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String code;
	
	@Column(insertable=false, updatable=false)
	private byte promotionId;
	 
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="promotionId")
	private Promotion promotion;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public byte getPromotionId() {
		return promotionId;
	}
	
	public Promotion getPromotion() {
		return promotion;
	}

	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}
	
	public static PromoCodeDto valueOf(PromoCode promoCode) {
		PromoCodeDto dto = new PromoCodeDto();
			dto.setPromoCode(promoCode.getCode());
		return dto;
	}
	
	public static List<PromoCodeDto> toPromoCodeDtoList(List<PromoCode> codes) {
		List<PromoCodeDto> dtoList = new ArrayList<PromoCodeDto>();
			for (PromoCode promoCode : codes) {
				dtoList.add(valueOf(promoCode));
			}
		return dtoList ;
	}

	@Override
	public String toString() {
		return "PromoCode [code=" + code + ", id=" + id + ", promotionId="
				+ promotionId + "]";
	}
}