package mobi.nowtechnologies.server.dto;

import java.util.*;

import javax.validation.constraints.Pattern;

import com.google.common.collect.ImmutableMap;
import mobi.nowtechnologies.server.assembler.FilterAsm;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType;
import mobi.nowtechnologies.server.shared.dto.admin.FilterDto;
import mobi.nowtechnologies.server.shared.enums.AdActionType;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import static mobi.nowtechnologies.server.shared.enums.AdActionType.NEWS;
import static mobi.nowtechnologies.server.shared.enums.AdActionType.PORTAL;
import static mobi.nowtechnologies.server.shared.enums.AdActionType.URL;
import static org.apache.commons.lang.StringUtils.substringBefore;

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
	@Pattern(regexp = ".{1,255}")
	private String message;

	private boolean activated;

	private Set<FilterDto> filterDtos;

	private MultipartFile file;

	private String imageFileName;

	private Integer position;

	private boolean removeImage;

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
		List<AdItemDto> result = new ArrayList<AdItemDto>();

		for (Message message : messages) {
			result.add(AdItemDto.toDtoItem(message));
		}
		return result;
	}

	public boolean isRemoveImage() {
		return removeImage;
	}

	public void setRemoveImage(boolean removeImage) {
		this.removeImage = removeImage;
	}

	public static AdItemDto toDtoItem(Message message) {
		AdItemDto dto = null;
		if (message != null) {
			dto = new AdItemDto();

			dto.setId(message.getId());
			dto.setAction(action(message));
			dto.setImageFileName(message.getImageFileName());
			dto.setRemoveImage(message.getImageFileName() == null || message.getImageFileName().isEmpty());
			dto.setMessage(message.getBody());
			dto.setActivated(message.isActivated());
			dto.setFilterDtos(FilterAsm.toDtos(message.getFilterWithCtiteria()));
			dto.setMessage(message.getBody());
			dto.setPosition(message.getPosition());
            dto.setActionType(actionType(message));
		}
		return dto;
	}

    static Map<String, AdActionType> actionTypeMap = ImmutableMap.<String, AdActionType>builder()
            .put("http", URL)
            .put("https", URL)
            .put("news", NEWS)
            .put("portal", PORTAL)
            .build();

    private static AdActionType actionType(Message message) {
			String title = message.getTitle();
        if(isTrackIdOrUnknownAdActionType(title)){
            return AdActionType.TRACK_ID;
			}
        return actionTypeMap.get(substringBefore(title, ":"));
		}

    private static boolean isTrackIdOrUnknownAdActionType(String title) {
        return title.indexOf(':') == -1 || !actionTypeMap.containsKey(substringBefore(title, ":"));
	}

	public static Message fromDto(AdItemDto adItemDto) {
		Message message = new Message();

		message.setId(adItemDto.getId());
		message.setActivated(adItemDto.isActivated());
		message.setBody(adItemDto.getMessage());
		message.setId(adItemDto.getId());
		message.setMessageType(MessageType.AD);
		message.setTitle(title(adItemDto));
		message.setImageFileName(adItemDto.getImageFileName());

		Integer position = adItemDto.getPosition();
		if (position != null) {
			message.setPosition(position);
		}

		return message;
	}

    static Map<AdActionType, String> titleMap = ImmutableMap.<AdActionType, String>builder()
            .put(NEWS, "news:")
            .put(PORTAL, "portal:")
            .build();

    private static String title(AdItemDto dto) {
        if(titleMap.containsKey(dto.getActionType()))
            return titleMap.get(dto.getActionType()) + dto.getAction();

        return dto.getAction();
    }

    private static String action(Message msg) {
        String title = msg.getTitle();
        for(String prefix: titleMap.values())
            if(title.startsWith(prefix))
                return StringUtils.substringAfter(title, prefix);

        return title;
	}

	@Override
	public String toString() {
		return "AdItemDto [id=" + id + ", action=" + action + ", message=" + message + ", activated=" + activated + ", imageFileName=" + imageFileName + ", actionType=" + actionType + ", filterDtos="
				+ filterDtos + ", position=" + position + ", removeImage=" + removeImage + "]";
	}

}
