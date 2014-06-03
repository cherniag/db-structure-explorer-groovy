package mobi.nowtechnologies.server.persistence.domain.streamzine.types;

import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.NewsType;

import java.util.ArrayList;
import java.util.List;

public enum TypeToSubTypePair {
    MEDIA_TRACK(ContentType.MUSIC, MusicType.TRACK),
    MEDIA_PLAYLIST(ContentType.MUSIC, MusicType.PLAYLIST),
    MEDIA_COMPILATION(ContentType.MUSIC, MusicType.MANUAL_COMPILATION),

    NEWS_STORY(ContentType.NEWS, NewsType.STORY),
    NEWS_LIST(ContentType.NEWS, NewsType.LIST),

    LINK_INTERNAL_AD(ContentType.PROMOTIONAL, LinkLocationType.INTERNAL_AD),
    LINK_EXTERNAL_AD(ContentType.PROMOTIONAL, LinkLocationType.EXTERNAL_AD);

    private ContentType contentType;
    private Enum<?> subType;

    TypeToSubTypePair(ContentType contentType, Enum<?> subType) {
        this.contentType = contentType;
        this.subType = subType;
    }

    public Enum<?> getSubType() {
        return subType;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public static Enum<?> restoreSubType(ContentType contentType, String value) {
        for (TypeToSubTypePair typeToSubTypePair : values()) {
            if(typeToSubTypePair.contentType == contentType) {
                return typeToSubTypePair.subType.valueOf(typeToSubTypePair.subType.getClass(), value);
            }
        }
        throw new IllegalArgumentException("Could not restore sub type for content type: " + contentType + " and value: " + value);
    }

    public static List<Enum<?>> getAllSubTypesByContentType(ContentType contentType) {
        List<Enum<?>> subTypes = new ArrayList<Enum<?>>();

        for (TypeToSubTypePair typeToSubTypePair : values()) {
            if(typeToSubTypePair.contentType == contentType) {
                subTypes.add(typeToSubTypePair.subType);
            }
        }

        return subTypes;
    }
}
