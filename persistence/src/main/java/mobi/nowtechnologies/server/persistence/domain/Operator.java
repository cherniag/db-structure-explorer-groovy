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

/**
 * The persistent class for the tb_operators database table.
 * 
 */
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
	
	public static Map<String,Operator> getMapAsMigNames() {
		return OperatorDao.getMapAsMigNames();
	}
	
	public static Map<Integer,Operator> getMapAsIds() {
		return OperatorDao.getMapAsIds();
	}

}