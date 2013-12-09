package mobi.nowtechnologies.o2;


/**
 *
 * Configs for O2 in different environments
 *
 */
public enum O2Config {

	PROD(
			"src/test/resources/prod/o2_soa_keystore.jks",
			"Fb320p",
			"G8ujtuheTUAaRFe",
			"https://prod.mqapi.com",
			"https://sdpapi.o2.co.uk/services/"),

	QA(
			"../service/src/main/resources/META-INF/keystore.jks",
			"Fb320p007++",
			"BA4sWteQ",
			"http://uat.mqapi.com",
			"https://sdpapi.ref.o2.co.uk/services/"),

	LOCAL_STUB("../service/src/main/resources/META-INF/keystore.jks",
			"Fb320p007++",
			"BA4sWteQ",
			"http://localhost:8998/services",
			"https://sdpapi.ref.o2.co.uk/services/");
	
	private final String KeystoreFile;
	private final String KeystorePassword;
	private final String tokenPassword;
	private final String proxyUrl;
	private final String serverUrl;
	
	O2Config(String KeystoreFile, String KeystorePassword, String tokenPassword, String proxyUrl, String serverUrl) {
		this.KeystoreFile = KeystoreFile;
		this.KeystorePassword = KeystorePassword;
		this.tokenPassword = tokenPassword;
		this.proxyUrl = proxyUrl;
		this.serverUrl = serverUrl;
	}

	public String getKeystoreFile() {
		return KeystoreFile;
	}

	public String getKeystorePassword() {
		return KeystorePassword;
	}

	public String getTokenPassword() {
		return tokenPassword;
	}

	public String getProxyUrl() {
		return proxyUrl;
	}

	public String getServerUrl() {
		return serverUrl;
	}
	
}
