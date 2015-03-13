package mobi.nowtechnologies.server.shared.dto.admin;

import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageFrequence;
import mobi.nowtechnologies.server.shared.enums.MessageActionType;
import mobi.nowtechnologies.server.shared.enums.MessageType;

import java.util.Date;

/**
 * @author Titov Mykhaylo (titov)
 */
public class MessageDtoFactory {

    public static MessageDto createMessageDto() {
        MessageDto messageDto = new MessageDto();

        messageDto.setHeadline("headline");
        messageDto.setActivated(true);
        messageDto.setBody("body");
        messageDto.setFrequence(MessageFrequence.ONCE_AFTER_1ST_TRACK_DOWNLOAD);
        messageDto.setMessageType(MessageType.RICH_POPUP);
        messageDto.setPublishTime(new Date(0));
        messageDto.setPosition(Integer.MAX_VALUE);

        messageDto.setAction("action");
        messageDto.setActionType(MessageActionType.A_SPECIFIC_NEWS_STORY);
        messageDto.setActionButtonText("actionButtonText");

        return messageDto;
    }
}