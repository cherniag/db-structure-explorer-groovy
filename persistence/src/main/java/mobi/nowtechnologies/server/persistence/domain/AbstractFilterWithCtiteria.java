package mobi.nowtechnologies.server.persistence.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;


@Entity
@Table(name = "filters", uniqueConstraints = { @javax.persistence.UniqueConstraint(columnNames = { "name" }) })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "name", discriminatorType = DiscriminatorType.STRING, length = 255)
public abstract class AbstractFilterWithCtiteria implements java.io.Serializable {
	
	private static final long serialVersionUID = -1926998423L;
	
	private Integer id;
	
	private String name;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	protected void setName(String name) {
		this.name = name;
	}

	@Column(nullable = false, insertable = false, updatable = false)
	public String getName() {
		return this.name;
	}

    public abstract boolean doFilter(User user);

    public AbstractFilterWithCtiteria() {
    }

	
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .toString();
    }

}