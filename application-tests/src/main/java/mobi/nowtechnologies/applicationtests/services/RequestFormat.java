package mobi.nowtechnologies.applicationtests.services;

import java.util.*;

public enum RequestFormat {
    JSON(".json"), XML("");

    private String ext;

    RequestFormat(String ext) {
        this.ext = ext;
    }

    public boolean json() {
        return JSON == this;
    }

    public String getExt() {
        return ext;
    }

    public static Set<RequestFormat> from(Set<String> formats) {
        Set<RequestFormat> set = new HashSet<RequestFormat>();
        for (String format : formats) {
            set.add(valueOf(format));
        }
        return set;
    }
}
