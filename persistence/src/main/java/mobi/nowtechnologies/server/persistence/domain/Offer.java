package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.shared.enums.ItemType;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 */
@Entity
@Table(name = "offers")
public class Offer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JoinColumn(name = "community_id")
	@ManyToOne(optional = false)
	private Community community;

	@Column(name = "community_id", insertable = false, updatable = false)
	private int communityId;

	@Column(nullable = false)
	private String title;

	private BigDecimal price;

	private String currency;

	@Column(nullable = false)
	private String coverFileName;

	@Column(nullable = false)
	private String description;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE })
	private Set<AbstractFilterWithCtiteria> filterWithCtiteria = new HashSet<AbstractFilterWithCtiteria>();

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE })
	@JoinTable(name = "offer_items", joinColumns = @JoinColumn(name = "offer_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "item_id", referencedColumnName = "i"))
	private List<Item> items = new LinkedList<Item>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	public int getCommunityId() {
		return communityId;
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

	public Set<AbstractFilterWithCtiteria> getFilterWithCtiteria() {
		return filterWithCtiteria;
	}

	public void setFilterWithCtiteria(Set<AbstractFilterWithCtiteria> filterWithCtiteria) {
		this.filterWithCtiteria = filterWithCtiteria;
	}

	public List<Item> getItems() {
		return items;
	}

	@SuppressWarnings("unchecked")
	public List<Media> getMediaItems()
	{
		final List<Media> mediaList;
		if (items != null && !items.isEmpty()) {
			mediaList = new LinkedList<Media>();

			for (Item item : items) {
				if (item.getType().equals(ItemType.MEDIA))
					mediaList.add((Media) item);
			}
		} else
			mediaList = Collections.EMPTY_LIST;

		return mediaList;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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
        return new ToStringBuilder(this)
                .append("id", id)
                .append("communityId", communityId)
                .append("title", title)
                .append("price", price)
                .append("currency", currency)
                .append("coverFileName", coverFileName)
                .append("description", description)
                .toString();
    }


}