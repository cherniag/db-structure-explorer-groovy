package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/8/2014
 */
@Entity
@Table(name = "subscription_campaign")
public class SubscriptionCampaignRecord implements Serializable{
    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "mobile")
    private String mobile;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_code_id")
    private PromoCode promoCode;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public PromoCode getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(PromoCode promoCode) {
        this.promoCode = promoCode;
    }
}
