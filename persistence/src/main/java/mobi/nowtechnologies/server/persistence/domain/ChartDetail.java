package mobi.nowtechnologies.server.persistence.domain;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import mobi.nowtechnologies.server.persistence.dao.PersistenceException;
import mobi.nowtechnologies.server.shared.AppConstants;
import mobi.nowtechnologies.server.shared.dto.BonusChartDetailDto;
import mobi.nowtechnologies.server.shared.dto.ChartDetailDto;
import mobi.nowtechnologies.server.shared.dto.PurchasedChartDetailDto;
import mobi.nowtechnologies.server.shared.enums.ChgPosition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The persistent class for the tb_chartDetail database table.
 * 
 */
@Entity
@Table(name = "tb_chartDetail", uniqueConstraints = @UniqueConstraint(columnNames = { "media", "chart", "publishTimeMillis" }))
@NamedQueries({
		@NamedQuery(name = ChartDetail.NQ_IS_BOUNUS_TRACK, query = "select count(chartDetail) from ChartDetail chartDetail join chartDetail.chart chart join chartDetail.media media where chart.communityId=?1 and media.isrc=?2 and chartDetail.position>(chart.numTracks-chart.numBonusTracks)"),
		@NamedQuery(name = ChartDetail.NQ_IS_TRACK_CAN_BE_BOUGHT_ACCORDING_TO_LICENSE, query = "select count(media) from Media media where media.isrc=?1 and media.publishDate<=?2")
})
public class ChartDetail {
	public static final String NQ_IS_BOUNUS_TRACK = "isBounusTrack";
	public static final String NQ_IS_TRACK_CAN_BE_BOUGHT_ACCORDING_TO_LICENSE = "isTrackCanBeBoughtAccordingToLicense";
	public static final String NQ_FIND_CONTENT_INFO_BY_DRM_TYPE = "ChartDetail.findContentInfoByDrmType";
	public static final String NQ_FIND_CONTENT_INFO_BY_ISRC = "ChartDetail.findContentInfoByIsrc";

	private static final Logger LOGGER = LoggerFactory.getLogger(ChartDetail.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer i;

	@Column(name = "chart", insertable = false, updatable = false)
	private Byte chartId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "chart", columnDefinition = "int(10) unsigned")
	private Chart chart;

	@Column(name = "media", insertable = false, updatable = false)
	private int mediaId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "media")
	private Media media;

	@Column(name = "info", columnDefinition = "text")
	@Lob()
	private String info;

	private byte position;

	private byte prevPosition;

	@Column(name = "chgPosition", insertable = false, updatable = false)
	private int chgPositionId;

	@Enumerated(EnumType.ORDINAL)
	private ChgPosition chgPosition;

	private String channel;
	
	private long publishTimeMillis;
	
	@Version
	private int version;

	public ChartDetail() {
	}

	public Integer getI() {
		return this.i;
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

	public Byte getChartId() {
		return chartId;
	}

	public Media getMedia() {
		return this.media;
	}

	public void setMedia(Media media) {
		this.media = media;
		mediaId = media.getI();
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

	public byte getPrevPosition() {
		return prevPosition;
	}

	public void setPrevPosition(byte prevPosition) {
		this.prevPosition = prevPosition;
	}

	public ChgPosition getChgPosition() {
		return chgPosition;
	}

	public void setChgPosition(ChgPosition chgPosition) {
		this.chgPosition = chgPosition;
	}

	public int getChgPositionId() {
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

	public static List<ChartDetailDto> toChartDetailDtoList(List<ChartDetail> chartDetails, String defaultAmazonUrl) {
		if (chartDetails == null)
			throw new PersistenceException("The parameter chartDetails is null");
		if (defaultAmazonUrl == null)
			throw new NullPointerException("The parameter defaultAmazonUrl is null");

		LOGGER.debug("input parameters chartDetails: [{}]", new Object[] { chartDetails });
		List<ChartDetailDto> chartDetailDtos = new LinkedList<ChartDetailDto>();
		for (ChartDetail chartDetail : chartDetails) {
			if (chartDetail.getChart().getType() == ChartType.BASIC_CHART)
				chartDetailDtos.add(chartDetail.toChartDetailDto(new ChartDetailDto(), defaultAmazonUrl));
			else
				chartDetailDtos.add(chartDetail.toChartDetailDto(new BonusChartDetailDto(), defaultAmazonUrl));
		}
		LOGGER.debug("Output parameter chartDetailDtos=[{}]", chartDetailDtos);
		return chartDetailDtos;
	}

	private ChartDetailDto toChartDetailDto(ChartDetailDto chartDetailDto, String defaultAmazonUrl) {
		List<Drm> drms = media.getDrms();
		Drm drm;
		int drmSize = drms.size();
		if (drmSize == 1) {
			drm = drms.get(0);
		} else
			throw new IllegalArgumentException("There are [" + drmSize + "] of drm found but 1 expected");

		Integer audioSize = media.getAudioSize();
		int headerSize = media.getHeaderSize();
		ChartType chartType = getChart().getType();
		
		byte pos = chartType == ChartType.HOT_TRACKS ? (byte)(position+40) : position;
		pos = chartType == ChartType.OTHER_CHART ? (byte)(position+50) : pos;
		
		chartDetailDto.setPosition(pos);

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
		chartDetailDto.setHeaderVersion(media.getHeaderFile().getVersion());
		chartDetailDto.setAudioVersion(media.getAudioFile().getVersion());
		chartDetailDto.setImageLargeVersion(media.getImageFIleLarge().getVersion());
		chartDetailDto.setImageSmallVersion(media.getImageFileSmall().getVersion());
	
		String enocodediTunesUrl = null;
		String enocodedAmazonUrl = null;
		try {
			String iTunesUrl = media.getiTunesUrl();
			if (iTunesUrl != null)
				enocodediTunesUrl = URLEncoder.encode(iTunesUrl, AppConstants.UTF_8);
			String amazonUrl = media.getAmazonUrl();	
			if (amazonUrl == null || amazonUrl.isEmpty()){
				amazonUrl = defaultAmazonUrl;
			}
			enocodedAmazonUrl = URLEncoder.encode(amazonUrl, AppConstants.UTF_8);
			
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PersistenceException(e.getMessage(), e);
		}
		chartDetailDto.setAmazonUrl(enocodedAmazonUrl);
		chartDetailDto.setiTunesUrl(enocodediTunesUrl);
		chartDetailDto.setIsArtistUrl(media.getAreArtistUrls());
		chartDetailDto.setPreviousPosition(prevPosition);
		chartDetailDto.setChangePosition(chgPosition.getLabel());
		chartDetailDto.setChannel(channel);

		LOGGER.debug("Output parameter chartDetailDto=[{}]", chartDetailDto);
		return chartDetailDto;
	}

	public static List<PurchasedChartDetailDto> toPurchasedChartDetailDtoList(List<ChartDetail> chartDetails) {
		LOGGER.debug("input parameters chartDetails: [{}]", chartDetails);

		List<PurchasedChartDetailDto> purchasedChartDetailDtos = new LinkedList<PurchasedChartDetailDto>();
		for (ChartDetail chartDetail : chartDetails) {
			purchasedChartDetailDtos.add(chartDetail.toPurchasedChartDetailDto(new PurchasedChartDetailDto()));
		}
		LOGGER.debug("Output parameter purchasedChartDetailDtos=[{}]", purchasedChartDetailDtos);
		return purchasedChartDetailDtos;
	}

	public PurchasedChartDetailDto toPurchasedChartDetailDto(PurchasedChartDetailDto purchasedChartDetailDto) {
		LOGGER.debug("input parameters purchasedChartDetailDto: [{}]", purchasedChartDetailDto);

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

		String enocodediTunesUrl = null;
		try {
			String iTunesUrl = media.getiTunesUrl();
			if (iTunesUrl != null)
				enocodediTunesUrl = URLEncoder.encode(iTunesUrl, AppConstants.UTF_8);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PersistenceException(e.getMessage(), e);
		}
		purchasedChartDetailDto.setiTunesUrl(enocodediTunesUrl);
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
		newChartDetail.setMedia(chartDetail.getMedia());
		newChartDetail.setPosition(chartDetail.getPosition());
		newChartDetail.setPrevPosition(chartDetail.getPrevPosition());
		newChartDetail.setPublishTimeMillis(chartDetail.getPublishTimeMillis());
		
		LOGGER.info("Output parameter newChartDetail=[{}]", newChartDetail);
		return newChartDetail;
	}
	
	@Override
	public String toString() {
		return "ChartDetail [chartId=" + chartId + ", chgPositionId=" + chgPositionId + ", i=" + i + ", mediaId=" + mediaId + ", position=" + position
				+ ", prevPosition=" + prevPosition + ", channel=" + channel + ", info=" + info + ", publishTimeMillis=" + publishTimeMillis + ", version="
				+ version + "]";
	}

}