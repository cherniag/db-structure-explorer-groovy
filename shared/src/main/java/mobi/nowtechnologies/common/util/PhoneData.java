package mobi.nowtechnologies.common.util;

import com.google.common.base.Preconditions;

public class PhoneData {
    private static final String PLUS = "+";

    private String mobile;

    public PhoneData(String mobile) {
        Preconditions.checkArgument(mobile != null && !mobile.isEmpty());

        this.mobile = extractDataOnlyIfNeeded(mobile);
    }

    public String getData() {
        return mobile;
    }

    public String getMobile() {
        return PLUS + mobile;
    }

    private static String extractDataOnlyIfNeeded(String mobile) {
        if(mobile.startsWith(PLUS)) {
            return mobile.substring(1);
        }
        return mobile;
    }

}
