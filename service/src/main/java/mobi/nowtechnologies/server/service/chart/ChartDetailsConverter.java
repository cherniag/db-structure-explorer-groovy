package mobi.nowtechnologies.server.service.chart;

import mobi.nowtechnologies.server.persistence.dao.PersistenceException;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Drm;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaFile;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;
import mobi.nowtechnologies.server.service.streamzine.BadgesService;
import mobi.nowtechnologies.server.shared.AppConstants;
import mobi.nowtechnologies.server.shared.dto.ChartDetailDto;
import mobi.nowtechnologies.server.shared.dto.PlaylistDto;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.web.util.UriComponentsBuilder;

/**
 * Author: Gennadii Cherniaiev Date: 3/11/14
 */
public class ChartDetailsConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChartDetailsConverter.class);
    private static final String URL_PARAMETER = "&url=";
    private CommunityResourceBundleMessageSource messageSource;
    private long iTunesLinkFormatCutoverTimeMillis;
    private BadgesService badgesService;

    static String replacePathSegmentInUrl(String url, int index, String newValue) {
        LOGGER.debug("url=[{}], index=[{}], newValue=[{}]", url, index, newValue);
        final UriComponentsBuilder original = UriComponentsBuilder.fromUriString(url);

        ArrayList<String> pathSegments = new ArrayList<>(original.build().getPathSegments());
        pathSegments.set(index, newValue);

        original.replacePath("").pathSegment(pathSegments.toArray(new String[0]));

        return original.build().toString();
    }

    public List<ChartDetailDto> toChartDetailDtoList(List<ChartDetail> chartDetails, Community community, String defaultAmazonUrl) {
        if (chartDetails == null) {
            throw new PersistenceException("The parameter chartDetails is null");
        }
        if (defaultAmazonUrl == null) {
            throw new NullPointerException("The parameter defaultAmazonUrl is null");
        }

        LOGGER.debug("input parameters chartDetails: [{}]", new Object[] {chartDetails});
        List<ChartDetailDto> chartDetailDtos = new LinkedList<ChartDetailDto>();
        for (ChartDetail chartDetail : chartDetails) {
            chartDetailDtos.add(toChartDetailDto(chartDetail, community, defaultAmazonUrl));
        }
        LOGGER.debug("Output parameter chartDetailDtos=[{}]", chartDetailDtos);
        return chartDetailDtos;
    }

    public ChartDetailDto toChartDetailDto(ChartDetail chartDetail, Community community, String defaultAmazonUrl) {
        ChartDetailDto chartDetailDto = new ChartDetailDto();
        Media media = chartDetail.getMedia();

        Drm drm = getDrm(media);

        MediaFile headerFile = media.getHeaderFile();
        Integer audioSize = media.getAudioSize();
        int headerSize = media.getHeaderSize();
        Chart chart = chartDetail.getChart();

        byte pos = getPosition(chartDetail, chart);

        chartDetailDto.setPosition(pos);

        chartDetailDto.setPlaylistId(chart.getI());
        chartDetailDto.setArtist(media.getArtistName());
        chartDetailDto.setAudioSize(audioSize);
        chartDetailDto.setDrmType(drm.getDrmType().getName());
        chartDetailDto.setDrmValue(drm.getDrmValue());
        chartDetailDto.setGenre1(chart.getGenre().getName());
        chartDetailDto.setGenre2(media.getGenre().getName());

        chartDetailDto.setHeaderSize(headerSize);
        chartDetailDto.setImageLargeSize(media.getImageLargeSize());
        chartDetailDto.setImageSmallSize(media.getImageSmallSize());
        chartDetailDto.setInfo(chartDetail.getInfo());
        chartDetailDto.setMedia(media.getIsrcTrackId());
        chartDetailDto.setTitle(media.getTitle());
        chartDetailDto.setTrackSize(headerSize + audioSize - 2);
        chartDetailDto.setChartDetailVersion(chartDetail.getVersionAsPrimitive());
        chartDetailDto.setHeaderVersion(headerFile != null ?
                                        headerFile.getVersionAsPrimitive() :
                                        0);
        chartDetailDto.setAudioVersion(media.getAudioFile().getVersionAsPrimitive());
        chartDetailDto.setImageLargeVersion(media.getImageFIleLarge().getVersionAsPrimitive());
        chartDetailDto.setImageSmallVersion(media.getImageFileSmall().getVersionAsPrimitive());
        chartDetailDto.setDuration(media.getAudioFile().getDuration());

        chartDetailDto.setAmazonUrl(getAmazonUrl(media.getAmazonUrl(), defaultAmazonUrl, community.getRewriteUrlParameter()));
        chartDetailDto.setiTunesUrl(getITunesUrl(media.getiTunesUrl(), community.getRewriteUrlParameter()));
        chartDetailDto.setIsArtistUrl(media.getAreArtistUrls());
        chartDetailDto.setPreviousPosition(chartDetail.getPrevPosition());
        chartDetailDto.setChangePosition(chartDetail.getChgPosition().getLabel());
        chartDetailDto.setChannel(chartDetail.getChannel());

        LOGGER.debug("Output parameter chartDetailDto=[{}]", chartDetailDto);
        return chartDetailDto;
    }

    public PlaylistDto toPlaylistDto(ChartDetail chartDetail, Resolution resolution, Community community, final boolean switchable, boolean isPlayListLockSupported, boolean areAllTracksLocked) {
        LOGGER.debug("input parameters chart: [{}], switchable: [{}]", chartDetail, switchable);

        PlaylistDto playlistDto = new PlaylistDto();
        playlistDto.setId(chartDetail.getChart().getI() != null ?
                          chartDetail.getChart().getI() :
                          null);
        playlistDto.setPlaylistTitle(chartDetail.getTitle() != null ?
                                     chartDetail.getTitle() :
                                     chartDetail.getChart().getName());
        playlistDto.setSubtitle(chartDetail.getSubtitle());
        playlistDto.setImage(chartDetail.getImageFileName());
        playlistDto.setImageTitle(chartDetail.getImageTitle());
        playlistDto.setDescription(chartDetail.getChartDescription());
        playlistDto.setPosition(chartDetail.getPosition());
        playlistDto.setSwitchable(switchable);
        playlistDto.setType(chartDetail.getChartType());

        if (chartDetail.getBadgeId() != null && resolution != null) {
            String badgeFileName = badgesService.getBadgeFileName(chartDetail.getBadgeId(), community, resolution);
            playlistDto.setBadgeIcon(badgeFileName);
        }

        if (isPlayListLockSupported) {
            playlistDto.setLocked(areAllTracksLocked);
        }

        LOGGER.info("Output parameter playlistDto=[{}]", playlistDto);
        return playlistDto;
    }

    private byte getPosition(ChartDetail chartDetail, Chart chart) {
        ChartType chartType = chart.getType();

        byte position = chartDetail.getPosition();
        byte pos = chartType == ChartType.HOT_TRACKS && position <= 40 ?
                   (byte) (position + 40) :
                   position;
        pos = chartType == ChartType.OTHER_CHART && position <= 50 ?
              (byte) (position + 50) :
              pos;
        return pos;
    }

    private Drm getDrm(Media media) {
        List<Drm> drms = media.getDrms();
        if (drms.size() != 1) {
            throw new IllegalArgumentException("There are [" + drms.size() + "] of drm found but 1 expected");
        }
        return drms.get(0);
    }

    private String getAmazonUrl(String mediaAmazonUrl, String defaultAmazonUrl, String communityRewriteUrlParameter) {
        if (isBlank(mediaAmazonUrl)) {
            mediaAmazonUrl = defaultAmazonUrl;
        }

        String newCountryCode = getCountryCode(communityRewriteUrlParameter);

        if (isBlank(mediaAmazonUrl) || isBlank(newCountryCode)) {
            return mediaAmazonUrl;
        }

        try {
            return getEncodedUTF8Text(replacePathSegmentInUrl(mediaAmazonUrl, 0, newCountryCode));
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return getEncodedUTF8Text(mediaAmazonUrl);
        }
    }

    private String getCountryCode(String communityRewriteUrlParameter) {
        return messageSource.getMessage(communityRewriteUrlParameter, "itunes.urlCountryCode", null, null);
    }

    private String getITunesUrl(String existingITunesUrl, String communityRewriteUrl) {
        String countryCode = getCountryCode(communityRewriteUrl);
        if (isBlank(existingITunesUrl) || isBlank(countryCode)) {
            return existingITunesUrl;
        }

        try {
            String decodedITunesUrl = URLDecoder.decode(existingITunesUrl, "UTF-8");
            if (System.currentTimeMillis() >= iTunesLinkFormatCutoverTimeMillis) {
                if (hasOldPartnerFormat(decodedITunesUrl)) {
                    decodedITunesUrl = getUrlParameterValue(decodedITunesUrl);
                }
                String newUrlValue = enrichWithAffiliateCampaignParameters(decodedITunesUrl, communityRewriteUrl);
                String withCountryCode = replacePathSegmentInUrl(newUrlValue, 0, countryCode);
                return getEncodedUTF8Text(withCountryCode);
            } else {
                return replaceCountryPathToUrlParameter(countryCode, decodedITunesUrl);
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return existingITunesUrl;
        }

    }

    private String replaceCountryPathToUrlParameter(String countryCode, String decodedITunesUrl) {
        String urlParameterValue = getUrlParameterValue(decodedITunesUrl);
        String newUrlParameterValue = replacePathSegmentInUrl(urlParameterValue, 0, countryCode);
        int urlParameterStartIndex = decodedITunesUrl.indexOf(URL_PARAMETER);
        return getEncodedUTF8Text(decodedITunesUrl.substring(0, urlParameterStartIndex) + URL_PARAMETER + newUrlParameterValue);
    }

    private String enrichWithAffiliateCampaignParameters(String decodedUrl, String communityRewriteUrlParameter) {
        UriComponentsBuilder iTunesUriComponentsBuilder = UriComponentsBuilder.fromUriString(decodedUrl);
        iTunesUriComponentsBuilder.replaceQueryParam("partnerId");

        String affiliateToken = messageSource.getMessage(communityRewriteUrlParameter, "itunes.affiliate.token", null, null, null);
        LOGGER.debug("Affiliate token is [{}]", affiliateToken);
        if (!StringUtils.isEmpty(affiliateToken)) {
            iTunesUriComponentsBuilder.queryParam("at", affiliateToken);
        }

        String campaignToken = messageSource.getMessage(communityRewriteUrlParameter, "itunes.campaign.token", null, null, null);
        LOGGER.debug("Campaign token is [{}]", campaignToken);
        if (!StringUtils.isEmpty(campaignToken)) {
            iTunesUriComponentsBuilder.queryParam("ct", campaignToken);
        }
        return iTunesUriComponentsBuilder.build().toString();
    }

    private String getUrlParameterValue(String decodedUrl) {
        int startIndex = decodedUrl.indexOf(URL_PARAMETER);
        return decodedUrl.substring(startIndex + URL_PARAMETER.length());
    }

    private boolean hasOldPartnerFormat(String mediaITunesUrl) {
        return mediaITunesUrl.startsWith("http://clkuk.tradedoubler.com");
    }

    private String getEncodedUTF8Text(String text) {
        try {
            String encodedText = null;
            if (StringUtils.isNotBlank(text)) {
                encodedText = URLEncoder.encode(text, AppConstants.UTF_8);
            }
            return encodedText;
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setiTunesLinkFormatCutoverTimeMillis(long iTunesLinkFormatCutoverTimeMillis) {
        this.iTunesLinkFormatCutoverTimeMillis = iTunesLinkFormatCutoverTimeMillis;
    }

    public void setBadgesService(BadgesService badgesService) {
        this.badgesService = badgesService;
    }
}
