/**
 * 
 */
package mobi.nowtechnologies.server.assembler;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType;
import mobi.nowtechnologies.server.shared.dto.admin.FilterDto;
import mobi.nowtechnologies.server.shared.dto.admin.NewsItemDto;

/**
 * Class for assembling News entities to NewsDto objects and vise versa
 * 
 * @author Mayboroda Dmytro
 * 
 */
public class NewsAsm {

	public static NewsItemDto toDto(Message message) {
		if (null != message) {
			NewsItemDto newsItemDto = new NewsItemDto();

			newsItemDto.setId(message.getId());
			newsItemDto.setActivated(message.isActivated());
			newsItemDto.setBody(message.getBody());
			newsItemDto.setFrequence(message.getFrequence());
			newsItemDto.setHeadline(message.getTitle());
			newsItemDto.setMessageType(message.getMessageType());
			newsItemDto.setPosition(message.getPosition());
			newsItemDto
					.setPublishTime(new Date(message.getPublishTimeMillis()));
			newsItemDto.setImageFileName(message.getImageFileName());

			Set<FilterDto> filterDtos = FilterAsm.toDtos(message.getFilterWithCtiteria());
			newsItemDto.setFilterDtos(filterDtos);

			return newsItemDto;
		}
		return null;
	}

	public static List<NewsItemDto> toDtos(List<Message> messages) {
		List<NewsItemDto> newsItemDtos = new LinkedList<NewsItemDto>();
		for (Message message : messages) {
			newsItemDtos.add(toDto(message));
		}
		return newsItemDtos;
	}
	
	public static NewsDetailDto toNewsDetailDto(Message message) {
		NewsDetailDto newsDetailDto = new NewsDetailDto();
		newsDetailDto.setDetail(message.getTitle());
		newsDetailDto.setPosition(message.getPosition());
		newsDetailDto.setBody(message.getBody());
		
		newsDetailDto.setId(message.getId());
		if (MessageType.NEWS.equals(message.getMessageType())) {
			newsDetailDto.setI(message.getPosition());
		}else{
			newsDetailDto.setI(message.getId());
		}
		newsDetailDto.setMessageFrequence(message.getFrequence());
		newsDetailDto.setMessageType(message.getMessageType());
		newsDetailDto.setTimestampMilis(message.getPublishTimeMillis());
		newsDetailDto.setImageFileName(message.getImageFileName());
		return newsDetailDto;
	}
	
	/**
	 * @param user
	 * @param messages
	 * @return
	 */
	public static List<NewsDetailDto> toNewsDetailDtos(User user, List<Message> messages) {
		if (user != null) {
			List<NewsDetailDto> newsDetailDtos = new LinkedList<NewsDetailDto>();
			for (Message message : messages) {
				final Set<AbstractFilterWithCtiteria> filterWithCtiteriaSet = message.getFilterWithCtiteria();
				boolean filtrate = true;
				for (AbstractFilterWithCtiteria abstractFilterWithCtiteria : filterWithCtiteriaSet) {
					filtrate = abstractFilterWithCtiteria.doFilter(user);
					if (filtrate == false)
						break;
				}
				if (filtrate)
					newsDetailDtos.add(toNewsDetailDto(message));
			}
			return newsDetailDtos;
		}
		return null;
	}
}