package mobi.nowtechnologies.server.track_repo.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 *
 */
@Controller
public class SignInController extends AbstractCommonController{
	
	@RequestMapping(value="/signin")
	public @ResponseBody Boolean login(HttpServletRequest request) {
		return true;
	}
}
