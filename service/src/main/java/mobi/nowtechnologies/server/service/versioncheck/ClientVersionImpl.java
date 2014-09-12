package mobi.nowtechnologies.server.service.versioncheck;

/**
 * Created by Oleg Artomov on 9/11/2014.
 */
public  class ClientVersionImpl implements ClientVersion {
    private int major;
    private int minor;
    private int revision;

    public ClientVersionImpl(int major, int minor, int revision) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
    }

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
}