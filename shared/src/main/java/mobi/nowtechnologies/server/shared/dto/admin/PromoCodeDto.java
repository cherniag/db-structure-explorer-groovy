package mobi.nowtechnologies.server.shared.dto.admin;

/**
 * @author Titov Mykhaylo (titov)
 */
public class PromoCodeDto {

    private int id;
    private String code;

    private PromotionDto promotionDto;

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

    public PromotionDto getPromotionDto() {
        return promotionDto;
    }

    public void setPromotionDto(PromotionDto promotionDto) {
        this.promotionDto = promotionDto;
    }

    @Override
    public String toString() {
        return "PromoCodeDto [code=" + code + ", id=" + id + ", promotionDto=" + promotionDto + "]";
    }

}
