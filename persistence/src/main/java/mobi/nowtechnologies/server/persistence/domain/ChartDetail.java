package mobi.nowtechnologies.server.persistence.domain;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

import javax.persistence.*;

import mobi.nowtechnologies.server.persistence.dao.PersistenceException;
import mobi.nowtechnologies.server.shared.AppConstants;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.ChartDetailDto;
import mobi.nowtechnologies.server.shared.dto.PurchasedChartDetailDto;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.shared.enums.ChgPosition;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Entity
@Table(name = "tb_chartDetail", uniqueConstraints = @UniqueConstraint(columnNames = { "media", "chart", "publishTimeMillis" }))
@NamedQueries({
		@NamedQuery(name = ChartDetail.NQ_IS_TRACK_CAN_BE_BOUGHT_ACCORDING_TO_LICENSE, query = "select count(media) from Media media where media.isrc=?1 and media.publishDate<=?2")
})
public class ChartDetail {

    public static final String NQ_IS_TRACK_CAN_BE_BOUGHT_ACCORDING_TO_LICENSE = "isTrackCanBeBoughtAccordingToLicense";
	public static final String NQ_FIND_CONTENT_INFO_BY_DRM_TYPE = "ChartDetail.findContentInfoByDrmType";
	public static final String NQ_FIND_CONTENT_INFO_BY_ISRC = "ChartDetail.findContentInfoByIsrc";

    private static final Map<String, String> countryCodeForCommunityMap;

    static {
        Map<String, String> map = new HashMap<String, String>();

        map.put(Community.O2_COMMUNITY_REWRITE_URL, "GB");
        map.put(Community.VF_NZ_COMMUNITY_REWRITE_URL, "NZ");

        countryCodeForCommunityMap = Collections.unmodifiableMap(map);
    }

	private static final Logger LOGGER = LoggerFactory.getLogger(ChartDetail.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer i;

	@Column(name = "chart", insertable = false, updatable = false, columnDefinition = "tinyint(4)")
	private Integer chartId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "chart", columnDefinition = "tinyint(4)")
	private Chart chart;

	@Column(name = "media", insertable = false, updatable = false)
	private Integer mediaId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "media")
	private Media media;

	@Column(name = "info", columnDefinition = "text")
	@Lob()
	private String info;

	private byte position;

	private Byte prevPosition;

	@Column(name = "chgPosition", insertable = false, updatable = false)
	private Integer chgPositionId;

	@Enumerated(EnumType.ORDINAL)
	private ChgPosition chgPosition;

	private String channel;

	@Column(name = "image_filename")
	private String imageFileName;

	@Column(name = "image_title")
	private String imageTitle;

	@Column(name = "title", columnDefinition = "char(50)", nullable = true)
	private String title;

	@Column(name = "subtitle", columnDefinition = "char(50)", nullable = true)
	private String subtitle;

	private long publishTimeMillis;
	
	private Boolean locked;
	
	private Boolean defaultChart;

	@Version
	private int version;

    public ChartDetail() {
	}

    public Integer getI() {
        return this.i;
    }

    public boolean isDefaultChart() {
        return defaultChart;
    }

	public void setDefaultChart(boolean defaultChart) {
		this.defaultChart = defaultChart;
	}

	public void setI(Integer i) {
		this.i = i;
	}

	public Chart getChart() {
		return this.chart;
	}

	public void setChart(Chart chart) {
		this.chart = chart;
		chartId = chart.getI();
	}

	public Integer getChartId() {
		return chartId;
	}

	public Media getMedia() {
		return this.media;
	}

	public void setMedia(Media media) {
		this.media = media;
		mediaId = media.getI() != null ? media.getI() : null;
	}

	public Boolean getLocked() {
		return locked;
	}
	
	public ChartType getChartType(){
		return chart.getType();
	}
	
	public String getChartDescription(){
		return getInfo();
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public Boolean getDefaultChart() {
		return defaultChart;
	}

	public void setDefaultChart(Boolean defaultChart) {
		this.defaultChart = defaultChart;
	}

	public String getImageFileName() {
		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}

	public String getImageTitle() {
		return imageTitle;
	}

	public void setImageTitle(String imageTitle) {
		this.imageTitle = imageTitle;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public byte getPosition() {
		return this.position;
	}

	public void setPosition(byte position) {
		this.position = position;
	}

	public int getMediaId() {
		return mediaId;
	}

	public Byte getPrevPosition() {
		return prevPosition;
	}

	public void setPrevPosition(Byte prevPosition) {
		this.prevPosition = prevPosition;
	}

	public ChgPosition getChgPosition() {
		return chgPosition;
	}

	public void setChgPosition(ChgPosition chgPosition) {
		this.chgPosition = chgPosition;
	}

	public Integer getChgPositionId() {
		return chgPositionId;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public long getPublishTimeMillis() {
		return publishTimeMillis;
	}

	public void setPublishTimeMillis(long publishTimeMillis) {
		this.publishTimeMillis = publishTimeMillis;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public boolean isChartItem() {
		return media != null;
	}

    public static List<ChartDetailDto> toChartDetailDtoList(Community community, List<ChartDetail> chartDetails, String defaultAmazonUrl) {
		if (chartDetails == null)
			throw new PersistenceException("The parameter chartDetails is null");
		if (defaultAmazonUrl == null)
			throw new NullPointerException("The parameter defaultAmazonUrl is null");

		LOGGER.debug("input parameters chartDetails: [{}]", new Object[] { chartDetails });
		List<ChartDetailDto> chartDetailDtos = new LinkedList<ChartDetailDto>();
		for (ChartDetail chartDetail : chartDetails) {
			chartDetailDtos.add(chartDetail.toChartDetailDto(community, new ChartDetailDto(), defaultAmazonUrl));
		}
		LOGGER.debug("Output parameter chartDetailDtos=[{}]", chartDetailDtos);
		return chartDetailDtos;
	}

	public ChartDetailDto toChartDetailDto(Community community, ChartDetailDto chartDetailDto, String defaultAmazonUrl) {
		List<Drm> drms = media.getDrms();
		Drm drm;
		int drmSize = drms.size();
		if (drmSize == 1) {
			drm = drms.get(0);
        } else {
            throw new IllegalArgumentException("There are [" + drmSize + "] of drm found but 1 expected");
        }

        MediaFile headerFile = media.getHeaderFile();
        Integer audioSize = media.getAudioSize();
        int headerSize = media.getHeaderSize();
        ChartType chartType = chart.getType();

        byte pos = chartType == ChartType.HOT_TRACKS && position <= 40 ? (byte) (position + 40) : position;
        pos = chartType == ChartType.OTHER_CHART && position <= 50 ? (byte) (position + 50) : pos;

        chartDetailDto.setPosition(pos);

        chartDetailDto.setPlaylistId(chart.getI().intValue());
        chartDetailDto.setArtist(media.getArtistName());
        chartDetailDto.setAudioSize(audioSize);
        chartDetailDto.setDrmType(drm.getDrmType().getName());
        chartDetailDto.setDrmValue(drm.getDrmValue());
        chartDetailDto.setGenre1(chart.getGenre().getName());
        chartDetailDto.setGenre2(media.getGenre().getName());

        chartDetailDto.setHeaderSize(headerSize);
        chartDetailDto.setImageLargeSize(media.getImageLargeSize());
        chartDetailDto.setImageSmallSize(media.getImageSmallSize());
        chartDetailDto.setInfo(info);
        chartDetailDto.setMedia(media.getIsrc());
        chartDetailDto.setTitle(media.getTitle());
        chartDetailDto.setTrackSize(headerSize + audioSize - 2);
        chartDetailDto.setChartDetailVersion(version);
		chartDetailDto.setHeaderVersion(headerFile != null ? headerFile.getVersion() : 0);
		chartDetailDto.setAudioVersion(media.getAudioFile().getVersion());
		chartDetailDto.setImageLargeVersion(media.getImageFIleLarge().getVersion());
		chartDetailDto.setImageSmallVersion(media.getImageFileSmall().getVersion());
        chartDetailDto.setDuration(media.getAudioFile().getDuration());

        chartDetailDto.setAmazonUrl(getAmazonUrl(community, defaultAmazonUrl));
		chartDetailDto.setiTunesUrl(getITunesUrl(community));
		chartDetailDto.setIsArtistUrl(media.getAreArtistUrls());
		chartDetailDto.setPreviousPosition(prevPosition);
		chartDetailDto.setChangePosition(chgPosition.getLabel());
		chartDetailDto.setChannel(channel);

		LOGGER.debug("Output parameter chartDetailDto=[{}]", chartDetailDto);
		return chartDetailDto;
	}

    private String getAmazonUrl(Community community, String defaultAmazonUrl) {
        String amazonUrl = media.getAmazonUrl();

        if(isBlank(amazonUrl)) {
            amazonUrl = defaultAmazonUrl;
        }

        String newCountryCode = countryCodeForCommunityMap.get(community.getRewriteUrlParameter());

        if(isBlank(amazonUrl) || isBlank(newCountryCode)) {
            return amazonUrl;
        }

        try {
            return getEncodedUTF8Text(Utils.replacePathSegmentInUrl(amazonUrl, 0, newCountryCode));
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return getEncodedUTF8Text(amazonUrl);
        }
    }

    private String getITunesUrl(Community community) {
        String url = media.getiTunesUrl();
        String newCountryCode = countryCodeForCommunityMap.get(community.getRewriteUrlParameter());

        if(isBlank(url)|| isBlank(newCountryCode)) {
            return url;
        }

        try {
            String decodedUrl = URLDecoder.decode(url, "UTF-8");
            String urlToken = "&url=";
            int startIndex = decodedUrl.indexOf(urlToken);

            String urlParameterValue = decodedUrl.substring(startIndex + urlToken.length());
            String newUrlValue = Utils.replacePathSegmentInUrl(urlParameterValue, 0, newCountryCode);

            return url.replace(urlParameterValue, newUrlValue);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return url;
        }
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

	public static List<PurchasedChartDetailDto> toPurchasedChartDetailDtoList(Community community, List<ChartDetail> chartDetails) {
		LOGGER.debug("input parameters community, chartDetails: [{}], [{}]", community, chartDetails);

		List<PurchasedChartDetailDto> purchasedChartDetailDtos = new LinkedList<PurchasedChartDetailDto>();
		for (ChartDetail chartDetail : chartDetails) {
			purchasedChartDetailDtos.add(chartDetail.toPurchasedChartDetailDto(community, new PurchasedChartDetailDto()));
		}
		LOGGER.debug("Output parameter purchasedChartDetailDtos=[{}]", purchasedChartDetailDtos);
		return purchasedChartDetailDtos;
	}

	public PurchasedChartDetailDto toPurchasedChartDetailDto(Community community,  PurchasedChartDetailDto purchasedChartDetailDto) {
		LOGGER.debug("input parameters community, purchasedChartDetailDto: [{}], [{}]", community, purchasedChartDetailDto);

		List<Drm> drms = media.getDrms();
		Drm drm;
		int drmSize = drms.size();
		if (drmSize == 1) {
			drm = drms.get(0);
		} else
			throw new IllegalArgumentException("There are [" + drmSize + "] of drm found but 1 expected");

		Integer audioSize = media.getAudioSize();
		int headerSize = media.getHeaderSize();

		purchasedChartDetailDto.setArtist(media.getArtistName());
		purchasedChartDetailDto.setAudioSize(audioSize);
		purchasedChartDetailDto.setDrmType(drm.getDrmType().getName());
		purchasedChartDetailDto.setDrmValue(drm.getDrmValue());
		purchasedChartDetailDto.setGenre1(chart.getGenre().getName());
		purchasedChartDetailDto.setGenre2(media.getGenre().getName());

		purchasedChartDetailDto.setHeaderSize(headerSize);
		purchasedChartDetailDto.setImageLargeSize(media.getImageLargeSize());
		purchasedChartDetailDto.setImageSmallSize(media.getImageSmallSize());
		purchasedChartDetailDto.setInfo(info);
		purchasedChartDetailDto.setMedia(media.getIsrc());
		purchasedChartDetailDto.setPosition(position);
		purchasedChartDetailDto.setTitle(media.getTitle());
		purchasedChartDetailDto.setTrackSize(headerSize + audioSize - 2);

        purchasedChartDetailDto.setiTunesUrl(getITunesUrl(community));
		purchasedChartDetailDto.setPreviousPosition(prevPosition);
		purchasedChartDetailDto.setChangePosition(chgPosition.getLabel());
		purchasedChartDetailDto.setChannel(channel);

		LOGGER.debug("Output parameter [{}]", purchasedChartDetailDto);
		return purchasedChartDetailDto;
	}

	public static ChartDetail newInstance(ChartDetail chartDetail) {
		LOGGER.debug("input parameters chartDetail: [{}]", chartDetail);

		ChartDetail newChartDetail = new ChartDetail();
		newChartDetail.setChannel(chartDetail.getChannel());
		newChartDetail.setChart(chartDetail.getChart());
		newChartDetail.setChgPosition(chartDetail.getChgPosition());
		newChartDetail.setInfo(chartDetail.getInfo());
		if(chartDetail.getMedia() != null)
			newChartDetail.setMedia(chartDetail.getMedia());
		newChartDetail.setPosition(chartDetail.getPosition());
		newChartDetail.setPrevPosition(chartDetail.getPrevPosition());
		newChartDetail.setTitle(chartDetail.getTitle());
		newChartDetail.setSubtitle(chartDetail.getSubtitle());
		newChartDetail.setImageFileName(chartDetail.getImageFileName());
		newChartDetail.setImageTitle(chartDetail.getImageTitle());
		newChartDetail.setPublishTimeMillis(chartDetail.getPublishTimeMillis());
		newChartDetail.setDefaultChart(chartDetail.getDefaultChart());

		LOGGER.info("Output parameter newChartDetail=[{}]", newChartDetail);
		return newChartDetail;
	}

    public ChartDetail withMedia(Media media) {
        setMedia(media);
        return this;
    }

    public ChartDetail withChart(Chart chart) {
        setChart(chart);
        return this;
    }

    public ChartDetail withPrevPosition(Byte prevPosition) {
        setPrevPosition(prevPosition);
        return this;
    }

    public ChartDetail withChgPosition(ChgPosition chgPosition) {
        setChgPosition(chgPosition);
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("i", i)
                .append("chartId", chartId)
                .append("mediaId", mediaId)
                .append("info", info)
                .append("position", position)
                .append("prevPosition", prevPosition)
                .append("chgPositionId", chgPositionId)
                .append("channel", channel)
                .append("imageFileName", imageFileName)
                .append("imageTitle", imageTitle)
                .append("title", title)
                .append("subtitle", subtitle)
                .append("publishTimeMillis", publishTimeMillis)
                .append("locked", locked)
                .append("defaultChart", defaultChart)
                .append("version", version)
                .toString();
    }
}