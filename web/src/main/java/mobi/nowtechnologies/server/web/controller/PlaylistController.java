package mobi.nowtechnologies.server.web.controller;

import java.util.Collections;
import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import mobi.nowtechnologies.server.web.dtos.PlaylistDto;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PlaylistController extends CommonController {
	
	public static final String VIEW_PLAYLIST = "playlist";
	public static final String VIEW_PLAYLIST_TRACKS = "playlist/tracks";
	
	public static final String PAGE_PLAYLIST = VIEW_PLAYLIST;
	public static final String PAGE_PLAYLIST_TRACKS = "playlist/{playlistId}/tracks";
	public static final String REDIREC_PAGE_SWAP = "redirect:swap.html";
	
	private UserService userService;
	
	private ChartService chartService;
	
	@RequestMapping(value=PAGE_PLAYLIST, method=RequestMethod.GET)
	public ModelAndView getPlaylistPage(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityURL) {
		List<ChartDetail> playlists = chartService.getChartsByCommunity(communityURL, null, ChartType.OTHER_CHART);
		
		//TODO get temparally one playlist for direct select
		User user = userService.getUserWithSelectedCharts(getSecurityContextDetails().getUserId());
		List<PlaylistDto> playlistDtos = Collections.singletonList(new PlaylistDto());
		for (ChartDetail chartDetail : playlists) {
			if((!chartDetail.getDefaultChart() && user.getSelectedCharts().size() == 0)
					|| (user.getSelectedCharts().size() > 0 && !user.getSelectedCharts().get(0).getI().equals(chartDetail.getChartId()))){
				playlistDtos.get(0).setId(chartDetail.getChartId().intValue());
				break;
			}	
		}
		
		//-------------------------------------------
		
		ModelAndView modelAndView = new ModelAndView(VIEW_PLAYLIST);
		modelAndView.addObject(PlaylistDto.NAME_LIST, playlistDtos);
			
		return modelAndView;
	}
	
	@RequestMapping(value=PAGE_PLAYLIST_TRACKS, method=RequestMethod.POST)
	public ModelAndView selectPlaylists(@PathVariable("playlistId") Integer playlistId,
			@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityURL) {
		
		chartService.selectChartByType(getSecurityContextDetails().getUserId(), playlistId);
		
		return new ModelAndView(REDIREC_PAGE_SWAP);
	}

	public void setChartService(ChartService chartService) {
		this.chartService = chartService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}