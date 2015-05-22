/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.device.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * todo refactor this to enum and remove {@link DeviceTypeCache} and {@link DeviceTypeRepository}
 */
@Entity
@Table(name = "tb_deviceTypes")
public class DeviceType {

    public static final String NONE = "NONE";
    public static final String ANDROID = "ANDROID";
    public static final String J2ME = "J2ME";
    public static final String IOS = "IOS";
    public static final String WINDOWS_PHONE = "WINDOWS_PHONE";
    public static final String SYMBIAN = "SYMBIAN";
    public static final String BLACKBERRY = "BLACKBERRY";
    public static final Set<String> ALL_DEVICE_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(ANDROID, J2ME, IOS, BLACKBERRY, SYMBIAN, WINDOWS_PHONE)));

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private byte i;

    @Column(name = "name", columnDefinition = "char(25)", nullable = false)
    private String name;

    public DeviceType() {
    }

    public byte getI() {
        return this.i;
    }

    public void setI(byte i) {
        this.i = i;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DeviceType that = (DeviceType) o;

        return i == that.i && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = (int) i;
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("i", i).append("name", name).toString();
    }

}
