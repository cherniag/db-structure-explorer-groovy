package mobi.nowtechnologies.mvc.controller;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.domain.AssetFile;
import mobi.nowtechnologies.domain.Track;
import mobi.nowtechnologies.ingestors.DDEXParser;
import mobi.nowtechnologies.service.dao.TrackDAO;
import mobi.nowtechnologies.util.Constants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class DeleteTrackController extends ParameterizableViewController implements Constants {
	protected static final Log LOG = LogFactory.getLog(DeleteTrackController.class);

	private TrackDAO trackDAO;

	public void setTrackDAO(TrackDAO trackDAO) {
		this.trackDAO = trackDAO;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse arg1) throws Exception {

		String id = req.getParameter(PARAM_ID);
		if (id != null) {
			Long idLong = null;
			try {
				idLong = Long.valueOf(id);
				Track track = (Track) trackDAO.findById(idLong);
				for (AssetFile file : track.getFiles()) {
					if (file.getType() != AssetFile.FileType.IMAGE) {
						System.out.println("Deleting file " + file.getPath());
						try {
							File fd = new File(file.getPath());
							if (!fd.delete()) {
								LOG.error("Cannot delete file " + file.getPath());
							}
						} catch (Exception e) {
							LOG.error("Cannot delete file " + file.getPath() + " " + e.getMessage());
						}
					}
				}
				trackDAO.delete(idLong);
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
