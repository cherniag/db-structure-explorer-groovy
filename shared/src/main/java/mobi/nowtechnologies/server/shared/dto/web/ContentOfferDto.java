package mobi.nowtechnologies.server.shared.dto.web;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 */
public class ContentOfferDto {

    public static final String OFFER_DTO = "contentOfferDto";

    public static final String OFFER_DTO_LIST = "contentOfferDtoList";

    private Integer id;

    private String title;

    private BigDecimal price;

    private String currency;

    private String coverFileName;

    private String description;

    private List<ContentOfferItemDto> contentOfferItemDtos;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<ContentOfferItemDto> getContentOfferItemDtos() {
        return contentOfferItemDtos;
    }

    public void setContentOfferItemDtos(List<ContentOfferItemDto> contentOfferItemDtos) {
        this.contentOfferItemDtos = contentOfferItemDtos;
    }

    public String getCoverFileName() {
        return coverFileName;
    }

    public void setCoverFileName(String coverFileName) {
        this.coverFileName = coverFileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ContentOfferDto [currency=" + currency + ", id=" + id + ", price=" + price + ", title=" + title + ", coverFileName=" + coverFileName + ", description=" + description + "]";
    }

}
