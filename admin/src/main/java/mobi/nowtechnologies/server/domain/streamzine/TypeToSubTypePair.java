package mobi.nowtechnologies.server.domain.streamzine;

import mobi.nowtechnologies.server.dto.streamzine.MusicType;
import mobi.nowtechnologies.server.dto.streamzine.NewsType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.LinkLocationType;

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
