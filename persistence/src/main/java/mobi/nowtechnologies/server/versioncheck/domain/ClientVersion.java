/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.versioncheck.domain;

public class ClientVersion {

    private int major;
    private int minor;
    private int revision;
    private String qualifier;

    ClientVersion() {
    }

    public static ClientVersion from(String versionString) throws IllegalArgumentException {
        ClientVersion ver = new ClientVersion();

        String[] parts = versionString.split("-");
        switch (parts.length) {
            case 2:
                ver.qualifier = parts[1];
            case 1:
                String[] digits = parts[0].split("\\.");
                try {
                    switch (digits.length) {
                        case 3:
                            ver.revision = Integer.parseInt(digits[2]);
                        case 2:
                            ver.minor = Integer.parseInt(digits[1]);
                        case 1:
                            ver.major = Integer.parseInt(digits[0]);
                        default:
                            break;
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(parts[0], e);
                }
            default:
                break;
        }
        return ver;
    }

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

    @Override
    public String toString() {
        return major + "." + minor + "." + revision + "-" + qualifier;
    }
}
