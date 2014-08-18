package mobi.nowtechnologies.server.assembler.streamzine;

import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.DeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.InformationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.RecognizedPage;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;

public enum FeatureValueType {
    PAGE("page"), WEB("web"), CONTENT("content");

    private String id;

    public static FeatureValueType of(DeeplinkInfo deeplinkInfo) {
        if(deeplinkInfo instanceof InformationDeeplinkInfo) {
            InformationDeeplinkInfo i = (InformationDeeplinkInfo) deeplinkInfo;

            if(i.getLinkType() == LinkLocationType.INTERNAL_AD) {
                if (RecognizedPage.GENERIC_NEWS.equals(RecognizedPage.recognize(i.getUrl()))) {
                    return CONTENT;
                }
                return PAGE;
            }

            if(i.getLinkType() == LinkLocationType.EXTERNAL_AD) {
                return WEB;
            }

            throw new IllegalArgumentException("Could not decide for type: " + i.getLinkType());
        } else {
            return CONTENT;
        }
    }

    FeatureValueType(String id) {
        this.id = id;

    }

    public String getId() {
        return id;

    }
}
