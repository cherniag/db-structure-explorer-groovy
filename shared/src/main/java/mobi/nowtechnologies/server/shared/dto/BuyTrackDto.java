package mobi.nowtechnologies.server.shared.dto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name="buyTrack")
public class BuyTrackDto {

	public static enum Status{
		OK(),ALREADYPURCHASED(),NOTDOWNLOAD(),BALANCETOOLOW(), FAIL();
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

	@Override
	public String toString() {
		return "BuyTrackDto [status=" + status + "]";
	}
}
