package mobi.nowtechnologies.server.persistence.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="tb_mediaLogTypes")
public class MediaLogType implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static enum Fields{
		name();
	}

	private int i;

	private String name;

    public MediaLogType() {
    }

    @Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getI() {
		return this.i;
	}

	public void setI(int i) {
		this.i = i;
	}

	@Column(name="name",columnDefinition="char(20)")
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