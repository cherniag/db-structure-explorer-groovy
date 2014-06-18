package mobi.nowtechnologies.server.service.streamzine.asm;

import mobi.nowtechnologies.server.dto.streamzine.mapping.TilesMappingInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.rules.BadgeMappingRules;
import mobi.nowtechnologies.server.persistence.domain.streamzine.rules.TitlesMappingRules;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.TypeToSubTypePair;

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

    public List<TilesMappingInfo> getTitlesMappingInfo() {
        List<TilesMappingInfo> infos = new ArrayList<TilesMappingInfo>();
        for (TitlesMappingRules titlesMappingRule : TitlesMappingRules.values()) {
            TilesMappingInfo info = new TilesMappingInfo();
            info.setShapeType(titlesMappingRule.getShapeType().name());
            info.setTitle(titlesMappingRule.isTitle());
            info.setSubTitle(titlesMappingRule.isSubTitle());
            infos.add(info);
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
}
