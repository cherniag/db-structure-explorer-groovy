package mobi.nowtechnologies.server.track_repo;

import org.springframework.core.io.Resource;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 *
 */
public class ApplicationProperties {

	private Resource binZip;

	public Resource getBinZip() {
		return binZip;
	}

	public void setBinZip(Resource binZip) {
		this.binZip = binZip;
	}
}
