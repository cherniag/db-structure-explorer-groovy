package mobi.nowtechnologies.server.persistence.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name="tb_genres")
public class Genre implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static enum Fields{
		i,name;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer i;

	@Column(name="name",columnDefinition="char(25)")
	private String name;

    public Genre() {
    }

	public Integer getI() {
		return i;
	}

	public void setI(Integer i) {
		this.i = i;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("i", i)
                .append("name", name)
                .toString();
    }
}