package mobi.nowtechnologies.server.assembler.streamzine;

import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.NewsType;

public enum ContentSubType {
    NEWS_LIST("news", NewsType.LIST),
    NEWS_STORY("story", NewsType.STORY),
    PLAYLIST("playlist", MusicType.PLAYLIST),
    MUSIC_TRACK("track", MusicType.TRACK),
    NOT_SUPPORTED("", null);

    private String name;
    private Enum<?> subType;

    ContentSubType(String name, Enum<?> subType) {
        this.name = name;
        this.subType = subType;
    }

    public static ContentSubType of(Enum<?> subType) {
        for (ContentSubType contentSubType : values()) {
            if (contentSubType.subType == subType) {
                return contentSubType;
            }
        }
        return NOT_SUPPORTED;
    }

    public String getName() {
        return name;
    }
}
