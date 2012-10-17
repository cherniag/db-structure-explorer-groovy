package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_userStatus database table.
 * 
 */
@Entity
@Table(name="tb_userStatus")
public class UserStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private byte i;

	@Column(name="name",columnDefinition="char(25)")
	private String name;

    public UserStatus() {
    }

	public byte getI() {
		return this.i;
	}

	public void setI(byte i) {
		this.i = i;
	}

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
		if (!(obj instanceof UserStatus))
			return false;
		UserStatus other = (UserStatus) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserStatus [i=" + i + ", name=" + name + "]";
	}
}