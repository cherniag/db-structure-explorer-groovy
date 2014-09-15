package mobi.nowtechnologies.server.persistence.domain.versioncheck;

import org.apache.commons.lang3.math.NumberUtils;

public class ClientVersion {
    private int major;
    private int minor;
    private int revision;
    private String qualifier;

    public int major() {
        return major;
    }

    public int minor() {
        return minor;
    }

    public int revision() {
        return revision;
    }

    private ClientVersion() {
    }

    public static ClientVersion from(String versionString) {
        String[] parts = versionString.split("\\.");

        ClientVersion ver = new ClientVersion();
        ver.major = Integer.parseInt(parts[0]);
        ver.minor = Integer.parseInt(parts[1]);

        if(parts.length == 3) {
            ver.revision = ( NumberUtils.isDigits(parts[2]) ) ? Integer.parseInt(parts[2]) : 0;
        }

        if(parts.length == 3 && !NumberUtils.isDigits(parts[2])) {
            // qualifier in the third position
            ver.revision = 0;
            ver.qualifier = parts[2];
        }

        if(parts.length == 4) {
            ver.revision = 0;
            ver.qualifier = parts[3];
        }

        return ver;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + revision + "-" + qualifier;
    }
}
