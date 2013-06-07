package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.admin.validator.MessageDtoValidator;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.shared.dto.admin.MessageDto;
import mobi.nowtechnologies.server.shared.web.utils.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@Controller
public class MessageController extends AbstractMessageController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

	@InitBinder({ MessageDto.MESSAGE_DTO })
	public void initMessageBinder(WebDataBinder binder) {
		binder.setValidator(new MessageDtoValidator());
	}

	@RequestMapping(value = "/messages", method = RequestMethod.GET)
	public ModelAndView getMessagesPage(HttpServletRequest request) {
		LOGGER.debug("input parameters request [{}]", request);

		String communityURL = RequestUtils.getCommunityURL();

		List<MessageDto> messageDtos = messageService.getMessageDtos(communityURL);

		ModelAndView modelAndView = new ModelAndView("message/messages");
		modelAndView.getModelMap().put(MessageDto.MESSAGE_DTO_LIST, messageDtos);

		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

	@RequestMapping(value = "/messages/", method = RequestMethod.GET)
	public ModelAndView getAddMessagePage(HttpServletRequest request) {
		LOGGER.debug("input parameters request [{}]", request);

		ModelAndView modelAndView = new ModelAndView("message/add");
		modelAndView.getModelMap().put(MessageDto.MESSAGE_DTO, new MessageDto());

		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

	@RequestMapping(value = "/messages/", method = RequestMethod.POST)
	public ModelAndView saveMessage(HttpServletRequest request, @Valid @ModelAttribute(MessageDto.MESSAGE_DTO) MessageDto messageDto,
			BindingResult bindingResult) {

		LOGGER.debug("input parameters request, messageDto, bindingResult: [{}], [{}], [{}]", new Object[] { request, messageDto, bindingResult });

		ModelAndView modelAndView;
		if (bindingResult.hasErrors()) {
			modelAndView = new ModelAndView("message/add");
		} else {

			String communityURL = RequestUtils.getCommunityURL();

			messageService.save(messageDto, communityURL);
			modelAndView = new ModelAndView("redirect:/messages");
		}
		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

	@RequestMapping(value = "/messages/{messageId}", method = RequestMethod.POST)
	public ModelAndView updateMessage(HttpServletRequest request, @Valid @ModelAttribute(MessageDto.MESSAGE_DTO) MessageDto messageDto,
			BindingResult bindingResult) {

		LOGGER.debug("input parameters request, messageDto, bindingResult: [{}], [{}], [{}]", new Object[] { request, messageDto, bindingResult });

		ModelAndView modelAndView;
		if (bindingResult.hasErrors()) {
			modelAndView = new ModelAndView("message/add");
		} else {

			String communityURL = RequestUtils.getCommunityURL();

			Message message = messageService.update(messageDto, communityURL);
			if (message == null) {
				modelAndView = new ModelAndView("message/add");
				messageDto.setId(null);
				modelAndView.getModelMap().put(MessageDto.MESSAGE_DTO, messageDto);
				bindingResult.addError(new ObjectError(MessageDto.MESSAGE_DTO, new String[] { "message.edit.error.couldNotFindMessage" }, null,
						"Couldn't find this message in the DB. To save it as new item click 'Save changes' button."));
			} else
				modelAndView = new ModelAndView("redirect:/messages");
		}
		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

	@RequestMapping(value = "/messages/{messageId}", method = RequestMethod.GET)
	public ModelAndView getUpdateMessagePage(HttpServletRequest request, @PathVariable("messageId") Integer messageId) {
		LOGGER.debug("input parameters request, messageId: [{}], [{}]", request, messageId);

		MessageDto messageDto = messageService.getMessageDto(messageId);

		ModelAndView modelAndView;
		if (messageDto != null) {
			modelAndView = new ModelAndView("message/edit");
			modelAndView.getModelMap().put(MessageDto.MESSAGE_DTO, messageDto);
		} else
			modelAndView = new ModelAndView("redirect:/messages");

		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

	@RequestMapping(value = "/messages/{messageId}", method = RequestMethod.DELETE)
	public ModelAndView delete(HttpServletRequest request, @PathVariable("messageId") Integer messageId) {
		LOGGER.debug("input parameters request, messageId: [{}], [{}]", request, messageId);

		messageService.delete(messageId);

		ModelAndView modelAndView = new ModelAndView("redirect:/messages");

		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

}
