package mobi.nowtechnologies.server.trackrepo.service.impl;


import com.brightcove.proserve.mediaapi.wrapper.ReadApi;
import com.brightcove.proserve.mediaapi.wrapper.WriteApi;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.Video;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.*;
import com.brightcove.proserve.mediaapi.wrapper.exceptions.BrightcoveException;
import com.brightcove.proserve.mediaapi.wrapper.exceptions.MediaApiException;
import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.service.CloudFileService;
import mobi.nowtechnologies.server.trackrepo.SearchTrackCriteria;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.domain.Territory;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.enums.AudioResolution;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import mobi.nowtechnologies.server.trackrepo.enums.ImageResolution;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepository;
import mobi.nowtechnologies.server.trackrepo.service.TrackService;
import mobi.nowtechnologies.server.trackrepo.utils.BitRate;
import mobi.nowtechnologies.server.trackrepo.utils.EncodeManager;
import mobi.nowtechnologies.server.trackrepo.utils.ExternalCommand;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;

// @author Alexander Kolpakov (akolpakov)
public class TrackServiceImpl implements TrackService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(TrackServiceImpl.class);
    private static final java.util.logging.Logger BRIGHTCOVE_LOGGER = java.util.logging.Logger.getLogger("BrightcoveLog");

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

    private String brightcoveWriteToken;
    private String brightcoveReadToken;
    private Boolean brightcoveGeoFiltering;
    private String sevenDigitalApiUrl;
    private String sevenDigitalApiKey;

    protected TrackRepository trackRepository;

    private WriteApi brightcoveWriteService;
    private ReadApi brightcoveReadService;
    private RestTemplate restTemplate;

    private EncodeManager encodeManager;

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

        brightcoveWriteService = new WriteApi(BRIGHTCOVE_LOGGER);
        brightcoveReadService = new ReadApi(BRIGHTCOVE_LOGGER);
    }

    @Override
    public Track encode(Long trackId, Boolean isHighRate, Boolean licensed) {
        LOGGER.info("encode(trackId:{}, isHighRate:{})", trackId, isHighRate);

        Track track = trackRepository.findOneWithCollections(trackId);

        LOGGER.debug("Found track : {}", track);
        if (track == null)
            return null;

        try {
            track.setStatus(TrackStatus.ENCODING);
            track.setPublishDate(null);
            trackRepository.save(track);

            licensed = licensed == null ? Boolean.FALSE : licensed;

            encodeManager.encode(track, isHighRate, licensed);

            track.setItunesUrl(getITunesUrl(track.getArtist(), track.getTitle()));
            track.setAmazonUrl(getAmazonUrl(track.getIsrc()));
            track.setStatus(TrackStatus.ENCODED);
            track.setResolution(isHighRate != null && isHighRate ? AudioResolution.RATE_96 : AudioResolution.RATE_48);
            track.setLicensed(licensed);
            trackRepository.save(track);

            LOGGER.info("Track {} is encoded", trackId);
            return track;
        } catch (Exception e) {
            LOGGER.error("Cannot encode track {} files or create zip package: {}", trackId, e.getMessage(), e);

            track.setStatus(TrackStatus.NONE);
            track.setResolution(AudioResolution.RATE_ORIGINAL);
            trackRepository.save(track);

            throw new RuntimeException("Cannot encode track files or create zip package: " + e.getMessage(), e);
        }
    }

    @Override
    public Track pull(Long trackId) {
        LOGGER.debug("input pull(trackId): [{}]", new Object[]{trackId});

        Track track = trackRepository.findOneWithCollections(trackId);

        LOGGER.debug("Found track in db: {}", track);

        if (track == null || (track.getStatus() != TrackStatus.ENCODED && track.getStatus() != TrackStatus.PUBLISHED))
            return track;

        TrackStatus oldStatus = track.getStatus();

        track.setStatus(TrackStatus.PUBLISHING);
        trackRepository.save(track);

        try {
            pull(track);

            track.setStatus(TrackStatus.PUBLISHED);
        } catch (Exception e) {
            LOGGER.error("Exception while pulling track {} : {}", track.getUniqueTrackId(), e.getMessage(), e);
            track.setStatus(oldStatus);
        }

        trackRepository.save(track);

        LOGGER.info("output pull track: {}", track);
        return track;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    protected Track pull(Track track) {
        LOGGER.info("start pull process: uniqueTrackId:{}", track.getUniqueTrackId());

        try {

            List<String> pullFiles = buildListOfSourceFiles(track);

            LOGGER.info("files to pull: {}", pullFiles);

            for (String fileName : pullFiles) {
                cloudFileService.copyFile(srcPullContainer, track.getId() + "_" + fileName, destPullContainer, fileName);
            }

            //upload video on brightcove if it exists
            createVideo(track);

            moveFiles(workDir.getFile(), publishDir.getFile());

            track.setPublishDate(new Date());
            trackRepository.save(track);
        } catch (Exception e) {
            LOGGER.error("Cannot pull encoded track {} : {}", track.getUniqueTrackId(), e.getMessage(), e);
            throw new RuntimeException("Cannot pull encoded track.");
        }

        LOGGER.info("End of pull process: [{}]", new Object[]{track.getUniqueTrackId()});
        return track;
    }

    private List<String> buildListOfSourceFiles(Track track) {
        String trackId = track.getUniqueTrackId();
        AssetFile audioFile = track.getFile(AssetFile.FileType.DOWNLOAD);

        List<String> result = Lists.newArrayList();
        if (audioFile != null) {
            result.add(trackId + "." + FileType.MOBILE_AUDIO.getExt());
            result.add(trackId + "." + FileType.MOBILE_ENCODED.getExt());
            result.add(trackId + "_" + BitRate.BITRATE48.getValue() + "." + FileType.MOBILE_HEADER.getExt());
            result.add(trackId + "_" + BitRate.BITRATE48.getValue() + "." + FileType.MOBILE_AUDIO.getExt());
            result.add(trackId + "_" + BitRate.BITRATE96.getValue() + "." + FileType.MOBILE_HEADER.getExt());
            result.add(trackId + "_" + BitRate.BITRATE96.getValue() + "." + FileType.MOBILE_AUDIO.getExt());
        }
        result.add(trackId + ImageResolution.SIZE_21.getSuffix() + "." + FileType.IMAGE.getExt());
        result.add(trackId + ImageResolution.SIZE_22.getSuffix() + "." + FileType.IMAGE.getExt());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Track> find(String query, Pageable page) {
        LOGGER.debug("input find(query, page): [{}]", new Object[]{query});

        Page<Track> pagelist = new PageImpl<Track>(Collections.<Track>emptyList(), page, 0L);
        try {
            if (query != null && !query.isEmpty()) {
                pagelist = trackRepository.find("%" + query + "%", page);
            }
        } catch (Exception e) {
            LOGGER.error("Cannot find tracks.", e);
            throw new RuntimeException("Cannot find tracks.");
        }

        LOGGER.debug("output find(query, page): [{}]", new Object[]{pagelist});
        return pagelist;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Track> find(SearchTrackCriteria searchTrackCriteria, Pageable pageable) {
        LOGGER.debug("input find(searchTrackDto, page): [{}]", new Object[]{searchTrackCriteria});

        Page<Track> page = new PageImpl<Track>(Collections.<Track>emptyList(), pageable, 0L);
        try {
            if (searchTrackCriteria != null) {
                page = trackRepository.find(searchTrackCriteria, pageable);
            }
        } catch (Exception e) {
            LOGGER.error("Cannot find tracks.", e);
            throw new RuntimeException("Cannot find tracks.");
        }

        LOGGER.debug("output find(searchTrackDto, page): [{}]", new Object[]{page});
        return page;
    }

    private void moveFiles(File srcDir, File destDir) {
        LOGGER.info("Start moving files from {} to {}", srcDir, destDir);
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
                File newDestSubDir = new File(destPath + File.separator + dirName);

                File destFile = new File(newDestSubDir, file.getName());
                FileUtils.deleteQuietly(destFile);

                try {
                    LOGGER.debug("Trying to move file {} to {}", file, newDestSubDir);
                    FileUtils.moveFileToDirectory(file, newDestSubDir, true);
                } catch (IOException e) {
                    LOGGER.error("Could not move file {} to {} : {}", file, newDestSubDir, e.getMessage(), e);
                }
            }
        }

    }

    protected AssetFile createVideo(Track track) throws BrightcoveException {
        AssetFile videoFile = track.getFile(AssetFile.FileType.VIDEO);

        LOGGER.info("Video file is : {}", videoFile);

        if (videoFile == null)
            return null;

        Video video = new Video();

        LOGGER.info("Setting up video object to write");

        // --------------------- Video Write API Methods ------------------
        Boolean createMultipleRenditions = true;
        Boolean preserveSourceRendition = true;
        Boolean h264NoProcessing = false;

        // ---- Required fields ----
        video.setName(track.getTitle());
        video.setShortDescription(track.getArtist() + (track.getAlbum() != null ? "/" + track.getAlbum() : ""));

        // ---- Optional fields ----
        video.setAccountId(null);
        video.setEconomics(EconomicsEnum.FREE);
        video.setItemState(ItemStateEnum.ACTIVE);
        video.setLinkText("Brightcove");
        video.setLinkUrl("http://www.brightcove.com");
        video.setLongDescription(track.getInfo());
        video.setReferenceId(track.getUniqueTrackId());
        video.setStartDate(track.getIngestionDate());

        // ---- Complex (and optional) fields ----
        // End date must be in the future - add 30 minutes to "now"
        Date endDate = null;
        video.setEndDate(endDate);

        // Geo-filtering must be combined with filtered countries
        if (brightcoveGeoFiltering) {
            video.setGeoFiltered(true);
            List<GeoFilterCodeEnum> geoFilteredCountries = new ArrayList<GeoFilterCodeEnum>();
            for (Territory territory : track.getTerritories()) {
                GeoFilterCodeEnum code = Territory.WWW_TERRITORY.equalsIgnoreCase(territory.getCode()) ? GeoFilterCodeEnum.GB : GeoFilterCodeEnum.valueOf(territory.getCode());
                geoFilteredCountries.add(code);
            }
            video.setGeoFilteredCountries(geoFilteredCountries);
            video.setGeoFilteredExclude(true);
        }

        // Tags must be added as a list of strings
        List<String> tags = new ArrayList<String>();
        tags.add(track.getIngestor());
        video.setTags(tags);


        Long videoId = null;
        try {
            videoId = brightcoveWriteService.CreateVideo(brightcoveWriteToken, video, videoFile.getPath(), TranscodeEncodeToEnum.MP4, createMultipleRenditions, preserveSourceRendition, h264NoProcessing);
            LOGGER.info("Create video with referenceId=[{}] and id=[{}]", new Object[]{video.getReferenceId(), videoId});
        } catch (MediaApiException e) {
            String error = e.getResponseMessage();
            if (error.equals("Reference ID " + video.getReferenceId() + " is already in use")) {
                brightcoveWriteService.DeleteVideo(brightcoveWriteToken, video.getId(), video.getReferenceId(), true, true);
                LOGGER.info("This video is not new. Video with referenceId=[{}] has been deleted", video.getReferenceId());
                videoId = brightcoveWriteService.CreateVideo(brightcoveWriteToken, video, videoFile.getPath(), TranscodeEncodeToEnum.MP4, createMultipleRenditions, preserveSourceRendition, h264NoProcessing);
                LOGGER.info("This video is new. Video with referenceId=[{}] doesn't exist", video.getReferenceId());
            } else {
                throw e;
            }
        }


        videoFile.setExternalId(videoId.toString());
        if (videoFile.getDuration() == null) {
            UploadStatusEnum status = brightcoveWriteService.GetUploadStatus(brightcoveWriteToken, videoId, video.getReferenceId());
            while (status != UploadStatusEnum.COMPLETE) {
                status = brightcoveWriteService.GetUploadStatus(brightcoveWriteToken, videoId, video.getReferenceId());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

            video = brightcoveReadService.FindVideoById(brightcoveReadToken, videoId, EnumSet.of(VideoFieldEnum.LENGTH), Collections.<String>emptySet());
            videoFile.setDuration(video.getLength().intValue());
        }

        return videoFile;
    }

    private String getITunesUrl(String artist, String title) throws IOException, InterruptedException {
        ExternalCommand command = new ExternalCommand();
        command.setCommand(itunesScript);
        try {
            return command.executeCommand(artist, title);
        } catch (Exception e) {
            LOGGER.error("ERROR", e);
            return null;
        }
    }

    String getAmazonUrl(String isrc) {

        try {

            DOMSource response = restTemplate.getForObject(sevenDigitalApiUrl, DOMSource.class, isrc, sevenDigitalApiKey);

            XPath xPath = XPathFactory.newInstance().newXPath();
            String releaseId = xPath.evaluate("/response/searchResults/searchResult[1]/track/release/@id", response.getNode().getFirstChild());
            String trackId = xPath.evaluate("/response/searchResults/searchResult[1]/track/@id", response.getNode().getFirstChild());

            if (StringUtils.isBlank(releaseId)) {
                return null;
            }

            if (StringUtils.isBlank(trackId)) {
                trackId = "";
            } else {
                trackId = "#t" + trackId;
            }

            return "https://m.7digital.com/GB/releases/" + releaseId + trackId + "?partner=3734";

        } catch (Exception e) {

            LOGGER.error("GET_AMAZON_URL error_msg[{}] for isrc=" + isrc, e);
            return null;
        }
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

    public void setBrightcoveWriteToken(String brightcoveWriteToken) {
        this.brightcoveWriteToken = brightcoveWriteToken;
    }

    public void setBrightcoveGeoFiltering(Boolean brightcoveGeoFiltering) {
        this.brightcoveGeoFiltering = brightcoveGeoFiltering;
    }

    public void setBrightcoveReadToken(String brightcoveReadToken) {
        this.brightcoveReadToken = brightcoveReadToken;
    }

    public void setSevenDigitalApiUrl(String sevenDigitalApiUrl) {
        this.sevenDigitalApiUrl = sevenDigitalApiUrl;
    }

    public void setSevenDigitalApiKey(String sevenDigitalApiKey) {
        this.sevenDigitalApiKey = sevenDigitalApiKey;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setEncodeManager(EncodeManager encodeManager) {
        this.encodeManager = encodeManager;
    }
}