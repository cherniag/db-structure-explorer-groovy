package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.shared.enums.ItemType;
import static mobi.nowtechnologies.common.util.TrackIdGenerator.buildUniqueTrackId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import static javax.persistence.FetchType.EAGER;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 */
@Entity
@Table(name = "tb_media")
public class Media extends Item implements Serializable {

    private static final long serialVersionUID = 416356472074800767L;
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "artist")
    private Artist artist;
    @Column(name = "artist", insertable = false, updatable = false)
    private Integer artistId;
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "audioFile")
    private MediaFile audioFile;
    @Column(name = "audioFile", insertable = false, updatable = false)
    private Integer audioFileId;
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "genre")
    private Genre genre;
    @Column(name = "genre", insertable = false, updatable = false)
    private Integer genreId;
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "headerFile")
    private MediaFile headerFile;
    @Column(name = "headerFile", insertable = false, updatable = false)
    private Integer headerFileId;
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "imageFIleLarge")
    private MediaFile imageFIleLarge;
    @Column(name = "imageFIleLarge", insertable = false, updatable = false)
    private Integer imageFIleLargeId;
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "imageFileSmall")
    private MediaFile imageFileSmall;
    @Column(name = "imageFileSmall", insertable = false, updatable = false)
    private Integer imageFileSmallId;
    @Column(name = "isrc", columnDefinition = "char(15)")
    private String isrc;
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "label")
    private Label label;
    @Column(name = "price_currency", columnDefinition = "char(4)")
    private String price_currency;
    @ManyToOne
    @JoinColumn(name = "imgFileResolution")
    private MediaFile imgFileResolution;
    @Column(name = "imgFileResolution", insertable = false, updatable = false)
    private Integer imgFileResolutionId;
    @ManyToOne
    @JoinColumn(name = "purchasedFile")
    private MediaFile purchasedFile;
    @Column(name = "purchasedFile", insertable = false, updatable = false)
    private Integer purchasedFileId;
    @ManyToOne
    @JoinColumn(name = "audioPreviewFile")
    private MediaFile audioPreviewFile;
    @Column(name = "audioPreviewFile", insertable = false, updatable = false)
    private Integer audioPreviewFileId;
    @ManyToOne
    @JoinColumn(name = "headerPreviewFile")
    private MediaFile headerPreviewFile;
    @Column(name = "headerPreviewFile", insertable = false, updatable = false)
    private Integer headerPreviewFileId;
    @OneToMany(mappedBy = "media")
    private List<MediaLog> mediaLogs;
    @Column(name = "info", columnDefinition = "text")
    @Lob()
    private String info;
    private String iTunesUrl;
    @Column(nullable = true)
    private String amazonUrl;
    private int publishDate;
    @Column(name = "trackId", nullable = false)
    private Long trackId;
    private boolean areArtistUrls;

    public Artist getArtist() {
        return this.artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
        artistId = artist != null ?
                   artist.getI() :
                   null;
    }

    @Override
    public ItemType getType() {
        return ItemType.MEDIA;
    }

    public MediaFile getAudioFile() {
        return this.audioFile;
    }

    public void setAudioFile(MediaFile audioFile) {
        this.audioFile = audioFile;
        audioFileId = audioFile != null ?
                      audioFile.getI() :
                      null;
    }

    public Genre getGenre() {
        return this.genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
        genreId = genre != null ?
                  genre.getI() :
                  null;
    }

    public MediaFile getHeaderFile() {
        return this.headerFile;
    }

    public void setHeaderFile(MediaFile headerFile) {
        this.headerFile = headerFile;
        headerFileId = headerFile != null ?
                       headerFile.getI() :
                       null;
    }

    public MediaFile getImageFIleLarge() {
        return this.imageFIleLarge;
    }

    public void setImageFIleLarge(MediaFile imageFIleLarge) {
        this.imageFIleLarge = imageFIleLarge;
        imageFIleLargeId = imageFIleLarge != null ?
                           imageFIleLarge.getI() :
                           null;
    }

    public MediaFile getImageFileSmall() {
        return this.imageFileSmall;
    }

    public void setImageFileSmall(MediaFile imageFileSmall) {
        this.imageFileSmall = imageFileSmall;
        imageFileSmallId = imageFileSmall != null ?
                           imageFileSmall.getI() :
                           null;
    }

    public String getIsrc() {
        return this.isrc;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }

    public Label getLabel() {
        return this.label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public String getArtistName() {
        return artist.getName();
    }

    public String getGenreName() {
        return genre.getName();
    }

    public Integer getAudioSize() {
        return audioFile.getSize();
    }

    public Integer getImageLargeSize() {
        return imageFIleLarge.getSize();
    }

    public int getImageSmallSize() {
        return imageFileSmall.getSize();
    }

    public int getHeaderSize() {
        return headerFile != null ?
               headerFile.getSize() :
               0;
    }

    public String getPrice_currency() {
        return price_currency;
    }

    public void setPrice_currency(String aPriceCurrency) {
        price_currency = aPriceCurrency;
    }

    public MediaFile getImgFileResolution() {
        return imgFileResolution;
    }

    public void setImgFileResolution(MediaFile aImageFileResolution) {
        this.imgFileResolution = aImageFileResolution;
        imgFileResolutionId = aImageFileResolution != null ?
                              aImageFileResolution.getI() :
                              null;
    }

    public MediaFile getPurchasedFile() {
        return purchasedFile;
    }

    public void setPurchasedFile(MediaFile purchasedFile) {
        this.purchasedFile = purchasedFile;
        purchasedFileId = purchasedFile != null ?
                          purchasedFile.getI() :
                          null;
    }

    public MediaFile getAudioPreviewFile() {
        return audioPreviewFile;
    }

    public void setAudioPreviewFile(MediaFile audioPreviewFile) {
        this.audioPreviewFile = audioPreviewFile;
        audioPreviewFileId = audioPreviewFile != null ?
                             audioPreviewFile.getI() :
                             null;
    }

    public String getiTunesUrl() {
        return iTunesUrl;
    }

    public void setiTunesUrl(String iTunesUrl) {
        this.iTunesUrl = iTunesUrl;
    }

    public MediaFile getHeaderPreviewFile() {
        return headerPreviewFile;
    }

    public void setHeaderPreviewFile(MediaFile headerPreviewFile) {
        this.headerPreviewFile = headerPreviewFile;
        headerPreviewFileId = headerPreviewFile != null ?
                              headerPreviewFile.getI() :
                              null;
    }

    public int getArtistId() {
        return artistId;
    }

    public int getAudioFileId() {
        return audioFileId;
    }

    public int getGenreId() {
        return genreId;
    }

    public int getHeaderFileId() {
        return headerFileId;
    }

    public int getImageFIleLargeId() {
        return imageFIleLargeId;
    }

    public int getImageFileSmallId() {
        return imageFileSmallId;
    }

    public int getImgFileResolutionId() {
        return imgFileResolutionId;
    }

    public int getPurchasedFileId() {
        return purchasedFileId;
    }

    public int getAudioPreviewFileId() {
        return audioPreviewFileId;
    }

    public int getHeaderPreviewFileId() {
        return headerPreviewFileId;
    }

    public int getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(int publishDate) {
        this.publishDate = publishDate;
    }

    public List<MediaLog> getMediaLogs() {
        return mediaLogs;
    }

    public void setMediaLogs(List<MediaLog> mediaLogs) {
        this.mediaLogs = mediaLogs;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getAmazonUrl() {
        return amazonUrl;
    }

    public void setAmazonUrl(String amazonUrl) {
        this.amazonUrl = amazonUrl;
    }

    public boolean getAreArtistUrls() {
        return areArtistUrls;
    }

    public void setAreArtistUrls(boolean areArtistUrls) {
        this.areArtistUrls = areArtistUrls;
    }

    public Long getTrackId() {
        return trackId;
    }

    public void setTrackId(Long trackId) {
        this.trackId = trackId;
    }

    public String getIsrcTrackId() {
        return buildUniqueTrackId(isrc, trackId);
    }

    public Media withIsrc(String isrc) {
        setIsrc(isrc);
        return this;
    }

    public Media withTitle(String title) {
        setTitle(title);
        return this;
    }

    public Media withArtist(Artist artist) {
        setArtist(artist);
        return this;
    }

    public Media withImageFileLarge(MediaFile mediaFile) {
        setImageFIleLarge(mediaFile);
        return this;
    }

    public Media withImageFileSmall(MediaFile mediaFile) {
        setImageFileSmall(mediaFile);
        return this;
    }

    public Media withAudioFile(MediaFile mediaFile) {
        setAudioFile(mediaFile);
        return this;
    }

    public Media withGenre(Genre genre) {
        setGenre(genre);
        return this;
    }

    public Media withITunesUrl(String iTunesUrl) {
        setiTunesUrl(iTunesUrl);
        return this;
    }

    public Media withTrackId(Long trackId) {
        setTrackId(trackId);
        return this;
    }

    public Media withLabel(Label label) {
        setLabel(label);
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("artistId", artistId).append("audioFileId", audioFileId).append("genreId", genreId).append("headerFileId", headerFileId)
                                        .append("imageFIleLargeId", imageFIleLargeId).append("imageFileSmallId", imageFileSmallId).append("isrc", isrc).append("price_currency", price_currency)
                                        .append("imgFileResolutionId", imgFileResolutionId).append("purchasedFileId", purchasedFileId).append("audioPreviewFileId", audioPreviewFileId)
                                        .append("headerPreviewFileId", headerPreviewFileId).append("info", info).append("iTunesUrl", iTunesUrl).append("amazonUrl", amazonUrl)
                                        .append("publishDate", publishDate).append("trackId", trackId).append("areArtistUrls", areArtistUrls).toString();
    }
}