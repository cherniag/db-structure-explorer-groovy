package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.FileService;
import mobi.nowtechnologies.server.service.FileService.FileType;
import mobi.nowtechnologies.server.service.UserService;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

/**
 * FileController
 * 
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
@Controller
public class FileController extends CommonController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(FileController.class.getName());

	private UserService userService;
	private FileService fileService;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	
	@RequestMapping(method = RequestMethod.POST, value = {"/GET_FILE", "**/GET_FILE"})
	public ModelAndView getFile(
			@RequestParam("ID") final String mediaId,
			@RequestParam("TYPE") String fileType,
			@RequestParam("APP_VERSION") String appVersion,
			@RequestParam("API_VERSION") String apiVersion,
			@RequestParam("COMMUNITY_NAME") String communityName,
			@RequestParam("USER_NAME") final String userName,
			@RequestParam("USER_TOKEN") String userToken,
			@RequestParam("TIMESTAMP") String timestamp,
			@RequestParam(value = "RESOLUTION", required = false) String resolution,
			final HttpServletRequest request,
			HttpServletResponse response) {
		LOGGER.info("command proccessing for [{}] user, [{}] community", userName, communityName);
		try {
			if (userName == null)
				throw new NullPointerException("The parameter userName is null");
			if (communityName == null)
				throw new NullPointerException("The parameter communityName is null");
			
			@SuppressWarnings("deprecation")
			User user = userService.checkCredentials(userName, userToken, timestamp, communityName);
			final File file = fileService.getFile(mediaId, FileType
					.valueOf(fileType), resolution, user);
			final String contentType = getContentType(file.getName());
			return new ModelAndView(new View() {
				@Override
				public void render(Map<String, ?> arg0,
						HttpServletRequest arg1, HttpServletResponse response)
						throws Exception {
					// uitsService.process(userName, mediaId, "distributor", new
					// FileInputStream(file), response
					// .getOutputStream());
					FileInputStream fileInputStream = new FileInputStream(file);
					String rangeAttribute = (String)request.getAttribute(HttpHeaders.RANGE);
					if (StringUtils.hasText(rangeAttribute)) {
						Long range = Long.valueOf(rangeAttribute);
						IOUtils.skipFully(fileInputStream, range);
						response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
					}
					try {
						IOUtils.copy(fileInputStream, response.getOutputStream());
					} finally {
						fileInputStream.close();
					}
				}

				@Override
				public String getContentType() {
					return contentType;
				}
			}, "EMPTY", new Object());
		} finally {
			LOGGER.info("GET_FILE command processing finished");
		}
	}

	private String getContentType(String name) {
		return fileService.getContentType(name);
	}
}
