package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.shared.dto.PromoCodeDto;
import mobi.nowtechnologies.server.shared.enums.MediaType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.enums.MediaType.VIDEO_AND_AUDIO;

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

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char(255)", name = "media_type", nullable = false)
    private MediaType mediaType;
	
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

    public PromoCode withCode(String code) {
        this.code = code;
        return this;
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

    public boolean forVideoAndMusic() {
        return isNotNull(mediaType) && VIDEO_AND_AUDIO.equals(mediaType);
    }

	@Override
	public String toString() {
		return "PromoCode [code=" + code + ", id=" + id + ", promotionId="
				+ promotionId + "]";
	}
}