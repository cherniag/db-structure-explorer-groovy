package mobi.nowtechnologies.server.shared.dto.web;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/**
 * @author Titov Mykhaylo (titov)
 */
public class PurchasedTrackDto {

    public static final String PURCHASED_TRACK_DTO_LIST = "purchasedTrackDtoList";
    public static final String PURCHASED_TRACK_DTO = "purchasedTrackDto";

    private String mediaIsrc;

    private int mediaId;

    private String trackName;

    private String artistName;

    @DateTimeFormat(iso = ISO.DATE)
    private Date purchasedDate;

    private boolean isDownloadedOriginal;

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public Date getPurchasedDate() {
        return purchasedDate;
    }

    public void setPurchasedDate(Date purchasedDate) {
        this.purchasedDate = purchasedDate;
    }

    public boolean getIsDownloadedOriginal() {
        return isDownloadedOriginal;
    }

    public void setDownloadedOriginal(boolean isDownloadedOriginal) {
        this.isDownloadedOriginal = isDownloadedOriginal;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaIsrc() {
        return mediaIsrc;
    }

    public void setMediaIsrc(String mediaIsrc) {
        this.mediaIsrc = mediaIsrc;
    }

    @Override
    public String toString() {
        return "PurchasedTrackDto [artistName=" + artistName + ", isDownloadedOriginal=" + isDownloadedOriginal + ", mediaId=" + mediaId + ", mediaIsrc=" + mediaIsrc + ", purchasedDate=" +
               purchasedDate + ", trackName=" + trackName + "]";
    }

}
