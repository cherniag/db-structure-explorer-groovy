package mobi.nowtechnologies.server.shared.enums;

/**
 * Created by oar on 3/14/14.
 */
public enum Gender {
    MALE("male"), FEMALE("female");

    private String key;

    Gender(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static Gender restore(String value) {
        for (Gender gender : values()) {
            if(gender.key.equals(value)) {
                return gender;
            }
        }
        return null;
    }
}
