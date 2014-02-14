package mobi.nowtechnologies.server.shared.enums;

import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;

public enum ProviderType {
    O2("o2"), NON_O2("non-o2"), VF("vf"), NON_VF("non-vf"), FACEBOOK("fb"), EMAIL("email");

    private String key;

    public static ProviderType valueOfKey(String key) {
        if (isNull(key)) {
            return null;
        }
        for (ProviderType currentType : ProviderType.values()) {
            if (currentType.key.equals(key)) {
                return currentType;
            }
        }
        throw new IllegalArgumentException("Unknown key [" + key + "]");
    }

    private ProviderType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return name();
    }
}
