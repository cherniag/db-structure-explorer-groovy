package mobi.nowtechnologies.server.persistence.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.nowtechnologies.server.persistence.domain.Operator;

/**
 * OperatorDao
 * 
 * @author Maksym Chernolevskyi (maksym)
 *
 */
public class OperatorDao {
	private static final String NOT_SPECIFIED = "Not Specified";
	private static Map<String,Operator> OPERATOR_MAP_MIGNAME_AS_KEY; 
	private static Map<Integer,Operator> OPERATOR_MAP_ID_AS_KEY;
	private static Map<Integer,String> MAP_NAME_ID_AS_KEY;
	private static Map<Integer,String> MAP_NAME_ID_AS_KEY_MIG_ONLY;
	
	private static void setEntityDao(EntityDao entityDao) {
		List<Operator> list = entityDao.findAll(Operator.class);
		Map<String,Operator> mapMigName = new HashMap<String,Operator>();
		Map<Integer,Operator> mapId = new HashMap<Integer,Operator>();
		Map<Integer,String> mapNameIdAsKey = new HashMap<Integer,String>();
		Map<Integer,String> mapNameIdAsKeyMigOnly = new HashMap<Integer,String>();
		for (Operator operator : list) {
			String migName = operator.getMigName();
			mapMigName.put(migName, operator);
			mapId.put(operator.getId(), operator);
			
			String name = operator.getName();
			if (!migName.equals(NOT_SPECIFIED)) mapNameIdAsKeyMigOnly.put(operator.getId(), name);
			mapNameIdAsKey.put(operator.getId(), name);
		}
		OPERATOR_MAP_MIGNAME_AS_KEY = Collections.unmodifiableMap(mapMigName);
		OPERATOR_MAP_ID_AS_KEY = Collections.unmodifiableMap(mapId);
		MAP_NAME_ID_AS_KEY_MIG_ONLY = Collections.unmodifiableMap(mapNameIdAsKeyMigOnly);
		MAP_NAME_ID_AS_KEY =  Collections.unmodifiableMap(mapNameIdAsKey);
	}
	
	public static Map<String,Operator> getMapAsMigNames() {
		return OPERATOR_MAP_MIGNAME_AS_KEY;
	}
	
	public static Map<Integer,Operator> getMapAsIds() {
		return OPERATOR_MAP_ID_AS_KEY;
	}
	
	public static Map<Integer,String> getMAP_NAME_ID_AS_KEY_MIG_ONLY() {
		return MAP_NAME_ID_AS_KEY_MIG_ONLY;
	}
	
	public static Map<Integer, String> getMAP_NAME_ID_AS_KEY() {
		return MAP_NAME_ID_AS_KEY;
	}
}
