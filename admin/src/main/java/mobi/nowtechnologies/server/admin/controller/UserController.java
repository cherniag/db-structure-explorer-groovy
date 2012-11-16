package mobi.nowtechnologies.server.admin.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import mobi.nowtechnologies.server.admin.validator.UserDtoValidator;
import mobi.nowtechnologies.server.assembler.UserAsm;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import mobi.nowtechnologies.server.shared.enums.UserStatus;
import mobi.nowtechnologies.server.shared.enums.UserType;
import mobi.nowtechnologies.server.shared.web.utils.RequestUtils;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@Controller
public class UserController extends AbstractCommonController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	private UserService userService;
	protected DateFormat userManagedateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@InitBinder( { UserDto.USER_DTO })
	public void initNewsBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(userManagedateTimeFormat, true));
		binder.setValidator(new UserDtoValidator());
	}

	@RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
	public ModelAndView getUser(HttpServletRequest request, @PathVariable(value = "userId") Integer userId) {
		LOGGER.debug("input parameters request, userId: [{}], [{}]", request, userId);

		User user = userService.findById(userId);
		UserDto userDto = UserAsm.toUserDto(user);

		final ModelAndView modelAndView = getEditUserModelAndView(userDto);

		LOGGER.info("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

	private ModelAndView getEditUserModelAndView(UserDto userDto) {
		final ModelAndView modelAndView = new ModelAndView("users/edit");
		modelAndView.addObject(UserDto.USER_DTO, userDto);
		modelAndView.addObject("userStatuses", UserStatus.values());
		modelAndView.addObject("userTypes", UserType.values());
		return modelAndView;
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public ModelAndView findUsers(HttpServletRequest request, @RequestParam(value = "q", required = false) String searchWords) {
		LOGGER.debug("input parameters request, searchWords: [{}], [{}]", request, searchWords);

		final ModelAndView modelAndView = new ModelAndView("users/users");
		if (searchWords != null) {
			String communityURL = RequestUtils.getCommunityURL();

			Collection<User> users = userService.findUsers(searchWords, communityURL);
			Collection<UserDto> userDtos = UserAsm.toUserDtos(users);

			modelAndView.addObject(UserDto.USER_DTO_LIST, userDtos);
			modelAndView.addObject("searchWords", searchWords);
			modelAndView.addObject("userStatuses", UserStatus.values());
			modelAndView.addObject("userTypes", UserType.values());
		}

		LOGGER.info("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

	@RequestMapping(value = "/users/{userId}", method = RequestMethod.PUT)
	public ModelAndView updateUser(HttpServletRequest request, HttpServletResponse httpServletResponse, @ModelAttribute(UserDto.USER_DTO) @Valid UserDto userDto, BindingResult bindingResult) {
		LOGGER.debug("input parameters request, httpServletResponse, userDto, bindingResult: [{}], [{}], [{}], [{}]", new Object[] { request, httpServletResponse, userDto, bindingResult });

		final ModelAndView modelAndView;
		if (!bindingResult.hasErrors()) {
			User user = userService.updateUser(userDto);
			modelAndView = new ModelAndView("redirect:/users?q=" + user.getUserName());
		} else {
			modelAndView = getEditUserModelAndView(userDto);
			httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		LOGGER.info("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

	@RequestMapping(value = "/users/{userId}/changePassword", method = RequestMethod.PUT)
	public ModelAndView changePassword(@PathVariable("userId") Integer userId, @RequestParam("password") String password) {
		LOGGER.debug("input parameters password: [{}]", new Object[] { password });

		User user = userService.changePassword(userId, password);
		ModelAndView modelAndView = new ModelAndView("redirect:/users?q=" + user.getUserName());

		LOGGER.info("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}
}
