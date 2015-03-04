package mobi.nowtechnologies.server.shared.enums;

/**
 * @deprecated If possible, try to use {@link mobi.nowtechnologies.server.persistence.domain.UserStatusType} instead.
 */
@Deprecated
public enum UserStatus {
    SUBSCRIBED((byte) 10),
    EULA((byte) 4),
    LOCKED((byte) 12),
    LIMITED((byte) 11);

    @Deprecated
    private final byte code;

    @Deprecated
    private UserStatus(byte code) {
        this.code = code;
    }

    @Deprecated
    public byte getCode() {
        return code;
    }
}