package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.Operator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OperatorDao
 *
 * @author Maksym Chernolevskyi (maksym)
 */
public class OperatorDao {

    private static final String NOT_SPECIFIED = "Not Specified";
    private static Map<Integer, Operator> OPERATOR_MAP_ID_AS_KEY;

    private static void setEntityDao(EntityDao entityDao) {
        List<Operator> list = entityDao.findAll(Operator.class);
        Map<String, Operator> mapMigName = new HashMap<String, Operator>();
        Map<Integer, Operator> mapId = new HashMap<Integer, Operator>();
        Map<Integer, String> mapNameIdAsKey = new HashMap<Integer, String>();
        Map<Integer, String> mapNameIdAsKeyMigOnly = new HashMap<Integer, String>();
        for (Operator operator : list) {
            String migName = operator.getMigName();
            mapMigName.put(migName, operator);
            mapId.put(operator.getId(), operator);

            String name = operator.getName();
            if (!migName.equals(NOT_SPECIFIED)) {
                mapNameIdAsKeyMigOnly.put(operator.getId(), name);
            }
            mapNameIdAsKey.put(operator.getId(), name);
        }
        OPERATOR_MAP_ID_AS_KEY = Collections.unmodifiableMap(mapId);
    }

    public static Map<Integer, Operator> getMapAsIds() {
        return OPERATOR_MAP_ID_AS_KEY;
    }

}
