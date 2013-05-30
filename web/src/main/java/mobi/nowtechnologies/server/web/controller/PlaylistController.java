package mobi.nowtechnologies.server.web.controller;

import java.io.IOException;
import java.util.*;

import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.service.ChartDetailService;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import mobi.nowtechnologies.server.web.dtos.PlaylistDto;

import mobi.nowtechnologies.server.web.dtos.TrackDto;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
    private ChartDetailService chartDetailService;
	private ChartService chartService;
    private String urlToCDN;
	
	@RequestMapping(value=PAGE_PLAYLIST, method=RequestMethod.GET)
	public ModelAndView getPlaylistPage(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityURL) {
		List<ChartDetail> playlists = chartService.getChartsByCommunity(communityURL, null, ChartType.OTHER_CHART);
		
		//TODO get temparally one playlist for direct select
		User user = userService.getUserWithSelectedCharts(getSecurityContextDetails().getUserId());
		List<PlaylistDto> playlistDtos = new ArrayList<PlaylistDto>();
		for (ChartDetail chartDetail : playlists) {
			if((!chartDetail.getDefaultChart() && user.getSelectedCharts().size() == 0)
					|| (user.getSelectedCharts().size() > 0 && !user.getSelectedCharts().get(0).getI().equals(chartDetail.getChartId()))){
				playlistDtos.add(new PlaylistDto(chartDetail));
				break;
			}	
		}

		ModelAndView modelAndView = new ModelAndView(VIEW_PLAYLIST);
		modelAndView.addObject(PlaylistDto.NAME_LIST, playlistDtos);
			
		return modelAndView;
	}

    @RequestMapping(value="playlists", produces = "application/json", method=RequestMethod.GET)
    public ModelAndView getPlaylists(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityURL) throws IOException {
        List<ChartDetail> charts = chartService.getChartsByCommunity(communityURL, null, ChartType.OTHER_CHART);
        return new ModelAndView().addObject("playlists", PlaylistDto.toList(charts));
    }

    @RequestMapping(value="playlists/{playlistID}", produces = "application/json", method=RequestMethod.GET)
    public ModelAndView getTracks(@PathVariable("playlistID")Byte playlistID){
        List<ChartDetail> chartDetails = chartDetailService.getChartItemsByDate(playlistID, new Date(), false);
        List<TrackDto> tracks = TrackDto.toList(chartDetails, urlToCDN);
        return new ModelAndView().addObject("tracks", tracks);
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

    public void setChartDetailService(ChartDetailService chartDetailService) {
        this.chartDetailService = chartDetailService;
    }

    public void setUrlToCDN(String urlToCDN) {
        this.urlToCDN = urlToCDN;
    }
}