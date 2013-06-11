package mobi.nowtechnologies.server.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public static final String VIEW_PLAYLIST_PREVIEW = "playlist/preview";
    public static final String PAGE_PLAYLIST = "playlists/{playlistType}/playlist.html";
    public static final String JSON_PLAYLIST = "playlists/{playlistType}";
    public static final String JSON_PLAYLIST_TRACKS = "playlists/{playlistId}/tracks";

    private ChartDetailService chartDetailService;
    private ChartService chartService;
    private UserService userService;
    private Map<String, Object> env;

    @RequestMapping(value = PAGE_PLAYLIST, method = RequestMethod.GET)
    public ModelAndView getPlaylistPage(@PathVariable("playlistType") ChartType playlistType,
                                        @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityURL) {
    	User user = userService.findById(getUserId());
    	if(user.isLimited())
    		return new ModelAndView(VIEW_PLAYLIST_PREVIEW);
    	else	
    		return new ModelAndView(VIEW_PLAYLIST).addObject("playlistType", playlistType);
    }

    @RequestMapping(value = JSON_PLAYLIST, produces = "application/json", method = RequestMethod.GET)
    public ModelAndView getPlaylists(@PathVariable("playlistType") ChartType playlistType,
                                     @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityURL) throws IOException {
    	User user = userService.getUserWithSelectedCharts(getUserId());
        List<ChartDetail> charts = chartService.getChartsByCommunity(communityURL, null, playlistType);
        env.put(PlaylistDto.SELECTED_CHART_ID, user.getSelectedChartId(playlistType));
        return new ModelAndView()
                .addObject("playlists", PlaylistDto.toList(charts, env));
    }

    @RequestMapping(value = JSON_PLAYLIST + "/{playlistID}", produces = "application/json", method = RequestMethod.PUT)
    public ModelAndView updatePlaylist(@PathVariable("playlistID") Integer playlistId) throws IOException {
        chartService.selectChartByType(getUserId(), playlistId);
        List<PlaylistDto> playlists = null;
        return new ModelAndView()
                .addObject("playlists", playlists);
    }

    @RequestMapping(value = JSON_PLAYLIST_TRACKS, produces = "application/json", method = RequestMethod.GET)
    public ModelAndView getTracks(@PathVariable("playlistId") Byte playlistID) {
        List<ChartDetail> chartDetails = chartDetailService.getChartItemsByDate(playlistID, new Date(), false);
        List<TrackDto> tracks = TrackDto.toList(chartDetails, env);
        return new ModelAndView().addObject("tracks", tracks);
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

    public void setEnv(Map<String, Object> env) {
        this.env = env;
    }
}