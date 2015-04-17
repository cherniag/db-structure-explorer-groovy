/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.device.domain;

import mobi.nowtechnologies.server.persistence.domain.PersistenceException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * todo: refactor {@link DeviceType} to enum and remove this
 *
 * @author Titov Mykhaylo (titov)
 */
@Deprecated
public final class DeviceTypeCache {

    private static Map<Byte, DeviceType> DEVICE_TYPE_MAP_ID_AS_KEY_AND_DEVICE_TYPE_VALUE;
    private static Map<String, DeviceType> DEVICE_TYPE_MAP_NAME_AS_KEY_AND_DEVICE_TYPE_VALUE;

    private static DeviceType iOSDeviceType;
    private static DeviceType noneDeviceType;
    private static DeviceType androidDeviceType;
    private static DeviceType j2meDeviceType;
    private static DeviceType blackberryDeviceType;
    private static DeviceType symbianDeviceType;

    private DeviceTypeCache() {
    }

    public static void setDeviceTypeRepository(DeviceTypeRepository deviceTypeRepository) {
        List<DeviceType> deviceTypes = deviceTypeRepository.findAll();
        Map<Byte, DeviceType> deviceTypeMapIdAsKeyAndDeviceTypeValue = new LinkedHashMap<>();
        Map<String, DeviceType> deviceTypeMapNameAsKeyAndDeviceTypeValue = new LinkedHashMap<>();

        for (DeviceType deviceType : deviceTypes) {
            byte i = deviceType.getI();
            String name = deviceType.getName();

            deviceTypeMapIdAsKeyAndDeviceTypeValue.put(i, deviceType);
            deviceTypeMapNameAsKeyAndDeviceTypeValue.put(name, deviceType);

            switch (name) {
                case DeviceType.IOS:
                    iOSDeviceType = deviceType;
                    break;
                case DeviceType.NONE:
                    noneDeviceType = deviceType;
                    break;
                case DeviceType.ANDROID:
                    androidDeviceType = deviceType;
                    break;
                case DeviceType.J2ME:
                    j2meDeviceType = deviceType;
                    break;
                case DeviceType.BLACKBERRY:
                    blackberryDeviceType = deviceType;
                    break;
                case DeviceType.SYMBIAN:
                    symbianDeviceType = deviceType;
                    break;
                default:
                    break;
            }
        }

        if (iOSDeviceType == null) {
            throw new PersistenceException("Couldn't find [" + DeviceType.IOS + "] device type in the database");
        } else if (noneDeviceType == null) {
            throw new PersistenceException("Couldn't find [" + DeviceType.NONE + "] device type in the database");
        } else if (androidDeviceType == null) {
            throw new PersistenceException("Couldn't find [" + DeviceType.ANDROID + "] device type in the database");
        } else if (j2meDeviceType == null) {
            throw new PersistenceException("Couldn't find [" + DeviceType.J2ME + "] device type in the database");
        } else if (blackberryDeviceType == null) {
            throw new PersistenceException("Couldn't find [" + DeviceType.BLACKBERRY + "] device type in the database");
        } else if (symbianDeviceType == null) {
            throw new PersistenceException("Couldn't find [" + DeviceType.SYMBIAN + "] device type in the database");
        }

        DEVICE_TYPE_MAP_ID_AS_KEY_AND_DEVICE_TYPE_VALUE = Collections.unmodifiableMap(deviceTypeMapIdAsKeyAndDeviceTypeValue);
        DEVICE_TYPE_MAP_NAME_AS_KEY_AND_DEVICE_TYPE_VALUE = Collections.unmodifiableMap(deviceTypeMapNameAsKeyAndDeviceTypeValue);
    }

    public static Map<Byte, DeviceType> getDeviceTypeMapIdAsKeyAndDeviceTypeValue() {
        return DEVICE_TYPE_MAP_ID_AS_KEY_AND_DEVICE_TYPE_VALUE;
    }

    public static Map<String, DeviceType> getDeviceTypeMapNameAsKeyAndDeviceTypeValue() {
        return DEVICE_TYPE_MAP_NAME_AS_KEY_AND_DEVICE_TYPE_VALUE;
    }

    public static DeviceType getIOSDeviceType() {
        return iOSDeviceType;
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

}
