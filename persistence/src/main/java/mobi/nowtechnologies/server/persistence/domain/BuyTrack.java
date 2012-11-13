/**
 * 
 */
package mobi.nowtechnologies.server.persistence.domain;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@XmlRootElement(name="buyTrack")
public class BuyTrack {
	public static enum Status{
		OK(),ALREADYPURCHASED(),NOTDOWNLOAD(),BALANCE(), FAIL();
	}
	
	private Status status=Status.FAIL;

	@XmlTransient
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	@XmlValue
	public String getStatusValue() {
		return status.name();
	}
	
}
