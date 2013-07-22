package com.brightcove.proserve.mediaapi.wrapper;

import com.brightcove.proserve.mediaapi.wrapper.apiobjects.Image;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.Playlist;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.Video;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.*;
import com.brightcove.proserve.mediaapi.wrapper.exceptions.BrightcoveException;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 7/22/13
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class WriteApiTest {
    public static void main(String[] args) {
        if(args.length != 6){
            usage(new Exception("[ERR] Incorrect number of arguments."));
        }
        String writeToken     = args[0];
        String videoFilename  = args[1];
        Long   accountId      = new Long(args[2]);
        String thumbFilename  = args[3];
        String stillFilename  = args[4];
        Long   childAccountId = new Long(args[5]);

        System.out.println("[INF] Config:");
        System.out.println("[INF]     Write token:          '" + writeToken     + "'.");
        System.out.println("[INF]     Video filename:       '" + videoFilename  + "'.");
        System.out.println("[INF]     Account id:           '" + accountId      + "'.");
        System.out.println("[INF]     Thumbnail filename:   '" + thumbFilename  + "'.");
        System.out.println("[INF]     Video still filename: '" + stillFilename  + "'.");
        System.out.println("[INF]     Child account id:     '" + childAccountId + "'.");

        Video video     = new Video();

        // Instantiate a WriteApi wrapper object to make the actual calls
        Logger log  = Logger.getLogger("ApiTests");
        WriteApi wapi = new WriteApi(log);

        log.info("Setting up video object to write");

        // --------------------- Video Write API Methods ------------------
        Boolean createMultipleRenditions = false;
        Boolean preserveSourceRendition  = false;
        Boolean h264NoProcessing         = false;

        // ---- Required fields ----
        video.setName("this is the video name");
        video.setShortDescription("this is the short description");

        // ---- Optional fields ----
        video.setAccountId(accountId);
        video.setEconomics(EconomicsEnum.FREE);
        video.setItemState(ItemStateEnum.ACTIVE);
        video.setLinkText("Brightcove");
        video.setLinkUrl("http://www.brightcove.com");
        video.setLongDescription("this is the long description");
        video.setReferenceId("this is the reference id");
        video.setStartDate(new Date((new Date()).getTime() - 30*1000*60 )); // 30 minutes ago

        // ---- Complex (and optional) fields ----
        // End date must be in the future - add 30 minutes to "now"
        Date endDate = new Date();
        endDate.setTime(endDate.getTime() + (30*1000*60));
        video.setEndDate(endDate);

        // Geo-filtering must be combined with filtered countries
        video.setGeoFiltered(true);
        List<GeoFilterCodeEnum> geoFilteredCountries = new ArrayList<GeoFilterCodeEnum>();
        geoFilteredCountries.add(GeoFilterCodeEnum.lookupByName("UNITED STATES"));
        geoFilteredCountries.add(GeoFilterCodeEnum.CA);
        video.setGeoFilteredCountries(geoFilteredCountries);
        video.setGeoFilteredExclude(true);

        // Tags must be added as a list of strings
        List<String> tags = new ArrayList<String>();
        tags.add("tag one");
        tags.add("tag two");
        video.setTags(tags);

        // By definition, custom fields are custom and vary by account
        // Essentially though, you have a list of key-value pairs
        // List<CustomField> customFields = new ArrayList<CustomField>();
        // customFields.add(new CustomField("foo", "bar"));
        // video.setCustomFields(customFields);

        Long newVideoId = null;

        try{
            log.info("Writing video to Media API");
            newVideoId = wapi.CreateVideo(writeToken, video, videoFilename, TranscodeEncodeToEnum.FLV, createMultipleRenditions, preserveSourceRendition, h264NoProcessing);
            log.info("New video id: '" + newVideoId + "'.");
        }
        catch(BrightcoveException be){
            usage(be);
        }

        try{
            log.info("Setting up Image objects to add thumbnail and video still to newly created video.");
            Image thumbnail  = new Image();
            Image videoStill = new Image();

            thumbnail.setReferenceId("this is the thumbnail refid");
            videoStill.setReferenceId("this is the video still refid");

            thumbnail.setDisplayName("this is the thumbnail");
            videoStill.setDisplayName("this is the video still");

            thumbnail.setType(ImageTypeEnum.THUMBNAIL);
            videoStill.setType(ImageTypeEnum.VIDEO_STILL);

            log.info("Writing images to Media API");
            Boolean resizeImage = true;

            Image thumbReturn = wapi.AddImage(writeToken, thumbnail, thumbFilename, newVideoId, null, resizeImage);
            log.info("Thumbnail image: " + thumbReturn + ".");

            Image stillReturn = wapi.AddImage(writeToken, videoStill, stillFilename, newVideoId, null, resizeImage);
            log.info("Video still image: " + stillReturn + ".");
        }
        catch(BrightcoveException be){
            usage(be);
        }

        try{
            log.info("Getting status of created video");
            UploadStatusEnum status = wapi.GetUploadStatus(writeToken, newVideoId, null);
            log.info("Status: '" + status + "'.");
        }
        catch(BrightcoveException be){
            usage(be);
        }

        try{
            log.info("Sharing video out to child account");
            Boolean autoAccept   = true; // If enabled in the account, bypasses normal manual workflow
            Boolean forceReshare = true; // Forces the reshare
            List<Long> shareeAccountIds = new ArrayList<Long>();
            shareeAccountIds.add(childAccountId);
            List<Long> shareResponse = wapi.ShareVideo(writeToken, newVideoId, autoAccept, shareeAccountIds, forceReshare);
            log.info("Shared ids: '" + shareResponse + "'.");
        }
        catch(BrightcoveException be){
            usage(be);
        }

        Long       playlistId = null;
        Playlist playlist   = new Playlist();
        List<Long> videoIds   = new ArrayList<Long>();

        try{
            log.info("Setting up new playlist");
            List<String> filterTags = new ArrayList<String>();
            filterTags.add("this is a filter tag");

            videoIds.add(newVideoId);

            List<Video> videos = new ArrayList<Video>();
            videos.add(video);

            playlist.setFilterTags(filterTags);
            playlist.setName("this is the playlist name");
            playlist.setPlaylistType(PlaylistTypeEnum.EXPLICIT);
            playlist.setReferenceId("this is the playlist reference id");
            playlist.setShortDescription("this is the playlist short description");

            log.info("Creating new playlist in Media API");
            playlistId = wapi.CreatePlaylist(writeToken, playlist);
            log.info("New playlist id: '" + playlistId + "'.");
        }
        catch(BrightcoveException be){
            usage(be);
        }

        try{
            log.info("Updating the playlist with a new name and a video");
            playlist.setName("this is also the playlist name");
            playlist.setVideoIds(videoIds);
            Playlist updatedPlaylist = wapi.UpdatePlaylist(writeToken, playlist);
            log.info("Updated playlist: '" + updatedPlaylist + "'.");
        }
        catch(BrightcoveException be){
            usage(be);
        }

        try{
            log.info("Deleting playlist");
            Boolean cascadePlaylistDelete = true; // Set this to true if you'd like to delete this playlist even if it is referenced by players
            String playlistDeleteResponse = "" + wapi.DeletePlaylist(writeToken, playlistId, null, cascadePlaylistDelete);
            log.info("Response from server for delete: '" + playlistDeleteResponse + "'.");
        }
        catch(BrightcoveException be){
            usage(be);
        }

        try{
            log.info("Deleting created video");
            Boolean cascade      = true; // Deletes even if it is in use by playlists/players
            Boolean deleteShares = true; // Deletes if shared to child accounts
            String deleteResponse = "" + wapi.DeleteVideo(writeToken, newVideoId, null, cascade, deleteShares);
            log.info("Response from server for delete (no message is perfectly OK): '" + deleteResponse + "'.");
        }
        catch(BrightcoveException be){
            usage(be);
        }
    }

    public static void usage(Exception e){
        usage(e, System.out, 1);
    }

    public static void usage(Exception e, PrintStream out, Integer exitCode){
        out.println("[USE] Usage: java com.brightcove.proserve.mediaapi.wrapper.WriteApiTest <Write Api Token> <Video Filename> <Account Publisher Id> <Thumbnail Filename> <Video Still Filename> <Child Account Id>");
        e.printStackTrace(out);
        System.exit(exitCode);
    }
}
