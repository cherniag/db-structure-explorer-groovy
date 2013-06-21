package mobi.nowtechnologies.mvc.controller;

import mobi.nowtechnologies.domain.Track;
import mobi.nowtechnologies.ingestors.emi.EmiTeleporter;
import mobi.nowtechnologies.service.dao.TrackDAO;
import mobi.nowtechnologies.util.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class ListRemoteFilesController extends SimpleFormController implements Constants {

	private TrackDAO trackDAO;

	private static RemoteFiles data;

	public ListRemoteFilesController() {
		setCommandClass(RemoteFiles.class);
		setCommandName("searchCommand");
	}

	public TrackDAO getTrackDAO() {
		return trackDAO;
	}

	public void setTrackDAO(TrackDAO trackDAO) {
		this.trackDAO = trackDAO;
	}

	@Override
	protected Object formBackingObject(HttpServletRequest req) throws Exception {
		if (data == null) {
			data = new RemoteFiles(); 
		    data.setFiles(new ArrayList<RemoteFiles.RemoteFile>());
		} else {
			data.getFiles().clear();
		}
		System.out.println("ListRemoteFilesController getting remote files");
		EmiTeleporter teleporter = new EmiTeleporter();
		List<String> result = teleporter.getDrops();
		for (String item:result) {
			RemoteFiles.RemoteFile file = data.new RemoteFile();
			file.setName(item);
			file.setSelected(false);
			data.getFiles().add(file);
		}
		return data;
	}

	@Override
	protected ModelAndView onSubmit(Object arg0, BindException errors) throws Exception {
		System.out.println("SUBMIT");
		RemoteFiles command = (RemoteFiles) arg0;
		data = command;
		ModelAndView mav = new ModelAndView(getSuccessView(), getCommandName(), command);
		mav.addAllObjects(errors.getModel());
		return mav;
	}


}
