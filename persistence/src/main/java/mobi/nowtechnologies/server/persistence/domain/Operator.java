package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.dao.OperatorDao;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

@Entity
@Table(name = "tb_operators")
public class Operator implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "i")
	private int id;

	private String name;
	
	private String migName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMigName() {
		return migName;
	}

	public void setMigName(String migName) {
		this.migName = migName;
	}
	
	public static Map<Integer,Operator> getMapAsIds() {
		return OperatorDao.getMapAsIds();
	}

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("migName", migName)
                .toString();
    }
}