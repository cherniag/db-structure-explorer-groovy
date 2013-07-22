package com.brightcove.proserve.mediaapi.wrapper;

import com.brightcove.proserve.mediaapi.wrapper.apiobjects.Playlist;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.Playlists;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.Video;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.Videos;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.*;
import com.brightcove.proserve.mediaapi.wrapper.exceptions.BrightcoveException;
import com.brightcove.proserve.mediaapi.wrapper.exceptions.ExceptionType;
import com.brightcove.proserve.mediaapi.wrapper.exceptions.MediaApiException;
import com.brightcove.proserve.mediaapi.wrapper.utils.CollectionUtils;

import java.io.PrintStream;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 7/22/13
 * Time: 9:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReadApiTestIT {
    public static void main(String[] args) {
        Logger log = Logger.getLogger("ApiTests");

        if(args.length != 14){
            usage(new Exception("[ERR] Incorrect number of arguments."));
        }
        String readToken            = args[0];
        Long   videoId              = new Long(args[1]);
        Long   videoId2             = new Long(args[2]);
        String referenceId          = args[3];
        String referenceId2         = args[4];
        String userId               = args[5];
        String campaignId           = args[6];
        String searchText           = args[7];
        String searchTag            = args[8];
        Long   playlistId           = new Long(args[9]);
        Long   playlistId2          = new Long(args[10]);
        String playlistReferenceId  = args[11];
        String playlistReferenceId2 = args[12];
        String playerId             = args[13];

        log.info("Configuration:" +
                "\tRead token:             '" + readToken            + "'." +
                "\tVideo id:               '" + videoId              + "'." +
                "\tVideo id 2:             '" + videoId2             + "'." +
                "\tReference id:           '" + referenceId          + "'." +
                "\tReference id 2:         '" + referenceId2         + "'." +
                "\tUser id:                '" + userId               + "'." +
                "\tCampaign id:            '" + campaignId           + "'." +
                "\tSearch text:            '" + searchText           + "'." +
                "\tSearch tag:             '" + searchTag            + "'." +
                "\tPlaylist id:            '" + playlistId           + "'." +
                "\tPlaylist id2:           '" + playlistId2          + "'." +
                "\tPlaylist reference id:  '" + playlistReferenceId  + "'." +
                "\tPlaylist reference id2: '" + playlistReferenceId2 + "'." +
                "\tPlayer id:              '" + playerId             + "'.");

        // Sets of Playlist/Video/Reference Ids are needed for some Media API calls that look up
        // multiple videos at once
        Set<Long> videoIds = CollectionUtils.CreateEmptyLongSet();
        videoIds.add(videoId);
        videoIds.add(videoId2);

        Set<String> referenceIds = CollectionUtils.CreateEmptyStringSet();
        referenceIds.add(referenceId);
        referenceIds.add(referenceId2);

        Set<Long> playlistIds = CollectionUtils.CreateEmptyLongSet();
        playlistIds.add(playlistId);
        playlistIds.add(playlistId2);

        Set<String> playlistReferenceIds = CollectionUtils.CreateEmptyStringSet();
        playlistReferenceIds.add(playlistReferenceId);
        playlistReferenceIds.add(playlistReferenceId2);

        // Video fields determine which standard fields to fill out on returned videos
        EnumSet<VideoFieldEnum> videoFields = VideoFieldEnum.CreateFullEnumSet();

        EnumSet<VideoFieldEnum> partialVideoFields = VideoFieldEnum.CreateEmptyEnumSet();
        partialVideoFields.add(VideoFieldEnum.ID);
        partialVideoFields.add(VideoFieldEnum.NAME);

        // Custom fields determine which custom fields to fill out on returned videos
        Set<String> customFields = CollectionUtils.CreateEmptyStringSet();

        // Playlist fields determine which fields to fill out on returned playlists
        EnumSet<PlaylistFieldEnum> playlistFields = PlaylistFieldEnum.CreateFullEnumSet();

        // Video state filters determine which videos to return on a FindModifiedVideos call
        Set<VideoStateFilterEnum> videoStateFilters = VideoStateFilterEnum.CreateEmptySet();
        videoStateFilters.add(VideoStateFilterEnum.PLAYABLE);

        // Pick a date 30 days in the past to use for time-based calls (e.g. FindModifiedVideos)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -30);
        Long thirtyDaysAgo = cal.getTimeInMillis() / 1000 / 60; // Time in minutes, not milliseconds

        // And/or tags for searching
        Set<String> andTags = CollectionUtils.CreateEmptyStringSet();
        andTags.add(searchTag);

        Set<String> orTags = CollectionUtils.CreateEmptyStringSet();

        // Instantiate a ReadApi wrapper object to make the actual calls
        ReadApi rapi = new ReadApi(log);

        // --------------------- Video Read API Methods ------------------

        // First example will also demonstrate retry logic
        Boolean retry = true;
        Integer maxAttempts      = 10;
        Integer currentAttempt   = 0;
        Long    retryPauseMillis = 15000l; // 15 seconds
        while(retry){
            currentAttempt++;

            try{
                log.info("Looking up a single video - id '" + videoId + "'.");
                Video video = rapi.FindVideoById(readToken, videoId, videoFields, customFields);
                log.info("Video: '" + video + "'.");
                log.info("----------------------------------------");

                retry = false;
            }
            catch(BrightcoveException be){
                log.severe("Exception caught: " + be + ".");

                log.info("    Request failed, checking for retry.");
                if(be.getType().equals(ExceptionType.MEDIA_API_EXCEPTION)){
                    log.info("        Exception was thrown by the Media API.  Checking to see if it was a timeout error.");
                    Integer exceptionCode = ((MediaApiException)be).getResponseCode();
                    if((exceptionCode != null) && (exceptionCode == 103)){
                        log.info("            Exception was a timeout (code 103).");
                        if(currentAttempt >= maxAttempts){
                            log.info("                Maximum number of attempts reached.  No retry will be attempted.");
                            retry = false;
                        }
                        else{
                            log.info("                Pausing " + retryPauseMillis + " milliseconds and retrying the request.");
                            try{
                                Thread.sleep(retryPauseMillis);
                            }
                            catch(InterruptedException ie){}
                        }
                    }
                    else{
                        log.info("        Exception was not a timeout (code " + exceptionCode + ").  No retry will be attempted.");
                        retry = false;
                    }
                }
                else{
                    log.info("        Exception was thrown by the Wrapper or a run time Java exception.  No retry will be attempted.");
                    retry = false;
                }
            }
        }


        // The rest of the examples will not implement any retry logic

        try{
            log.info("Looking up first page of all videos in account.");
            Videos videos = rapi.FindAllVideos(readToken, 100, 0, SortByTypeEnum.MODIFIED_DATE, SortOrderTypeEnum.DESC, videoFields, customFields);
            for(Video video : videos){
                log.info("Video: '" + video + "'.");
                log.info("----------------------------------------");
            }
            log.info("First page returned " + videos.size() + " videos.  Account contains " + videos.getTotalCount() + " active videos.");
        }
        catch(BrightcoveException be){
            log.severe("Exception caught: " + be + ".");
        }

        Boolean earlyExit = true;
        if(earlyExit == true){
            log.info("Exiting early - skipping the rest of the tests.");
            System.exit(1);
        }

        try{
            log.info("Looking up related videos for video id '" + videoId + "'.");
            Videos videos = rapi.FindRelatedVideos(readToken, videoId, null, 100, 0, videoFields, customFields);
            for(Video video : videos){
                log.info("Video: '" + video + "'.");
                log.info("----------------------------------------");
            }
        }
        catch(BrightcoveException be){
            log.severe("Exception caught: " + be + ".");
        }

        try{
            log.info("Looking up videos by multiple ids - '" + videoIds + "'.");
            Videos videos = rapi.FindVideosByIds(readToken, videoIds, videoFields, customFields);
            for(Video video : videos){
                log.info("Video: '" + video + "'.");
                log.info("----------------------------------------");
            }
        }
        catch(BrightcoveException be){
            log.severe("Exception caught: " + be + ".");
        }

        try{
            log.info("Looking up video by reference id - '" + referenceId + "'.");
            Video video = rapi.FindVideoByReferenceId(readToken, referenceId, videoFields, customFields);
            log.info("Video: '" + video + "'.");
            log.info("----------------------------------------");
        }
        catch(BrightcoveException be){
            log.severe("Exception caught: " + be + ".");
        }

        try{
            log.info("Looking up multiple videos by reference ids - '" + referenceIds + "'.");
            Videos videos = rapi.FindVideosByReferenceIds(readToken, referenceIds, videoFields, customFields);
            for(Video video : videos){
                log.info("Video: '" + video + "'.");
                log.info("----------------------------------------");
            }
        }
        catch(BrightcoveException be){
            log.severe("Exception caught: " + be + ".");
        }

        try{
            log.info("Looking up videos by user id - '" + userId + "'.");
            Videos videos = rapi.FindVideosByUserId(readToken, userId, 100, 0, SortByTypeEnum.MODIFIED_DATE, SortOrderTypeEnum.DESC, videoFields, customFields);
            for(Video video : videos){
                log.info("Video: '" + video + "'.");
                log.info("----------------------------------------");
            }
        }
        catch(BrightcoveException be){
            log.severe("Exception caught: " + be + ".");
        }

        try{
            log.info("Looking up videos by campaign id - '" + campaignId + "'.");
            Videos videos = rapi.FindVideosByCampaignId(readToken, campaignId, 100, 0, SortByTypeEnum.MODIFIED_DATE, SortOrderTypeEnum.DESC, videoFields, customFields);
            for(Video video : videos){
                log.info("Video: '" + video + "'.");
                log.info("----------------------------------------");
            }
        }
        catch(BrightcoveException be){
            log.severe("Exception caught: " + be + ".");
        }

        try{
            log.info("Looking up videos modified in the last 30 days.");
            Videos videos = rapi.FindModifiedVideos(readToken, thirtyDaysAgo, videoStateFilters, 100, 0, SortByTypeEnum.MODIFIED_DATE, SortOrderTypeEnum.DESC, videoFields, customFields);
            for(Video video : videos){
                log.info("Video: '" + video + "'.");
                log.info("----------------------------------------");
            }
        }
        catch(BrightcoveException be){
            log.severe("Exception caught: " + be + ".");
        }

        try{
            log.info("Looking up videos by text search for '" + searchText + "'.");
            Videos videos = rapi.FindVideosByText(readToken, searchText, 100, 0, videoFields, customFields);
            for(Video video : videos){
                log.info("Video: '" + video + "'.");
                log.info("----------------------------------------");
            }
        }
        catch(BrightcoveException be){
            log.severe("Exception caught: " + be + ".");
        }

        try{
            log.info("Looking up videos by tags '" + andTags + "' '" + orTags + "'.'");
            Videos videos = rapi.FindVideosByTags(readToken, andTags, orTags, 100, 0, SortByTypeEnum.MODIFIED_DATE, SortOrderTypeEnum.DESC, videoFields, customFields);
            for(Video video : videos){
                log.info("Video: '" + video + "'.");
                log.info("----------------------------------------");
            }
        }
        catch(BrightcoveException be){
            log.severe("Exception caught: " + be + ".");
        }

        // --------------------- Playlist Read API Methods ---------------

        try{
            log.info("Looking up first page of all playlists.");
            Playlists playlists = rapi.FindAllPlaylists(readToken, 100, 0, SortByTypeEnum.MODIFIED_DATE, SortOrderTypeEnum.DESC, videoFields, customFields, playlistFields);
            for(Playlist playlist : playlists){
                log.info("Playlist: '" + playlist + "'.");
                log.info("----------------------------------------");
            }
        }
        catch(BrightcoveException be){
            log.severe("Exception caught: " + be + ".");
        }

        try{
            log.info("Looking up playlist by playlist id - '" + playlistId + "'.");
            Playlist playlist = rapi.FindPlaylistById(readToken, playlistId, videoFields, customFields, playlistFields);
            log.info("Playlist: '" + playlist + "'.");
            log.info("----------------------------------------");
        }
        catch(BrightcoveException be){
            log.severe("Exception caught: " + be + ".");
        }

        try{
            log.info("Looking up playlist by reference id - '" + playlistReferenceId + "'.");
            Playlist playlist = rapi.FindPlaylistByReferenceId(readToken, playlistReferenceId, videoFields, customFields, playlistFields);
            log.info("Playlist: '" + playlist + "'.");
            log.info("----------------------------------------");
        }
        catch(BrightcoveException be){
            log.severe("Exception caught: " + be + ".");
        }

        try{
            log.info("Looking up multiple playlists by ids - '" + playlistIds + "'.");
            Playlists playlists = rapi.FindPlaylistsByIds(readToken, playlistIds, videoFields, customFields, playlistFields);
            for(Playlist playlist : playlists){
                log.info("Playlist: '" + playlist + "'.");
                log.info("----------------------------------------");
            }
        }
        catch(BrightcoveException be){
            log.severe("Exception caught: " + be + ".");
        }

        try{
            log.info("Looking up multiple playlists by reference ids - '" + playlistReferenceIds + "'.");
            Playlists playlists = rapi.FindPlaylistsByReferenceIds(readToken, playlistReferenceIds, videoFields, customFields, playlistFields);
            for(Playlist playlist : playlists){
                log.info("Playlist: '" + playlist + "'.");
                log.info("----------------------------------------");
            }
        }
        catch(BrightcoveException be){
            log.severe("Exception caught: " + be + ".");
        }

        try{
            log.info("Looking up playlists for player id '" + playerId + "'.");
            Playlists playlists = rapi.FindPlaylistsForPlayerId(readToken, playerId, 100, 0, videoFields, customFields, playlistFields);
            for(Playlist playlist : playlists){
                log.info("Playlist: '" + playlist + "'.");
                log.info("----------------------------------------");
            }
        }
        catch(BrightcoveException be){
            log.severe("Exception caught: " + be + ".");
        }
    }

    public static void usage(Exception e){
        usage(e, System.out, 1);
    }

    public static void usage(Exception e, PrintStream out, Integer exitCode){
        out.println("[USE] Usage: java com.brightcove.proserve.mediaapi.wrapper.ReadTests <read api token> <video id 1> <video id 2> <reference id 1> <reference id 2> <user id> <campaign id> <search text> <search tag> <playlist id 1> <playlist id 2> <playlist reference id 1> <playlist reference id 2> <player id>");

        e.printStackTrace(out);
        System.exit(exitCode);
    }
}
