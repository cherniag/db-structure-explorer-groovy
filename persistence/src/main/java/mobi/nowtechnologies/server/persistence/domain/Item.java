package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.shared.enums.ItemType;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 */
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class Item implements Serializable{
	
	private static final long serialVersionUID = 2546198857668889092L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Integer i;
	
	@Column(name="title",columnDefinition="char(50)")
	private String title;
	
	@Column(precision=5, scale=2)
	private BigDecimal price;
	
	@Column(name="type" ,insertable=false, updatable=false)
	private Integer typeId;
	
	@Enumerated(EnumType.ORDINAL)
	private ItemType type;
	
	public Integer getI() {
		return i;
	}

	public void setI(Integer i) {
		this.i = i;
	}

	public ItemType getType() {
		return type;
	}

	public void setType(ItemType type) {
		this.type = type;
	}

	public Integer getTypeId() {
		return typeId;
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

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("i", i)
                .append("title", title)
                .append("price", price)
                .append("typeId", typeId)
                .toString();
    }
}