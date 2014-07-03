package mobi.nowtechnologies.applicationtests.services.device.domain;

import org.springframework.util.Assert;

public class UserDeviceData {
    private HasVersion apiVersion;
    private String community;
    private String deviceType;

    public UserDeviceData(HasVersion apiVersion, String community, String deviceType) {
        Assert.notNull(apiVersion);
        Assert.notNull(community);
        Assert.notNull(deviceType);

        this.apiVersion = apiVersion;
        this.community = community;
        this.deviceType = deviceType;
    }

    public HasVersion getApiVersion() {
        return apiVersion;
    }

    public String getCommunityUrl() {
        return community;
    }

    public String getDeviceType() {
        return deviceType;
    }

    @Override
    public String toString() {
        return "UserDeviceData{" +
                "apiVersion=" + apiVersion +
                ", community='" + community + '\'' +
                ", deviceType='" + deviceType + '\'' +
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

        return true;
    }

    @Override
    public int hashCode() {
        int result = apiVersion.hashCode();
        result = 31 * result + community.hashCode();
        result = 31 * result + deviceType.hashCode();
        return result;
    }
}
