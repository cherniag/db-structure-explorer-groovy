package mobi.nowtechnologies.server.assembler.streamzine;

import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.*;

public enum ContentSubType {
    NEWS_LIST("news"),
    NEWS_STORY("story"),
    PLAYLIST("playlist"),
    MUSIC_TRACK("track"),
    NOT_SUPPORTED("");

    private String name;

    ContentSubType(String name) {
        this.name = name;
    }

    public static ContentSubType of(DeeplinkInfo info) {
        if(info instanceof MusicPlayListDeeplinkInfo) {
            return PLAYLIST;
        }

        if(info instanceof MusicTrackDeeplinkInfo) {
            return MUSIC_TRACK;
        }

        if(info instanceof NewsListDeeplinkInfo) {
            return NEWS_LIST;
        }

        if(info instanceof NewsStoryDeeplinkInfo) {
            return NEWS_STORY;
        }

        return NOT_SUPPORTED;
    }

    public String getName() {
        return name;
    }
}
