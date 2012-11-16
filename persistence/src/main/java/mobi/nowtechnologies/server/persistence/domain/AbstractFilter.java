package mobi.nowtechnologies.server.persistence.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="tb_filter")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="filterType",discriminatorType=DiscriminatorType.STRING, columnDefinition="char(31)")
public abstract class AbstractFilter {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(columnDefinition="tinyint unsigned")
	private byte id;
	
	@Column(name="filterType", insertable=false, updatable=false)
	private String filterType;
	
	@ManyToMany(fetch=FetchType.LAZY)
	private List<NewsDetail> newDetails;

	public byte getId() {
		return id;
	}

	public void setId(byte id) {
		this.id = id;
	}
	
	public String getFilterType() {
		return filterType;
	}
	
	public abstract boolean doFilter(User user, Object param);

	@Override
	public String toString() {
		return "filterType=" + filterType + ", id=" + id;
	}
}
