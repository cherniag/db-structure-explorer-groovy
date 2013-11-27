package mobi.nowtechnologies.server.shared.enums;

//TODO remove UserStatus domain and UserStatusDao class
public enum UserStatus {
	SUBSCRIBED((byte) 10),
	EULA((byte) 4),
	LOCKED((byte) 12),
	LIMITED((byte) 11);

	@Deprecated
	private final byte code;

	@Deprecated
	public byte getCode() {
		return code;
	}

	@Deprecated
	private UserStatus(byte code) {
		this.code = code;
	}
}