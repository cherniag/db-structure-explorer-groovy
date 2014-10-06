package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.FileService;
import mobi.nowtechnologies.server.service.FileService.FileType;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.web.servlet.FileInResponseView;
import mobi.nowtechnologies.server.shared.web.servlet.PlainTextView;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

/**
 * FileController
 *
 * @author Maksym Chernolevskyi (maksym)
 *
 */
@Controller
public class FileController extends CommonController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class.getName());

    @Resource
    private FileService fileService;

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/{apiVersion:3\\.[6-9]|[4-9]{1}\\.[0-9]{1,3}}/GET_FILE"
    })
    public View getFile(
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

            user = checkUser(userName, userToken, timestamp, deviceUID, false, ActivationStatus.ACTIVATED);

            FileType fileType = FileType.valueOf(fileTypeName);
            if(fileType == FileType.VIDEO){
                final String videoURL = fileService.getVideoURL(user, mediaId);
                final String textValue = StringUtils.isEmpty(videoURL) ? "" : videoURL;
                return new PlainTextView(textValue);
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

    private View processGetFile(User user, String mediaId, FileType fileType, String resolution,final HttpServletRequest request){

        Media media = fileService.getMedia(mediaId, fileType, resolution, user);

        String mediaFileName = fileService.getFileName(media, fileType, user.getDeviceTypeId());

        final InputStream fileStream = fileService.getFileStreamForMedia(media, mediaId, resolution, mediaFileName, fileType, user);

        final String contentType = fileService.getContentType(mediaFileName);
        return new FileInResponseView(contentType, fileStream);
    }

}
