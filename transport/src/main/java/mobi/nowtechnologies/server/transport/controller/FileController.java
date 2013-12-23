package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.FileService;
import mobi.nowtechnologies.server.service.FileService.FileType;
import mobi.nowtechnologies.server.shared.web.servlet.PlainTextModalAndView;
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
	private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class.getName());

	private FileService fileService;

	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/{apiVersion:3\\.[6-9]|[4-9]{1}\\.[0-9]{1,3}}/GET_FILE"
    })
    public ModelAndView getFile(
            @RequestParam("ID") final String mediaId,
            @RequestParam("TYPE") String fileTypeName,
            @RequestParam("USER_NAME") final String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(value = "DEVICE_UID", required = false) String deviceUID,
            @RequestParam(value = "RESOLUTION", required = false) String resolution,
            final HttpServletRequest request) throws Exception {
        User user = null;
        Exception ex = null;
        String community = getCurrentCommunityUri();
        try {
            LOGGER.info("command processing started");

            if (isValidDeviceUID(deviceUID)) {
                user = userService.checkCredentials(userName, userToken, timestamp, community, deviceUID);
            }
            else {
                user = userService.checkCredentials(userName, userToken, timestamp, community);
            }

            FileType fileType = FileType.valueOf(fileTypeName);
            if(fileType == FileType.VIDEO){
                final String videoURL = fileService.getVideoURL(user, mediaId);

                return new PlainTextModalAndView(videoURL);
            } else {
                return processGetFile(user, mediaId, fileType, resolution, request);
            }
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(null, community, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }

    protected User checkCredentials(String userName, String userToken, String timestamp, String communityName){
        if (userName == null)
            throw new NullPointerException("The parameter userName is null");
        if (communityName == null)
            throw new NullPointerException("The parameter communityName is null");
        return userService.checkCredentials(userName, userToken, timestamp, communityName);
    }

    protected ModelAndView processGetFile(User user, String mediaId, FileType fileType, String resolution,final HttpServletRequest request){

        final File file = fileService.getFile(mediaId, fileType, resolution, user);
        final String contentType = getContentType(file.getName());
        return new ModelAndView(new View() {
            @Override
            public void render(Map<String, ?> arg0,
                               HttpServletRequest arg1, HttpServletResponse response)
                    throws Exception {
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
    }

	private String getContentType(String name) {
		return fileService.getContentType(name);
	}
}
