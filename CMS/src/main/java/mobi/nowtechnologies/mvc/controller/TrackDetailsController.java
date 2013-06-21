package mobi.nowtechnologies.mvc.controller;

import mobi.nowtechnologies.domain.Track;
import mobi.nowtechnologies.util.Constants;

import mobi.nowtechnologies.service.dao.TrackDAO;

import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class TrackDetailsController extends ParameterizableViewController {

	private TrackDAO trackDAO;

	public TrackDAO getTrackDAO() {
		return trackDAO;
	}

	public void setTrackDAO(TrackDAO trackDAO) {
		this.trackDAO = trackDAO;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse arg1) throws Exception {

		String id = req.getParameter(Constants.PARAM_ID);
		Long idLong = null;
		if (id != null) {
			try {
				idLong = Long.valueOf(id);
			} catch (NumberFormatException e) {
				throw new UnsupportedOperationException(e);
			}
		} else {
			throw new UnsupportedOperationException("required entity id");
		}
		Track track = (Track) trackDAO.findById(idLong);
		if (track == null)
			throw new EntityNotFoundException();

		ModelAndView mv = new ModelAndView(getViewName());
		mv.addObject("track", track);


		return mv;
	}

}
