package mobi.nowtechnologies.applicationtests.features.verification.phone_number;

import cucumber.api.Transformer;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DeviceTypesTransformer extends Transformer<Set<String>> {
    @Override
    public Set<String> transform(String value) {
        // parsing ...
        DeviceDefinition deviceDefinition = DeviceDefinition.valueOf(value);

        return deviceDefinition.devices();
    }

    public enum DeviceDefinition {
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
                return all;
            }
        };

        public abstract Set<String> devices();

        public String toString() {
            return name() + ":" + devices();
        }
    }
}
