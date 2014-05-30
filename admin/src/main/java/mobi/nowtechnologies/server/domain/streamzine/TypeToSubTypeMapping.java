package mobi.nowtechnologies.server.domain.streamzine;

import mobi.nowtechnologies.server.dto.streamzine.MusicType;
import mobi.nowtechnologies.server.dto.streamzine.NewsType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.LinkLocationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum TypeToSubTypeMapping {
    MEDIA(ContentType.MUSIC,
            MusicType.TRACK, MusicType.PLAYLIST, MusicType.MANUAL_COMPILATION),
    NEWS(ContentType.NEWS,
            NewsType.LIST, NewsType.STORY),
    LINK(ContentType.PROMOTIONAL,
            LinkLocationType.EXTERNAL_AD, LinkLocationType.INTERNAL_AD);
/*    SOCIAL(ContentType.SOCIAL);*/

    private ContentType contentType;
    private List<Enum<?>> subTypes;

    TypeToSubTypeMapping(ContentType contentType, Enum<?> ... subTypes) {
        this.contentType = contentType;
        this.subTypes = (subTypes != null) ? Arrays.asList(subTypes) : Collections.<Enum<?>>emptyList();
    }

    public static TypeToSubTypeMapping find(ContentType contentType) {
        for (TypeToSubTypeMapping typeToSubTypeMapping : values()) {
            if(typeToSubTypeMapping.contentType == contentType) {
                return typeToSubTypeMapping;
            }
        }
        throw new IllegalArgumentException("Not found mapping for: " +  contentType);
    }

    public List<Enum<?>> getSubTypes() {
        return new ArrayList<Enum<?>>(subTypes);
    }
}
