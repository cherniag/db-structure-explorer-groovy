package mobi.nowtechnologies.applicationtests.services.device.domain;

import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import org.springframework.util.Assert;

public class UserDeviceData implements Comparable<UserDeviceData> {
    private String apiVersion;
    private String community;
    private String deviceType;
    private RequestFormat format;
    private String qualifier;

    public UserDeviceData(String apiVersion, String community, String deviceType, RequestFormat format) {
        this(apiVersion, community, deviceType, format, "");
    }

    public UserDeviceData(String apiVersion, String community, String deviceType, RequestFormat format, String qualifier) {
        Assert.notNull(apiVersion);
        Assert.notNull(community);
        Assert.notNull(deviceType);
        Assert.notNull(format);

        this.apiVersion = apiVersion;
        this.community = community;
        this.deviceType = deviceType;
        this.format = format;
        this.qualifier = qualifier;
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
        return "[" + apiVersion + ", " + community + ", " + deviceType + ", " + format + "]";
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
        if (qualifier != null ? !qualifier.equals(that.qualifier) : that.qualifier != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = apiVersion.hashCode();
        result = 31 * result + community.hashCode();
        result = 31 * result + deviceType.hashCode();
        result = 31 * result + format.hashCode();
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(UserDeviceData o) {
        final int apiVersionCompareResult = apiVersion.compareTo(o.apiVersion);
        if(apiVersionCompareResult != 0) {
            return apiVersionCompareResult;
        } else {
            final int communityCompareResult = community.compareTo(o.community);
            if(communityCompareResult != 0) {
                return communityCompareResult;
            } else {
                final int deviceTypeCompareResult = deviceType.compareTo(o.deviceType);
                if(deviceTypeCompareResult != 0) {
                    return deviceTypeCompareResult;
                } else {
                    final int formatCompareResult = format.compareTo(o.format);
                    if(formatCompareResult != 0) {
                        return formatCompareResult;
                    } else {
                        return maskNull(qualifier).compareTo(maskNull(o.qualifier));
                    }
                }
            }
        }
    }

    private String maskNull(String v) {
        return (v == null) ? "" : v;
    }
}
