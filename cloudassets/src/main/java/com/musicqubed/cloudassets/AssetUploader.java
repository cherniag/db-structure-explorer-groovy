package com.musicqubed.cloudassets;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class AssetUploader {
	private static final Logger LOGGER = LoggerFactory.getLogger(AssetUploader.class);

	public static void main(String[] args) throws Exception {
		if(args.length!=1){
            String fileName = AssetUploader.class.getClassLoader().getResource("cloudAssetSettings.xml").getPath();
			args = new String[]{fileName};
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
