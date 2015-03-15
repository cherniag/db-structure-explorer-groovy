package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.DeviceType;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 */
public class DeviceTypeDao {

    public static final String USER_RESULT_MAP_KEY = "user";
    public static final String DEVICE_SET_RESULT_MAP_KEY = "deviceSet";
    public static final String IOS = "IOS";
    public static final String NONE = "NONE";
    public static final String ANDROID = "ANDROID";
    public static final String J2ME = "J2ME";
    public static final String BLACKBERRY = "BLACKBERRY";
    public static final String SYMBIAN = "SYMBIAN";

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceTypeDao.class.getName());

    private static EntityDao entityDao;

    private static Map<Byte, DeviceType> DEVICE_TYPE_MAP_ID_AS_KEY_AND_DEVICE_TYPE_VALUE;
    private static Map<Byte, String> DEVICE_TYPE_MAP_ID_AS_KEY;
    private static Map<String, String> DEVICE_TYPE_MAP_NAME_AS_KEY;
    private static Map<String, DeviceType> DEVICE_TYPE_MAP_NAME_AS_KEY_AND_DEVICE_TYPE_VALUE;

    private static DeviceType iOSDeviceType;
    private static DeviceType noneDeviceType;
    private static DeviceType androidDeviceType;
    private static DeviceType j2meDeviceType;
    private static DeviceType blackberryDeviceType;
    private static DeviceType symbianDeviceType;

    private static void setEntityDao(EntityDao entityDao) {
        DeviceTypeDao.entityDao = entityDao;
        List<DeviceType> deviceTypes = entityDao.findAll(DeviceType.class);
        Map<Byte, String> deviceTypeMapId = new LinkedHashMap<Byte, String>();
        Map<String, String> deviceTypeMapName = new LinkedHashMap<String, String>();
        Map<Byte, DeviceType> deviceTypeMapIdAsKeyAndDeviceTypeValue = new LinkedHashMap<Byte, DeviceType>();
        Map<String, DeviceType> deviceTypeMapNameAsKeyAndDeviceTypeValue = new LinkedHashMap<String, DeviceType>();

        for (DeviceType deviceType : deviceTypes) {
            String deviceTypeName = deviceType.getName();
            byte deviceTypeId = deviceType.getI();
            deviceTypeMapId.put(deviceTypeId, deviceTypeName);
            deviceTypeMapName.put(deviceTypeName, deviceTypeName);
            deviceTypeMapIdAsKeyAndDeviceTypeValue.put(deviceTypeId, deviceType);
            deviceTypeMapNameAsKeyAndDeviceTypeValue.put(deviceTypeName, deviceType);

            if (deviceTypeName.equals(IOS)) {
                iOSDeviceType = deviceType;
            } else if (deviceTypeName.equals(NONE)) {
                noneDeviceType = deviceType;
            } else if (deviceTypeName.equals(ANDROID)) {
                androidDeviceType = deviceType;
            } else if (deviceTypeName.equals(J2ME)) {
                j2meDeviceType = deviceType;
            } else if (deviceTypeName.equals(BLACKBERRY)) {
                blackberryDeviceType = deviceType;
            } else if (deviceTypeName.equals(SYMBIAN)) {
                symbianDeviceType = deviceType;
            }
        }

        if (iOSDeviceType == null) {
            throw new PersistenceException("Coldn't find [" + IOS + "] device type in the database");
        } else if (noneDeviceType == null) {
            throw new PersistenceException("Coldn't find [" + NONE + "] device type in the database");
        } else if (androidDeviceType == null) {
            throw new PersistenceException("Coldn't find [" + ANDROID + "] device type in the database");
        } else if (j2meDeviceType == null) {
            throw new PersistenceException("Coldn't find [" + J2ME + "] device type in the database");
        } else if (blackberryDeviceType == null) {
            throw new PersistenceException("Coldn't find [" + BLACKBERRY + "] device type in the database");
        } else if (symbianDeviceType == null) {
            throw new PersistenceException("Coldn't find [" + SYMBIAN + "] device type in the database");
        }

        DEVICE_TYPE_MAP_ID_AS_KEY = Collections.unmodifiableMap(deviceTypeMapId);
        DEVICE_TYPE_MAP_NAME_AS_KEY = Collections.unmodifiableMap(deviceTypeMapName);
        DEVICE_TYPE_MAP_ID_AS_KEY_AND_DEVICE_TYPE_VALUE = Collections.unmodifiableMap(deviceTypeMapIdAsKeyAndDeviceTypeValue);
        DEVICE_TYPE_MAP_NAME_AS_KEY_AND_DEVICE_TYPE_VALUE = Collections.unmodifiableMap(deviceTypeMapNameAsKeyAndDeviceTypeValue);
    }

    public static DeviceType getIOSDeviceType() {
        return iOSDeviceType;
    }

    public static Map<Byte, String> getDeviceTypeMapWhitIdAsKey() {
        return DEVICE_TYPE_MAP_ID_AS_KEY;
    }

    public static Map<String, String> getDeviceTypeMapWhitNameAsKey() {
        return DEVICE_TYPE_MAP_NAME_AS_KEY;
    }

    public static Map<Byte, DeviceType> getDeviceTypeMapIdAsKeyAndDeviceTypeValue() {
        return DEVICE_TYPE_MAP_ID_AS_KEY_AND_DEVICE_TYPE_VALUE;
    }

    public static Map<String, DeviceType> getDeviceTypeMapNameAsKeyAndDeviceTypeValue() {
        return DEVICE_TYPE_MAP_NAME_AS_KEY_AND_DEVICE_TYPE_VALUE;
    }

    public static DeviceType getNoneDeviceType() {
        return noneDeviceType;
    }

    public static DeviceType getAndroidDeviceType() {
        return androidDeviceType;
    }

    public static DeviceType getJ2meDeviceType() {
        return j2meDeviceType;
    }

    public static DeviceType getBlackberryDeviceType() {
        return blackberryDeviceType;
    }

    public static DeviceType getSymbianDeviceType() {
        return symbianDeviceType;
    }

}
