package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.service.FilterService;
import mobi.nowtechnologies.server.service.MessageService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageFrequence;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType;
import mobi.nowtechnologies.server.shared.dto.admin.FilterDto;
import mobi.nowtechnologies.server.shared.dto.admin.MessageDto;
import mobi.nowtechnologies.server.shared.dto.admin.NewsItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.text.ParseException;
import java.util.Date;
import java.util.Set;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public abstract class AbstractMessageController extends AbstractCommonController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMessageController.class);

	protected MessageService messageService;
	protected FilterService filterService;

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	public void setFilterService(FilterService filterService) {
		this.filterService = filterService;
	}

	@InitBinder( { MessageDto.MESSAGE_DTO, NewsItemDto.NEWS_ITEM_DTO })
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Set.class, "filterDtos", new CustomCollectionEditor(Set.class) {
			protected Object convertElement(Object element) {
				FilterDto selectedfilterDto = null;
				if (element instanceof FilterDto) {
					selectedfilterDto = (FilterDto) element;
				} else if (element instanceof String) {
					Set<FilterDto> allMessageFilterDtos = populateAllMessageFilterDtos();
					for (FilterDto filterDto : allMessageFilterDtos) {
						if (filterDto.getName().equals(element)) {
							selectedfilterDto = filterDto;
							break;
						}
					}
				}
				return selectedfilterDto;
			}
		});

	}

	@ModelAttribute("allMessageFilterDtos")
	public Set<FilterDto> populateAllMessageFilterDtos() {
		return filterService.getAllFilters();
	}

	@ModelAttribute("allFrequencies")
	public MessageFrequence[] populateAllFrequencies() {
		return MessageFrequence.values();
	}

	@ModelAttribute("allMessageDtoTypes")
	public MessageType[] populateAllMessageDtoTypes() {
		return new MessageType[] { MessageType.NOTIFICATION, MessageType.POPUP };
	}
	
	protected Date validateSelectedPublishDate(String selectedPublishDate) {
		try {
			Date choosedPublishDate = dateFormat.parse(selectedPublishDate);
			return choosedPublishDate;
		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ServiceException("messages.pages.invalidSelectedPublishDateFormat", "Invalid selectedPublishDate format. It muse be in " + URL_DATE_FORMAT + " format");
		}
	}

}
