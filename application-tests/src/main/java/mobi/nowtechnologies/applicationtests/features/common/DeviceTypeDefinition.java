package mobi.nowtechnologies.applicationtests.features.common;

import mobi.nowtechnologies.server.persistence.domain.DeviceType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum DeviceTypeDefinition {
    IOS {
        @Override
        public Set<String> devices() {
            return Collections.unmodifiableSet(Collections.singleton(DeviceType.IOS));
        }
    },

    NOT_IOS {
        @Override
        public Set<String> devices() {
            Set<String> all = new HashSet<String>(DeviceType.getAll());
            all.removeAll(IOS.devices());
            all.remove(DeviceType.J2ME);
            all.remove(DeviceType.SYMBIAN);
            return all;
        }
    };

    public abstract Set<String> devices();

    public String toString() {
        return name() + ":" + devices();
    }
}
