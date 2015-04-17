package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.Artist;
import mobi.nowtechnologies.server.persistence.domain.Genre;
import mobi.nowtechnologies.server.persistence.domain.Label;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaFile;
import mobi.nowtechnologies.server.persistence.repository.ArtistRepository;
import mobi.nowtechnologies.server.persistence.repository.GenreRepository;
import mobi.nowtechnologies.server.persistence.repository.LabelRepository;
import mobi.nowtechnologies.server.persistence.repository.MediaFileRepository;
import mobi.nowtechnologies.server.persistence.repository.MediaRepository;
import mobi.nowtechnologies.server.service.TrackRepoService;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.TrackRepositoryClient;
import mobi.nowtechnologies.server.trackrepo.dto.IngestWizardDataDto;
import mobi.nowtechnologies.server.trackrepo.dto.ResourceFileDto;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackReportingOptionsDto;
import mobi.nowtechnologies.server.trackrepo.enums.AudioResolution;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import mobi.nowtechnologies.server.trackrepo.enums.ImageResolution;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

// @author Alexander Kolpakov (akolpakov)
public class TrackRepoServiceImpl implements TrackRepoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackRepoServiceImpl.class);

    private Set<Long> pullingTrackSet = Collections.synchronizedSet(new HashSet<Long>());

    private TrackRepositoryClient client;
    private MediaRepository mediaRepository;
    private MediaFileRepository mediaFileRepository;
    private ArtistRepository artistRepository;
    private GenreRepository genreRepository;
    private long iTunesLinkFormatCutoverTimeMillis;

    private LabelRepository labelRepository;

    public void setLabelRepository(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    public void setiTunesLinkFormatCutoverTimeMillis(long iTunesLinkFormatCutoverTimeMillis) {
        this.iTunesLinkFormatCutoverTimeMillis = iTunesLinkFormatCutoverTimeMillis;
    }

    public void setClient(TrackRepositoryClient client) {
        this.client = client;
    }

    public void setMediaRepository(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public void setMediaFileRepository(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    public void setArtistRepository(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public void setGenreRepository(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public IngestWizardDataDto getDrops(String... ingestors) {
        LOGGER.debug("input getDrops({})", Arrays.toString(ingestors));

        IngestWizardDataDto data = client.getDrops(ingestors);

        LOGGER.debug("output getDrops(): [{}]", data);
        return data;
    }

    @Override
    public IngestWizardDataDto selectDrops(IngestWizardDataDto input) {
        LOGGER.debug("input selectDrops(): [{}]", new Object[] {input});

        IngestWizardDataDto data = client.selectDrops(input);

        LOGGER.debug("output selectDrops(): [{}]", data);
        return data;
    }

    @Override
    public IngestWizardDataDto selectTrackDrops(IngestWizardDataDto input) {
        LOGGER.debug("input selectTrackDrops(): [{}]", new Object[] {input});

        IngestWizardDataDto data = client.selectTrackDrops(input);

        LOGGER.debug("output selectTrackDrops(): [{}]", data);
        return data;
    }

    @Override
    public Boolean commitDrops(IngestWizardDataDto data) {
        LOGGER.debug("input commitDrops(): [{}]", new Object[] {data});

        Boolean result = client.commitDrops(data);

        LOGGER.debug("output commitDrops(): [{}]", result);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public PageListDto<TrackDto> find(String criteria, Pageable page) {
        LOGGER.debug("input find(criteria, page): [{}]", new Object[] {criteria, page});

        PageListDto<TrackDto> tracks = client.search(criteria, page);

        fillTracks(tracks);

        LOGGER.info("output find(criteria): [{}]", tracks);
        return tracks;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public TrackDto pull(TrackDto config) {
        LOGGER.debug("input pull(track): [{}]", config);

        TrackDto track = null;
        try {
            if (config == null || config.getId() == null) {
                throw new IllegalArgumentException("Given track is illegal");
            }

            Long id = config.getId();

            pullingTrackSet.add(id);

            track = client.pullTrack(id);

            if (track == null) {
                throw new ServiceException("Pulled track is null");
            }

            if (track.getPublishDate() == null) {
                throw new ServiceException("Pulled track is not published (publish date is null)");
            }

            Media media = createOrUpdateMedia(track, config);

            track.setPublishDate(new Date(media.getPublishDate() * 1000L));
            track.setPublishArtist(media.getArtistName());
            track.setTitle(media.getTitle());
            track.setPublishTitle(track.getTitle());
            track.setInfo(media.getInfo());
            track.setItunesUrl(config.getItunesUrl());
            track.setStatus(TrackStatus.PUBLISHED);
            track.setAmazonUrl(config.getAmazonUrl());
            track.setAreArtistUrls(config.getAreArtistUrls());

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceException("tracks.pull.error", "Couldn't pull track");
        } finally {
            if (config != null && config.getId() != null) {
                pullingTrackSet.remove(config.getId());
            }
        }

        LOGGER.info("output pull(track): [{}]", track);

        return track;
    }

    protected Media createOrUpdateMedia(TrackDto track, TrackDto config) {
        Media media = mediaRepository.findByTrackId(config.getId());

        if (media == null) {
            media = new Media();
            media.setIsrc(config.getIsrc());
            media.setTrackId(config.getId());
        }

        media.setLabel(getLabel(track));
        media.setTitle(config.getTitle());

        // Building genre
        Genre genre = createOrUpdateGenre(track);
        media.setGenre(genre);

        // Building artist
        Artist artist = createOrUpdateArtist(track, config);
        media.setArtist(artist);
        media.setInfo(artist.getInfo());

        // Building iTunesUrl
        media.setiTunesUrl(getiTunesURL(config));
        media.setAmazonUrl(config.getAmazonUrl());
        media.setAreArtistUrls(config.getAreArtistUrls());

        // Building media files
        ResourceFileDto audioFileDto = track.getFile(FileType.DOWNLOAD, AudioResolution.RATE_ORIGINAL);
        if (audioFileDto != null) {
            media.setPurchasedFile(createMediaFile(audioFileDto));

            media.setAudioPreviewFile(createMediaFile(track.getFile(FileType.MOBILE_AUDIO, AudioResolution.RATE_PREVIEW)));
            media.setHeaderPreviewFile(createMediaFile(track.getFile(FileType.MOBILE_HEADER, AudioResolution.RATE_PREVIEW)));

            if (track.getLicensed() == null || !track.getLicensed()) {
                media.setAudioFile(media.getAudioPreviewFile());
                media.setHeaderFile(media.getHeaderPreviewFile());
            } else {
                media.setAudioFile(createMediaFile(track.getFile(FileType.MOBILE_AUDIO, track.getResolution())));
                media.setHeaderFile(createMediaFile(track.getFile(FileType.MOBILE_HEADER, track.getResolution())));
            }
        }

        ResourceFileDto videoFileDto = track.getFile(FileType.VIDEO, AudioResolution.RATE_ORIGINAL);
        if (videoFileDto != null) {
            media.setAudioFile(createMediaFile(videoFileDto));
        }

        media.setImageFIleLarge(createMediaFile(track.getFile(FileType.IMAGE, ImageResolution.SIZE_22)));
        media.setImageFileSmall(createMediaFile(track.getFile(FileType.IMAGE, ImageResolution.SIZE_21)));
        media.setImgFileResolution(createMediaFile(track.getFile(FileType.IMAGE, ImageResolution.SIZE_ORIGINAL)));

        Date publishDate = track.getPublishDate() != null ?
                           track.getPublishDate() :
                           new Date();
        media.setPublishDate((int) (publishDate.getTime() / 1000));

        mediaRepository.save(media);

        LOGGER.debug("Output media is :{}", media);

        return media;
    }

    private Label getLabel(TrackDto trackDto) {
        String label = trackDto.getLabel();
        if (!StringUtils.isEmpty(label)) {
            Label result = labelRepository.findByName(label);
            return result != null ?
                   result :
                   createAndSaveLabel(label);
        }
        return null;
    }

    private Label createAndSaveLabel(String label) {
        Label result = new Label();
        result.setName(label);
        labelRepository.save(result);
        return result;
    }

    private String getiTunesURL(TrackDto sourceTrackDto) {
        if (System.currentTimeMillis() >= iTunesLinkFormatCutoverTimeMillis) {
            return sourceTrackDto.getItunesUrl();
        } else {
            return "http://clkuk.tradedoubler.com/click?p=23708%26a=1997010%26url=" + (sourceTrackDto.getItunesUrl() != null ?
                                                                                       sourceTrackDto.getItunesUrl().replace("&", "%26") :
                                                                                       "") + "%26partnerId=2003";
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private Artist createOrUpdateArtist(TrackDto track, TrackDto config) {
        Artist artist = artistRepository.findByName(config.getArtist());
        if (artist == null) {
            artist = new Artist();
        }
        artist.setName(config.getArtist());
        artist.setRealName(track.getArtist());
        artist.setInfo(config.getInfo());
        artistRepository.save(artist);

        return artist;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private Genre createOrUpdateGenre(TrackDto track) {
        if (track.getGenre() == null || "".equals(track.getGenre())) {
            track.setGenre("Default");
        }
        String genreName = track.getGenre();
        genreName = genreName.replaceAll("&", "");
        Genre genre = (Genre) genreRepository.findByName(genreName);
        if (genre == null) {
            genre = new Genre();
            genre.setName(genreName);
            genreRepository.save(genre);
        }
        return genre;
    }

    @Override
    public TrackDto encode(TrackDto config) {
        LOGGER.debug("input encode(track): [{}]", config);

        TrackDto track = null;
        try {
            if (config == null || config.getId() == null) {
                throw new IllegalArgumentException("Given track is illegal");
            }

            Media media = mediaRepository.findByTrackId(config.getId());
            if (media != null) {
                media.setPublishDate(0);
                mediaRepository.save(media);
            }

            track = client.encodeTrack(config.getId(), config.getResolution() == AudioResolution.RATE_96 ?
                                                       true :
                                                       false, config.getLicensed());
            track.setPublishArtist(track.getArtist());
            track.setPublishTitle(track.getTitle());
            track.setInfo(getArtistInfo(track.getArtist()));
        } catch (Exception e) {
            LOGGER.error("Exception encoding track {}: {}", config, e.getMessage(), e);
            throw new ExternalServiceException("tracks.encode.error", "Couldn't encode track");
        }

        LOGGER.info("output encode(id): [{}]", track);
        return track;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public MediaFile createMediaFile(ResourceFileDto fileDto) {
        LOGGER.info("createMediaFile fileDto is {}", fileDto);
        if (fileDto == null) {
            return null;
        }

        String fullFileName = fileDto.getFullFilename();
        MediaFile file = mediaFileRepository.findByName(fullFileName);

        if (file == null) {
            file = new MediaFile();
            file.setFilename(fullFileName);
            mobi.nowtechnologies.server.persistence.domain.FileType fileType = new mobi.nowtechnologies.server.persistence.domain.FileType();
            fileType.setI(FileType.valueOf(fileDto.getType()).getId().byteValue());
            file.setFileType(fileType);

            file.setDuration(fileDto.getDuration() != null ?
                             fileDto.getDuration() :
                             0);
        }

        file.setSize(fileDto.getSize());
        LOGGER.debug("save MediaFile {}", file);
        mediaFileRepository.save(file);

        return file;
    }

    @Override
    public PageListDto<TrackDto> find(SearchTrackDto criteria, Pageable page) {
        LOGGER.debug("input find(criteria, page): [{}]", new Object[] {criteria, page});

        PageListDto<TrackDto> tracks = client.search(criteria, page);

        fillTracks(tracks);

        LOGGER.info("output find(tracks): [{}]", tracks);
        return tracks;
    }

    @Transactional(readOnly = true)
    protected String getArtistInfo(String artistName) {
        Pageable one = new PageRequest(0, 1);
        List<Artist> artists = artistRepository.findByNames(artistName, one);
        if (artists == null || artists.size() == 0) {
            return "";
        } else {
            Artist artist = artists.get(0);
            return artist.getInfo() != null ?
                   artist.getInfo() :
                   "";
        }
    }

    @Transactional(readOnly = true)
    protected void fillTracks(PageListDto<TrackDto> tracks) {
        Map<String, TrackDto> map = new HashMap<String, TrackDto>();
        for (TrackDto track : tracks.getList()) {

            track.setInfo(getArtistInfo(track.getArtist()));
            track.setPublishArtist(track.getArtist());
            track.setPublishTitle(track.getTitle());

            if (pullingTrackSet.contains(track.getId())) {
                track.setStatus(TrackStatus.PUBLISHING);
            } else if (track.getStatus() == TrackStatus.ENCODED || track.getStatus() == TrackStatus.PUBLISHED) {
                map.put(track.getIsrc(), track);
            }

        }

        if (map.size() > 0) {
            List<Media> medias = mediaRepository.findByIsrcs(map.keySet());

            for (Media media : medias) {
                TrackDto track = map.get(media.getIsrc());
                if (media.getPublishDate() > 0) {
                    track.setStatus(TrackStatus.PUBLISHED);
                    track.setPublishDate(new Date(media.getPublishDate() * 1000L));
                    track.setPublishTitle(media.getTitle());
                    track.setInfo(media.getArtist().getInfo());
                    track.setPublishArtist(media.getArtistName());
                    track.setAreArtistUrls(media.getAreArtistUrls());
                    final String amazonUrl = media.getAmazonUrl();
                    if (amazonUrl != null && !amazonUrl.isEmpty()) {
                        track.setAmazonUrl(amazonUrl);
                    }
                    track.setItunesUrl(media.getiTunesUrl());
                    if (media.getiTunesUrl() != null && !"".equals(media.getiTunesUrl())) {
                        try {
                            Matcher m = Pattern.compile("url=.*\\%26").matcher(media.getiTunesUrl());
                            if (m.find()) {
                                String url = m.group();
                                if (url != null && !"".equals(url)) {
                                    url = url.substring(4, url.length() - 3).replace("%26", "&");
                                    track.setItunesUrl(url);
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.warn("Can't get iTunes URL from media for original value: " + media.getiTunesUrl(), e);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Map<String, List<TrackDto>> encodeTracks(List<TrackDto> tracks) {
        Map<String, List<TrackDto>> result = new HashMap<String, List<TrackDto>>();

        List<TrackDto> fails = new ArrayList<TrackDto>();
        List<TrackDto> successes = new ArrayList<TrackDto>();

        for (int i = 0; i < tracks.size(); i++) {

            TrackDto track = tracks.get(i);
            try {
                if (track == null || track.getId() == null) {
                    throw new IllegalArgumentException("Given track is illegal");
                }

                Media media = mediaRepository.findByTrackId(track.getId());
                if (media != null) {
                    media.setPublishDate(0);
                    mediaRepository.save(media);
                }

                track = client.encodeTrack(track.getId(), track.getResolution() == AudioResolution.RATE_96 ?
                                                          true :
                                                          false, track.getLicensed());
                track.setPublishArtist(track.getArtist());
                track.setPublishTitle(track.getTitle());
                track.setInfo(getArtistInfo(track.getArtist()));
                successes.add(track);
            } catch (Exception e) {
                LOGGER.error("Encoding track failed! [TrackId=" + track.getId() + "] Title=[" + track.getTitle() + "]", e);
                fails.add(track);
            }

        }

        result.put("success", successes);
        result.put("fail", fails);

        return result;
    }

    @Override
    public void assignReportingOptions(TrackReportingOptionsDto trackReportingOptionsDto) {
        client.assignReportingOptions(trackReportingOptionsDto);
    }
}
