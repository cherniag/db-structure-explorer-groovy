package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import mobi.nowtechnologies.server.persistence.dao.OperatorDao;
import org.apache.commons.lang.builder.ToStringBuilder;

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