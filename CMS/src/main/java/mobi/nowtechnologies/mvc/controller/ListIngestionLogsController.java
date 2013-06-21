package mobi.nowtechnologies.mvc.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mobi.nowtechnologies.domain.IngestionLog;
import mobi.nowtechnologies.service.dao.IngestionLogDAO;
import mobi.nowtechnologies.util.Constants;

import org.springframework.web.servlet.mvc.SimpleFormController;

public class ListIngestionLogsController extends SimpleFormController implements Constants {

	private IngestionLogDAO ingestionLogDAO;
	
//	private static SearchData data;

	public ListIngestionLogsController() {
		//setCommandClass(List<IngestionLog>.class);
		setCommandName("data");
		setSessionForm(true);
	}


	public IngestionLogDAO getIngestionLogDAO() {
		return ingestionLogDAO;
	}


	public void setIngestionLogDAO(IngestionLogDAO ingestionLogDAO) {
		this.ingestionLogDAO = ingestionLogDAO;
	}


	@Override
	protected Object formBackingObject(HttpServletRequest req) throws Exception {
		SearchData data = (SearchData) req.getSession().getAttribute("listData");
		
		System.out.println("formBackingObject "+data);


		return ingestionLogDAO.getLastLogs();
	}

}
