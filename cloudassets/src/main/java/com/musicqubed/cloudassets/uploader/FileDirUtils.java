package com.musicqubed.cloudassets.uploader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileDirUtils {
	private static  final Logger LOGGER = LoggerFactory.getLogger(FileDirUtils.class);



	public static List<FileWithName> createFileEntries(File dir, String prefix, List<File> files) {
		List<FileWithName> fileWithNames=new ArrayList<FileWithName>();
		
		for (File file : files) {
			String fullFileName = prefix + getFileName(dir, file);


			FileWithName fileWithName=new FileWithName();
			fileWithName.setContentType(getContentType(file.getName()));
			fileWithName.setFilePath(file.getAbsolutePath());
			fileWithName.setNameToUse(fullFileName);
			
			fileWithNames.add(fileWithName);
		}
		return fileWithNames;
	}

	public static List<FileWithName> prepareFileEntries(File dir, String prefix) {
		List<File> files = FileDirUtils.listAndSortFiles(dir);
		//files = filterFiles(files);
		LOGGER.info("filtered: " + files.size() + " files");
		return FileDirUtils.createFileEntries(dir, prefix, files);
	}



	
	public static List<File> listAndSortFiles(File dir) {
		List<File> files = listFiles(dir);

		//printExtensions(files);

		LOGGER.info("found " + files.size() + " files");
		Collections.sort(files, getFileNameComparator());
		return files;
	}

	public static Comparator<? super File> getFileNameComparator() {
		return new Comparator<File>() {
			public int compare(File o1, File o2) {
				return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
			}
		};
	}

	public static String getContentType(String name) {
		if (name.endsWith("css")) {
			return "text/css;charset=UTF-8";
		}
		if (name.endsWith("htm") || name.endsWith("html")) {
			return "text/html;charset=UTF-8";
		}
		return null;
	}

	public static List<File> filterFiles(List<File> files) {
		List<File> filteredFiles = new ArrayList<File>();
		for (File file : files) {

			//boolean include = file.getName().endsWith("css");
			//boolean include=file.getAbsolutePath().contains("o2") && file.getName().endsWith("css");
			boolean include = true;

			if (include) {
				filteredFiles.add(file);
			}
		}
		return filteredFiles;
	}

	/** @return relative path to given file within a given dir*/
	public static String getFileName(File dir, File file) {
		String dirPath = dir.getAbsolutePath();
		String filePath = file.getAbsolutePath().substring(dirPath.length());
		return filePath.replace(File.separator, "/");

	}

	public static List<File> listFiles(File dir) {
		List<File> res = new ArrayList<File>();
		File[] children = dir.listFiles();
		for (File child : children) {
			if (child.isDirectory()) {
				res.addAll(listFiles(child));
			} else {
				res.add(child);
			}
		}
		return res;
	}

	public static void printExtensions(List<File> files) {
		Set<String> extensions = new HashSet<String>();
		for (File file : files) {
			extensions.add(FilenameUtils.getExtension(file.getName()));
		}
		LOGGER.info("Extensions:" + extensions);
	}

}
