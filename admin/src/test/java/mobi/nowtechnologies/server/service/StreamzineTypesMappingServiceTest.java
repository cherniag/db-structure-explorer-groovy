package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.domain.streamzine.TypesMappingInfo;
import mobi.nowtechnologies.server.domain.streamzine.TypesMappingInfoItem;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.TypeToSubTypePair;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import mobi.nowtechnologies.server.service.streamzine.StreamzineTypesMappingService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public class StreamzineTypesMappingServiceTest {

    private StreamzineTypesMappingService service = new StreamzineTypesMappingService();

    @Test
    public void testGetTypesMappingInfosCreatedForEveryShapeType() throws Exception {
        TypesMappingInfo typesMappingInfos = service.getTypesMappingInfos();
        Set<TypesMappingInfoItem> rules = typesMappingInfos.getRules();
        assertEquals(rules.size(), ShapeType.values().length);
    }

    @Test
    public void testGetTypesMappingInfosCreatedForSubtypesOfWide() throws Exception {
        final ShapeType shapeType = ShapeType.WIDE;

        Map<ContentType, List<Enum<?>>> typeWithSubtypes = find(shapeType).getTypeWithSubtypes();

        // ALL content types for NARROW:
        for (ContentType contentType : ContentType.values()) {
            assertTrue("Mapping for:" + shapeType + " does not contain: " + contentType, typeWithSubtypes.containsKey(contentType));

            List<Enum<?>> subTypes = typeWithSubtypes.get(contentType);

            // ALL sub types for every Content Type of WIDE shape Type
            List<Enum<?>> allSubTypesByContentType = TypeToSubTypePair.getAllSubTypesByContentType(contentType);
            assertTrue("[" + shapeType + "-" + contentType + "] does not contain all types of " + allSubTypesByContentType, subTypes.containsAll(allSubTypesByContentType));
        }
    }

    @Test
    public void testGetTypesMappingInfosCreatedForSubtypesOfNarrow() throws Exception {
        final ShapeType shapeType = ShapeType.NARROW;

        Map<ContentType, List<Enum<?>>> typeWithSubtypes = find(shapeType).getTypeWithSubtypes();

        // ALL content types for NARROW:
        for (ContentType contentType : ContentType.values()) {
            assertTrue("Mapping for:" + shapeType + " does not contain: " + contentType, typeWithSubtypes.containsKey(contentType));

            List<Enum<?>> subTypes = typeWithSubtypes.get(contentType);

            // ALL sub types for every Content Type of WIDE shape Type
            List<Enum<?>> allSubTypesByContentType = TypeToSubTypePair.getAllSubTypesByContentType(contentType);
            assertTrue("[" + shapeType + "-" + contentType + "] does not contain all types of " + allSubTypesByContentType, subTypes.containsAll(allSubTypesByContentType));
        }
    }

    @Test
    public void testGetTypesMappingInfosCreatedForSubtypesOfSLimBanner() throws Exception {
        final ShapeType shapeType = ShapeType.SLIM_BANNER;

        Map<ContentType, List<Enum<?>>> typeWithSubtypes = find(shapeType).getTypeWithSubtypes();

        // PROMOTIONAL content type only for SLIM_BANNER:
        final ContentType promotional = ContentType.PROMOTIONAL;

        assertEquals(1, typeWithSubtypes.size());
        assertTrue("Mapping for:" + shapeType + " does not contain: " + promotional, typeWithSubtypes.containsKey(promotional));

        List<Enum<?>> subTypes = typeWithSubtypes.get(promotional);
        // ALL link sub type only for PROMOTIONAL Type of SLIM_BANNER shape type
        subTypes.containsAll(Arrays.asList(LinkLocationType.values()));
        assertEquals(2, subTypes.size());
    }

    private TypesMappingInfoItem find(ShapeType wide) {
        TypesMappingInfo typesMappingInfos = service.getTypesMappingInfos();
        List<TypesMappingInfoItem> rules = new ArrayList<TypesMappingInfoItem>(typesMappingInfos.getRules());
        final TypesMappingInfoItem key = new TypesMappingInfoItem(wide);
        return rules.get(rules.indexOf(key));
    }
}