package mobi.nowtechnologies.server.service.streamzine.asm;

import mobi.nowtechnologies.server.dto.streamzine.mapping.TilesMappingInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.rules.BadgeMappingRules;
import mobi.nowtechnologies.server.persistence.domain.streamzine.rules.TitlesMappingRules;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.TypeToSubTypePair;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.Opener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RulesInfoAsm {
    public Map<String, Map<String, List<String>>> getBadgeMappingInfo() {
        Map<String, Map<String, List<String>>> info = new HashMap<String, Map<String, List<String>>>();

        for (BadgeMappingRules badgeMappingRules : BadgeMappingRules.values()) {
            String shapeInfo = badgeMappingRules.getShapeType().name();

            info.put(shapeInfo, toMap(badgeMappingRules.getTypePairs()));
        }

        return info;
    }

    public Map<String, TilesMappingInfo> getTitlesMappingInfo() {
        Map<String, TilesMappingInfo> infos = new HashMap<String, TilesMappingInfo>();
        for (TitlesMappingRules titlesMappingRule : TitlesMappingRules.values()) {
            TilesMappingInfo info = new TilesMappingInfo();
            info.setTitle(titlesMappingRule.isTitle());
            info.setSubTitle(titlesMappingRule.isSubTitle());

            infos.put(titlesMappingRule.getShapeType().name(), info);
        }
        return infos;
    }

    private static Map<String, List<String>> toMap(List<TypeToSubTypePair> pairs) {
        Map<String, List<String>> mapView = new HashMap<String, List<String>>();

        for (TypeToSubTypePair typeToSubTypePair : pairs) {
            final String key = typeToSubTypePair.getContentType().name();

            if(!mapView.containsKey(key)) {
                mapView.put(key, new ArrayList<String>());
            }

            mapView.get(key).add(typeToSubTypePair.getSubType().name());
        }

        return mapView;
    }

    public Map<String, String> buildTypesForOpener() {
        Map<String, String> result = new HashMap<String, String>();
        result.put(Opener.BROWSER.name(), "In Browser");
        result.put(Opener.IN_APP.name(), "In-app");
        return result;
    }
}
