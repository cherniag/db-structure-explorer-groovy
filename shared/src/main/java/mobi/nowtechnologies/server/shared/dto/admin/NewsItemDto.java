package mobi.nowtechnologies.server.shared.dto.admin;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageFrequence;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType;
import mobi.nowtechnologies.server.validator.constraints.FileSize;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class NewsItemDto {
	
	public static final String NEWS_ITEM_DTO = "NEWS_ITEM_DTO";

	public static final String NEWS_ITEM_DTO_LIST = "NEWS_ITEM_DTO_LIST";
	
	private Integer position;
	
	private Integer id;
	
	@NotEmpty
	@Length(min=1,max=120)
	private String headline;
	
	@NotEmpty
	@Length(min=1,max=255)
	private String body;
	
	private boolean activated;
	
	private MessageFrequence frequence;

	private MessageType messageType;

	@DateTimeFormat(iso=ISO.DATE)
	private Date publishTime;
	
	private Set<FilterDto> filterDtos = new HashSet<FilterDto>();
	
	@FileSize(min=1, max=30720)
	private MultipartFile file;
	
	private String imageFileName;
	
	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public MessageFrequence getFrequence() {
		return frequence;
	}

	public void setFrequence(MessageFrequence frequence) {
		this.frequence = frequence;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public Set<FilterDto> getFilterDtos() {
		return filterDtos;
	}

	public void setFilterDtos(Set<FilterDto> filterDtos) {
		this.filterDtos = filterDtos;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getImageFileName() {
		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}

	@Override
	public String toString() {
		return "NewsItemDto [activated=" + activated + ", body=" + body + ", file=" + file + ", filterDtos=" + filterDtos + ", frequence=" + frequence + ", headline=" + headline + ", id=" + id
				+ ", messageType=" + messageType + ", position=" + position + ", publishTime=" + publishTime +", imageFileName="+ imageFileName+"]";
	}

}
