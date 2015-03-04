package mobi.nowtechnologies.server.shared.dto.admin;

import mobi.nowtechnologies.server.shared.dto.ItemDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 */
public class OfferDto {

    public static final String OFFER_ID = "offerId";

    public static final String OFFER_DTO = "OFFER_DTO";

    public static final String OFFER_DTO_LIST = "OFFER_DTO_LIST";

    private Integer id;

    @NotEmpty
    @Pattern(regexp = ".{1,255}")
    private String title;

    @NotNull
    private BigDecimal price;

    private String currency;

    private Set<FilterDto> filterDtos = new HashSet<FilterDto>();

    private List<ItemDto> itemDtos;

    @JsonIgnore
    private MultipartFile file;

    private String coverFileName;

    @NotEmpty
    @Pattern(regexp = ".{1,255}")
    private String description;

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

    public Set<FilterDto> getFilterDtos() {
        return filterDtos;
    }

    public void setFilterDtos(Set<FilterDto> filterDtos) {
        this.filterDtos = filterDtos;
    }

    public List<ItemDto> getItemDtos() {
        return itemDtos;
    }

    public void setItemDtos(List<ItemDto> itemDtos) {
        this.itemDtos = itemDtos;
    }

    public List<Integer> getItemIds() {
        if (itemDtos == null) {
            return null;
        }

        List<Integer> ids = new LinkedList<Integer>();

        for (ItemDto item : itemDtos) {
            ids.add(item.getId());
        }

        return ids;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverFileName() {
        return coverFileName;
    }

    public void setCoverFileName(String coverFileName) {
        this.coverFileName = coverFileName;
    }

    @Override
    public String toString() {
        return "OfferDto [id=" + id + ", title=" + title + ", price=" + price + ", currency=" + currency + ", filterDtos=" + filterDtos + ", itemDtos=" + itemDtos + ", file=" + file +
               ", coverFileName=" + coverFileName + ", description=" + description + "]";
    }
}