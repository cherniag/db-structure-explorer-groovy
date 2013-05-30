package mobi.nowtechnologies.server.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.service.ChartDetailService;
import mobi.nowtechnologies.server.service.ChartService;
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
	public static final String PAGE_PLAYLIST = VIEW_PLAYLIST+"/{playlistType}";
	public static final String REDIREC_PAGE_SWAP = "redirect:/playlist/swap.html";
	
    private ChartDetailService chartDetailService;
	private ChartService chartService;
    private String urlToCDN;
	
	@RequestMapping(value=PAGE_PLAYLIST, method=RequestMethod.GET)
	public ModelAndView getPlaylistPage(@PathVariable("playlistType")ChartType playlistType, @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityURL) {
		ModelAndView modelAndView = new ModelAndView(VIEW_PLAYLIST);
			
		return modelAndView;
	}

    @RequestMapping(value="playlists", produces = "application/json", method=RequestMethod.GET)
    public ModelAndView getPlaylists(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityURL) throws IOException {
        List<ChartDetail> charts = chartService.getChartsByCommunity(communityURL, null, ChartType.OTHER_CHART);
        return new ModelAndView().addObject("playlists", PlaylistDto.toList(charts));
    }

    @RequestMapping(value="playlists/{playlistId}", produces = "application/json", method=RequestMethod.GET)
    public ModelAndView getTracks(@PathVariable("playlistId")Byte playlistID){
        List<ChartDetail> chartDetails = chartDetailService.getChartItemsByDate(playlistID, new Date(), false);
        List<TrackDto> tracks = TrackDto.toList(chartDetails, urlToCDN);
        return new ModelAndView().addObject("tracks", tracks);
    }
	
	@RequestMapping(value="playlists/{playlistId}", method=RequestMethod.POST)
	public ModelAndView selectPlaylists(@PathVariable("playlistId") Integer playlistId,
			@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityURL) {
		
		chartService.selectChartByType(getSecurityContextDetails().getUserId(), playlistId);
		
		return new ModelAndView(REDIREC_PAGE_SWAP);
	}

	public void setChartService(ChartService chartService) {
		this.chartService = chartService;
	}


    public void setChartDetailService(ChartDetailService chartDetailService) {
        this.chartDetailService = chartDetailService;
    }

    public void setUrlToCDN(String urlToCDN) {
        this.urlToCDN = urlToCDN;
    }
}