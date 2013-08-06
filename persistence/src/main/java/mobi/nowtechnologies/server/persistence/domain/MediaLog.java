package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import mobi.nowtechnologies.server.shared.dto.web.PurchasedTrackDto;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The persistent class for the tb_mediaLog database table.
 * 
 */
/**
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
@Entity
@Table(name = "tb_mediaLog")
@NamedQueries({
	@NamedQuery(name=MediaLog.NQ_GET_PURCHASED_TRACKS_BY_USER_ID, query="select mediaLog from MediaLog mediaLog join FETCH mediaLog.media media where mediaLog.logType=? and mediaLog.userUID=?"),
	@NamedQuery(name=MediaLog.NQ_IS_DOWNLOADED_ORIGINAL, query="select count(mediaLog) from MediaLog mediaLog where mediaLog.logType=? and mediaLog.userUID=? and mediaLog.mediaUID=?")
})
public class MediaLog implements Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(MediaLog.class);

	public static final String NQ_GET_PURCHASED_TRACKS_BY_USER_ID = "getPurchasedTracksByUserId";
	public static final String NQ_IS_DOWNLOADED_ORIGINAL = "isDowloadedOriginal";

	private static final long serialVersionUID = 1L;

	public static enum Fields {
		i();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int i;

	private int logTimestamp;

	private byte logType;
	
	@Column(name = "mediaUID", insertable = false, updatable = false)
	private int mediaUID;

	private int userUID;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "mediaUID")
	@Fetch(FetchMode.JOIN)
	private Media media;
	
	@Transient
	private boolean alreadyDownloadedOriginal;

	public MediaLog() {
	}

	public int getI() {
		return this.i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getLogTimestamp() {
		return this.logTimestamp;
	}

	public void setLogTimestamp(int logTimestamp) {
		this.logTimestamp = logTimestamp;
	}

	public byte getLogType() {
		return this.logType;
	}

	public void setLogType(byte logType) {
		this.logType = logType;
	}

	public int getMediaUID() {
		return this.mediaUID;
	}

	public int getUserUID() {
		return this.userUID;
	}

	public void setUserUID(int userUID) {
		this.userUID = userUID;
	}

	public boolean isAlreadyDownloadedOriginal() {
		return alreadyDownloadedOriginal;
	}

	public void setAlreadyDownloadedOriginal(boolean alreadyDownloadedOriginal) {
		this.alreadyDownloadedOriginal = alreadyDownloadedOriginal;
	}

	@Override
	public String toString() {
		return "MediaLog [alreadyDownloadedOriginal=" + alreadyDownloadedOriginal + ", i=" + i + ", logTimestamp=" + logTimestamp + ", logType=" + logType + ", mediaUID=" + mediaUID + ", userUID="
				+ userUID + "]";
	}

	public static List<PurchasedTrackDto> toPurchasedTrackDtoList(List<MediaLog> mediaLogs) {
		LOGGER.debug("input parameters mediaLogShallows: [{}]", mediaLogs);
		List<PurchasedTrackDto> purchasedTrackDtos = new ArrayList<PurchasedTrackDto>(mediaLogs.size());
		for (MediaLog mediaLog : mediaLogs) {
			purchasedTrackDtos.add(mediaLog.toPurchasedTrackDto());
		}
		LOGGER.debug("Output parameter purchasedTrackDtos=[{}]", purchasedTrackDtos);
		return purchasedTrackDtos;
	}

	public PurchasedTrackDto toPurchasedTrackDto() {
		PurchasedTrackDto purchasedTrackDto = new PurchasedTrackDto();

		purchasedTrackDto.setMediaIsrc(media.getIsrc());
		purchasedTrackDto.setMediaId(mediaUID);
		purchasedTrackDto.setPurchasedDate(new Date(logTimestamp * 1000L));
		purchasedTrackDto.setTrackName(media.getTitle());
		purchasedTrackDto.setArtistName(media.getArtistName());

		LOGGER.debug("Output parameter purchasedTrackDto=[{}]", purchasedTrackDto);
		return purchasedTrackDto;
	}

	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		this.media = media;
		this.mediaUID = media.getI();
	}
}