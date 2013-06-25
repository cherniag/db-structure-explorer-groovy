package mobi.nowtechnologies.service.dao;

import mobi.nowtechnologies.domain.Track;

import java.util.Date;
import java.util.List;

public interface ITrackDAO {

	public abstract List<Track> listByArtist(String artist);
	public abstract Track getByISRC(String isrc);


}
