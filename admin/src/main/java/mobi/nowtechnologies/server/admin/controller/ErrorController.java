package mobi.nowtechnologies.server.admin.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ErrorController {

	@RequestMapping(value = "/errors/404.html")
	public ModelAndView notFoundError404(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getModelAndView(request, "/errors/404");
	}
	
	@RequestMapping(value = "/errors/500.html")
	public ModelAndView internalServerError500(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getModelAndView(request, "/errors/500");
	}

	protected ModelAndView getModelAndView(HttpServletRequest request, String viewName) {
		ModelAndView modelAndView = new ModelAndView(viewName);

			Object code = request.getAttribute("javax.servlet.error.status_code");
			Object message = request.getAttribute("javax.servlet.error.message");
			Object type = request.getAttribute("javax.servlet.error.exception_type");
			Object uri = request.getAttribute("javax.servlet.error.request_uri");
			Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");

			modelAndView.addObject("errorCode", code != null ? code.toString() : null);
			modelAndView.addObject("errorMessage", message != null ? message.toString() : null);
			modelAndView.addObject("exeptionType", type != null ? type.toString() : null);
			modelAndView.addObject("requestedUri", uri != null ? uri.toString() : null);
			modelAndView.addObject("exception", throwable);
		
		return modelAndView;
	}
}
