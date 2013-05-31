package mobi.nowtechnologies.server.web.controller;

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

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class PlaylistController extends CommonController {

    public static final String VIEW_PLAYLIST = "playlist";
    public static final String PAGE_PLAYLIST = "playlists/{playlistType}/playlist.html";
    public static final String JSON_PLAYLIST = "playlists/{playlistType}";
    public static final String JSON_PLAYLIST_TRACKS = "playlists/{playlistId}/tracks";
    public static final String REDIRECT_PAGE_SWAP = "redirect:/playlist/swap.html";

    private ChartDetailService chartDetailService;
    private ChartService chartService;
    private Map<String, String> env;

    @RequestMapping(value = PAGE_PLAYLIST, method = RequestMethod.GET)
    public ModelAndView getPlaylistPage(@PathVariable("playlistType") ChartType playlistType,
                                        @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityURL) {
        return new ModelAndView(VIEW_PLAYLIST)
                .addObject("playlistType", playlistType);
    }

    @RequestMapping(value = JSON_PLAYLIST, produces = "application/json", method = RequestMethod.GET)
    public ModelAndView getPlaylists(@PathVariable("playlistType") ChartType playlistType,
                                     @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityURL) throws IOException {
        List<ChartDetail> charts = chartService.getChartsByCommunity(communityURL, null, playlistType);
        return new ModelAndView()
                .addObject("playlists", PlaylistDto.toList(charts, env));
    }

    @RequestMapping(value = JSON_PLAYLIST + "/{playlistID}", produces = "application/json", method = RequestMethod.PUT)
    public ModelAndView updatePlaylist(@PathVariable("playlistID") Integer playlistId,
                                       @ModelAttribute(PlaylistDto.NAME) PlaylistDto playlist) throws IOException {
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


    public void setChartDetailService(ChartDetailService chartDetailService) {
        this.chartDetailService = chartDetailService;
    }

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }
}