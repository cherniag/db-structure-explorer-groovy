package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.common.util.TrackIdGenerator;
import mobi.nowtechnologies.server.persistence.dao.MediaLogTypeDao;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.MediaRepository;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import static mobi.nowtechnologies.server.shared.AppConstants.SEPARATOR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.List;

import com.brightcove.proserve.mediaapi.wrapper.ReadApi;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.Rendition;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.Video;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.MediaDeliveryEnum;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.TranscodeEncodeToEnum;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.VideoFieldEnum;
import com.brightcove.proserve.mediaapi.wrapper.exceptions.BrightcoveException;
import com.rackspacecloud.client.cloudfiles.FilesNotFoundException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.lang.StringUtils.containsAny;
import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notNull;

import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

/**
 * @author Alexander Kolpakov (akolpakov)
 * @author Maksym Chernolevskyi (maksym)
 */
public class FileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class.getName());
    private static final java.util.logging.Logger BRIGHTCOVE_LOGGER = java.util.logging.Logger.getLogger("BrightcoveLog");
    private static final String POINT = ".";
    private static final String UNDERSCORE = "_";
    private UserService userService;
    private Resource storePath;
    private MediaService mediaService;

    private ReadApi brightcoveReadService;
    private String brightcoveReadToken;

    private MediaRepository mediaRepository;

    private CloudFileService cloudFileService;

    private String destinationContainerNameForAudioContent;

    public String getDestinationContainerNameForAudioContent() {
        return destinationContainerNameForAudioContent;
    }

    public void setDestinationContainerNameForAudioContent(String destinationContainerNameForAudioContent) {
        this.destinationContainerNameForAudioContent = destinationContainerNameForAudioContent;
    }

    public void setCloudFileService(CloudFileService cloudFileService) {
        this.cloudFileService = cloudFileService;
    }

    public void setMediaRepository(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public void init() {
        notNull(storePath, "The parameter storePath is null");

        setBrightcoveReadService(new ReadApi(BRIGHTCOVE_LOGGER));
    }

    public void setBrightcoveReadService(ReadApi readApi) {this.brightcoveReadService = readApi;}

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
        notNull(mediaIsrc, "The parameter mediaIsrc is null");
        notNull(fileType, "The parameter fileType is null");
        notNull(user, "The parameter user is null");

        int userId = user.getId();
        Media media = mediaService.findByIsrc(mediaIsrc);

        notNull(media, "error finding filename in db, mediaId=" + mediaIsrc + ", fileType=" + fileType +
                       ", resolution=" + resolution + ", userId=" + userId);
        String mediaFileName = getFileName(media, fileType, user.getDeviceTypeId());

        File file = getFileFromLocalStorage(mediaIsrc, fileType, resolution, mediaFileName);
        logEvents(media, fileType, user);
        return file;
    }

    private File getFileFromLocalStorage(String mediaIsrc, FileType fileType, String resolution, String mediaFileName) {
        String folderPath = getFolder(fileType.getFolderName());

        File fileName;
        if (fileType.equals(FileType.IMAGE_RESOLUTION)) {
            notNull(resolution, "The parameter fileResolution is null");
            isTrue(!containsAny(resolution, "/\\"), "The parameter resolution couldn't contain \\ and / symbols");

            StringBuilder builder = new StringBuilder(mediaFileName);
            builder.insert(mediaFileName.lastIndexOf(POINT), UNDERSCORE + resolution);
            builder.insert(0, folderPath + SEPARATOR);
            fileName = new File(builder.toString());
        } else {
            fileName = new File(folderPath, mediaFileName);
        }
        File file = fileName;
        isTrue(file.exists(), "Could not find file type [" + fileType + "] for media isrc [" + mediaIsrc +
                              "], path=" + file.getAbsolutePath());
        return file;
    }

    private Media getMediaByTrackId(String trackId) {
        try {
            Pair<String, Long> stringLongPair = TrackIdGenerator.parseUniqueTrackId(trackId);
            return mediaRepository.findByTrackId(stringLongPair.getRight());
        } catch (Exception e) {
            LOGGER.error("Problem with track id [{}]", trackId, e);
            return mediaService.findByIsrc(trackId);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public InputStream getFileStreamForMedia(Media media, String mediaId, String resolution, String mediaFileName, FileType fileType, User user) {
        logEvents(media, fileType, user);
        try {
            return cloudFileService.getInputStream(destinationContainerNameForAudioContent, mediaFileName);
        } catch (FilesNotFoundException e) {
            LOGGER.error("ERROR download from cloud [{}]. Try to find on local storage", mediaFileName);
            File file = getFileFromLocalStorage(mediaId, fileType, resolution, mediaFileName);
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e1) {
                LOGGER.error("ERROR find on local storage[{}]", mediaFileName);
                throw new ExternalServiceException("cloudFile.service.externalError.couldnotopenstream", "Coudn't find  file");
            }
        }
    }

    private void logEvents(Media media, FileType fileType, User user) {
        if (fileType.equals(FileType.PURCHASED)) {
            mediaService.logMediaEvent(user.getId(), media, MediaLogTypeDao.DOWNLOAD_ORIGINAL);
        }

        if (fileType.equals(FileType.HEADER)) {
            LOGGER.info("conditionalUpdateByUserAndMedia user [{}], media [{}]", user.getId(), media.getI());
            mediaService.conditionalUpdateByUserAndMedia(user.getId(), media.getI());
        }
    }

    public Media getMedia(String trackId, FileType fileType, String resolution, User user) {
        notNull(trackId, "The parameter mediaIsrc is null");
        notNull(fileType, "The parameter fileType is null");
        notNull(user, "The parameter user is null");

        int userId = user.getId();


        Media media = getMediaByTrackId(trackId);

        notNull(media, "error finding filename in db, mediaId=" + trackId + ", fileType=" + fileType +
                       ", resolution=" + resolution + ", userId=" + userId);

        return media;
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

        LOGGER.debug("input parameters mediaIsrc, fileType, resolution, userId, outputStream: [{}], [{}]", new Object[] {mediaIsrc, fileType, resolution, userId});
        User user = userService.findById(userId);
        File file = getFile(mediaIsrc, fileType, resolution, user);
        LOGGER.debug("Output parameter file=[{}]", file);
        return file;
    }

    public String getFileName(Media media, FileType fileType, byte deviceTypeId) {
        switch (fileType) {
            case AUDIO:
                  /* Christophe: no longer return preview for iPhone
            if (deviceTypeId == DeviceType.IOS)
				return media.getAudioPreviewFile() == null ?
						null : media.getAudioPreviewFile().getFilename();
                  */
                return media.getAudioFile() == null ?
                       null :
                       media.getAudioFile().getFilename();
            case HEADER:
                  /* Christophe: no longer return preview for iPhone
            if (deviceTypeId == DeviceType.IOS)
				return media.getHeaderPreviewFile() == null ?
						null : media.getHeaderPreviewFile().getFilename();
                    */
                return media.getHeaderFile() == null ?
                       null :
                       media.getHeaderFile().getFilename();
            case IMAGE_LARGE:
                return media.getImageFIleLarge() == null ?
                       null :
                       media.getImageFIleLarge().getFilename();
            case IMAGE_SMALL:
                return media.getImageFileSmall() == null ?
                       null :
                       media.getImageFileSmall().getFilename();
            case IMAGE_RESOLUTION:
                return media.getImgFileResolution() == null ?
                       null :
                       media.getImgFileResolution().getFilename();
            case PURCHASED:
                return media.getPurchasedFile() == null ?
                       null :
                       media.getPurchasedFile().getFilename();
            default:
                return null;
        }
    }

    public String getExtention(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public String getContentType(String name) {
        return name.endsWith(".jpg") ?
               IMAGE_JPEG_VALUE :
               APPLICATION_OCTET_STREAM_VALUE;
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
        LOGGER.debug("Get video url for isrc={}", mediaIsrc);

        boolean isWindowsPhoneUser = DeviceType.WINDOWS_PHONE.equals(user.getDeviceType().getName());
        MediaDeliveryEnum mediaDelivery = isWindowsPhoneUser ?
                                          MediaDeliveryEnum.HTTP :
                                          MediaDeliveryEnum.HTTP_IOS;

        Video video = brightcoveReadService.FindVideoByReferenceId(brightcoveReadToken, mediaIsrc, EnumSet.of(VideoFieldEnum.FLVURL, VideoFieldEnum.RENDITIONS), null, mediaDelivery);
        String url = video.getFlvUrl();
        if (isWindowsPhoneUser) {
            url = url != null && url.endsWith(TranscodeEncodeToEnum.MP4.name().toLowerCase()) ?
                  url :
                  null;
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

        LOGGER.debug("Return video url=[{}] for isrc=[{}]", new Object[] {video.getFlvUrl(), mediaIsrc});
        return url;
    }

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
}
