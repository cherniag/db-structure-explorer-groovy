package com.musicqubed.cloudassets;

//import static org.jclouds.blobstore.options.PutOptions.Builder.multipart;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.PutOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.musicqubed.cloudassets.uploader.FileUploader;
import com.musicqubed.cloudassets.uploader.FileWithName;

// USED JUST FOR TESTING
public class CloudFileOpenSourceUploader implements FileUploader {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CloudFileOpenSourceUploader.class);

	private CloudFileSettings settings;
	private BlobStore storage;
	private final BlobStoreContext context;

	public static void main(String[] args) throws Exception {
		CloudFileSettings settings = SampleCloudAssetsSettingsFileCreator
				.createTestCloudSettings();

		CloudFileOpenSourceUploader u = new CloudFileOpenSourceUploader(
				settings);
		FileWithName f = new FileWithName();
		f.setContentType("application/font-woff");
		//f.setFilePath(new File("src/main/resources/logo.png").getAbsolutePath());
		f.setFilePath(new File("../web/src/main/webapp/assets/mobile/mobo/fonts/droidsans-bold-webfont.woff").getAbsolutePath());
		f.setNameToUse("xxxx-mobo"+new Date().getTime()+".woff");
		u.uploadFile(f);
	}

	public CloudFileOpenSourceUploader(CloudFileSettings settings) {
		try {
			this.settings = settings;

			context = ContextBuilder
					.newBuilder(settings.getProvider())
					.credentials(settings.getUserName(), settings.getPassword())
					.buildView(BlobStoreContext.class);
			storage = context.getBlobStore();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public String uploadFile(FileWithName f) throws Exception {
		File file=new File(f.getFilePath());
		// LOGGER.info("copy File to Cloud " + file.getAbsolutePath() +" " +
		// cloudName + " " + idx);
		if (!file.exists()) {
			throw new IllegalArgumentException("File don't exist "
					+ file.getAbsolutePath());
		}

		// storage.removeBlob(settings.getContainerName(), cloudName);

		Blob blob = null;
		if (f.getContentType() == null) {
			blob = storage.blobBuilder(f.getNameToUse()).payload(file).build();
		} else {

			Map<String, String> map=new HashMap<String, String>();
			//ret.put("X-Container-Meta-Access-Control-Allow-Origin", "*");
			
			blob = storage.blobBuilder(f.getNameToUse()).payload(file)
					.contentType(f.getContentType()).userMetadata(map).build();
		}

		String eTag = storage.putBlob(settings.getContainerName(), blob,
				PutOptions.Builder.multipart());

		/*if (printUri) {
			Blob retrievedBlob = storage.getBlob(settings.getContainerName(),
					cloudName);
			LOGGER.info("File: " + file.getAbsolutePath() + " URI: "
					+ retrievedBlob.getMetadata().getPublicUri() + " eTag="
					+ eTag + " " + cloudName + " " + idx);
		} else {
			LOGGER.info("File: " + file.getAbsolutePath() + " eTag=" + eTag
					+ " " + cloudName + " " + idx);
		}*/
		LOGGER.info("File: " + file.getAbsolutePath() + " eTag=" + eTag
				+ " " + f.getNameToUse() );

		return eTag;
	}

}