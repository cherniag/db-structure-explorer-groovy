/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.device.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.ToStringBuilder;


//@Entity
//@Table(name = "device")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "varchar(25)", nullable = false, updatable = false)
    private String type;

    @Column(columnDefinition = "varchar(255)", nullable = false, updatable = false)
    private String uid;

    @Column(columnDefinition = "varchar(255)")
    private String deviceModel;

    protected Device() {
    }

    public Device(DeviceType deviceType, String uid, String deviceModel) {
        Preconditions.checkNotNull(type);
        this.type = deviceType.getName();
        this.uid = uid;
        this.deviceModel = deviceModel;
    }

    public int getId() {
        return id;
    }

    public DeviceType getType() {
        return DeviceTypeCache.getDeviceTypeMapNameAsKeyAndDeviceTypeValue().get(type);
    }

    public String getUid() {
        return uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Device device = (Device) o;

        return id == device.id && getType().equals(device.getType()) && uid.equals(device.uid);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + uid.hashCode();
        result = 31 * result + getType().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("type", getType()).append("uid", uid).append("deviceModel", deviceModel).toString();
    }
}
