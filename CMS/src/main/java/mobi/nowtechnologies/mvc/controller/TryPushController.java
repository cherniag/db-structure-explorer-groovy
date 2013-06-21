package mobi.nowtechnologies.mvc.controller;

import javax.servlet.http.HttpServletRequest;

import mobi.nowtechnologies.ExternalCommandThread;
import mobi.nowtechnologies.util.Constants;
import mobi.nowtechnologies.util.Property;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class TryPushController extends SimpleFormController implements Constants {

	protected static final Log LOG = LogFactory.getLog(TryPushController.class);

	@Override
	protected Object formBackingObject(HttpServletRequest req) throws Exception {
		
		String id = req.getParameter("id");
		TryPushData data = new TryPushData();
		data.setKey(id);
		return data;
	}

	@Override
	protected ModelAndView onSubmit(Object arg0) throws Exception {
		TryPushData data = (TryPushData) arg0;
		ExternalCommandThread thread = new ExternalCommandThread();
		thread.setCommand(Property.getInstance().getStringValue("push.try"));
		thread.addParam(data.getUid());
		thread.addParam(data.getKey());
		thread.run();
		if (thread.getExitCode() != 0) {
			LOG.error("Cannot try push");
		}
		return super.onSubmit(arg0);
	}

}
