package com.musicqubed.cloudassets;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class AssetUploader {
	private static final Logger LOGGER = LoggerFactory.getLogger(AssetUploader.class);

	public static void main(String[] args) throws Exception {
		if(args.length!=1){
			LOGGER.error("Expecting one parameter - xml file with configuration, see cloudAssetSettings.xml ");
			return;
		}
		
		int numThreads = 30;
		String fileName=args[0];
		CloudAssetsSettings cloudAssetsSettings=read(new File(fileName));
		
		MultithreadedFileUploader.uploadFiles(numThreads, cloudAssetsSettings);
	}

	private static CloudAssetsSettings read(File file) {
		return (CloudAssetsSettings)new XStream().fromXML(file);
	}
	
}
