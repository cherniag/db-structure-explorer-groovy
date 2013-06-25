package mobi.nowtechnologies.mvc.controller;

import java.text.DateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import mobi.nowtechnologies.service.PublishService;
import mobi.nowtechnologies.util.Constants;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class PublishFormController extends SimpleFormController implements Constants {

	private PublishService publishService;

	

	public void setPublishService(PublishService publishService) {
		this.publishService = publishService;
	}


	@Override
	protected Object formBackingObject(HttpServletRequest req) throws Exception {
		String id = req.getParameter(PARAM_ID);
		return publishService.prepareData(id);

	}

	@Override
	protected ModelAndView onSubmit(Object arg0) throws Exception {
		PublishData data = (PublishData) arg0;
		boolean result = publishService.process(data);
		return super.onSubmit(arg0);
	}


	@Override
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, request.getLocale());
		binder.registerCustomEditor(Date.class, new CustomDateEditor(df, true));

	}

}
