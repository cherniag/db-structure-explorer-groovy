package mobi.nowtechnologies.server.utils;

import mobi.nowtechnologies.server.persistence.dao.PersistenceException;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.shared.AppConstants;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.ChartDetailDto;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Author: Gennadii Cherniaiev
 * Date: 3/11/14
 */
public class ChartDetailsConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChartDetailsConverter.class);
    private static final Map<String, String> countryCodeForCommunityMap;
    private static final String URL_PARAMETER = "&url=";
    private CommunityResourceBundleMessageSource messageSource;
    private long iTunesLinkFormatCutoverTimeMillis;

    static {
        Map<String, String> map = new HashMap<String, String>();
        map.put(Community.O2_COMMUNITY_REWRITE_URL, "GB");
        map.put(Community.VF_NZ_COMMUNITY_REWRITE_URL, "NZ");
        countryCodeForCommunityMap = Collections.unmodifiableMap(map);
    }

    public List<ChartDetailDto> toChartDetailDtoList(List<ChartDetail> chartDetails, Community community, String defaultAmazonUrl) {
		if (chartDetails == null)
			throw new PersistenceException("The parameter chartDetails is null");
		if (defaultAmazonUrl == null)
			throw new NullPointerException("The parameter defaultAmazonUrl is null");

		LOGGER.debug("input parameters chartDetails: [{}]", new Object[] { chartDetails });
		List<ChartDetailDto> chartDetailDtos = new LinkedList<ChartDetailDto>();
		for (ChartDetail chartDetail : chartDetails) {
			chartDetailDtos.add(toChartDetailDto(chartDetail, community, defaultAmazonUrl));
		}
		LOGGER.debug("Output parameter chartDetailDtos=[{}]", chartDetailDtos);
		return chartDetailDtos;
	}

    public ChartDetailDto toChartDetailDto(ChartDetail chartDetail, Community community, String defaultAmazonUrl) {
        ChartDetailDto chartDetailDto = new ChartDetailDto();
        Media media =  chartDetail.getMedia();

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
        chartDetailDto.setMedia(media.getIsrc());
        chartDetailDto.setTitle(media.getTitle());
        chartDetailDto.setTrackSize(headerSize + audioSize - 2);
        chartDetailDto.setChartDetailVersion(chartDetail.getVersion());
        chartDetailDto.setHeaderVersion(headerFile != null ? headerFile.getVersion() : 0);
        chartDetailDto.setAudioVersion(media.getAudioFile().getVersion());
        chartDetailDto.setImageLargeVersion(media.getImageFIleLarge().getVersion());
        chartDetailDto.setImageSmallVersion(media.getImageFileSmall().getVersion());
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

    private byte getPosition(ChartDetail chartDetail, Chart chart) {
        ChartType chartType = chart.getType();

        byte position = chartDetail.getPosition();
        byte pos = chartType == ChartType.HOT_TRACKS && position <= 40 ? (byte) (position + 40) : position;
        pos = chartType == ChartType.OTHER_CHART && position <= 50 ? (byte) (position + 50) : pos;
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
        if(isBlank(mediaAmazonUrl)) {
            mediaAmazonUrl = defaultAmazonUrl;
        }

        String newCountryCode = countryCodeForCommunityMap.get(communityRewriteUrlParameter);

        if(isBlank(mediaAmazonUrl) || isBlank(newCountryCode)) {
            return mediaAmazonUrl;
        }

        try {
            return getEncodedUTF8Text(Utils.replacePathSegmentInUrl(mediaAmazonUrl, 0, newCountryCode));
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return getEncodedUTF8Text(mediaAmazonUrl);
        }
    }

    private String getITunesUrl(String mediaITunesUrl, String communityRewriteUrlParameter) {
        String newCountryCode = countryCodeForCommunityMap.get(communityRewriteUrlParameter);
        if(isBlank(mediaITunesUrl)|| isBlank(newCountryCode)) {
            LOGGER.info("Media iTunes url [{}] or new country code [{}] is empty", mediaITunesUrl, newCountryCode);
            return mediaITunesUrl;
        }
        try {
            String decodedITunesUrl = URLDecoder.decode(mediaITunesUrl, "UTF-8");
            if (System.currentTimeMillis() >= iTunesLinkFormatCutoverTimeMillis){
                if(hasOldFormat(decodedITunesUrl)){
                    decodedITunesUrl = getUrlParameterValue(decodedITunesUrl);
                }
                String newUrlValue = enrichITunesUrl(decodedITunesUrl, newCountryCode, communityRewriteUrlParameter);
                return getEncodedUTF8Text(newUrlValue);
            }else{
                String urlParameterValue = getUrlParameterValue(decodedITunesUrl);
                String newUrlParameterValue = Utils.replacePathSegmentInUrl(urlParameterValue, 0, newCountryCode);
                int urlParameterStartIndex = decodedITunesUrl.indexOf(URL_PARAMETER);
                return getEncodedUTF8Text(decodedITunesUrl.substring(0, urlParameterStartIndex) + URL_PARAMETER + newUrlParameterValue);
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return mediaITunesUrl;
        }

    }

    private String enrichITunesUrl(String decodedUrl, String newCountryCode, String communityRewriteUrlParameter) {
        UriComponentsBuilder iTunesUriComponentsBuilder = UriComponentsBuilder.fromUriString(decodedUrl);
        iTunesUriComponentsBuilder.replaceQueryParam("partnerId");
        String at = messageSource.getMessage(communityRewriteUrlParameter, "itunes.affiliate.token", null, null, null);
        LOGGER.debug("Affiliate token is [{}]", at);
        if (!StringUtils.isEmpty(at)){
            iTunesUriComponentsBuilder.queryParam("at", at);
        }
        String ct = messageSource.getMessage(communityRewriteUrlParameter, "itunes.campaign.token", null, null, null);
        LOGGER.debug("Campaign token is [{}]", ct);
        if (!StringUtils.isEmpty(ct)){
            iTunesUriComponentsBuilder.queryParam("ct", ct);
        }
        return Utils.replacePathSegmentInUrl(iTunesUriComponentsBuilder.build().toString(), 0, newCountryCode);
    }

    private String getUrlParameterValue(String decodedUrl) {
        int startIndex = decodedUrl.indexOf(URL_PARAMETER);
        return decodedUrl.substring(startIndex + URL_PARAMETER.length());
    }

    private boolean hasOldFormat(String mediaITunesUrl) {
        return mediaITunesUrl.startsWith("http://clkuk.tradedoubler.com");
    }

    private String getEncodedUTF8Text(String text) {
        try {
            String encodedText = null;
            if (StringUtils.isNotBlank(text))
                encodedText = URLEncoder.encode(text, AppConstants.UTF_8);
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
}
