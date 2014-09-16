package mobi.nowtechnologies.server.persistence.domain.versioncheck;

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

    public String qualifier() {
        return qualifier;
    }

    private ClientVersion() {
    }

    public static ClientVersion from(String versionString) {
        ClientVersion ver = new ClientVersion();

        String[] parts = versionString.split("-");
        if(parts.length == 2) {
            ver.qualifier = parts[1];
        }

        String[] digits = parts[0].split("\\.");

        ver.major = Integer.parseInt(digits[0]);
        ver.minor = Integer.parseInt(digits[1]);
        if(digits.length == 3) {
            ver.revision = Integer.parseInt(digits[2]);
        }
        return ver;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + revision + "-" + qualifier;
    }
}
