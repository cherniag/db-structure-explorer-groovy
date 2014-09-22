package mobi.nowtechnologies.applicationtests.services.device.domain;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import org.springframework.util.Assert;

public class UserDeviceData {
    private String apiVersion;
    private String community;
    private String deviceType;
    private RequestFormat format;

    public UserDeviceData(String apiVersion, String community, String deviceType, RequestFormat format) {
        Assert.notNull(apiVersion);
        Assert.notNull(community);
        Assert.notNull(deviceType);
        Assert.notNull(format);

        this.apiVersion = apiVersion;
        this.community = community;
        this.deviceType = deviceType;
        this.format = format;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getCommunityUrl() {
        return community;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public RequestFormat getFormat() {
        return format;
    }

    @Override
    public String toString() {
        return "UserDeviceData{" +
                "apiVersion=" + apiVersion +
                ", community='" + community + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", format=" + format +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDeviceData that = (UserDeviceData) o;

        if (!apiVersion.equals(that.apiVersion)) return false;
        if (!community.equals(that.community)) return false;
        if (!deviceType.equals(that.deviceType)) return false;
        if (format != that.format) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = apiVersion.hashCode();
        result = 31 * result + community.hashCode();
        result = 31 * result + deviceType.hashCode();
        result = 31 * result + format.hashCode();
        return result;
    }
}
