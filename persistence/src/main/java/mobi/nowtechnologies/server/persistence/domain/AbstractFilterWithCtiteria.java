package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;


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

	public String toString() {
		return " id=" + id + " name=" + name;
	}

	
	public abstract boolean doFilter(User user);

	public AbstractFilterWithCtiteria() {
	} 
}