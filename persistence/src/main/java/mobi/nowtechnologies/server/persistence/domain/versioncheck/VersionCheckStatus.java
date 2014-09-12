package mobi.nowtechnologies.server.persistence.domain.versioncheck;

public enum VersionCheckStatus {

    CURRENT(4),
    SUGGESTED_UPDATE(3),
    FORCED_UPDATE(2),
    REVOKED(1);

    private int orderPosition;

    VersionCheckStatus(int orderPosition) {
        this.orderPosition = orderPosition;
    }

    public int getOrderPosition() {
        return orderPosition;
    }
}
