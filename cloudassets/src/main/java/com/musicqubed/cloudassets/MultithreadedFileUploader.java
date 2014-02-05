package com.musicqubed.cloudassets;

import com.musicqubed.cloudassets.uploader.FileDirUtils;
import com.musicqubed.cloudassets.uploader.FileUploader;
import com.musicqubed.cloudassets.uploader.FileUploaderFactory;
import com.musicqubed.cloudassets.uploader.FileWithName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MultithreadedFileUploader {
	private static final Logger LOGGER = LoggerFactory.getLogger(MultithreadedFileUploader.class);

	private static final int MAX_ATTEMPTS = 100;

	private final List<FileWithName> list;
	private final FileUploaderFactory fileUploaderFactory;
	private final int numThreads;
	private List<FileWithName> errors = new ArrayList<FileWithName>();

	public MultithreadedFileUploader(List<FileWithName> list, FileUploaderFactory fileUploaderFactory, int numThreads) {
		super();
		this.list = list;
		this.fileUploaderFactory = fileUploaderFactory;
		this.numThreads = Math.max(1, Math.min(numThreads, list.size()));
	}

	public static void uploadFiles(int numThreads, CloudAssetsSettings cloudAssetsSettings) throws IOException {
        uploadFiles(numThreads, new File(getPath(cloudAssetsSettings.getDir())), cloudAssetsSettings.getPrefix(), cloudAssetsSettings.getSettings());
	}

    public static String getPath(String dir) throws IOException {
        if(dir.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)){
            String relativePath = dir.substring(ResourceUtils.CLASSPATH_URL_PREFIX.length());
            String classPath = ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX+".").getPath();
            dir = new File(classPath + relativePath).getCanonicalPath();
        }

        return dir;
    }

	public static void uploadFiles(int numThreads, File dir, String prefix, final CloudFileSettings settings) {
		LOGGER.info("start ");
		try {
			List<FileWithName> list = FileDirUtils.prepareFileEntries(dir, prefix);
			FileUploaderFactory fileUploaderFactory = new FileUploaderFactory() {

				@Override
				public FileUploader newInstance() {
					//return new RackspaceNativeFileUploader(settings);
					//return createDummyNewFileUploader();
					return new CloudFileOpenSourceUploader(settings);
				}
			};

			int attemptNo = 0;
			List<FileWithName> unprocessed = processListSeveralAttempts(numThreads, list, settings, fileUploaderFactory, attemptNo);
			for (FileWithName f : unprocessed) {
				LOGGER.info("unprocessed " + f.getFilePath() + " " + f.getNameToUse());
			}
			LOGGER.info("Completed with " + unprocessed.size() + " unprocessed ");

		} catch (Exception ex) {
			LOGGER.error("Error " + ex, ex);
			ex.printStackTrace();
		}
	}

	private static List<FileWithName> processListSeveralAttempts(int numThreads, List<FileWithName> list,
			final CloudFileSettings settings, FileUploaderFactory fileUploaderFactory, int attemptNo) throws Exception {

		if (attemptNo > MAX_ATTEMPTS) {
			return list;
		}
		LOGGER.info("starting " + attemptNo + " attempt" + list.size() + " size");

		MultithreadedFileUploader u = new MultithreadedFileUploader(list, fileUploaderFactory, numThreads);
		u.processFiles();
		attemptNo++;
		LOGGER.info("Completed " + attemptNo + " attempt" + u.errors + " run Index");

		List<FileWithName> errs = u.errors;
		if (errs.size() > 0) {
			return processListSeveralAttempts(numThreads, errs, settings, fileUploaderFactory, attemptNo);
		}
		return errs;
	}

	public static FileUploader createDummyNewFileUploader() {
		return new FileUploader() {
			@Override
			public String uploadFile(FileWithName fileWithName) throws Exception {
				LOGGER.info("fileWithName: " + fileWithName);
				return "OK";
			}
		};
	}

	private void processFiles() throws Exception {
		CountDownLatch stopLatch = new CountDownLatch(numThreads);
		for (int i = 0; i < numThreads; i++) {
			new Thread(createRunnable(i, stopLatch)).start();
		}
		stopLatch.await();
	}

	private Runnable createRunnable(final int threadIndex, final CountDownLatch stopLatch) {

		final FileUploader fileUploader = fileUploaderFactory.newInstance();

		return new Runnable() {

			@Override
			public void run() {
				try {
					doUploads(fileUploader, threadIndex);
				} finally {
					stopLatch.countDown();
				}
			}

		};
	}

	private void doUploads(FileUploader fileUploader, int threadIndex) {
		LOGGER.info("do uploads " + threadIndex);
		int numberFilesProcessed = 0;
		boolean error = false;
		while (true) {

			FileWithName f = getNextFile();
			if (f == null) {
				break;
			}
			LOGGER.info("another file " + list.size());
			try {

				@SuppressWarnings("unused")
				String tag = fileUploader.uploadFile(f);
				numberFilesProcessed++;
			} catch (Exception ex) {
				LOGGER.error("Error:" + f.getNameToUse() + " " + ex + " ", ex);
				addError(f);
				error = true;
				break;
			}
		}
		if (error) {
			LOGGER.info("threadIndex " + threadIndex + " has error");
		}
		LOGGER.info("do uploads completed " + threadIndex + " numberFilesProcessed=" + numberFilesProcessed);
	}

	private synchronized FileWithName getNextFile() {
		if (list.size() == 0) {
			return null;
		}
		return list.remove(0);
	}

	private synchronized void addError(FileWithName f) {
		errors.add(f);
	}


}