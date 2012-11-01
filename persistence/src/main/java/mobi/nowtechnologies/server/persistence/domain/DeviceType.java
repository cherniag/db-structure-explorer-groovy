package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_deviceTypes database table.
 * 
 */
@Entity
@Table(name="tb_deviceTypes")
public class DeviceType implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//TODO remove
	public static final byte IOS = (byte) 5;
	
	public static enum Fields{
		name();
	}

	private byte i;

	private String name;

    public DeviceType() {
    }

    @Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public byte getI() {
		return this.i;
	}

	public void setI(byte i) {
		this.i = i;
	}

	@Column(name="name",columnDefinition="char(25)")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DeviceType))
			return false;
		DeviceType other = (DeviceType) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DeviceType [i=" + i + ", name=" + name + "]";
	}

}