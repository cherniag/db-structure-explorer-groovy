/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.social.domain;

/**
 * Created by oar on 3/14/14.
 */
public enum GenderType {
    MALE("male"), FEMALE("female");

    private String key;

    GenderType(String key) {
        this.key = key;
    }

    public static GenderType restore(String value) {
        for (GenderType genderType : values()) {
            if (genderType.key.equals(value)) {
                return genderType;
            }
        }
        return null;
    }

    public String getKey() {
        return key;
    }
}
