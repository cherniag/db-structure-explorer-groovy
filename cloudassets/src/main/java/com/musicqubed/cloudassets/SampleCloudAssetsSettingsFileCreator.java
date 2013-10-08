package com.musicqubed.cloudassets;

import java.io.FileOutputStream;

import com.thoughtworks.xstream.XStream;

public class SampleCloudAssetsSettingsFileCreator {
	
	public static void main(String[] args) throws Exception {
		CloudAssetsSettings s = createTestSettings();
		new XStream().toXML(s, new FileOutputStream("cloudAssetSettings.xml"));
	}

	public static CloudAssetsSettings createTestSettings() {
		CloudAssetsSettings s = new CloudAssetsSettings();
		s.setDir("../web/src/main/webapp/assets");
		s.setPrefix("my17/assets");
		s.setSettings(createTestCloudSettings());
		return s;
	}

	public static CloudFileSettings createTestCloudSettings() {
		CloudFileSettings settings = new CloudFileSettings();
		settings.setUserName("chartsnow");
		settings.setPassword("b283a8dec498dee9e6a11f459bdcb194");
		settings.setContainerName("ldntestCloudFiles");
		settings.setProvider("cloudfiles-uk");
		settings.setAuthenticationURL("https://lon.auth.api.rackspacecloud.com/v1.0");
		return settings;
	}

}
