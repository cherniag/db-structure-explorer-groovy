package mobi.nowtechnologies.server.persistence.domain;


import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="tb_drmTypes")
public class DrmType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private byte i;

	@Column(name="name",columnDefinition="char(25)")
	private String name;

    public DrmType() {
    }

	public byte getI() {
		return this.i;
	}

	public String getName() {
		return this.name;
	}

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("i", i)
                .append("name", name)
                .toString();
    }
}