package com.musicqubed.cloudassets;

import com.musicqubed.cloudassets.uploader.FileUploader;
import com.musicqubed.cloudassets.uploader.FileWithName;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.PutOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

public class CloudFileOpenSourceUploader implements FileUploader {
	private static final Logger LOGGER = LoggerFactory.getLogger(CloudFileOpenSourceUploader.class);

	private CloudFileSettings settings;
	private BlobStore storage;
	private final BlobStoreContext context;

	public static void main(String[] args) throws Exception {
		CloudFileSettings settings = SampleCloudAssetsSettingsFileCreator.createTestCloudSettings();

		CloudFileOpenSourceUploader u = new CloudFileOpenSourceUploader(settings);
		FileWithName f = new FileWithName();
		f.setContentType("application/font-woff");
		//f.setFilePath(new File("src/main/resources/logo.png").getAbsolutePath());
		f.setFilePath(new File("../web/src/main/webapp/assets/mobile/mobo/fonts/droidsans-bold-webfont.woff")
				.getAbsolutePath());
		f.setNameToUse("xxxx-mobo" + new Date().getTime() + ".woff");
		u.uploadFile(f);
	}

	public CloudFileOpenSourceUploader(CloudFileSettings settings) {
		try {
			this.settings = settings;
			ContextBuilder contextBuilder = ContextBuilder.newBuilder(settings.getProvider()).credentials(
					settings.getUserName(), settings.getPassword());
			contextBuilder.getApiMetadata().getDefaultProperties().setProperty(PROPERTY_USER_METADATA_PREFIX, "");
			context = contextBuilder.buildView(BlobStoreContext.class);

			storage = context.getBlobStore();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public String uploadFile(FileWithName f) throws Exception {
		File file = new File(f.getFilePath());
		if (!file.exists()) {
			throw new IllegalArgumentException("File don't exist " + file.getAbsolutePath());
		}

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Access-Control-Allow-Origin", "*");

		Blob blob = null;
		if (f.getContentType() == null) {
			blob = storage.blobBuilder(f.getNameToUse()).payload(file).userMetadata(headers).build();
		} else {
			blob = storage.blobBuilder(f.getNameToUse()).payload(file).contentType(f.getContentType())
					.userMetadata(headers).build();
		}

		String eTag = storage.putBlob(settings.getContainerName(), blob, PutOptions.Builder.multipart());

		LOGGER.info("File: " + file.getAbsolutePath() + " eTag=" + eTag + " " + f.getNameToUse());

		return eTag;
	}

}