package mobi.nowtechnologies.server.shared.dto;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 *
 */
public enum AudioResolution implements Resolution{
	
	RATE_ORIGINAL("", ""),
	RATE_48("_48", "48kpbs"),
	RATE_96("_96", "96kpbs"),
	RATE_256("_256", "256kpbs"),
	RATE_PREVIEW("P", "unknown");
	
	private String suffix;
	private String value;
	
	private AudioResolution(String suffix, String value)
	{
		this.suffix = suffix;
		this.value = value;
	}

	public String getSuffix() {
		return suffix;
	}

	@Override
	public String getValue() {
		return value;
	}
}