package mobi.nowtechnologies.server.editor;

import mobi.nowtechnologies.server.service.versioncheck.ClientVersion;
import org.apache.commons.lang3.math.NumberUtils;

class ClientVersionImpl implements ClientVersion {
    private int major;
    private int minor;
    private int revision;
    private String qualifier;

    @Override
    public int major() {
        return major;
    }

    @Override
    public int minor() {
        return minor;
    }

    @Override
    public int revision() {
        return revision;
    }

    @Override
    public String qualifier() {
        return qualifier;
    }

    private ClientVersionImpl() {
    }

    public static ClientVersion from(String versionString) {
        String[] parts = versionString.split("\\.");

        ClientVersionImpl ver = new ClientVersionImpl();
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
}
