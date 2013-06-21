package mobi.nowtechnologies.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.service.IngestService;
import mobi.nowtechnologies.util.Constants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;

public class IngestTracksWizardController extends AbstractWizardFormController implements Constants {

	protected static final Log LOG = LogFactory.getLog(IngestTracksWizardController.class);

	private IngestService ingestService;

	
	public void setIngestService(IngestService ingestService) {
		this.ingestService = ingestService;
	}


	public IngestTracksWizardController() {
		setCommandClass(IngestWizardData.class);
		setCommandName("data");
		setSessionForm(true);
	}



	@Override
	protected Object formBackingObject(HttpServletRequest req) throws Exception {

		String parserName = req.getParameter("PARSER");
		return ingestService.prepareData(parserName);
	}

	@Override
	protected ModelAndView processFinish(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		LOG.debug("INGEST processFinish");
		
		boolean result = ingestService.processFinish((IngestWizardData) command);

		return new ModelAndView(getSuccessView());
	}

	protected void postProcessPage(HttpServletRequest request, Object command, Errors errors, int page) throws Exception {
		LOG.debug("POST PROCESS " + page);
		ingestService.postProcessPage((IngestWizardData) command, page);
	}


	// returns the last page as the success view
	private String getSuccessView() {
		return getPages()[getPages().length - 1];
	}
	

}
