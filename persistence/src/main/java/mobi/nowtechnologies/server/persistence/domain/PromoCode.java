package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.shared.enums.MediaType;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.MediaType.VIDEO_AND_AUDIO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

// @deprecated The 'code', 'mediaType' columns should be moved into Promotion class
@Deprecated
@Entity
@Table(name = "tb_promoCode")
public class PromoCode {

    public static final String PROMO_CODE_FOR_FREE_TRIAL_BEFORE_SUBSCRIBE = "TwoWeeksOnSubscription";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String code;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "promotionId")
    private Promotion promotion;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char(255)", name = "media_type", nullable = false)
    private MediaType mediaType;

    public boolean forVideoAndAudio() {
        return isNotNull(mediaType) && VIDEO_AND_AUDIO.equals(mediaType);
    }

    public boolean forAudio() {
        return isNotNull(mediaType) && AUDIO.equals(mediaType);
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
        if (isNotNull(promotion)) {
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

    public boolean isWhiteListed() {
        return isNotNull(promotion) && promotion.isWhiteListed();
    }

    public PromoCode withMediaType(MediaType mediaType) {
        setMediaType(mediaType);
        return this;
    }

    public PromoCode withCode(String code) {
        setCode(code);
        return this;
    }

    public PromoCode withPromotion(Promotion promotion) {
        setPromotion(promotion);
        return this;
    }

    public boolean isTwoWeeksOnSubscription() {
        return PROMO_CODE_FOR_FREE_TRIAL_BEFORE_SUBSCRIBE.equals(code);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("code", code).append("promotionId", getPromotionId()).append("mediaType", mediaType).toString();
    }
}