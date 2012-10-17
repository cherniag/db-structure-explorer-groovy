package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

/**
 * The persistent class for the tb_appVersions database table.
 * 
 */
@Entity
@Table(name = "tb_appVersions")
public class AppVersion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private byte i;

	@Column(name="description",columnDefinition="char(50)")
	private String description;

	@Column(name="name",columnDefinition="char(25)")
	private String name;

	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name = "tb_appVersionCountry", joinColumns = { @JoinColumn(name = "appVersion_id") }, inverseJoinColumns = { @JoinColumn(name = "country_id") })
	private Set<Country> countries = new HashSet<Country>();

	public AppVersion() {
	}

	public byte getI() {
		return this.i;
	}

	public void setI(byte i) {
		this.i = i;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Country> getCountries() {
		return countries;
	}

	public void setCountries(Set<Country> countries) {
		this.countries = countries;
	}

}