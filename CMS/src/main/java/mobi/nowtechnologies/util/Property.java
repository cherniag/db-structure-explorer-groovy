package mobi.nowtechnologies.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

public class Property {
	private Properties props;
	
	private static Property property;

	protected Property() {
		props = new Properties();
		try {
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config.properties");
			props.load(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Property getInstance() {
		if (property == null) {
			property = new Property();
		}
		return property;
	}

	public String getStringValue(String key) {
		return props.getProperty(key);
	}

	public long getLongValue(String key) {
		String value = getStringValue(key);
		if (key == null)
			return 0;
		long result = 0;
		try {
			result = Long.parseLong(value);
		} catch (Exception e) {

		}
		return result;
	}

}
