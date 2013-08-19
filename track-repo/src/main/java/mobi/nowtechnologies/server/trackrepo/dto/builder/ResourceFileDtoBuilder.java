package mobi.nowtechnologies.server.trackrepo.dto.builder;

import mobi.nowtechnologies.java.server.uits.MP3Manager;
import mobi.nowtechnologies.java.server.uits.MP4Manager;
import mobi.nowtechnologies.server.trackrepo.Resolution;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.dto.ResourceFileDto;
import mobi.nowtechnologies.server.trackrepo.enums.AudioResolution;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import mobi.nowtechnologies.server.trackrepo.enums.ImageResolution;
import mobi.nowtechnologies.server.trackrepo.enums.VideoResolution;
import mobi.nowtechnologies.server.trackrepo.service.impl.TrackServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ResourceFileDtoBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(TrackServiceImpl.class);
	
	private MP3Manager mp3Manager = new MP3Manager();
	private MP4Manager mp4manager = new MP4Manager();

	private Resource publishDir;
	private Resource workDir;

	public void setPublishDir(Resource publishDir) {
		this.publishDir = publishDir;
	}

	public void setWorkDir(Resource workDir) {
		this.workDir = workDir;
	}

	public void init() throws Exception {
		if (publishDir == null || !publishDir.exists())
			throw new IllegalArgumentException("There is no folder under the following context property trackRepo.encode.destination");
		if (workDir == null || !workDir.exists())
			throw new IllegalArgumentException("There is no folder under the following context property trackRepo.encode.workdir");
	}
	
	public List<ResourceFileDto> build(Track track) throws IOException{
        String isrc = track.getIsrc();

		String workDirPath = workDir.getFile().getAbsolutePath();
		String distDirPath = publishDir.getFile().getAbsolutePath();

		List<ResourceFileDto> files = new ArrayList<ResourceFileDto>(15);

        AssetFile audioFile = track.getFile(AssetFile.FileType.DOWNLOAD);
		if(audioFile != null){
            String mp3hash = getMediaHash(getFilePath(FileType.ORIGINAL_MP3, AudioResolution.RATE_ORIGINAL, workDirPath, isrc));
            String aac48hash = getMediaHash(getFilePath(FileType.ORIGINAL_ACC, AudioResolution.RATE_48, workDirPath, isrc));
            String aac96hash = getMediaHash(getFilePath(FileType.ORIGINAL_ACC, AudioResolution.RATE_96, workDirPath, isrc));

            files.add(build(FileType.MOBILE_HEADER, AudioResolution.RATE_48, distDirPath, isrc, null));
            files.add(build(FileType.MOBILE_HEADER, AudioResolution.RATE_96, distDirPath, isrc, null));
            files.add(build(FileType.MOBILE_HEADER, AudioResolution.RATE_PREVIEW, distDirPath, isrc, null));
            files.add(build(FileType.MOBILE_AUDIO, AudioResolution.RATE_48, distDirPath, isrc, aac48hash));
            files.add(build(FileType.MOBILE_AUDIO, AudioResolution.RATE_96, distDirPath, isrc, aac96hash));
            files.add(build(FileType.MOBILE_AUDIO, AudioResolution.RATE_PREVIEW, distDirPath, isrc, null));
            files.add(build(FileType.DOWNLOAD, AudioResolution.RATE_ORIGINAL, distDirPath, isrc, mp3hash));
        }

        AssetFile videoFile = track.getFile(AssetFile.FileType.VIDEO);
        if(videoFile != null){
            ResourceFileDto videoFileDto = build(FileType.VIDEO, VideoResolution.RATE_ORIGINAL, null, videoFile.getExternalId(), null);
            videoFileDto.setDuration(videoFile.getDuration());
            files.add(videoFileDto);
        }

		files.add(build(FileType.IMAGE, ImageResolution.SIZE_ORIGINAL, distDirPath, isrc, null));
		files.add(build(FileType.IMAGE, ImageResolution.SIZE_LARGE, distDirPath, isrc, null));
		files.add(build(FileType.IMAGE, ImageResolution.SIZE_SMALL, distDirPath, isrc, null));
		files.add(build(FileType.IMAGE, ImageResolution.SIZE_22, distDirPath, isrc, null));
		files.add(build(FileType.IMAGE, ImageResolution.SIZE_21, distDirPath, isrc, null));
		files.add(build(FileType.IMAGE, ImageResolution.SIZE_11, distDirPath, isrc, null));
		files.add(build(FileType.IMAGE, ImageResolution.SIZE_6, distDirPath, isrc, null));
		files.add(build(FileType.IMAGE, ImageResolution.SIZE_3, distDirPath, isrc, null));
		
		return files;
	}
	
	public ResourceFileDto build(FileType type, Resolution resolution, String dir, String filename, String mediaHash) throws IOException {
		ResourceFileDto resourceFileDto = new ResourceFileDto(type, resolution, filename, mediaHash);

        if(dir != null){
            Integer fileSize = getFileSize(getFilePath(type, resolution, dir, filename));
            resourceFileDto.setSize(fileSize);
        } else {
            resourceFileDto.setSize(0);
        }

		return resourceFileDto;
	}

	private Integer getFileSize(String filepath) throws IOException {
		InputStream stream = null;
		try {
			File file = new File(filepath);
			if (file.exists()) {
				stream = file.toURI().toURL().openStream();
				return stream.available();
			}
		} finally {
			if (stream != null)
				stream.close();
		}

		return 0;
	}

	private String getFilePath(FileType type, Resolution resolution, String dir, String isrc) {
		String subdir = type.getPack() == null || type.getPack().isEmpty() ? "" : type.getPack() + "/";
		return dir + "/" + subdir + isrc + resolution.getSuffix() + "." + type.getExt();
	}
	
	private String getMediaHash(String fileName) {
		InputStream in = null;
		try {
			String mediaHash = null;
			if (fileName.toLowerCase().endsWith("." + FileType.DOWNLOAD.getExt())) {
				mediaHash = mp3Manager.mp3GetMediaHash(fileName);
			} else { // Assume AAC.....
				in = new FileInputStream(fileName);
				mediaHash = mp4manager.getMediaHash(in);
			}
			return mediaHash;
		} catch (Exception e) {
			LOGGER.error("Cannot get hash", e);
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOGGER.error("Cannot get hash", e);
				}
			}
		}
	}
}
