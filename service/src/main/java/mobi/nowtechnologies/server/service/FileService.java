package mobi.nowtechnologies.server.service;

import com.brightcove.proserve.mediaapi.wrapper.ReadApi;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.Rendition;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.Video;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.MediaDeliveryEnum;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.TranscodeEncodeToEnum;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.VideoFieldEnum;
import com.brightcove.proserve.mediaapi.wrapper.exceptions.BrightcoveException;
import mobi.nowtechnologies.server.persistence.dao.MediaLogTypeDao;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.EnumSet;
import java.util.List;

import static mobi.nowtechnologies.server.shared.AppConstants.SEPARATOR;
import static org.apache.commons.lang.StringUtils.containsAny;
import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notNull;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

/**
 * FileService
 *
 * @author Alexander Kolpakov (akolpakov)
 * @author Maksym Chernolevskyi (maksym)
 */
public class FileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class.getName());
    private static final java.util.logging.Logger BRIGHTCOVE_LOGGER = java.util.logging.Logger.getLogger("BrightcoveLog");

    private UserService userService;

    private static final String POINT = ".";
    private static final String UNDERSCORE = "_";

    private Resource storePath;
    private MediaService mediaService;

    private ReadApi brightcoveReadService;
    private String brightcoveReadToken;

    public static enum FileType {
        IMAGE_LARGE("image"),
        IMAGE_SMALL("image"),
        IMAGE_RESOLUTION("image"),
        HEADER("header"),
        AUDIO("audio"),
        VIDEO("video"),
        PURCHASED("purchased");

        private String folderName;

        FileType(String folderName) {
            this.folderName = folderName;
        }

        public String getFolderName() {
            return folderName;
        }
    }

    public void init() {
        notNull(storePath, "The parameter storePath is null");

        this.brightcoveReadService = new ReadApi(BRIGHTCOVE_LOGGER);
    }

    public void setBrightcoveReadToken(String brightcoveReadToken) {
        this.brightcoveReadToken = brightcoveReadToken;
    }

    public void setStorePath(Resource storePath) {
        try {
            File file = storePath.getFile();
            isTrue(storePath.exists(), "Path does not exist: " + file.getAbsolutePath() + ". Amend store.path property");
            this.storePath = storePath;
        } catch (IOException e) {
            LOGGER.error("Error to set up propertie 'store.path' in FileSirvice. store.path=" + storePath);
            throw new RuntimeException(e);
        }
    }

    public void setMediaService(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public File getFile(String mediaIsrc, FileType fileType, String resolution, User user) {
        int userId = user.getId();
        Media media = mediaService.findByIsrc(mediaIsrc);

        notNull(media, "error finding filename in db, mediaId=" + mediaIsrc + ", fileType=" + fileType +
                ", resolution=" + resolution + ", userId=" + userId);
        String mediaFileName = getFilename(media, fileType, user.getDeviceTypeId());

        String folderPath = getFolder(fileType.getFolderName());

        File fileName;
        if (fileType.equals(FileType.IMAGE_RESOLUTION)) {
            notNull(resolution, "The parameter fileResolution is null");
            isTrue(!containsAny(resolution, "/\\"), "The parameter resolution couldn't contain \\ and / symbols");

            StringBuilder builder = new StringBuilder(mediaFileName);
            builder.insert(mediaFileName.lastIndexOf(POINT), UNDERSCORE
                    + resolution);
            builder.insert(0, folderPath + SEPARATOR);
            fileName = new File(builder.toString());
        } else{
            fileName = new File(folderPath, mediaFileName);
        }
        File file = fileName;
        isTrue(file.exists(), "Could not find file type [" + fileType + "] for media isrc [" + mediaIsrc +
                "], path="+file.getAbsolutePath());

        if (fileType.equals(FileType.PURCHASED))
            mediaService.logMediaEvent(userId, media, MediaLogTypeDao.DOWNLOAD_ORIGINAL);

        if (fileType.equals(FileType.HEADER)) {
            LOGGER.info("conditionalUpdateByUserAndMedia user [{}], media [{}]", userId, media.getI());
            mediaService.conditionalUpdateByUserAndMedia(userId, media.getI());
        }
        return file;
    }

    public String getFolder(String folderName) {
        try {
            File file = storePath.getFile();
            return new File(file, folderName).getAbsolutePath();
        } catch (IOException e) {
            LOGGER.error(e.getStackTrace().toString());
            throw new RuntimeException(e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public File getFile(String mediaIsrc, FileType fileType, String resolution, int userId) throws IOException {
        notNull(mediaIsrc, "The parameter mediaIsrc is null");
        notNull(fileType, "The parameter fileType is null");

        LOGGER.debug("input parameters mediaIsrc, fileType, resolution, userId, outputStream: [{}], [{}]", new Object[]{mediaIsrc, fileType, resolution, userId});
        User user = userService.findById(userId);
        File file = getFile(mediaIsrc, fileType, resolution, user);
        LOGGER.debug("Output parameter file=[{}]", file);
        return file;
    }

    private String getFilename(Media media, FileType fileType, byte deviceTypeId) {
        switch (fileType) {
            case AUDIO:
                  /* Christophe: no longer return preview for iPhone
            if (deviceTypeId == DeviceType.IOS)
				return media.getAudioPreviewFile() == null ?
						null : media.getAudioPreviewFile().getFilename();
                  */
                return media.getAudioFile() == null ?
                        null : media.getAudioFile().getFilename();
            case HEADER:
                  /* Christophe: no longer return preview for iPhone
			if (deviceTypeId == DeviceType.IOS)
				return media.getHeaderPreviewFile() == null ?
						null : media.getHeaderPreviewFile().getFilename();
                    */
                return media.getHeaderFile() == null ?
                        null : media.getHeaderFile().getFilename();
            case IMAGE_LARGE:
                return media.getImageFIleLarge() == null ?
                        null : media.getImageFIleLarge().getFilename();
            case IMAGE_SMALL:
                return media.getImageFileSmall() == null ?
                        null : media.getImageFileSmall().getFilename();
            case IMAGE_RESOLUTION:
                return media.getImgFileResolution() == null ?
                        null : media.getImgFileResolution().getFilename();
            case PURCHASED:
                return media.getPurchasedFile() == null ?
                        null : media.getPurchasedFile().getFilename();
            default:
                return null;
        }
    }

    public String getExtention(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public String getContentType(String name) {
        return name.endsWith(".jpg") ? IMAGE_JPEG_VALUE : APPLICATION_OCTET_STREAM_VALUE;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public File downloadOriginalFile(OutputStream outputStream, String isrc, int userId) throws ServiceException {
        File file = null;
        try {
            file = getFile(isrc, FileService.FileType.PURCHASED, null, userId);
        } catch (IOException e) {
            LOGGER.error("Can't find purchased file. path=" + file.getAbsolutePath() + ", isrc=" + isrc);
            throw new ServiceException("error.download.file", "Can't download puchased file");
        } catch (ServiceException e) {
            throw e;
        }

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            IOUtils.copy(fileInputStream, outputStream);
        } catch (FileNotFoundException e) {
            LOGGER.error("Can't find purchased file {}", file.getAbsoluteFile());
            throw new ServiceException("error.download.file", "Can't download puchased file");
        } catch (IOException e) {
            LOGGER.error("User interrupted downloading process of file {}", file.toString());
            throw new ServiceException("error.download.file", "Can't download puchased file");
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
        return file;
    }

    @Transactional(readOnly = true)
    public String getVideoURL(User user, String mediaIsrc) throws BrightcoveException {
        LOGGER.debug("Get video url for isrc=" + mediaIsrc);

        boolean isWindowsPhoneUser = DeviceType.WINDOWS_PHONE.equals(user.getDeviceType().getName());
        MediaDeliveryEnum mediaDelivery = isWindowsPhoneUser ? MediaDeliveryEnum.HTTP : MediaDeliveryEnum.HTTP_IOS;

        Video video = brightcoveReadService.FindVideoByReferenceId(brightcoveReadToken, mediaIsrc, EnumSet.of(VideoFieldEnum.FLVURL, VideoFieldEnum.RENDITIONS), null, mediaDelivery);
        String url = video.getFlvUrl();
        if (isWindowsPhoneUser) {
            url = url != null && url.endsWith(TranscodeEncodeToEnum.MP4.name().toLowerCase()) ? url : null;
            if (url == null) {
                List<Rendition> renditions = video.getRenditions();
                if (renditions != null) {
                    for (Rendition rendition : renditions) {
                        if (rendition.getVideoContainer().equals(TranscodeEncodeToEnum.MP4.name())) {
                            url = rendition.getUrl();
                        }
                    }
                }
            }
        }

        LOGGER.debug("Return video url=[{}] for isrc=[{}]", new Object[]{video.getFlvUrl(), mediaIsrc});
        return url;
    }
}
