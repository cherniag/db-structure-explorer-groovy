package mobi.nowtechnologies.server.admin.settings.asm.dto.playlisttype;

public class MetaInfo {

    private boolean tracksInfoSupported;
    private boolean tracksPlayDurationSupported;

    public boolean isTracksInfoSupported() {
        return tracksInfoSupported;
    }

    public void setTracksInfoSupported(boolean tracksInfoSupported) {
        this.tracksInfoSupported = tracksInfoSupported;
    }

    public boolean isTracksPlayDurationSupported() {
        return tracksPlayDurationSupported;
    }

    public void setTracksPlayDurationSupported(boolean tracksPlayDurationSupported) {
        this.tracksPlayDurationSupported = tracksPlayDurationSupported;
    }
}
