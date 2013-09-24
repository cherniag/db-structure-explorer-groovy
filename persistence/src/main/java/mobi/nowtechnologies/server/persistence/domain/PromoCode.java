package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.shared.ObjectUtils;
import mobi.nowtechnologies.server.shared.dto.PromoCodeDto;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.enums.MediaType.VIDEO_AND_AUDIO;


// @deprecated The 'code', 'mediaType' columns should be moved into Promotion class
@Deprecated
@Entity
@Table(name="tb_promoCode")
public class PromoCode {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String code;
	 
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="promotionId")
	private Promotion promotion;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char(255)", name = "media_type", nullable = false)
    private MediaType mediaType;

    public boolean forVideoAndMusic() {
        return isNotNull(mediaType) && VIDEO_AND_AUDIO.equals(mediaType);
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

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

	public Integer getPromotionId() {
        if (isNotNull(promotion)){
            return promotion.getI();
        }
		return null;
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

    public boolean isWhiteListed() {
        return isNotNull(promotion) && promotion.isWhiteListed();
    }

    public PromoCode withMediaType(MediaType mediaType){
        setMediaType(mediaType);
        return this;
    }

    public PromoCode withCode(String code){
        setCode(code);
        return this;
    }

    public PromoCode withPromotion(Promotion promotion){
        setPromotion(promotion);
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("code", code)
                .append("promotionId", getPromotionId())
                .append("mediaType", mediaType)
                .toString();
    }
}