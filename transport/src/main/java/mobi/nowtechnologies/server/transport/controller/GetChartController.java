package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.error.ThrottlingException;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.BonusChartDetailDto;
import mobi.nowtechnologies.server.shared.dto.ChartDetailDto;
import mobi.nowtechnologies.server.shared.dto.ChartDto;
import net.spy.memcached.CASMutation;
import net.spy.memcached.CASMutator;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.transcoders.IntegerTranscoder;
import net.spy.memcached.transcoders.LongTranscoder;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

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
	private MemcachedClient memcachedClient;

	@RequestMapping(method = RequestMethod.POST, value = {"/GET_CHART", "/{apiVersion:3\\.4}/GET_CHART", "/{apiVersion:3\\.4\\.0}/GET_CHART", "*/GET_CHART", "*/{apiVersion:3\\.4}/GET_CHART", "*/{apiVersion:3\\.4\\.0}/GET_CHART"})
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
	
	@RequestMapping(method = RequestMethod.POST, value = {"/{apiVersion:[3-9]{1,2}\\.[4-9][0-9]{0,2}\\.[1-9]{1,3}}/GET_CHART", "/{apiVersion:[3-9]{1,2}\\.[4-9][0-9]{0,2}}/GET_CHART", "*/{apiVersion:[3-9]{1,2}\\.[4-9][0-9]{0,2}\\.[1-9]{1,3}}/GET_CHART", "*/{apiVersion:[3-9]{1,2}\\.[4-9][0-9]{0,2}}/GET_CHART"})
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
	
	public final static String THROTTLING_HEADER = "X-Throttling-3g";
	public final static String MAX_AMOUNT_OF_REQUESTS = "max_requests_amount";
	public final static Integer MAX_AMOUNT_OF_REQUESTS_DEFAULT_VALUE = 27;
	public static final int CACHE_EXPIRE_SEC = 60;
	
	@RequestMapping(method = RequestMethod.POST, value = {"/{community:o2}/3.6/GET_CHART", "*/{community:o2}/3.6/GET_CHART"})
	public ModelAndView getChart_O2(
			HttpServletRequest request,
			@RequestParam("APP_VERSION") String appVersion,
			@RequestParam("COMMUNITY_NAME") String communityName,
			@RequestParam("API_VERSION") String apiVersion,
			@RequestParam("USER_NAME") String userName,
			@RequestParam("USER_TOKEN") String userToken,
			@RequestParam("TIMESTAMP") String timestamp,
			@RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
			@PathVariable("community") String community) {
		
		if (request.getHeader(THROTTLING_HEADER) != null && request.getHeader(THROTTLING_HEADER).equalsIgnoreCase("true")) {
			try {
				int maxRequests = getMaxAmountOfRequests();
				int i=0;
				boolean reject = false;
				do {
					if (!shouldReject(i)) {
						reject = false;
						break;
					}
					reject = true;
					i++;
				} while (i < maxRequests);
				
				if (reject)
					throw new ThrottlingException(userName, community);
			} catch (Exception e) {
				LOGGER.error("Error while making throtlling", e);
			}
		}
		
		User user = userService.checkCredentials(userName, userToken, timestamp, community, deviceUID);
		
		Object[] objects = chartService.processGetChartCommand(user, community);
		
		proccessRememberMeToken(objects);
		return new ModelAndView(view, Response.class.toString(), new Response(objects));
	}
	
	protected boolean shouldReject(final int i) throws Exception {
		final Long initial = new Long(i);
		CASMutator<Long> mutator = new CASMutator<Long>(memcachedClient, new LongTranscoder());
		CASMutation<Long> m = new CASMutation<Long>() {
			public Long getNewValue(Long current) {
				return initial.equals(current)?Long.MAX_VALUE:initial;
			}
		};
		if(mutator.cas("thread-"+i, initial, CACHE_EXPIRE_SEC, m) != Long.MAX_VALUE)
			return false;
		return true;
	}

	public int getMaxAmountOfRequests() throws Exception {
		CASMutator<Integer> mutator = new CASMutator<Integer>(memcachedClient, new IntegerTranscoder());
		CASMutation<Integer> m = new CASMutation<Integer>() {
			public Integer getNewValue(Integer current) {
				return current;
			}
		};
		return mutator.cas(MAX_AMOUNT_OF_REQUESTS, MAX_AMOUNT_OF_REQUESTS_DEFAULT_VALUE, 0, m);
	}
	
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setChartService(ChartService chartService) {
		this.chartService = chartService;
	}
	
	public void setMemcachedClient(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}
}