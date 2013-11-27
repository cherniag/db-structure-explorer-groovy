package com.musicqubed.cloudassets;

import java.io.File;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.musicqubed.cloudassets.uploader.FileUploader;
import com.musicqubed.cloudassets.uploader.FileWithName;
import com.rackspacecloud.client.cloudfiles.FilesClient;

public class RackspaceNativeFileUploader implements FileUploader {

	private static final Logger LOGGER = LoggerFactory.getLogger(RackspaceNativeFileUploader.class);

	private HttpClient defaultHttpClient = new DefaultHttpClient();
	private FilesClient filesClient;
	private CloudFileSettings settings;

	public static void main(String[] args) throws Exception {
		CloudFileSettings settings = SampleCloudAssetsSettingsFileCreator.createTestCloudSettings();

		RackspaceNativeFileUploader u = new RackspaceNativeFileUploader(settings);
		FileWithName f = new FileWithName();
		f.setContentType(null);
		f.setFilePath(new File("src/main/resources/logo.png").getAbsolutePath());
		f.setNameToUse("fileWithName");
		u.uploadFile(f);
	}

	public RackspaceNativeFileUploader(CloudFileSettings settings) {
		try {
			this.settings = settings;
			filesClient = loginInternal(settings);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private FilesClient loginInternal(CloudFileSettings settings) throws Exception {
		String account = null;
		int connectionTimeOutMilliseconds = 10 * 1000;
		FilesClient filesClient = new FilesClient(defaultHttpClient, settings.getUserName(), settings.getPassword(),
				settings.getAuthenticationURL(), account, connectionTimeOutMilliseconds);
		boolean loginResult = filesClient.login();
		if (!loginResult) {
			throw new RuntimeException("Unable to login");
		}
		return filesClient;

	}

	private Map<String, String> CORS_MAP = ImmutableMap.of(
            "Access-Control-Allow-Origin", "*",
            "X-Container-Meta-Access-Control-Allow-Origin", "*",
//            "Access-Control-Max-Age", "600",
//            "Access-Control-Allow-Headers", "X-My-Header",
            "X-Object-Meta-Access-Control-Allow-Origin","*");
	
	@Override
	public String uploadFile(FileWithName f) throws Exception {
		File file = new File(f.getFilePath());
		String name = f.getNameToUse();
		String contentType = f.getContentType();

		LOGGER.info("uploading file " + file + " " + name);
		if (!file.exists()) {
			throw new RuntimeException("File not exist " + file.getAbsolutePath());
		}
		String defaultContentType = "application/unknown";

		if (contentType == null) {
			contentType = defaultContentType;
		}
		
		String tag = null;
		tag = filesClient.storeObjectAs(settings.getContainerName(), file, contentType, name, CORS_MAP);

		LOGGER.info("uploading file completed " + name + " " + tag);
		return tag;
	}

}
