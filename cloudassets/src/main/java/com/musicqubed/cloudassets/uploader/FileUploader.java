package com.musicqubed.cloudassets.uploader;

public interface FileUploader {
	String uploadFile(FileWithName fileWithName) throws Exception;
}
