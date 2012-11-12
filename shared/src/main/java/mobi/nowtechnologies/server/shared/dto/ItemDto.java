package mobi.nowtechnologies.server.shared.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public class ItemDto {
	private Integer id;

	@NotEmpty
	@Pattern(regexp = ".{1,255}")
	private String title;

	private BigDecimal price;
	
	private Integer typeId;

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

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	@Override
	public String toString() {
		return "ItemDto [id=" + id + ", title=" + title + ", price=" + price + ", typeId=" + typeId + "]";
	}
}
