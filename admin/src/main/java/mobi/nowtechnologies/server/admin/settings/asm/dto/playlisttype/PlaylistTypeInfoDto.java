package mobi.nowtechnologies.server.admin.settings.asm.dto.playlisttype;

public class PlaylistTypeInfoDto {

    private MetaInfo metaInfo = new MetaInfo();
    private int playTrackSeconds;
    private boolean offline;
    private TracksInfoDto skipTracks = new TracksInfoDto();
    private TracksInfoDto maxTracks = new TracksInfoDto();

    public int getPlayTrackSeconds() {
        return playTrackSeconds;
    }

    public void setPlayTrackSeconds(int playTrackSeconds) {
        this.playTrackSeconds = playTrackSeconds;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public TracksInfoDto getSkipTracks() {
        return skipTracks;
    }

    public TracksInfoDto getMaxTracks() {
        return maxTracks;
    }

    public MetaInfo getMetaInfo() {
        return metaInfo;
    }
}
