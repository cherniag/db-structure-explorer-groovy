package mobi.nowtechnologies.server.transport.controller;

import javax.servlet.http.HttpServletRequest;

import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.BonusChartDetailDto;
import mobi.nowtechnologies.server.shared.dto.ChartDetailDto;
import mobi.nowtechnologies.server.shared.dto.ChartDto;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * GetChartController
 * 
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 * 
 */
@Controller
public class GetChartController extends CommonController{

	private UserService userService;
	private ChartService chartService;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setChartService(ChartService chartService) {
		this.chartService = chartService;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/GET_CHART", "/{apiVersion:3\\.4}/GET_CHART", "/{apiVersion:3\\.4\\.0}/GET_CHART"})
	public ModelAndView getChart(
			HttpServletRequest request,
			@RequestParam("APP_VERSION") String appVersion,
			@RequestParam("COMMUNITY_NAME") String communityName,
			@RequestParam("API_VERSION") String apiVersion,
			@RequestParam("USER_NAME") String userName,
			@RequestParam("USER_TOKEN") String userToken,
			@RequestParam("TIMESTAMP") String timestamp) {
		try {
			LOGGER.info("command proccessing started");
			if (userName == null)
				throw new NullPointerException("The parameter userName is null");
			if (communityName == null)
				throw new NullPointerException("The parameter communityName is null");
			if (null == appVersion)
				throw new NullPointerException("The argument aAppVersion is null");
			if (null == apiVersion)
				throw new NullPointerException("The argument aApiVersion is null");
			if (null == userToken)
				throw new NullPointerException("The argument aUserToken is null");
			if (null == timestamp)
				throw new NullPointerException("The argument aTimestamp is null");

			String ip = Utils.getIpFromRequest(request);
			User user = userService.checkCredentials(userName, userToken,
					timestamp, communityName);

			Object[] objects = chartService.processGetChartCommand(user, communityName);
			
			for (Object object : objects) {
				if (object instanceof ChartDto) {
					ChartDto chartDto = (ChartDto) object;
					
					ChartDetailDto[] chartDetailDtos = chartDto.getChartDetailDtos();
					for (ChartDetailDto chartDetailDto : chartDetailDtos) {
						if (chartDetailDto instanceof BonusChartDetailDto){
							BonusChartDetailDto bonusChartDetailDto = (BonusChartDetailDto) chartDetailDto;

							final String songTitle = bonusChartDetailDto.getTitle();
							final String artistName = bonusChartDetailDto.getArtist();
							final String channel = bonusChartDetailDto.getChannel().replaceAll("_", " ");

							bonusChartDetailDto.setArtist(artistName + "-" + songTitle);
							bonusChartDetailDto.setTitle(channel);
						}
					}
					break;
				}
			}
			proccessRememberMeToken(objects);
			return new ModelAndView(view, Response.class.toString(), new Response(
					objects));
		} finally {
			LOGGER.info("command processing finished");
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/{apiVersion:[3-9]{1,2}\\.[4-9][0-9]{0,2}\\.[1-9]{1,3}}/GET_CHART", "/{apiVersion:[3-9]{1,2}\\.[4-9][0-9]{0,2}}/GET_CHART"})
	public ModelAndView getChart_WithChanelSplintered(
			HttpServletRequest request,
			@RequestParam("APP_VERSION") String appVersion,
			@RequestParam("COMMUNITY_NAME") String communityName,
			@RequestParam("API_VERSION") String apiVersion,
			@RequestParam("USER_NAME") String userName,
			@RequestParam("USER_TOKEN") String userToken,
			@RequestParam("TIMESTAMP") String timestamp) {
		try {
			LOGGER.info("command proccessing started");
			if (userName == null)
				throw new NullPointerException("The parameter userName is null");
			if (communityName == null)
				throw new NullPointerException("The parameter communityName is null");
			if (null == appVersion)
				throw new NullPointerException("The argument aAppVersion is null");
			if (null == apiVersion)
				throw new NullPointerException("The argument aApiVersion is null");
			if (null == userToken)
				throw new NullPointerException("The argument aUserToken is null");
			if (null == timestamp)
				throw new NullPointerException("The argument aTimestamp is null");

			User user = userService.checkCredentials(userName, userToken,
					timestamp, communityName);

			Object[] objects = chartService.processGetChartCommand(user, communityName);
			proccessRememberMeToken(objects);
			
			return new ModelAndView(view, Response.class.toString(), new Response(
					objects));
		} finally {
			LOGGER.info("command processing finished");
		}
	}
}