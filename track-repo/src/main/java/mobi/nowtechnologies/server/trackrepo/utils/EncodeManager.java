package mobi.nowtechnologies.server.trackrepo.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.utils.image.ImageGenerator;

public class EncodeManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(EncodeManager.class);
	
	private static String MP3_ENCODING = "mp3";
	private static String ACC_ENCODING = "acc";
	private static String WAV_ENCODING = "wav";
	private static String PCMS16LE_ENCODING = "pcm_s16le";
	private static String UNKNNOWN_ENCODING = "unknown";
	
	private ExternalCommand commandGetEncoding;
	private ExternalCommand commandEncodeUnknown;
	private ExternalCommand commandEncodeMP3;
	private ExternalCommand commandEncodeACC;
	private ExternalCommand commandEncodeWAV;
	private ExternalCommand commandDecodeMP3;
	private ExternalCommand commandEncodeMobileAudio;
	private ExternalCommand commandEncodePreviewAudio;
	private ExternalCommand commandAddTag;
	private ImageGenerator imageGenerator;
	private UITSCommandAdapter commandUITS;
	private Resource workDir;
	private Resource purchasedDir;
	private Resource imageDir;
	private Resource headerDir;
	private Resource audioDir;
	private Resource encodedDir;
	private Resource previewDir;
	private Resource neroHome;
	private UploadToCloudFileManager cloudUploadFileManager;
	
	public void encode(Track track) throws IOException, InterruptedException {
		
		boolean isVideo = track.getFile(AssetFile.FileType.VIDEO) != null;
		
		List<String> filesToPrivate = new ArrayList<String>();
		List<String> filesToData = new ArrayList<String>();
		
		//Thumbnails generation
		try {
			filesToPrivate.addAll(imageGenerator.generateThumbnails(track.getFileName(AssetFile.FileType.IMAGE), 
											  						track.getIsrc(), 
											  						isVideo));
		} catch (Exception e) {
			LOGGER.error("Image generating failed", e);
		}
		
		if (!isVideo) {

			//Generating download audio
			String audioFilePath = track.getFileName(AssetFile.FileType.DOWNLOAD);
			LOGGER.debug("Start encoding for file: " + audioFilePath);

			String encoding = getEncoding(audioFilePath);
			LOGGER.debug("Encoding is " + encoding);
		
			if (UNKNNOWN_ENCODING.equalsIgnoreCase(encoding)) {
								
				LOGGER.debug("Tryng to fix unknown audio file: " + audioFilePath);
				
				String fixedFileName = getWorkDir() + track.getIsrc() + "_fix.mp3";
				commandEncodeUnknown.executeCommand(audioFilePath, fixedFileName);
				audioFilePath = fixedFileName;
				encoding = getEncoding(audioFilePath);
				LOGGER.debug("Encoding after fixing is " + encoding);
			}

			String purchasedFileName = getPurchasedDir() + track.getIsrc() + ".mp3";
			LOGGER.debug("Purchased file name: " + purchasedFileName);
			
			if (MP3_ENCODING.equalsIgnoreCase(encoding)) {
								
				commandEncodeMP3.executeCommand(audioFilePath, purchasedFileName);
				
			} else if (ACC_ENCODING.equalsIgnoreCase(encoding)) {
				
				commandEncodeACC.executeCommand(audioFilePath, purchasedFileName);
				
			} else if (WAV_ENCODING.equalsIgnoreCase(encoding)) {
				
				commandEncodeWAV.executeCommand(audioFilePath, purchasedFileName);
			}
			LOGGER.debug("Purchased file is created: " + new File(purchasedFileName).exists());
			
			String tempFileName = getWorkDir() + track.getIsrc() + ".mp3";
			LOGGER.debug("Temp file is " + tempFileName);
			commandUITS.executeDownloadFiles(purchasedFileName, tempFileName);
			
			moveFile(tempFileName, purchasedFileName);
			filesToPrivate.add(purchasedFileName);
			LOGGER.debug("Purchased file exists after UITS: " + new File(purchasedFileName).exists());
			LOGGER.debug("Temp file is removed: " + new File(tempFileName).exists());
			
			
			
			//Generating Mobile audio
			LOGGER.debug("Start generating mobile audio: " + audioFilePath);

			String encodeInputFile = audioFilePath;
			LOGGER.debug("Working file is " + encodeInputFile);
			if (MP3_ENCODING.equalsIgnoreCase(encoding)) {
				
				LOGGER.debug("MP3 decoding to wav");
				
				String decodedMP3File = getWorkDir() + track.getIsrc() + ".wav";
				LOGGER.debug("MP3 decoded file: " + decodedMP3File);
				commandDecodeMP3.executeCommand(decodedMP3File, audioFilePath);
				encodeInputFile = decodedMP3File;
				LOGGER.debug("Working file's been changed to " + encodeInputFile);
			}
			LOGGER.debug("Working file exists: " + new File(encodeInputFile).exists());
			
			//Encoding with 48 and 96 bitrate 
			
			String coverImageFileName = getImageDir() + track.getIsrc() + "_cover.png";
			LOGGER.debug("Cover file: " + coverImageFileName + " and it exists: " + new File(coverImageFileName).exists()); 

			for (String bitrate : new String[] {"48", "96"}) {
			
				LOGGER.debug("Start converting for bitrate " + bitrate);
				
				String bitrateFileName = getWorkDir() + track.getIsrc() + "_"+ bitrate + ".m4a";
				commandEncodeMobileAudio.executeCommand(encodeInputFile, bitrate, bitrateFileName);
				LOGGER.debug("Encoded file to bitrate " + bitrate + ": " + bitrateFileName + "and it exists: " + new File(bitrateFileName).exists());
				
				commandAddTag.executeCommand(neroHome.getFile().getAbsolutePath(),
											 bitrateFileName, 
											 emptyNull(track.getTitle()),
											 emptyNull(track.getArtist()),
											 emptyNull(track.getAlbum()),
											 emptyNull(track.getGenre()),
											 emptyNull(track.getYear()),
											 emptyNull(track.getArtist()),
											 "",
											 emptyNull(track.getCopyright()),
											 emptyNull(track.getIsrc()),
											 coverImageFileName);
				LOGGER.debug("Encoded file to bitrate " + bitrate + " with tag: " + bitrateFileName + "and it exists: " + new File(bitrateFileName).exists());
				
				String audFileName = track.getIsrc() + "_"+ bitrate + ".aud";
				String hdrFileName = track.getIsrc() + "_"+ bitrate + ".hdr";
				String encFileName = track.getIsrc() + "_"+ bitrate + ".enc";
				
				commandUITS.executeMobileFiles(bitrateFileName, getWorkDir() + audFileName, getWorkDir() + hdrFileName, getWorkDir() + encFileName);
				
				LOGGER.debug("Audio file is " + getWorkDir() + audFileName + " and exists: " + new File(getWorkDir() + audFileName).exists());
				LOGGER.debug("Header file is " + getWorkDir() + hdrFileName + " and exists: " + new File(getWorkDir() + hdrFileName).exists());
				LOGGER.debug("Encoded file is " + getWorkDir() + encFileName + " and exists: " + new File(getWorkDir() + encFileName).exists());
				
				moveFile(getWorkDir() + audFileName, getAudioDir() + audFileName);
				filesToPrivate.add(getAudioDir() + audFileName);
				
				moveFile(getWorkDir() + hdrFileName, getHeaderDir() + hdrFileName);
				filesToPrivate.add(getHeaderDir() + hdrFileName);

				moveFile(getWorkDir() + encFileName, getEncodedDir() + encFileName);
				filesToPrivate.add(getEncodedDir() + encFileName);

				LOGGER.debug("Audio file is moved to " + getAudioDir() + audFileName + " and exists: " + new File(getAudioDir() + audFileName).exists());
				LOGGER.debug("Header file is moved to " + getHeaderDir() + hdrFileName + " and exists: " + new File(getHeaderDir() + hdrFileName).exists());
				LOGGER.debug("Encoded file is moved to " + getEncodedDir() + encFileName + " and exists: " + new File(getEncodedDir() + encFileName).exists());
			}
			
			//Generating preview audio
			String previewFileName = getWorkDir() + track.getIsrc() + "P.m4a";
			LOGGER.debug("Preview file is " + previewFileName);
			commandEncodePreviewAudio.executeCommand(encodeInputFile, previewFileName);
			LOGGER.debug("Preview file is " + previewFileName + " and exists: " + new File(previewFileName).exists());
			
			commandAddTag.executeCommand(neroHome.getFile().getAbsolutePath(),
										 previewFileName, 
					 					 emptyNull(track.getTitle()),
					 					 emptyNull(track.getArtist()),
					 					 emptyNull(track.getAlbum()),
					 					 emptyNull(track.getGenre()),
					 					 emptyNull(track.getYear()),
					 					 emptyNull(track.getArtist()),
					 					 "",
					 					 emptyNull(track.getCopyright()),
					 					 emptyNull(track.getIsrc()),
					 					 coverImageFileName);
			LOGGER.debug("Preview file with tag is " + previewFileName + " and exists: " + new File(previewFileName).exists());
			
			String resultPreviewFileName = getPreviewDir() + track.getIsrc() + "P.m4a";
			moveFile(previewFileName, resultPreviewFileName);
			filesToData.add(resultPreviewFileName);
			LOGGER.debug("Preview file moved to " + getPreviewDir() + track.getIsrc() + "P.m4a" + " and exists: " + new File(getPreviewDir() + track.getIsrc() + "P.m4a").exists());
		}

		cloudUploadFileManager.uploadFilesToCloud(track, filesToPrivate, filesToData);
	}
	
	private String getEncoding(String audioFilePath) throws IOException, InterruptedException {
		String encoding = commandGetEncoding.executeCommand(audioFilePath);
		LOGGER.debug("Encoding is " + encoding);
		
		if (MP3_ENCODING.equalsIgnoreCase(encoding)) {
			return MP3_ENCODING;
		} else if (ACC_ENCODING.equalsIgnoreCase(encoding)) {
			return ACC_ENCODING;
		} else if (encoding != null && encoding.toLowerCase().startsWith(PCMS16LE_ENCODING)) {
			return WAV_ENCODING;
		} else {
			return UNKNNOWN_ENCODING;
		}
	}
	
	private void moveFile(String source, String target) {
		
		new File(target).delete();
		new File(source).renameTo(new File(target));
	}
	
	private String emptyNull(Object str) {
		if (str == null) {
			return "";
		}
		return str.toString();
	}
	
	public void setCommandGetEncoding(ExternalCommand commandGetEncoding) {
		this.commandGetEncoding = commandGetEncoding;
	}
	public void setCommandEncodeUnknown(ExternalCommand commandEncodeUnknown) {
		this.commandEncodeUnknown = commandEncodeUnknown;
	}
	public void setCommandEncodeMP3(ExternalCommand commandEncodeMP3) {
		this.commandEncodeMP3 = commandEncodeMP3;
	}
	public void setCommandEncodeACC(ExternalCommand commandEncodeACC) {
		this.commandEncodeACC = commandEncodeACC;
	}
	public void setCommandEncodeWAV(ExternalCommand commandEncodeWAV) {
		this.commandEncodeWAV = commandEncodeWAV;
	}
	public void setCommandDecodeMP3(ExternalCommand commandDecodeMP3) {
		this.commandDecodeMP3 = commandDecodeMP3;
	}
	public void setCommandEncodeMobileAudio(ExternalCommand commandEncodeMobileAudio) {
		this.commandEncodeMobileAudio = commandEncodeMobileAudio;
	}
	public void setCommandEncodePreviewAudio(ExternalCommand commandEncodePreviewAudio) {
		this.commandEncodePreviewAudio = commandEncodePreviewAudio;
	}
	public void setCommandAddTag(ExternalCommand commandAddTag) {
		this.commandAddTag = commandAddTag;
	}
	public void setImageGenerator(ImageGenerator imageGenerator) {
		this.imageGenerator = imageGenerator;
	}
	public void setCommandUITS(UITSCommandAdapter commandUITS) {
		this.commandUITS = commandUITS;
	}
	public void setWorkDir(Resource workDir) {
		this.workDir = workDir;
	}
	public void setPurchasedDir(Resource purchasedDir) {
		this.purchasedDir = purchasedDir;
	}
	public void setImageDir(Resource imageDir) {
		this.imageDir = imageDir;
	}
	public void setHeaderDir(Resource headerDir) {
		this.headerDir = headerDir;
	}
	public void setAudioDir(Resource audioDir) {
		this.audioDir = audioDir;
	}
	public void setEncodedDir(Resource encodedDir) {
		this.encodedDir = encodedDir;
	}
	public void setPreviewDir(Resource previewDir) {
		this.previewDir = previewDir;
	}
	private String getWorkDir() throws IOException {
		return workDir.getFile().getAbsolutePath() + "/";
	}
	private String getPurchasedDir() throws IOException {
		return purchasedDir.getFile().getAbsolutePath() + "/";
	}
	private String getImageDir() throws IOException {
		return imageDir.getFile().getAbsolutePath() + "/";
	}
	private String getHeaderDir() throws IOException {
		return headerDir.getFile().getAbsolutePath() + "/";
	}
	private String getAudioDir() throws IOException {
		return audioDir.getFile().getAbsolutePath() + "/";
	}
	private String getEncodedDir() throws IOException {
		return encodedDir.getFile().getAbsolutePath() + "/";
	}
	private String getPreviewDir() throws IOException {
		return previewDir.getFile().getAbsolutePath() + "/";
	}
	public void setNeroHome(Resource neroHome) {
		this.neroHome = neroHome;
	}
	public void setCloudUploadFileManager(UploadToCloudFileManager cloudUploadFileManager) {
		this.cloudUploadFileManager = cloudUploadFileManager;
	}
}
