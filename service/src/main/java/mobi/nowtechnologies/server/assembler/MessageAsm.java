/**
 *
 */

package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.shared.dto.admin.FilterDto;
import mobi.nowtechnologies.server.shared.dto.admin.MessageDto;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Mayboroda Dmytro
 */
public class MessageAsm {

    public static MessageDto toDto(Message message) {
        MessageDto messageDto = new MessageDto();
        messageDto.setId(message.getId());
        messageDto.setActivated(message.isActivated());
        messageDto.setBody(message.getBody());
        messageDto.setFrequence(message.getFrequence());
        messageDto.setHeadline(message.getTitle());
        messageDto.setMessageType(message.getMessageType());
        messageDto.setPosition(message.getPosition());
        messageDto.setPublishTime(new Date(message.getPublishTimeMillis()));
        messageDto.setAction(message.getAction());
        messageDto.setActionType(message.getActionType());
        messageDto.setActionButtonText(message.getActionButtonText());
        Set<FilterDto> filterDtos = FilterAsm.toDtos(message.getFilterWithCtiteria());
        messageDto.setFilterDtos(filterDtos);
        return messageDto;
    }

    public static List<MessageDto> toDtos(List<Message> messages) {
        List<MessageDto> messageDtos = new LinkedList<MessageDto>();
        for (Message message : messages) {
            messageDtos.add(toDto(message));
        }
        return messageDtos;
    }
}