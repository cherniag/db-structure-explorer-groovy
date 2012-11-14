package mobi.nowtechnologies.server.track_repo.service.impl;

import mobi.nowtechnologies.java.server.uits.MP3Manager;
import mobi.nowtechnologies.java.server.uits.MP4Manager;
import mobi.nowtechnologies.server.shared.dto.*;
import mobi.nowtechnologies.server.shared.dto.admin.SearchTrackDto;
import mobi.nowtechnologies.server.track_repo.assembler.TrackAsm;
import mobi.nowtechnologies.server.track_repo.domain.AssetFile;
import mobi.nowtechnologies.server.track_repo.domain.Territory;
import mobi.nowtechnologies.server.track_repo.domain.Track;
import mobi.nowtechnologies.server.track_repo.repository.TrackRepository;
import mobi.nowtechnologies.server.track_repo.service.TrackService;
import mobi.nowtechnologies.server.track_repo.utils.ExternalCommandThread;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public class TrackServiceImpl implements TrackService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TrackServiceImpl.class);

	private Resource encodeScript;
	private Resource itunesScript;
	private Resource encodeDestination;
	private Resource neroHome;
	private Resource workDir;
	private Resource classpath;
	private Resource privateKey;

	private TrackRepository trackRepository;

	public void init() throws Exception {
        Validate.notNull(encodeScript, "There is no resource under the following context property trackRepo.encode.script");
        Validate.isTrue(encodeScript.exists(), "There is no resource under the following context property trackRepo.encode.script");

		if (itunesScript == null || !itunesScript.exists())
			throw new IllegalArgumentException("There is no resource under the following context property trackRepo.itunes.script");
		if (privateKey == null || !privateKey.exists())
			throw new IllegalArgumentException("There is no resource under the following context property trackRepo.encode.privkey");
		if (encodeDestination == null || !encodeDestination.exists())
			throw new IllegalArgumentException("There is no folder under the following context property trackRepo.encode.destination");
		if (neroHome == null || !neroHome.exists())
			throw new IllegalArgumentException("There is no folder under the following context  property trackRepo.encode.nero.home");
		if (workDir == null || !workDir.exists())
			throw new IllegalArgumentException("There is no folder under the following context property trackRepo.encode.workdir");
		if (classpath == null || !classpath.exists())
			throw new IllegalArgumentException("There is no folder under the following context property trackRepo.encode.classpath");
	}

	@Override
	public TrackDto encode(Long trackId, Boolean isHighRate, Boolean licensed) {
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
			thread.addParam(encodeDestination.getFile().getAbsolutePath());
			thread.addParam(classpath.getFile().getAbsolutePath());
			thread.addParam(neroHome.getFile().getAbsolutePath());
			thread.addParam(workDir.getFile().getAbsolutePath());
			thread.addParam(isHighRate != null && isHighRate ? AudioResolution.RATE_96.getSuffix() : AudioResolution.RATE_48.getSuffix());
			thread.addParam(licensed != null && licensed ? "NO" : "YES");
			thread.addParam(privateKey.getFile().getAbsolutePath());
			thread.run();
			if (thread.getExitCode() != 0) {
				throw new RuntimeException("Cannot encode track files or create zip package.");
			}

			track.setItunesUrl(getITunesUrl(track.getArtist(), track.getTitle()));
			track.setStatus(TrackStatus.ENCODED);
			track.setResolution(isHighRate != null && isHighRate ? AudioResolution.RATE_96 : AudioResolution.RATE_48);
			track.setLicensed(licensed);
		} catch (Exception e) {
			track.setStatus(TrackStatus.NONE);
			track.setResolution(AudioResolution.RATE_ORIGINAL);

			LOGGER.error("Cannot encode track files or create zip package.", e);
			throw new RuntimeException(e.getMessage(), e);
		} finally {
            trackRepository.save(track);
        }

		TrackDto trackDto = TrackAsm.toTrackDto(track);
		LOGGER.info("output encode(trackId, isHighRate): [{}]", new Object[] { trackDto });
		return trackDto;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public TrackDto pull(Long trackId) {
		LOGGER.debug("input pull(trackId): [{}]", new Object[] { trackId });

		Track track = trackRepository.findOne(trackId);

		if (track == null)
			return null;

		TrackDto trackDto = TrackAsm.toTrackDto(track);

		TrackStatus currentStatus = trackDto.getStatus();
		if (currentStatus != TrackStatus.ENCODED)
			return trackDto;

		try {
			String isrc = track.getIsrc();
			String workDirPath = workDir.getFile().getAbsolutePath();
			String distDirPath = encodeDestination.getFile().getAbsolutePath();

			String mp3hash = getMediaHash(getFilePath(FileType.ORIGINAL_MP3, AudioResolution.RATE_ORIGINAL, workDirPath, isrc));
			String aac48hash = getMediaHash(getFilePath(FileType.ORIGINAL_ACC, AudioResolution.RATE_48, workDirPath, isrc));
			String aac96hash = getMediaHash(getFilePath(FileType.ORIGINAL_ACC, AudioResolution.RATE_96, workDirPath, isrc));

			List<ResourceFileDto> files = new ArrayList<ResourceFileDto>(15);
			files.add(createResourceFile(FileType.MOBILE_HEADER, AudioResolution.RATE_48, distDirPath, isrc, null));
			files.add(createResourceFile(FileType.MOBILE_HEADER, AudioResolution.RATE_96, distDirPath, isrc, null));
			files.add(createResourceFile(FileType.MOBILE_HEADER, AudioResolution.RATE_PREVIEW, distDirPath, isrc, null));
			files.add(createResourceFile(FileType.MOBILE_AUDIO, AudioResolution.RATE_48, distDirPath, isrc, aac48hash));
			files.add(createResourceFile(FileType.MOBILE_AUDIO, AudioResolution.RATE_96, distDirPath, isrc, aac96hash));
			files.add(createResourceFile(FileType.MOBILE_AUDIO, AudioResolution.RATE_PREVIEW, distDirPath, isrc, null));
			files.add(createResourceFile(FileType.DOWNLOAD, AudioResolution.RATE_ORIGINAL, distDirPath, isrc, mp3hash));
			files.add(createResourceFile(FileType.IMAGE, ImageResolution.SIZE_ORIGINAL, distDirPath, isrc, null));
			files.add(createResourceFile(FileType.IMAGE, ImageResolution.SIZE_LARGE, distDirPath, isrc, null));
			files.add(createResourceFile(FileType.IMAGE, ImageResolution.SIZE_SMALL, distDirPath, isrc, null));
			files.add(createResourceFile(FileType.IMAGE, ImageResolution.SIZE_22, distDirPath, isrc, null));
			files.add(createResourceFile(FileType.IMAGE, ImageResolution.SIZE_21, distDirPath, isrc, null));
			files.add(createResourceFile(FileType.IMAGE, ImageResolution.SIZE_11, distDirPath, isrc, null));
			files.add(createResourceFile(FileType.IMAGE, ImageResolution.SIZE_6, distDirPath, isrc, null));
			files.add(createResourceFile(FileType.IMAGE, ImageResolution.SIZE_3, distDirPath, isrc, null));
			trackDto.setFiles(files);

			Territory publishTerritory = getValidTerritory(track, Territory.GB_TERRITORY);
			if (publishTerritory != null) {
				trackDto.setPublishDate(publishTerritory.getStartDate());
			}
			
			track.setPublishDate(new Date());
			trackRepository.save(track);
		} catch (Exception e) {
			LOGGER.error("Cannot pull encoded track.", e);
			throw new RuntimeException("Cannot pull encoded track.");
		}

		LOGGER.info("output pull(trackId): [{}]", new Object[] { trackDto });
		return trackDto;
	}

	@Override
	@Transactional(readOnly = true)
	public PageListDto<TrackDto> find(String query, Pageable page) {
		LOGGER.debug("input find(query, page): [{}]", new Object[] { query });

		PageListDto<TrackDto> pagelist = new PageListDto<TrackDto>(Collections.<TrackDto> emptyList(), 0, page.getPageNumber() + 1, page.getPageSize());
		try {
			if (query != null && !query.isEmpty()) {
				List<Track> tracks = trackRepository.find("%" + query + "%", page);
				int total = (int) trackRepository.count(query);
				total = total % page.getPageSize() == 0 ? total / page.getPageSize() : total / page.getPageSize() + 1;
				pagelist = new PageListDto<TrackDto>(TrackAsm.toTrackDtos(tracks), total, page.getPageNumber() + 1, page.getPageSize());
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
	public PageListDto<TrackDto> find(SearchTrackDto searchTrackDto, Pageable page) {
		LOGGER.debug("input find(searchTrackDto, page): [{}]", new Object[] { searchTrackDto });

		PageListDto<TrackDto> pagelist = new PageListDto<TrackDto>(Collections.<TrackDto> emptyList(), 0, page.getPageNumber() + 1, page.getPageSize());
		try {
			if (searchTrackDto != null) {
				List<Track> tracks = trackRepository.find(searchTrackDto, page);
				int total = (int) trackRepository.count(searchTrackDto);
				total = total % page.getPageSize() == 0 ? total / page.getPageSize() : total / page.getPageSize() + 1;
				pagelist = new PageListDto<TrackDto>(TrackAsm.toTrackDtos(tracks), total, page.getPageNumber() + 1, page.getPageSize());
			}
		} catch (Exception e) {
			LOGGER.error("Cannot find tracks.", e);
			throw new RuntimeException("Cannot find tracks.");
		}

		LOGGER.info("output find(searchTrackDto, page): [{}]", new Object[] { pagelist });
		return pagelist;
	}

	private ResourceFileDto createResourceFile(FileType type, Resolution resolution, String dir, String isrc, String mediaHash) throws IOException {
		ResourceFileDto resourceFileDto = new ResourceFileDto(type, resolution, isrc, mediaHash);

		Integer fileSize = getFileSize(getFilePath(type, resolution, dir, isrc));
		resourceFileDto.setSize(fileSize);

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

	private String emptyNull(String str) {
		if (str == null) {
			return "";
		}
		return str;
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

	private String getMediaHash(String fileName) {
		InputStream in = null;
		try {
			String mediaHash = null;
			if (fileName.toLowerCase().endsWith("." + FileType.DOWNLOAD.getExt())) {
				MP3Manager mp3Manager = new MP3Manager();
				mediaHash = mp3Manager.mp3GetMediaHash(fileName);
			} else { // Assume AAC.....
				in = new FileInputStream(fileName);
				MP4Manager mp4manager = new MP4Manager();
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

	protected Territory getValidTerritory(Track track, String country) {
		Set<Territory> territories = track.getTerritories();
		if (territories != null && territories.size() > 0) {
			Territory countryTerritory = null;
			Territory worldwide = null;

			Iterator<Territory> it = territories.iterator();
			while (it.hasNext()) {
				Territory territory = it.next();
				if (territory.isDeleted())
					continue;
				if (country.equalsIgnoreCase(territory.getCode())) {
					countryTerritory = territory;
				}
				if (Territory.WWW_TERRITORY.equalsIgnoreCase(territory.getCode())) {
					worldwide = territory;
				}
			}
			if (countryTerritory != null) {
				return countryTerritory;
			}
			if (worldwide != null) {
				return worldwide;
			}
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

	public void setEncodeDestination(Resource encodeDestination) {
		this.encodeDestination = encodeDestination;
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
}