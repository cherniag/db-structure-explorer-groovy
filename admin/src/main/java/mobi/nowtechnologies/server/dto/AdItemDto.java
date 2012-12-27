package mobi.nowtechnologies.server.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import mobi.nowtechnologies.server.assembler.FilterAsm;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType;
import mobi.nowtechnologies.server.shared.dto.admin.FilterDto;
import mobi.nowtechnologies.server.shared.enums.AdActionType;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class AdItemDto {

	public static final String NAME = "AD_ITEM_DTO";

	public static final String LIST = "AD_ITEM_DTO_LIST";

	private Integer id;

	private AdActionType actionType;

	@NotEmpty
	@Pattern(regexp = ".{1,255}")
	private String action;

	@NotEmpty
	@Pattern(regexp = ".{1,30}")
	private String message;

	private boolean activated;

	private Set<FilterDto> filterDtos;

	private MultipartFile file;

	private String imageFileName;

	private Integer position;

	public AdActionType getActionType() {
		return actionType;
	}

	public void setActionType(AdActionType actionType) {
		this.actionType = actionType;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getImageFileName() {
		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public static List<AdItemDto> toDtoList(Collection<Message> messages) {
		if (messages == null)
			throw new NullPointerException("The parameter messages is null");

		List<AdItemDto> adItemDtos = new ArrayList<AdItemDto>();

		for (Message message : messages) {
			adItemDtos.add(AdItemDto.toDtoItem(message));
		}

		return adItemDtos;
	}

	public static AdItemDto toDtoItem(Message message) {
		AdItemDto adItemDto = null;
		if (message != null) {
			adItemDto = new AdItemDto();

			adItemDto.setId(message.getId());
			String title = message.getTitle();
			adItemDto.setAction(title);
			adItemDto.setImageFileName(message.getImageFileName());
			adItemDto.setMessage(message.getBody());
			adItemDto.setActivated(message.isActivated());
			adItemDto.setFilterDtos(FilterAsm.toDtos(message.getFilterWithCtiteria()));
			adItemDto.setMessage(message.getBody());
			adItemDto.setPosition(message.getPosition());

			if (title.startsWith("http")) {
				adItemDto.setActionType(AdActionType.URL);
			} else {
				adItemDto.setActionType(AdActionType.ISRC);
			}
		}
		return adItemDto;
	}

	public static Message fromDto(AdItemDto adItemDto) {
		Message message = new Message();

		message.setId(adItemDto.getId());
		message.setActivated(adItemDto.isActivated());
		message.setBody(adItemDto.getMessage());
		message.setId(adItemDto.getId());
		message.setMessageType(MessageType.AD);
		message.setTitle(adItemDto.getAction());
		message.setImageFileName(adItemDto.getImageFileName());

		Integer position = adItemDto.getPosition();
		if (position != null) {
			message.setPosition(position);
		}

		return message;
	}

	@Override
	public String toString() {
		return "AdItemDto [id=" + id + ", action=" + action + ", message=" + message + ", activated=" + activated + ", imageFileName=" + imageFileName + ", actionType=" + actionType + ", filterDtos="
				+ filterDtos + ", position=" + position + "]";
	}

}
