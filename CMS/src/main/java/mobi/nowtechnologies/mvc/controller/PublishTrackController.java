package mobi.nowtechnologies.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.domain.Track;
import mobi.nowtechnologies.server.persistence.domain.Artist;
import mobi.nowtechnologies.server.persistence.domain.Genre;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaFile;
import mobi.nowtechnologies.service.dao.ArtistDAO;
import mobi.nowtechnologies.service.dao.GenreDAO;
import mobi.nowtechnologies.service.dao.MediaDAO;
import mobi.nowtechnologies.service.dao.MediaFileDAO;
import mobi.nowtechnologies.service.dao.TrackDAO;
import mobi.nowtechnologies.util.Constants;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class PublishTrackController extends ParameterizableViewController implements Constants {

	private TrackDAO trackDAO;
	private MediaDAO mediaDAO;
	private ArtistDAO artistDAO;
	private MediaFileDAO fileDAO;
	private GenreDAO genreDAO;

	public void setTrackDAO(TrackDAO trackDAO) {
		this.trackDAO = trackDAO;
	}

	public void setMediaDAO(MediaDAO mediaDAO) {
		this.mediaDAO = mediaDAO;
	}

	public void setArtistDAO(ArtistDAO artistDAO) {
		this.artistDAO = artistDAO;
	}

	public void setFileDAO(MediaFileDAO fileDAO) {
		this.fileDAO = fileDAO;
	}

	public void setGenreDAO(GenreDAO genreDAO) {
		this.genreDAO = genreDAO;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse arg1) throws Exception {

		String id = req.getParameter(PARAM_ID);
		if (id != null) {
			Long idLong = null;
			try {
				idLong = Long.valueOf(id);
				Track track = (Track) trackDAO.findById(idLong.longValue());
				Media media = mediaDAO.getByISRC(track.getISRC());
				System.out.println("MEDIA " + media);
				if (media == null) {
					media = new Media();
					media.setIsrc(track.getISRC());
					media.setTitle(track.getTitle());
				}
				Genre genre = (Genre) genreDAO.getByname(track.getGenre());
				if (genre == null) {
					genre = new Genre();
					genre.setName(track.getGenre());
					genreDAO.persist(genre);
				}
				System.out.println("GENRE "+genre);
				media.setGenre(genre);
				Artist artist = media.getArtist();
				if (artist == null) {
					artist = artistDAO.getByname(track.getArtist());
					if (artist == null) {
						artist = new Artist();
						artist.setName(track.getArtist());
						artist.setInfo("");
					}
					media.setArtist(artist);
					mediaDAO.persist(artist);
				}
				media.setInfo(artist.getInfo());
				MediaFile audio = fileDAO.createFile(track.getISRC() + ".aud", (byte) 0);
				media.setAudioFile(audio);
				MediaFile header = fileDAO.createFile(track.getISRC() + ".hdr", (byte) 0);
				media.setHeaderFile(header);
				MediaFile imageFileLarge = fileDAO.createFile(track.getISRC() + "L.jpg", (byte) 0);
				media.setImageFIleLarge(imageFileLarge);
				MediaFile imageFileSmall = fileDAO.createFile(track.getISRC() + "S.jpg", (byte) 0);
				media.setImageFileSmall(imageFileSmall);
				mediaDAO.persist(media);
				System.out.println("PERSIST MEDIA " + media);

			} catch (NumberFormatException e) {
				throw new UnsupportedOperationException(e);
			}
		} else {
			throw new UnsupportedOperationException("required entity id");
		}

		ModelAndView mv = new ModelAndView(getViewName());

		return mv;
	}

}
