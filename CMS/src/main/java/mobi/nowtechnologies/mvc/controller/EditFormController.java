package mobi.nowtechnologies.mvc.controller;

import mobi.nowtechnologies.util.Constants;
import mobi.nowtechnologies.domain.Track;
import mobi.nowtechnologies.service.dao.TrackDAO;

import java.text.DateFormat;
import java.util.Date;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class EditFormController extends SimpleFormController implements Constants {

	private TrackDAO trackDAO;

	public void setTrackDAO(TrackDAO trackDAO) {
		this.trackDAO = trackDAO;
	}
	
	@Override
	protected Object formBackingObject(HttpServletRequest req) throws Exception {
		String id = req.getParameter(PARAM_ID);
		Track track = null;
		if (id != null) {
			Long idLong = null;
			try {
				idLong = Long.valueOf(id);
			} catch (NumberFormatException e) {
				throw new UnsupportedOperationException(e);
			}

			track = (Track) trackDAO.findById(idLong);
			if (track == null)
				throw new EntityNotFoundException();
		} else
			track = new Track();
		return track;

	}

	@Override
	protected ModelAndView onSubmit(Object arg0) throws Exception {
		Track p = (Track) arg0;
		trackDAO.persist(p);
		return super.onSubmit(arg0);
	}

	@Override
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, request
				.getLocale());
		binder.registerCustomEditor(Date.class, new CustomDateEditor(df, true));

	}
}
