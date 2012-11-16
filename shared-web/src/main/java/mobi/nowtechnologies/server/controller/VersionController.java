package mobi.nowtechnologies.server.controller;

import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.server.dto.VersionDto;
import mobi.nowtechnologies.server.service.VersionService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author Maksym Chernolevskyi (maksym)
 * @author Titov Mykhaylo (titov)
 *
 */
@Controller
@RequestMapping(value = {"/VERSION","/version"})
public class VersionController {
		
	private VersionService versionService;
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody VersionDto sendVersion(HttpServletResponse response) {
		return versionService.getVersion();
	}

	public void setVersionService(VersionService versionService) {
		this.versionService = versionService;
	}
	
}