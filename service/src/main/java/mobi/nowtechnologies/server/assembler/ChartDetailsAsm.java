package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.dao.PersistenceException;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Drm;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.shared.AppConstants;
import mobi.nowtechnologies.server.shared.dto.PurchasedChartDetailDto;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.enums.ChgPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public class ChartDetailsAsm {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChartDetailsAsm.class);

	public static List<PurchasedChartDetailDto> toPurchasedChartDetailDtoList(List<?> list) {
		List<PurchasedChartDetailDto> purchasedChartDetailDtos = new LinkedList<PurchasedChartDetailDto>();
		for (Object obj : list) {
			if (obj.getClass() == Drm.class)
				purchasedChartDetailDtos.add(toPurchasedChartDetailDto((Drm) obj));
			else if (obj.getClass() == ChartDetail.class)
				purchasedChartDetailDtos.add(toPurchasedChartDetailDto((ChartDetail) obj));
		}

		return purchasedChartDetailDtos;
	}

	public static PurchasedChartDetailDto toPurchasedChartDetailDto(Drm drm) {
		PurchasedChartDetailDto purchasedChartDetailDto = new PurchasedChartDetailDto();

		Media media = drm.getMedia();

		Integer audioSize = media.getAudioSize();
		int headerSize = media.getHeaderSize();

		purchasedChartDetailDto.setArtist(media.getArtistName());
		purchasedChartDetailDto.setAudioSize(audioSize);
		purchasedChartDetailDto.setDrmType(drm.getDrmType().getName());
		purchasedChartDetailDto.setDrmValue(drm.getDrmValue());
		purchasedChartDetailDto.setGenre1(media.getGenre().getName());
		purchasedChartDetailDto.setGenre2(media.getGenre().getName());

		purchasedChartDetailDto.setHeaderSize(headerSize);
		purchasedChartDetailDto.setImageLargeSize(media.getImageLargeSize());
		purchasedChartDetailDto.setImageSmallSize(media.getImageSmallSize());
		purchasedChartDetailDto.setInfo(media.getInfo());
		purchasedChartDetailDto.setMedia(media.getIsrc());

		// ---TODO hack magic position for client 4.3.1
		purchasedChartDetailDto.setPosition((byte) 88);
		purchasedChartDetailDto.setPreviousPosition((byte) 88);
		// ---------------------------

		purchasedChartDetailDto.setTitle(media.getTitle());
		purchasedChartDetailDto.setTrackSize(headerSize + audioSize - 2);

		String enocodediTunesUrl = null;
		try {
			String iTunesUrl = media.getiTunesUrl();
			if (iTunesUrl != null)
				enocodediTunesUrl = URLEncoder.encode(iTunesUrl, AppConstants.UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new PersistenceException(e.getMessage(), e);
		}
		purchasedChartDetailDto.setiTunesUrl(enocodediTunesUrl);
		purchasedChartDetailDto.setChangePosition(ChgPosition.UNCHANGED.name());
		purchasedChartDetailDto.setChannel("");

		return purchasedChartDetailDto;
	}

	public static PurchasedChartDetailDto toPurchasedChartDetailDto(ChartDetail purchasedChartDetail) {
		PurchasedChartDetailDto purchasedChartDetailDto = new PurchasedChartDetailDto();

		Media media = purchasedChartDetail.getMedia();
		Chart chart = purchasedChartDetail.getChart();
		String info = purchasedChartDetail.getInfo();
		byte position = purchasedChartDetail.getPosition();
		byte prevPosition = purchasedChartDetail.getPrevPosition();
		ChgPosition chgPosition = purchasedChartDetail.getChgPosition();
		String channel = purchasedChartDetail.getChannel();

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
		purchasedChartDetailDto.setInfo(info == null ? media.getInfo() : info);
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
			throw new PersistenceException(e.getMessage(), e);
		}
		purchasedChartDetailDto.setiTunesUrl(enocodediTunesUrl);
		purchasedChartDetailDto.setPreviousPosition(prevPosition);
		purchasedChartDetailDto.setChangePosition(chgPosition.getLabel());
		purchasedChartDetailDto.setChannel(channel);

		return purchasedChartDetailDto;
	}

	@SuppressWarnings("unchecked")
	public static List<ChartItemDto> toChartItemDtos(List<ChartDetail> list) {
		LOGGER.debug("input parameters chartDetails: [{}]", list);

		List<ChartItemDto> chartItemDtos;
		if (list.isEmpty()) {
			chartItemDtos = Collections.EMPTY_LIST;
		} else {
			chartItemDtos = new LinkedList<ChartItemDto>();

			for (ChartDetail chartDetail : list) {
				chartItemDtos.add(toChartItemDto(chartDetail));
			}
		}

		LOGGER.info("Output parameter chartItemDtos=[{}]", chartItemDtos);
		return chartItemDtos;

	}

	public static ChartItemDto toChartItemDto(ChartDetail chartDetail) {
		LOGGER.debug("input parameters chartDetail: [{}]", chartDetail);

		ChartItemDto chartItemDto = new ChartItemDto();

		chartItemDto.setId(chartDetail.getI());
		chartItemDto.setChannel(chartDetail.getChannel());
		chartItemDto.setChartId(chartDetail.getChartId());
		chartItemDto.setChgPosition(chartDetail.getChgPosition());
		chartItemDto.setInfo(chartDetail.getInfo());
		chartItemDto.setMediaDto(MediaAsm.toMediaDto(chartDetail.getMedia()));
		chartItemDto.setPosition(chartDetail.getPosition());
		chartItemDto.setPrevPosition(chartDetail.getPrevPosition());
		chartItemDto.setPublishTime(new Date(chartDetail.getPublishTimeMillis()));
		chartItemDto.setIsrc(chartDetail.getMedia().getIsrc());

		LOGGER.info("Output parameter chartItemDto=[{}]", chartItemDto);
		return chartItemDto;
	}

	public static ChartDetail fromChartItemDto(ChartItemDto chartItemDto, final ChartDetail chartDetail) {
		LOGGER.debug("input parameters chartItemDto, chartDetail: [{}], [{}]", chartItemDto, chartDetail);

		chartDetail.setChannel(chartItemDto.getChannel());
		chartDetail.setChgPosition(chartItemDto.getChgPosition());
		chartDetail.setInfo(chartItemDto.getInfo());
		chartDetail.setPosition(chartItemDto.getPosition());
		chartDetail.setPrevPosition(chartItemDto.getPrevPosition());
		chartDetail.setPublishTimeMillis(chartItemDto.getPublishTime().getTime());

		LOGGER.info("Output parameter chartDetail=[{}]", chartDetail);
		return chartDetail;
	}
	
	@SuppressWarnings("unchecked")
	public static List<ChartItemDto> toChartItemDtosFromMedia(Date selectedPublishDateTime, Byte chartId, List<Media> list) {
		LOGGER.debug("input parameters chartDetails: [{}]", list);

		List<ChartItemDto> chartItemDtos;
		if (list.isEmpty()) {
			chartItemDtos = Collections.EMPTY_LIST;
		} else {
			chartItemDtos = new LinkedList<ChartItemDto>();

			for (Media media : list) {
				chartItemDtos.add(toChartItemDto(media, selectedPublishDateTime, chartId));
			}
		}

		LOGGER.info("Output parameter chartItemDtos=[{}]", chartItemDtos);
		return chartItemDtos;

	}

	public static ChartItemDto toChartItemDto(Media media, Date selectedPublishDateTime, Byte chartId) {
		LOGGER.debug("input parameters media: [{}]", media);

		ChartItemDto chartItemDto = new ChartItemDto();

		chartItemDto.setChannel("");
		chartItemDto.setChartId(chartId);
		chartItemDto.setChgPosition(ChgPosition.UNCHANGED);
		chartItemDto.setInfo("");
		chartItemDto.setMediaDto(MediaAsm.toMediaDto(media));
		chartItemDto.setPosition((byte)0);
		chartItemDto.setPrevPosition((byte)0);
		chartItemDto.setPublishTime(selectedPublishDateTime);

		LOGGER.info("Output parameter chartItemDto=[{}]", chartItemDto);
		return chartItemDto;
	}
}
