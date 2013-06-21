package mobi.nowtechnologies.mvc.controller;

import mobi.nowtechnologies.domain.Track;
import mobi.nowtechnologies.service.dao.TrackDAO;
import mobi.nowtechnologies.util.Constants;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class ListTracksController extends SimpleFormController implements Constants {

	private TrackDAO trackDAO;
	
//	private static SearchData data;

	public ListTracksController() {
		setCommandClass(SearchData.class);
		setCommandName("searchCommand");
		setSessionForm(true);
	}

	public TrackDAO getTrackDAO() {
		return trackDAO;
	}

	public void setTrackDAO(TrackDAO trackDAO) {
		this.trackDAO = trackDAO;
	}
	@Override
	protected Object formBackingObject(HttpServletRequest req) throws Exception {
		SearchData data = (SearchData) req.getSession().getAttribute("listData");
		
		System.out.println("formBackingObject "+data);


		if (data == null){
			data = new SearchData();
			req.getSession().setAttribute("listData", data);
		}
		if (req.getAttribute("tracks") == null) {	
			List<Track> list = doSearch(data);
			if (list != null)
				req.setAttribute("tracks", list);
			else
				req.removeAttribute("tracks");
		}
		return data;
	}
	@Override
	protected ModelAndView onSubmit(Object arg0, BindException errors) throws Exception {
System.out.println("SUBMIT");
		SearchData command = (SearchData) arg0;
		SearchData data = command;
		List<Track> list = doSearch(data);
		ModelAndView mav = new ModelAndView(getSuccessView(), getCommandName(), command);
		mav.addAllObjects(errors.getModel());
		mav.addObject("tracks", list);
		return mav;
	}
	private List<Track> doSearch(SearchData command) {
		List<Track> list = null;
		if (command == null
				|| ((command.ISRC == null || "".equals(command.ISRC)) && 
					(command.title == null || "".equals(command.title)) && 
					(command.artist == null || "".equals(command.artist)) &&
					(command.artist == null || "".equals(command.artist)) &&
					(command.ingestor == null || "".equals(command.ingestor))&&
					(command.ingestTo == null|| "".equals(command.ingestTo)) &&
					(command.label == null|| "".equals(command.label)))) {
			System.out.println("NULL SEARCH");

		} else {
			System.out.println("SEARCHING "+command.ISRC+"," +command.title+"," +command.artist+"," +command.ingestFrom+"," +command.ingestTo);
			//this.data = data;
			list = (List<Track>) trackDAO.search(command.artist, command.title, command.ISRC, command.ingestFrom, command.ingestTo, command.label, command.ingestor);

		}
		return list;
	
	}

	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(dateFormat, true));
	}

}
