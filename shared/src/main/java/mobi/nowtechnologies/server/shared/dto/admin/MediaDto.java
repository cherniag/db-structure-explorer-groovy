package mobi.nowtechnologies.server.shared.dto.admin;

import mobi.nowtechnologies.server.shared.enums.ItemType;
import mobi.nowtechnologies.server.shared.enums.Label;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class MediaDto {
	
	public static final String MEDIA_DTO_LIST = "MEDIA_DTO_LIST";
	
	public static final String MEDIA_DTO = "MEDIA_DTO";

	private Integer id;
	
	private ArtistDto artistDto;

	private MediaFileDto audioFileDto;

	private MediaFileDto headerFileDto;

	private MediaFileDto imageFIleLargeDto;

	private MediaFileDto imageFileSmallDto;

	private String isrc;

	private Label label;

	private String priceCurrency;

	private MediaFileDto imgFileResolutionDto;

	private MediaFileDto purchasedFileDto;

	private MediaFileDto audioPreviewFileDto;

	private MediaFileDto headerPreviewFileDto;

	private String info;
	
	private String ITunesUrl;
	
	private String title;
	
	private BigDecimal price;
	
	private ItemType type;

	@DateTimeFormat(iso = ISO.DATE)
	private Date publishDate;

	public ArtistDto getArtistDto() {
		return artistDto;
	}

	public void setArtistDto(ArtistDto artistDto) {
		this.artistDto = artistDto;
	}

	public MediaFileDto getAudioFileDto() {
		return audioFileDto;
	}

	public void setAudioFileDto(MediaFileDto audioFileDto) {
		this.audioFileDto = audioFileDto;
	}

	public MediaFileDto getHeaderFileDto() {
		return headerFileDto;
	}

	public void setHeaderFileDto(MediaFileDto headerFileDto) {
		this.headerFileDto = headerFileDto;
	}

	public MediaFileDto getImageFIleLargeDto() {
		return imageFIleLargeDto;
	}

	public void setImageFIleLargeDto(MediaFileDto imageFIleLargeDto) {
		this.imageFIleLargeDto = imageFIleLargeDto;
	}

	public MediaFileDto getImageFileSmallDto() {
		return imageFileSmallDto;
	}

	public void setImageFileSmallDto(MediaFileDto imageFileSmallDto) {
		this.imageFileSmallDto = imageFileSmallDto;
	}

	public String getIsrc() {
		return isrc;
	}

	public void setIsrc(String isrc) {
		this.isrc = isrc;
	}

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public String getPriceCurrency() {
		return priceCurrency;
	}

	public void setPriceCurrency(String priceCurrency) {
		this.priceCurrency = priceCurrency;
	}

	public MediaFileDto getImgFileResolutionDto() {
		return imgFileResolutionDto;
	}

	public void setImgFileResolutionDto(MediaFileDto imgFileResolutionDto) {
		this.imgFileResolutionDto = imgFileResolutionDto;
	}

	public MediaFileDto getPurchasedFileDto() {
		return purchasedFileDto;
	}

	public void setPurchasedFileDto(MediaFileDto purchasedFileDto) {
		this.purchasedFileDto = purchasedFileDto;
	}

	public MediaFileDto getAudioPreviewFileDto() {
		return audioPreviewFileDto;
	}

	public void setAudioPreviewFileDto(MediaFileDto audioPreviewFileDto) {
		this.audioPreviewFileDto = audioPreviewFileDto;
	}

	public MediaFileDto getHeaderPreviewFileDto() {
		return headerPreviewFileDto;
	}

	public void setHeaderPreviewFileDto(MediaFileDto headerPreviewFileDto) {
		this.headerPreviewFileDto = headerPreviewFileDto;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getITunesUrl() {
		return ITunesUrl;
	}

	public void setITunesUrl(String iTunesUrl) {
		ITunesUrl = iTunesUrl;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public ItemType getType() {
		return type;
	}

	public void setType(ItemType type) {
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "MediaDto [artistDto=" + artistDto + ", audioFileDto=" + audioFileDto + ", audioPreviewFileDto=" + audioPreviewFileDto + ", headerFileDto="
				+ headerFileDto + ", headerPreviewFileDto=" + headerPreviewFileDto + ", ITunesUrl=" + ITunesUrl + ", id=" + id + ", imageFIleLargeDto="
				+ imageFIleLargeDto + ", imageFileSmallDto=" + imageFileSmallDto + ", imgFileResolutionDto=" + imgFileResolutionDto + ", info=" + info
				+ ", isrc=" + isrc + ", label=" + label + ", price=" + price + ", priceCurrency=" + priceCurrency + ", publishDate=" + publishDate
				+ ", purchasedFileDto=" + purchasedFileDto + ", title=" + title + ", type=" + type + "]";
	}

}
