package mobi.nowtechnologies.server.shared.dto.admin;

import java.util.Date;
import java.util.Set;

import javax.validation.constraints.Pattern;

import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageFrequence;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType;
import mobi.nowtechnologies.server.shared.enums.MessageActionType;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class MessageDto {
	
	public static final String MESSAGE_DTO = "MESSAGE_DTO";

	public static final String MESSAGE_DTO_LIST = "MESSAGE_DTO_LIST";
	
	private Integer position;
	
	private Integer id;
	
	@NotEmpty
	@Pattern(regexp = ".{1,255}")
	private String headline;
	
	@NotEmpty
	@Pattern(regexp = ".{1,255}")
	private String body;
	
	private boolean activated;
	
	private MessageFrequence frequence;

	private MessageType messageType;

	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS")
	private Date publishTime;
	
	private Set<FilterDto> filterDtos;
	
	private MultipartFile file;
	
	private MessageActionType actionType;
	
	private String action;
	
	private String actionButtonText;
	
	private String closeButtonText;
	
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
	
	public MessageActionType getActionType() {
		return actionType;
	}

	public void setActionType(MessageActionType actionType) {
		this.actionType = actionType;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getActionButtonText() {
		return actionButtonText;
	}

	public void setActionButtonText(String actionButtonText) {
		this.actionButtonText = actionButtonText;
	}

	public String getCloseButtonText() {
		return closeButtonText;
	}

	public void setCloseButtonText(String closeButtonText) {
		this.closeButtonText = closeButtonText;
	}

	@Override
	public String toString() {
		return "MessageDto [activated=" + activated + ", actionType=" + actionType + ", action=" + action + ", actionButtonText=" + actionButtonText + ", closeButtonText=" + closeButtonText
				+ ", body=" + body + ", file=" + file
				+ ", filterDtos=" + filterDtos + ", frequence=" + frequence
				+ ", headline=" + headline + ", id=" + id + ", messageType=" + messageType + ", position=" + position + ", publishTime=" + publishTime + "]";
	}

}
