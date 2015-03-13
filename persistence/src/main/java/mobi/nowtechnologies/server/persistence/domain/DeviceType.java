package mobi.nowtechnologies.server.persistence.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.builder.ToStringBuilder;


@Entity
@Table(name = "tb_deviceTypes")
public class DeviceType implements Serializable {

    public static final String NONE = "NONE";
    public static final String ANDROID = "ANDROID";
    public static final String J2ME = "J2ME";
    public static final String IOS = "IOS";
    public static final String BLACKBERRY = "BLACKBERRY";
    public static final String SYMBIAN = "SYMBIAN";
    public static final String WINDOWS_PHONE = "WINDOWS_PHONE";
    private static final long serialVersionUID = 1L;
    private byte i;
    private String name;

    public DeviceType() {
    }

    public static Set<String> getAll() {
        return Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(ANDROID, J2ME, IOS, BLACKBERRY, SYMBIAN, WINDOWS_PHONE)));
    }

    public static Set<String> all() {
        return Sets.newHashSet(ANDROID, J2ME, IOS, BLACKBERRY, SYMBIAN, WINDOWS_PHONE);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public byte getI() {
        return this.i;
    }

    public void setI(byte i) {
        this.i = i;
    }

    @Column(name = "name", columnDefinition = "char(25)")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ?
                                   0 :
                                   name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DeviceType)) {
            return false;
        }
        DeviceType other = (DeviceType) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("i", i).append("name", name).toString();
    }

    public static enum Fields {
        name();
    }


}