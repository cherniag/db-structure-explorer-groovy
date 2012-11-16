package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.MediaLogTypeDao;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;

import static mobi.nowtechnologies.server.shared.AppConstants.SEPARATOR;

/**
 * FileService
 * 
 * @author Alexander Kolpakov (akolpakov)
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
public class FileService{
	private static final Logger LOGGER = LoggerFactory
			.getLogger(FileService.class.getName());
	
	private UserService userService;

	private static final String POINT = ".";
	private static final String UNDERSCORE = "_";

	private Resource storePath;
	private MediaService mediaService;

	public static enum FileType {
		IMAGE_LARGE("image"),
		IMAGE_SMALL("image"),
		IMAGE_RESOLUTION("image"),
		HEADER("header"),
		AUDIO("audio"),
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
		Validate.notNull(storePath, "The parameter storePath is null");
	}

	public void setStorePath(Resource storePath) throws IOException {
		LOGGER.info("Store path for media files is [{}]", storePath);
        File file = storePath.getFile();
        Validate.isTrue(storePath.exists(), "Path does not exist: "+ file.getAbsolutePath() + ". Amend store.path property");
        this.storePath = storePath;
	}

	public void setMediaService(MediaService mediaService) {
		this.mediaService = mediaService;
	}
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public File getFile(String mediaIsrc, FileType fileType,
			String resolution, User user) {
		if (mediaIsrc == null)
			throw new ServiceException("The parameter mediaIsrc is null");
		if (fileType == null)
			throw new ServiceException("The parameter fileType is null");
		if (user == null)
			throw new ServiceException("The parameter user is null");
		int userId = user.getId();
		Media media = mediaService.findByIsrc(mediaIsrc);
		String mediaFileName = media == null ? null : getFilename(media, fileType, user.getDeviceTypeId());
		if (mediaFileName == null) {
			LOGGER.error("error finding filename in db, " +
					"mediaId [{}], fileType [{}], resolution [{}], userId [{}]",
					new Object[]{mediaIsrc, fileType, resolution, userId});
			throw new ServiceException("error finding filename in db");
		}

        String folderPath = storePath.getFilename() + SEPARATOR + fileType.getFolderName()
				+ SEPARATOR;

		String fileName;
		if (fileType.equals(FileType.IMAGE_RESOLUTION)) {
			if (resolution == null)
				throw new ServiceException(
						"The parameter fileResolution is null");
			if (resolution.contains("/")||resolution.contains("\\"))
				throw new ServiceException("The parameter resolution couldn't contain \\ and / symbols");
			StringBuilder builder = new StringBuilder(mediaFileName);
			builder.insert(mediaFileName.lastIndexOf(POINT), UNDERSCORE
					+ resolution);
			builder.insert(0, folderPath);
			fileName = builder.toString();
		} else
			fileName = folderPath + mediaFileName;
		File file = new File(fileName);
		if (!file.exists())
			throw new ServiceException("Could not find file type [" + fileType
					+ "] for media isrc [" + mediaIsrc + "]");
		
		if (fileType.equals(FileType.PURCHASED))
			mediaService.logMediaEvent(userId, media, MediaLogTypeDao.DOWNLOAD_ORIGINAL);
		/*
		else
			mediaService.logMediaEvent(userId, media.getI(),
					MediaLogTypeDao.DOWNLOAD);
		 */
		if (fileType.equals(FileType.HEADER)) {
			LOGGER.info(
					"conditionalUpdateByUserAndMedia user [{}], media [{}]", 
					userId, media.getI());
			mediaService.conditionalUpdateByUserAndMedia(userId, media.getI());
		}
		return file;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public File getFile(String mediaIsrc, FileType fileType, String resolution, int userId) throws IOException {
		if (mediaIsrc == null)
			throw new ServiceException("The parameter mediaIsrc is null");
		if (fileType == null)
			throw new ServiceException("The parameter fileType is null");

		LOGGER.debug("input parameters mediaIsrc, fileType, resolution, userId, outputStream: [{}], [{}]", new Object[] { mediaIsrc, fileType, resolution, userId});
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
		case IMAGE_LARGE: return media.getImageFIleLarge() == null ?
				null : media.getImageFIleLarge().getFilename();
		case IMAGE_SMALL: return media.getImageFileSmall() == null ?
				null : media.getImageFileSmall().getFilename();
		case IMAGE_RESOLUTION: return media.getImgFileResolution() == null ?
				null : media.getImgFileResolution().getFilename();
		case PURCHASED: return media.getPurchasedFile() == null ?
				null : media.getPurchasedFile().getFilename();
		default: return null;
		}
	}
	
	public String getExtention(String name) {
		return name.substring(name.lastIndexOf(".")+1);
	}

	public String getContentType(String name) {
		String result = null;
		if (name.endsWith(".jpg"))
			result = "image/jpeg";
		else
			result = "application/octet-stream";
		return result;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public File downloadOriginalFile(OutputStream outputStream, String isrc, int userId) throws ServiceException {
		File file = null;
		try {
			file = getFile(isrc, FileService.FileType.PURCHASED, null, userId);
		} catch (IOException e) {
			LOGGER.error("Can't find purchased file {}", isrc);
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
}
