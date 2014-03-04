package mobi.nowtechnologies.server.persistence.domain;


import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the tb_country database table.
 * 
 */
@Entity
@Table(name="tb_country")
public class Country implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static enum Fields {
		i,
		fullName, name
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="i",columnDefinition="smallint(5) unsigned")
	private int i;

	@Column(name="name",columnDefinition="char(10)")
	private String name;

	private String fullName;

    public Country() {
    }

	public int getI() {
		return this.i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("i", i)
                .append("name", name)
                .append("fullName", fullName)
                .toString();
    }
}