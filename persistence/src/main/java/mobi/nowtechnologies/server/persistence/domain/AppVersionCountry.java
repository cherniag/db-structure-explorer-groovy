/**
 * 
 */
package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.*;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@Entity
@Table(name="tb_appVersionCountry")
public class AppVersionCountry {
	  @Id
	  @GeneratedValue(strategy=javax.persistence.GenerationType.AUTO)
	  private long id;

	  public void setId(long id) {
	    this.id = id;
	  }

	  public long getId() {
	    return id;
	  }

	  @Column(name="appVersion_id",columnDefinition="tinyint(3) unsigned")
	  private byte appVersionId;

	  public void setAppVersionId(byte appVersionId) {
	    this.appVersionId = appVersionId;
	  }

	  public byte getAppVersionId() {
	    return appVersionId;
	  }

	  @Column(name="country_id")
	  private short countryId;

	  public void setCountryId(short countryId) {
	    this.countryId = countryId;
	  }

	  public short getCountryId() {
	    return countryId;
	  }
}
