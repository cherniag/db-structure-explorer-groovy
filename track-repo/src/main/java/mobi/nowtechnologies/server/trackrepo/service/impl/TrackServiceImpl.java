package mobi.nowtechnologies.server.trackrepo.service.impl;


import java.io.File;
import java.io.IOException;
import java.util.*;

import mobi.nowtechnologies.server.service.CloudFileService;
import mobi.nowtechnologies.server.trackrepo.SearchTrackCriteria;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.enums.*;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepository;
import mobi.nowtechnologies.server.trackrepo.service.TrackService;
import mobi.nowtechnologies.server.trackrepo.utils.ExternalCommandThread;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public class TrackServiceImpl implements TrackService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TrackServiceImpl.class);

	private CloudFileService cloudFileService;
	
	private Resource encodeScript;
	private Resource itunesScript;
	private Resource publishDir;
	private Resource neroHome;
	private Resource workDir;
	private Resource classpath;
	private Resource privateKey;
	
	private String srcPullContainer;
	private String destPullContainer;

	private TrackRepository trackRepository;

	public void setCloudFileService(CloudFileService cloudFileService) {
		this.cloudFileService = cloudFileService;
	}

	public void init() throws Exception {
		if (encodeScript == null || !encodeScript.exists())
			throw new IllegalArgumentException("There is no resource under the following context property trackRepo.encode.script");
		if (itunesScript == null || !itunesScript.exists())
			throw new IllegalArgumentException("There is no resource under the following context property trackRepo.itunes.script");
		if (privateKey == null || !privateKey.exists())
			throw new IllegalArgumentException("There is no resource under the following context property trackRepo.encode.privkey");
		if (publishDir == null || !publishDir.exists())
			throw new IllegalArgumentException("There is no folder under the following context property trackRepo.pull.publish");
		if (neroHome == null || !neroHome.exists())
			throw new IllegalArgumentException("There is no folder under the following context  property trackRepo.encode.nero.home");
		if (workDir == null || !workDir.exists())
			throw new IllegalArgumentException("There is no folder under the following context property trackRepo.encode.workdir");
		if (classpath == null || !classpath.exists())
			throw new IllegalArgumentException("There is no folder under the following context property trackRepo.encode.classpath");
	}

	@Override
	public Track encode(Long trackId, Boolean isHighRate, Boolean licensed) {
		LOGGER.debug("input encode(trackId, isHighRate): [{}], [{}]", new Object[] { trackId, isHighRate });

		Track track = trackRepository.findOneWithCollections(trackId);

		if (track == null)
			return null;

		try {
			track.setStatus(TrackStatus.ENCODING);
			trackRepository.save(track);

			licensed = licensed == null ? track.getLicensed() : licensed;

			File encodeScriptFile = encodeScript.getFile();
			ExternalCommandThread thread = new ExternalCommandThread();
			thread.setCommand(encodeScriptFile.getAbsolutePath());
			thread.addParam(isHighRate != null && isHighRate ? "" : emptyNull(track.getFileName(AssetFile.FileType.MOBILE)));
			thread.addParam("");
			thread.addParam(emptyNull(track.getFileName(AssetFile.FileType.DOWNLOAD)));
			thread.addParam(emptyNull(track.getFileName(AssetFile.FileType.IMAGE)));
			thread.addParam(emptyNull(track.getTitle()));
			thread.addParam(emptyNull(track.getArtist()));
			thread.addParam(emptyNull(track.getAlbum()));
			thread.addParam(emptyNull(track.getGenre()));
			thread.addParam("");
			thread.addParam(emptyNull(track.getYear()));
			thread.addParam(emptyNull(track.getCopyright()));
			thread.addParam(emptyNull(track.getIsrc()));
			thread.addParam(publishDir.getFile().getAbsolutePath());
			thread.addParam(classpath.getFile().getAbsolutePath());
			thread.addParam(neroHome.getFile().getAbsolutePath());
			thread.addParam(workDir.getFile().getAbsolutePath());
			thread.addParam(emptyNull(track.getId()));
			thread.addParam(licensed != null && licensed ? "NO" : "YES");
			thread.addParam(privateKey.getFile().getAbsolutePath());
			thread.addParam(isHighRate != null && isHighRate ? AudioResolution.RATE_96.getSuffix() : AudioResolution.RATE_48.getSuffix());
			thread.run();
			if (thread.getExitCode() != 0) {
				throw new RuntimeException("Cannot encode track files or create zip package.");
			}

			track.setItunesUrl(getITunesUrl(track.getArtist(), track.getTitle()));
			track.setStatus(TrackStatus.ENCODED);
			track.setResolution(isHighRate != null && isHighRate ? AudioResolution.RATE_96 : AudioResolution.RATE_48);
			track.setLicensed(licensed);
			trackRepository.save(track);
		} catch (Exception e) {
			track.setStatus(TrackStatus.NONE);
			track.setResolution(AudioResolution.RATE_ORIGINAL);
			trackRepository.save(track);

			LOGGER.error("Cannot encode track files or create zip package.", e);
			throw new RuntimeException(e.getMessage(), e);
		}

		LOGGER.info("output encode(trackId, isHighRate): [{}]", new Object[] { track });
		return track;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Track pull(Long trackId) {
		LOGGER.debug("input pull(trackId): [{}]", new Object[] { trackId });

		Track track = trackRepository.findOneWithCollections(trackId);

		if (track == null || track.getStatus() != TrackStatus.ENCODED)
			return track;

		try {
			
			String isrc = track.getIsrc();
			
			String[] pullFiles = new String[]{
						isrc+"."+FileType.MOBILE_AUDIO.getExt(),
						isrc+"."+FileType.MOBILE_ENCODED.getExt(),
						isrc+ImageResolution.SIZE_21.getSuffix()+"."+FileType.IMAGE.getExt(),
						isrc+ImageResolution.SIZE_22.getSuffix()+"."+FileType.IMAGE.getExt()
					};
			
			for (int i = 0; i < pullFiles.length; i++) {				
				cloudFileService.copyFile(pullFiles[i], destPullContainer, trackId+"_"+pullFiles[i], srcPullContainer);
			}
			
			moveFiles(workDir.getFile(), publishDir.getFile());
			
			track.setPublishDate(new Date());
			trackRepository.save(track);
		} catch (Exception e) {
			LOGGER.error("Cannot pull encoded track.", e);
			throw new RuntimeException("Cannot pull encoded track.");
		}

		LOGGER.info("output pull(trackId): [{}]", new Object[] { track });
		return track;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Track> find(String query, Pageable page) {
		LOGGER.debug("input find(query, page): [{}]", new Object[] { query });

		Page<Track> pagelist = new PageImpl<Track>(Collections.<Track> emptyList(), page, 0L);
		try {
			if (query != null && !query.isEmpty()) {
				pagelist = trackRepository.find("%" + query + "%", page);
			}
		} catch (Exception e) {
			LOGGER.error("Cannot find tracks.", e);
			throw new RuntimeException("Cannot find tracks.");
		}

		LOGGER.info("output find(query, page): [{}]", new Object[] { pagelist });
		return pagelist;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Track> find(SearchTrackCriteria searchTrackCriteria, Pageable page) {
		LOGGER.debug("input find(searchTrackDto, page): [{}]", new Object[] { searchTrackCriteria });

		Page<Track> pagelist = new PageImpl<Track>(Collections.<Track> emptyList(), page, 0L);
		try {
			if (searchTrackCriteria != null) {
				pagelist = trackRepository.find(searchTrackCriteria, page, true, true);
			}
		} catch (Exception e) {
			LOGGER.error("Cannot find tracks.", e);
			throw new RuntimeException("Cannot find tracks.");
		}

		LOGGER.info("output find(searchTrackDto, page): [{}]", new Object[] { pagelist });
		return pagelist;
	}

	private String emptyNull(Object str) {
		if (str == null) {
			return "";
		}
		return str.toString();
	}
	
	private void moveFiles(File srcDir, File destDir) throws IOException{
		String destPath = destDir.getAbsolutePath();
		Collection<File> moveDirs = FileUtils.listFilesAndDirs(srcDir, new NotFileFilter(TrueFileFilter.INSTANCE), DirectoryFileFilter.DIRECTORY);
		Iterator<File> i = moveDirs.iterator();
		i.next();
		i.remove();
		i.next();
		i.remove();
		
		for (File dir : moveDirs) {
			String dirName = dir.getName();
			Collection<File> moveFiles = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, null);
			for (File file : moveFiles) {
				File newDestSubDir = new File(destPath+File.separator+dirName);
				
				File destFile = new File(newDestSubDir, file.getName());
				FileUtils.deleteQuietly(destFile);
				
				FileUtils.moveFileToDirectory(file, newDestSubDir, true);
			}
		}
		
	}

	private String getITunesUrl(String artist, String title) throws IOException, InterruptedException {
		File itunesScriptFile = itunesScript.getFile();
		ExternalCommandThread thread = new ExternalCommandThread();
		thread.setCommand(itunesScriptFile.getAbsolutePath());
		thread.addParam(artist);
		thread.addParam(title);
		thread.run();
		if (thread.getExitCode() == 0) {
			return thread.getOutBuffer();
		}

		return null;
	}

	public void setTrackRepository(TrackRepository trackRepository) {
		this.trackRepository = trackRepository;
	}

	public void setEncodeScript(Resource encodeScript) {
		this.encodeScript = encodeScript;
	}

	public void setItunesScript(Resource itunesScript) {
		this.itunesScript = itunesScript;
	}

	public void setPublishDir(Resource publishDir) {
		this.publishDir = publishDir;
	}

	public void setNeroHome(Resource neroHome) {
		this.neroHome = neroHome;
	}

	public void setWorkDir(Resource workDir) {
		this.workDir = workDir;
	}

	public void setClasspath(Resource classpath) {
		this.classpath = classpath;
	}

	public void setPrivateKey(Resource privateKey) {
		this.privateKey = privateKey;
	}

	public void setSrcPullContainer(String srcPullContainer) {
		this.srcPullContainer = srcPullContainer;
	}

	public void setDestPullContainer(String destPullContainer) {
		this.destPullContainer = destPullContainer;
	}
}