package mobi.nowtechnologies.server.domain.streamzine;

import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ContentType;
import mobi.nowtechnologies.server.service.streamzine.DeepLinkInfoService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TypesMappingInfo {
    private Set<TypesMappingInfoItem> rules = new HashSet<TypesMappingInfoItem>();

    public Set<TypesMappingInfoItem> getRules() {
        return rules;
    }

    public boolean matches(DeepLinkInfoService.DeeplinkInfoData item) {
        for (TypesMappingInfoItem rule : rules) {
            if(rule.getShapeType() == item.getShapeType()) {
                for (Map.Entry<ContentType, List<Enum<?>>> contentTypeListEntry : rule.getTypeWithSubtypes().entrySet()) {
                    if(contentTypeListEntry.getKey() == item.getContentType()) {
                        for (Enum<?> anEnum : contentTypeListEntry.getValue()) {
                            if(item.getKey().equals(anEnum.name())) {
                                return true;
                            }
                        }

                    }
                }
            }
        }
        return false;
    }
}
