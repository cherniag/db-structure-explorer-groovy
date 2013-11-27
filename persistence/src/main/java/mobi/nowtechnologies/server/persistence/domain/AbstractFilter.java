package mobi.nowtechnologies.server.persistence.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.List;

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
        return new ToStringBuilder(this)
                .append("id", id)
                .append("filterType", filterType)
                .toString();
    }

}
