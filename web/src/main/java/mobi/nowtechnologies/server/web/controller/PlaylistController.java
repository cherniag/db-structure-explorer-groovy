package mobi.nowtechnologies.server.web.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.ChartDetailService;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import mobi.nowtechnologies.server.web.dtos.PlaylistDto;
import mobi.nowtechnologies.server.web.dtos.TrackDto;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PlaylistController extends CommonController {
	
	public static final String VIEW_PLAYLIST = "playlist";
	public static final String PAGE_PLAYLIST = "playlists/{playlistType}/playlist.html";
	public static final String JSON_PLAYLIST = "playlists/{playlistType}";
	public static final String JSON_PLAYLIST_TRACKS = "playlists/{playlistId}/tracks";
	public static final String REDIRECT_PAGE_SWAP = "redirect:/playlist/swap.html";
	
    private ChartDetailService chartDetailService;
	private ChartService chartService;
	private UserService userService;
    private String urlToCDN;
	
	@RequestMapping(value=PAGE_PLAYLIST, method=RequestMethod.GET)
	public ModelAndView getPlaylistPage(@PathVariable("playlistType")ChartType playlistType, @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityURL) {
		
		List<ChartDetail> playlists = chartService.getChartsByCommunity(communityURL, null, playlistType);
		
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
		modelAndView.addObject("playlistType", playlistType);
		modelAndView.addObject(PlaylistDto.NAME_LIST, playlistDtos);
		
		return modelAndView;
	}

    @RequestMapping(value=JSON_PLAYLIST, produces = "application/json", method=RequestMethod.GET)
    public ModelAndView getPlaylists(@PathVariable("playlistType")ChartType playlistType, @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityURL) throws IOException {
        List<ChartDetail> charts = chartService.getChartsByCommunity(communityURL, null, playlistType);
        return new ModelAndView().addObject("playlists", PlaylistDto.toList(charts));
    }

    @RequestMapping(value=JSON_PLAYLIST_TRACKS, produces = "application/json", method=RequestMethod.GET)
    public ModelAndView getTracks(@PathVariable("playlistId")Byte playlistID){
        List<ChartDetail> chartDetails = chartDetailService.getChartItemsByDate(playlistID, new Date(), false);
        List<TrackDto> tracks = TrackDto.toList(chartDetails, urlToCDN);
        return new ModelAndView().addObject("tracks", tracks);
    }
	
	@RequestMapping(value=JSON_PLAYLIST_TRACKS, method=RequestMethod.POST)
	public ModelAndView selectPlaylists(@PathVariable("playlistId") Integer playlistId,
			@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityURL) {
		
		chartService.selectChartByType(getSecurityContextDetails().getUserId(), playlistId);
		
		return new ModelAndView(REDIRECT_PAGE_SWAP);
	}

	public void setChartService(ChartService chartService) {
		this.chartService = chartService;
	}

    public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setChartDetailService(ChartDetailService chartDetailService) {
        this.chartDetailService = chartDetailService;
    }

    public void setUrlToCDN(String urlToCDN) {
        this.urlToCDN = urlToCDN;
    }
}